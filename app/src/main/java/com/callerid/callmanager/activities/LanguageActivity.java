package com.callerid.callmanager.activities;

import static com.callerid.callmanager.utilities.LocaleHelper.getPersistedData;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.callerid.callmanager.R;
import com.callerid.callmanager.adapters.LanguageAdapter;
import com.callerid.callmanager.models.LanguageModel;
import com.callerid.callmanager.utilities.LocaleHelper;
import com.callerid.callmanager.utilities.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LanguageActivity extends AppCompatActivity {

    private static final String TAG = "LanguageActivity";
    AppCompatImageView imgBack;
    AppCompatTextView txtContinue;
    RecyclerView rvLanguage;

    Boolean IsFromSetting = false;
    String languageCode = "en";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        Utility.setStatusBar(this);

        IsFromSetting = getIntent().getBooleanExtra("IsFromSetting", false);

        imgBack = findViewById(R.id.imgBack);
        txtContinue = findViewById(R.id.txtContinue);
        rvLanguage = findViewById(R.id.rvLanguage);
        rvLanguage.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        txtContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LocaleHelper.setLocale(getApplicationContext(), languageCode);

                if (!IsFromSetting) {
                    startActivity(new Intent(getApplicationContext(), DashboardActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                    finish();
                }else {
                    setResult(RESULT_OK);
                    finish();
                }

            }
        });
        languageCode = getPersistedData(this, Locale.getDefault().getLanguage());
        getLanguageList();
    }

    private void getLanguageList() {

        List<LanguageModel> languageList = new ArrayList<>();

        languageList.add(new LanguageModel("English", "English", "en", languageCode.equals("en")));
        languageList.add(new LanguageModel("Hindi", "हिन्दी", "hi", languageCode.equals("hi")));
        languageList.add(new LanguageModel("French", "Français", "fr", languageCode.equals("fr")));
        languageList.add(new LanguageModel("Spanish", "Española", "es", languageCode.equals("es")));
        languageList.add(new LanguageModel("Arabic", "عربي", "ar", languageCode.equals("ar")));
        languageList.add(new LanguageModel("Marathi", "मराठी", "mr", languageCode.equals("mr")));
        languageList.add(new LanguageModel("Kannada", "ಕನ್ನಡ", "kn", languageCode.equals("kn")));
        languageList.add(new LanguageModel("Tamil", "தமிழ்", "ta", languageCode.equals("ta")));
        languageList.add(new LanguageModel("Telugu", "తెలుగు", "te", languageCode.equals("te")));
        languageList.add(new LanguageModel("German", "Deutsch", "de", languageCode.equals("de")));
        languageList.add(new LanguageModel("Italian", "Italiano", "it", languageCode.equals("it")));
        languageList.add(new LanguageModel("Portuguese", "Português", "pt", languageCode.equals("pt")));
        languageList.add(new LanguageModel("Dutch", "Nederlands", "nl", languageCode.equals("nl")));
        languageList.add(new LanguageModel("Gujarati", "ગુજરાતી", "gu", languageCode.equals("gu")));



      /*  languageList.add(new LanguageModel("Hebrew", "עברית", "he"));
        languageList.add(new LanguageModel("Chinese", "中国人", "zh"));
        languageList.add(new LanguageModel("Japanese", "日本", "ja"));
        languageList.add(new LanguageModel("Korean", "한국인", "ko"));
        languageList.add(new LanguageModel("Thai", "แบบไทย", "th"));
        languageList.add(new LanguageModel("Vietnamese", "Tiếng Việt", "vi"));
        languageList.add(new LanguageModel("Turkish", "Türkçe", "tr"));
        languageList.add(new LanguageModel("Malay", "Melayu", "ms"));
        languageList.add(new LanguageModel("Filipino", "Filipino", "fil"));
        languageList.add(new LanguageModel("Persian", "فارسی", "fa"));
        languageList.add(new LanguageModel("Khmer", "ខ្មែរ", "km"));
        languageList.add(new LanguageModel("Indonesian", "Bahasa Indonesia", "id"));
        languageList.add(new LanguageModel("Uzbek", "O'zbek", "uz"));


        // European languages
        languageList.add(new LanguageModel("Polish", "Polski", "pl"));
        languageList.add(new LanguageModel("Swedish", "Svenska", "sv"));
        languageList.add(new LanguageModel("Romanian", "Română", "ro"));
        languageList.add(new LanguageModel("Czech", "Čeština", "cs"));
        languageList.add(new LanguageModel("Hungarian", "Magyar", "hu"));
        languageList.add(new LanguageModel("Finnish", "Suomi", "fi"));
        languageList.add(new LanguageModel("Catalan", "Català", "ca"));
        languageList.add(new LanguageModel("Estonian", "Eesti keel", "et"));
        languageList.add(new LanguageModel("Russian", "Русский", "ru"));*/


        LanguageAdapter adapter = new LanguageAdapter(languageList, language -> {
            // Handle language selection
            Log.e("LanguageSelector", "Selected: " + language.getEnglishName() + " (" + language.getCode() + ")");
            languageCode = language.getCode();
        });

        rvLanguage.setAdapter(adapter);
    }
}