package com.callerid.callmanager.utilities;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.BlockedNumberContract;
import android.provider.ContactsContract;
import android.telecom.TelecomManager;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.callerid.callmanager.BuildConfig;
import com.callerid.callmanager.R;

import java.io.InputStream;

public class Utility {

    public static void setStatusBar(Activity activity){

        View decor = activity.getWindow().getDecorView();
        boolean isDark = AppPref.getBooleanPref(activity, Constant.THEME_MODE, false);
        if (isDark) {
            // Clear the flag → Light icons for dark background
            decor.setSystemUiVisibility(0);
        } else {
            // Set light status bar → Dark icons for light background
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

    }
    public static void shareApp(Context context){
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));
            String shareMessage = context.getString(R.string.let_me_recommend_you);
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            context.startActivity(Intent.createChooser(shareIntent, "choose one"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void openAppInPlayStore(Context activity) {
        try {
            // Open the Play Store page of your app
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + activity.getPackageName()));
            activity.startActivity(intent);
        } catch (android.content.ActivityNotFoundException e) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + activity.getPackageName()));
            activity.startActivity(intent);
        }
    }

    public static Bitmap getContactImageByContactId(Context context , String contactId) {

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
//            ActivityCompat.requestPermissions(context,
//                    new String[]{Manifest.permission.READ_CONTACTS},
//                    REQUEST_CONTACTS_PERMISSION);
            return null;
        }


        if(contactId.contains("::")){
            return null;
        }
        if(contactId.equals("0")){
            return null;
        }
//        Log.e("sendSmsMessages","=smsCursor=allllllllllll=contactId="+contactId);
        ContentResolver contentResolver = context.getContentResolver();

        // Query to find contact ID using the phone number
//        Uri phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
//        String[] phoneProjection = new String[]{ContactsContract.CommonDataKinds.Phone.CONTACT_ID};
//        String phoneSelection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?";
//        String[] phoneSelectionArgs = new String[]{contactId};

//        Cursor phoneCursor = contentResolver.query(phoneUri, phoneProjection, phoneSelection, phoneSelectionArgs, null);

//        if (phoneCursor != null && phoneCursor.moveToFirst()) {
        // Get the Contact ID
//            long contactId = phoneCursor.getLong(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));

        // Query to get the photo URI for the contact
        Uri contactUri = ContactsContract.Contacts.CONTENT_URI;
        Uri photoUri = Uri.withAppendedPath(contactUri, String.valueOf(contactId));
        String[] photoProjection = new String[]{ContactsContract.Contacts.PHOTO_URI}; // Thumbnail URI
        Cursor photoCursor = contentResolver.query(photoUri, photoProjection, null, null, null);

        if (photoCursor != null && photoCursor.moveToFirst()) {

            int readIndex = photoCursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI);
            String photoPath = photoCursor.getString(readIndex);

            if (photoPath != null) {
                try {
                    // Convert photo URI to Bitmap
                    InputStream inputStream = contentResolver.openInputStream(Uri.parse(photoPath));
                    Bitmap photoBitmap = BitmapFactory.decodeStream(inputStream);

                    // Set the ImageView
//                        imageView.setImageBitmap(photoBitmap);

                    if (inputStream != null) {
                        inputStream.close();
                    }
                    Log.e(TAG, "getProfileImageByNumber:11 "+photoBitmap );
                    photoCursor.close();
                    return photoBitmap;
                } catch (Exception e) {
                    photoCursor.close();
                    e.printStackTrace();
                    return null;
                }
            } else {
                // Set default image if photo not available
                Log.e(TAG, "getProfileImageByNumber:22 " );
//                    imageView.setImageResource(R.drawable.default_placeholder);
                photoCursor.close();
                return null;
            }

        }
//            phoneCursor.close();
//        } else {
//            // No contact found for the given number
//            Log.e(TAG, "getProfileImageByNumber:33 " );
////            imageView.setImageResource(R.drawable.default_placeholder);
//            return null;
//        }
        Log.e(TAG, "getProfileImageByNumber:44 " );
        return null;
    }
    public static String normalizePhoneNumber(String phoneNumber) {
        return PhoneNumberUtils.normalizeNumber(phoneNumber);
    }

    public static  boolean isNumberBlocked(Context context, String phoneNumber) {
        if (canUseBlockedNumberApi(context)) {
            try {
                return BlockedNumberContract.isBlocked(context, phoneNumber);
            } catch (SecurityException e) {
                e.printStackTrace(); // or log
            }
        }
        return false;
    }
    public static boolean canUseBlockedNumberApi(Context context) {
        TelecomManager tm = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
        return tm != null && context.getPackageName().equals(tm.getDefaultDialerPackage());
    }

}
