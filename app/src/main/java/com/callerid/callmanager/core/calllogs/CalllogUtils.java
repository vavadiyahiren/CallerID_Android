package com.callerid.callmanager.core.calllogs;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.callerid.callmanager.BuildConfig;
import com.callerid.callmanager.core.contacts.ContactService;
import com.callerid.callmanager.core.contacts.ContactServiceImpl;
import com.callerid.callmanager.database.CallLogEntity;
import com.callerid.callmanager.database.ContactEntity;
import com.callerid.callmanager.database.Phone;
import com.callerid.callmanager.utilities.AppPref;
import com.callerid.callmanager.utilities.Constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CalllogUtils {


    public static void insertCallLogs(Activity activity) {

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_CALL_LOG}, 101);
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {

            // Load contact mappings
            Map<String, String> phoneToContactIdMap = loadContactsMapFast(activity);
            Map<String, String> contactNames = loadContactNamesFast(activity);

            List<CallLogEntity> callLogList = new ArrayList<>();
            long lastSyncTimePref = AppPref.getLongPref(activity, Constant.LAST_SYNC_TIME, 0);
            long newSyncTime = System.currentTimeMillis();

            String selection = null;
            String[] selectionArgs = null;

            if (lastSyncTimePref > 0) {
                selection = CallLog.Calls.DATE + " > ?";
                selectionArgs = new String[]{String.valueOf(lastSyncTimePref)};
            }

            AppPref.setLongPref(activity, Constant.LAST_SYNC_TIME, newSyncTime);

            // Load only required columns
            String[] projection = {
                    CallLog.Calls.NUMBER,
                    CallLog.Calls.CACHED_NAME,
                    CallLog.Calls.TYPE,
                    CallLog.Calls.DATE,
                    CallLog.Calls.DURATION
            };

            try (Cursor cursor = activity.getContentResolver().query(
                    CallLog.Calls.CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    CallLog.Calls.DATE + " DESC"
            )) {
                if (cursor == null || cursor.getCount() == 0) return;

                int numberIdx = cursor.getColumnIndexOrThrow(CallLog.Calls.NUMBER);
                int nameIdx = cursor.getColumnIndexOrThrow(CallLog.Calls.CACHED_NAME);
                int typeIdx = cursor.getColumnIndexOrThrow(CallLog.Calls.TYPE);
                int dateIdx = cursor.getColumnIndexOrThrow(CallLog.Calls.DATE);
                int durationIdx = cursor.getColumnIndexOrThrow(CallLog.Calls.DURATION);

                while (cursor.moveToNext()) {
                    String number = cursor.getString(numberIdx);
                    String name = cursor.getString(nameIdx);
                    int type = cursor.getInt(typeIdx);
                    long dateMillis = cursor.getLong(dateIdx);
                    String duration = cursor.getString(durationIdx);

                    String contactId = getContactIdFast(number, phoneToContactIdMap);
                    String cachedPhotoUri = contactId != null && !contactId.isEmpty()
                            ? String.valueOf(getContactPhotoUri(contactId))
                            : "";

                    if (name == null || name.isEmpty()) {
                        name = contactNames.getOrDefault(contactId, "");
                    }

                    String callType = getCallType(type);
                    String durationText = getFormattedDuration(duration);

                    if (BuildConfig.DEBUG) {
                        Log.e("CallLog", "Number: " + number + ", Name: " + name +
                                ", Type: " + callType + ", Date: " + dateMillis +
                                ", Duration: " + durationText + ", PhotoUri:" + cachedPhotoUri);
                    }

                    CallLogEntity callLogEntity = new CallLogEntity();
                    callLogEntity.name = name != null && !name.isEmpty() ? name : number;
                    callLogEntity.number = number;
                    callLogEntity.type = type;
                    callLogEntity.date = dateMillis;
                    callLogEntity.duration = duration;
                    callLogEntity.timeDuration = durationText;
                    callLogEntity.contactId = contactId;
                    callLogEntity.photo = cachedPhotoUri;
                    callLogEntity.address = "";
                    callLogEntity.isSaved = name != null && !name.isEmpty();
                    callLogEntity.isFavourite = false;

                    callLogList.add(callLogEntity);
                }
            }

            // Bulk insert
            if (!callLogList.isEmpty()) {

                CallLogsService callLogsService = CallLogsServiceImpl.getInstance();
                callLogsService.insertAll(callLogList);
                Log.e("callLogList.size()", "Inserted: " + callLogList.size());
            }

            fetchAndStoreContacts(activity);
        });
    }

    /**
     * Fast contact ID lookup using pre-loaded phone number map
     */
    public static String getContactIdFast(String phoneNumber, Map<String, String> phoneToContactIdMap) {
        if (phoneNumber == null) return "";

        // Try exact match first
        String contactId = phoneToContactIdMap.get(phoneNumber);
        if (contactId != null) return contactId;

        // Try normalized number
        String normalized = PhoneNumberUtils.normalizeNumber(phoneNumber);
        if (normalized != null) {
            contactId = phoneToContactIdMap.get(normalized);
            if (contactId != null) return contactId;

            // Try last 8 digits for partial match
            if (normalized.length() >= 8) {
                String lastDigits = normalized.substring(normalized.length() - 8);
                contactId = phoneToContactIdMap.get(lastDigits);
                if (contactId != null) return contactId;
            }
        }
        return "";
    }

   /* private Set<String> getContactIdsFast(String phoneNumber, Map<String, Set<String>> phoneToContactIdsMap) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) return Collections.emptySet();

        Set<String> result = new HashSet<>();

        // Try raw number
        if (phoneToContactIdsMap.containsKey(phoneNumber)) {
            result.addAll(phoneToContactIdsMap.get(phoneNumber));
        }

        // Try normalized number
        String normalized = PhoneNumberUtils.normalizeNumber(phoneNumber);
        if (normalized != null && phoneToContactIdsMap.containsKey(normalized)) {
            result.addAll(phoneToContactIdsMap.get(normalized));
        }

        // Try E.164 format
        TelephonyManager tm = (TelephonyManager) requireContext().getSystemService(Context.TELEPHONY_SERVICE);
        String countryIso = tm != null ? tm.getSimCountryIso().toUpperCase(Locale.getDefault()) : "US";

        String e164 = PhoneNumberUtils.formatNumberToE164(phoneNumber, countryIso);
        if (e164 != null && phoneToContactIdsMap.containsKey(e164)) {
            result.addAll(phoneToContactIdsMap.get(e164));
        }

        // Try last 8 digits
        if (normalized != null && normalized.length() >= 8) {
            String lastDigits = normalized.substring(normalized.length() - 8);
            if (phoneToContactIdsMap.containsKey(lastDigits)) {
                result.addAll(phoneToContactIdsMap.get(lastDigits));
            }
        }

        return result;
    }

    private String getFirstContactIdFast(String phoneNumber, Map<String, Set<String>> phoneToContactIdsMap) {
        Set<String> ids = getContactIdsFast(phoneNumber, phoneToContactIdsMap);
        return ids.isEmpty() ? "" : ids.iterator().next();
    }*/


    /**
     * Optimized contacts loading - loads all phone numbers to contact ID mappings at once
     */
    public static Map<String, String> loadContactsMapFast(Activity activity) {
        Map<String, String> phoneToContactIdMap = new HashMap<>();

        try {
            Cursor cursor = activity.getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[]{
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                            ContactsContract.CommonDataKinds.Phone.NUMBER
                    },
                    null,
                    null,
                    null
            );

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                    String number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));

                    if (number != null && contactId != null) {
                        // Store multiple variations for fast lookup
                        phoneToContactIdMap.put(number, contactId);

                        // Store normalized version
                        String normalized = PhoneNumberUtils.normalizeNumber(number);
                        if (normalized != null) {
                            phoneToContactIdMap.put(normalized, contactId);

                            // Store last 8 digits for partial matching
                            if (normalized.length() >= 8) {
                                String lastDigits = normalized.substring(normalized.length() - 8);
                                phoneToContactIdMap.put(lastDigits, contactId);
                            }
                        }
                    }
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e("loadContactsMapFast", "Error loading contacts: " + e.getMessage());
        }

        Log.e("loadContactsMapFast", "Loaded " + phoneToContactIdMap.size() + " phone number mappings");
        return phoneToContactIdMap;
    }

    /*private Map<String, Set<String>> loadContactsMapFast() {
        Map<String, Set<String>> phoneToContactIdsMap = new HashMap<>();
        ContentResolver resolver = requireActivity().getContentResolver();

        // Get country ISO for E.164 formatting
        TelephonyManager tm = (TelephonyManager) requireContext().getSystemService(Context.TELEPHONY_SERVICE);
        String countryIso = tm != null ? tm.getSimCountryIso().toUpperCase(Locale.getDefault()) : "US";

        String[] projection = {
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };

        try (Cursor cursor = resolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                null,
                null,
                null
        )) {
            if (cursor != null) {
                int contactIdIndex = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
                int numberIndex = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER);

                while (cursor.moveToNext()) {
                    String contactId = cursor.getString(contactIdIndex);
                    String rawNumber = cursor.getString(numberIndex);

                    if (contactId == null || rawNumber == null) continue;

                    Set<String> keys = new HashSet<>();

                    // Raw number
                    keys.add(rawNumber);

                    // Normalized number (basic formatting)
                    String normalized = PhoneNumberUtils.normalizeNumber(rawNumber);
                    if (normalized != null) {
                        keys.add(normalized);
                    }

                    // E.164 number (standard international format)
                    String e164 = PhoneNumberUtils.formatNumberToE164(rawNumber, countryIso);
                    if (e164 != null) {
                        keys.add(e164);
                    }

                    // Last 8 digits (for partial matching fallback)
                    if (normalized != null && normalized.length() >= 8) {
                        keys.add(normalized.substring(normalized.length() - 8));
                    }

                    // Insert all variations into the map
                    for (String key : keys) {
                        phoneToContactIdsMap
                                .computeIfAbsent(key, k -> new HashSet<>())
                                .add(contactId);
                    }
                }
            }
        } catch (SecurityException se) {
            Log.e("loadContactsMapFast", "Missing READ_CONTACTS permission", se);
        } catch (Exception e) {
            Log.e("loadContactsMapFast", "Error loading contacts", e);
        }

        Log.d("loadContactsMapFast", "Loaded " + phoneToContactIdsMap.size() + " phone number keys");
        return phoneToContactIdsMap;
    }
*/

    public static Map<String, String> loadContactNamesFast(Activity activity) {
        Map<String, String> contactNamesMap = new HashMap<>();


        Cursor cursor = null;
        try {
            cursor = activity.getContentResolver().query(
                    ContactsContract.Contacts.CONTENT_URI,
                    new String[]{ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME},
                    null,
                    null,
                    null
            );

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String id = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                    contactNamesMap.put(id, name != null ? name : "");
                }
            }
        } catch (Exception e) {
            Log.e("loadContactNamesFast", "Error loading contact names", e);
        } finally {
            if (cursor != null) cursor.close();

            Log.e("loadContactNamesFast", "ContactNamesFast size:" + contactNamesMap.size());
        }
        return contactNamesMap;
    }

    /**
     * Fast contact ID lookup using pre-loaded phone number map
     */

    public static Uri getContactPhotoUri(String contactId) {
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(contactId));
        return Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
    }

    public static void fetchAndStoreContacts(Activity activity) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            List<ContactEntity> contactList = new ArrayList<>();
            Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

            String[] projection = new String[]{
                    ContactsContract.CommonDataKinds.Phone._ID,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER,
                    ContactsContract.CommonDataKinds.Phone.TYPE,
                    ContactsContract.CommonDataKinds.Phone.LABEL,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID
            };

            long lastSavedDate = 0;
            Cursor cursor;

            long lastSyncTime = AppPref.getLongPref(activity, Constant.LAST_SYNC_TIME_CONTACT, 0);
            if (lastSyncTime == 0) {
                lastSyncTime = System.currentTimeMillis();
                AppPref.setLongPref(activity, Constant.LAST_SYNC_TIME_CONTACT, lastSyncTime);

                cursor = activity.getContentResolver().query(uri, projection, null, null,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

            } else {
                //lastSavedDate = callLogViewModel.getLastSavedCallDate();
                lastSavedDate = AppPref.getLongPref(activity, Constant.LAST_SYNC_TIME_CONTACT, 0);
                lastSyncTime = System.currentTimeMillis();
                AppPref.setLongPref(activity, Constant.LAST_SYNC_TIME_CONTACT, lastSyncTime);

                String selection = null;
                String[] selectionArgs = null;

                selection = ContactsContract.CommonDataKinds.Phone.CONTACT_LAST_UPDATED_TIMESTAMP + " > ?";
                selectionArgs = new String[]{String.valueOf(lastSavedDate)};

                cursor = activity.getContentResolver().query(uri, projection, selection,
                        selectionArgs, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

            }

            if (cursor != null) {
                Map<String, ContactEntity> contactMap = new HashMap<>(); // To avoid duplicates by contactId

                while (cursor.moveToNext()) {
                    String contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                    String displayName = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String normalizedNumber = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER));
                    int type = cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.TYPE));
                    String label = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.LABEL));

                    // Create phone object
                    Phone phone = new Phone();
                    phone.number = number;
                    phone.normalizedNumber = normalizedNumber != null ? normalizedNumber : number;
                    phone.type = type;
                    phone.label = label != null ? label : "";

                    ContactEntity contact = contactMap.get(contactId);
                    if (contact == null) {
                        contact = new ContactEntity();
                        contact.contactId = contactId;
                        contact.displayName = displayName;
                        contact.isFavourite = false; // default value, set as needed
                        contact.isSaved = true;
                        contact.isBlocked = false;
                        contact.spam = false;
                        contact.phones = new ArrayList<>();
                        contact.photo = null;
                        contact.address = "";
                        contact.callType = "";
                        contact.oprator = "";
                        contact.favoritesIndex = "";
                        contact.callTime = "";
                        contact.normalizedNumber = phone.normalizedNumber;

                        // Fetch accounts for this contactId
                        // contact.accounts = getAccountsForContact(contactId);

                        contactMap.put(contactId, contact);
                    }

                    // Add phone if not duplicate
                    boolean phoneExists = false;
                    for (Phone p : contact.phones) {
                        if (p.number.equals(phone.number)) {
                            phoneExists = true;
                            break;
                        }
                    }
                    if (!phoneExists) {
                        contact.phones.add(phone);
                    }
                }
                cursor.close();

                contactList.addAll(contactMap.values());

                ContactService contactService =  ContactServiceImpl.getInstance();

                contactService.insertAll(contactList);

            }
        });
    }

    public static String getCallType(int type) {
        switch (type) {
            case CallLog.Calls.INCOMING_TYPE:
                return "Incoming";
            case CallLog.Calls.OUTGOING_TYPE:
                return "Outgoing";
            case CallLog.Calls.MISSED_TYPE:
                return "Missed";
            case CallLog.Calls.REJECTED_TYPE:
                return "Rejected";
            case CallLog.Calls.BLOCKED_TYPE:
                return "Blocked";
            default:
                return "Unknown";
        }
    }

    public static String getFormattedDuration(String duration) {
        try {
            int totalSeconds = Integer.parseInt(duration);
            int hours = totalSeconds / 3600;
            int minutes = (totalSeconds % 3600) / 60;
            int seconds = totalSeconds % 60;

            if (hours > 0)
                return hours + "h " + minutes + "m " + seconds + "s";
            else if (minutes > 0)
                return minutes + "m " + seconds + "s";
            else
                return seconds + "s";
        } catch (NumberFormatException e) {
            return "0s";
        }
    }


}
