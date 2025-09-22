package com.callerid.callmanager.utilities;

import static android.content.Context.WINDOW_SERVICE;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.callerid.callmanager.models.LanguageModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocaleHelper {

    private static final String SELECTED_LANGUAGE = "Locale.Helper.Selected.Language";

    public static Context onAttach(Context context) {
        String lang = getPersistedData(context, Locale.getDefault().getLanguage());
        return setLocale(context, lang);
    }

    public static Context onAttach(Context context, String defaultLanguage) {
        String lang = getPersistedData(context, defaultLanguage);
        return setLocale(context, lang);
    }

    public static String getLanguage(Context context) {
        return getPersistedData(context, Locale.getDefault().getLanguage());
    }

    public static Context setLocale(Context context, String language) {
        persist(context, language);
        return updateResources(context, language);

    }

    public static String getPersistedData(Context context, String defaultLanguage) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(SELECTED_LANGUAGE, defaultLanguage);
    }

    public static void persist(Context context, String language) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SELECTED_LANGUAGE, language);
        editor.apply();
    }


    private static Context updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration configuration = context.getResources().getConfiguration();

        configuration.setLocale(locale);
        configuration.setLayoutDirection(locale);
        return context.createConfigurationContext(configuration);
    }

    private static Context updateResources1(Context context, String language) {
        Log.e("TAG", "updateResources:language:1--- "+language );

        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        configuration.setLayoutDirection(locale);

        context.createConfigurationContext(configuration);

        Resources resources = context.getResources();
        Configuration config = new Configuration(resources.getConfiguration());
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());


        return context;
    }

    @SuppressWarnings("deprecation")
    private static Context updateResourcesLegacy(Context context, String language) {
        Log.e("TAG", "updateResources:language:2--- "+language );
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLayoutDirection(locale);
        }
        configuration.locale = locale;


        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        return context;
    }
    public static String getLanguageName(String code) {
        List<LanguageModel> languageList = new ArrayList<>();

        languageList.add(new LanguageModel("English", "English", "en", true));
        languageList.add(new LanguageModel("Hindi", "हिन्दी", "hi"));
        languageList.add(new LanguageModel("French", "Français", "fr"));
        languageList.add(new LanguageModel("Spanish", "Española", "es"));
        languageList.add(new LanguageModel("Arabic", "عربي", "ar"));
        languageList.add(new LanguageModel("Marathi", "मराठी", "mr"));
        languageList.add(new LanguageModel("Kannada", "ಕನ್ನಡ", "kn"));
        languageList.add(new LanguageModel("Tamil", "தமிழ்", "ta"));
        languageList.add(new LanguageModel("Telugu", "తెలుగు", "te"));
        languageList.add(new LanguageModel("German", "Deutsch", "de"));
        languageList.add(new LanguageModel("Italian", "Italiano", "it"));
        languageList.add(new LanguageModel("Portuguese", "Português", "pt"));
        languageList.add(new LanguageModel("Dutch", "Nederlands", "nl"));
        languageList.add(new LanguageModel("Gujarati", "ગુજરાતી", "gu"));

        for (LanguageModel model : languageList) {
            if (model.getCode().equalsIgnoreCase(code)) {
                return model.getLocalizedName();
            }
        }

        return "English"; // or return default
    }



}