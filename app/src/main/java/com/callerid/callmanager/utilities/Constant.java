package com.callerid.callmanager.utilities;


public class Constant {

    public static final String PERMISSION_INFO_SHOW = "permission_info_show";

    public static final String KEY_RATING = "rating";
    public static final String FROM = "from";
    public static final String MENU = "Menu";
    public static final String LOGIN = "Login";

    public static final String LAST_SYNC_TIME = "LAST_SYNC_TIME";

    public static final String KEY_PAD_DIAL_TONE = "KEY_PAD_DIAL_TONE";
    public static final String THEME_MODE = "THEME_MODE";
    public static final String LAST_SYNC_TIME_CONTACT = "LAST_SYNC_TIME_CONTACT";
    public static final String KEY_EXIT_COUNT = "exit_count";
    public static final String LANGUAGE_SET = "LanguageSet";
    public static final String PERMISSION_SET = "PermissionSet";
    public static final String CURRENT_FAV = "current_fav";
    public static final String SUB_EVENT = "subscription_event";
    public static final String SUBSCRIPTION = "subscription";

    /*----------------    Log Event Keys    ---------------*/
    public static final String CURRENT_SCREEN = "current_screen";
    public static final String SCREEN_EVENT = "screen_event";



    public final static String SEARCH = "search";
    public static final String SKU_MONTH = "month_plan";
    public static final String SKU_YEAR = "year_plan";
    public static final String LIGHT_MODE = "light_mode";
    public static final String ENGLISH = "English";

    public static final String FCM_AOD = "/topics/AOD";
    public static final String FCM_ALL = "/topics/all";

    public static final int NIGHT = 1;
    public static final int LIGHT = 2;
    public static final int DEFAULT = 3;
    public static final String EMPTY_ADS = "e";
    public static final String REWARD_ADS = "r";

    public static final String SCREEN_CLICK = "screen_click";
    public static final String SOURCE = "source";
    public static final String EVENT = "event";

    public static final int ITEM_VIEW_TYPE_CONTENT = 0;
    public static final int ITEM_VIEW_TYPE_AD = 1;
    public static final int ITEMS_PER_AD = 5;
    public static final int FULL_NATIVE_AD = 3;
    public static final int SMALL_NATIVE_AD = 2;
    public static final int TYPE_ITEM_DMV_ADS = 5;
    public static final int TYPE_ITEM = 0;
    public static final int TYPE_LOADING = 1;
    public static final String ProgressItem = "ProgressItem";
    public static final String EULA_PERMISSION = "eula_permission";
    public static final String DEFAULT_MOBILE_NUMBER_ONLY = "DEFAULT_MOBILE_NUMBER_ONLY";
    public static boolean isLightMode = false;
    public static int AdLogCount = 0;
    public static String isFROM_LIVE_WALLPAPER = "isFromLiveWallPaper";
    public static String FIRST_TIME = "first_time";
    public static int pageLimit = 60;
    public static boolean isSubscribed = false;
    public static boolean IS_FROM_AOD = false;
    public static String FIRST_TIME_LOGIN = "first_time_login";
    public static String IS_LOGIN = "is_login";
    public static String IS_SUBSCRIBED = "is_subscribed";

    /*----------------  Analytics Log Event Keys    ---------------*/
    public static String USER_NAME = "user_name";
    public static String USER_ID = "user_id";
    public static String USER_PROFILE = "user_profile";
    public static String USER_TOKEN = "user_token";
    public static String USER_EMAIL = "user_email";
    public static String USER_FCM = "user_fcm";
    public static String USER_COUNTRY_CODE = "user_country_code";
    public static String SUBSCRIBED_EMAIL = "subscribed_email";
    public static String SUBSCRIBED_EXPIRE_TIME = "subscribed_expire_time";
    public static String EXPIRY_TIME_MILLIS = "expiryTimeMillis";

    public static String IS_FROM_SUBSCRIPTION = "isFromSubscription";
    public static String SELECTED_LANGUAGE = "selected_language";
    public static String[] countryList = {"English", "Hindi", "Spanish", "French", "German", "Korean", "Portuguese", "Italian", "Arabic"};
    public static String POSITION = "position";

    private static final int[] PRIVT_COLORS = {
            0xFFFAB1B1,
            0xFFEED898,
            0xFFFDBED4,
            0xFF9BC9EE,
            0xFFE1FDBE,
            0xFFBEE6FD,
            0xFF8FF3E1,
            0xFFFFC9B2,
            0xFFCEBEFD,
            0xFFFDEFBE,
            0xFFB3F3AE,
            0xFFF187AB};
    private static final int[] PRIVT_COLORS_Text = {
            0xFFC72A2A,
            0xFFDBAA16,
            0xFFFF2C75,
            0xFF2196F3,
            0xFF3FA924,
            0xFF1582BF,
            0xFF004D40,
            0xFFFF6F32,
            0xFF5636B6,
            0xFFDEAD00,
            0xFF38B82F,
            0xFFE91E63};


    public static int getColorForName(String name) {
        byte[] bytes = name.getBytes();
        int sum = 0;
        for (byte aByte : bytes) {
            sum += aByte;
        }
        return PRIVT_COLORS_Text[Math.abs(sum % PRIVT_COLORS_Text.length)];
    }

    public static int getColorForCardView(String name) {
        byte[] bytes = name.getBytes();
        int sum = 0;
        for (byte aByte : bytes) {
            sum += aByte;
        }
        return PRIVT_COLORS[Math.abs(sum % PRIVT_COLORS.length)];
    }
}

