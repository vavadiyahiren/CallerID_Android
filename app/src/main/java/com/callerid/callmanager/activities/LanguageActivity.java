package com.callerid.callmanager.activities;

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

import java.util.ArrayList;
import java.util.List;

public class LanguageActivity extends AppCompatActivity {

    private static final String TAG = "LanguageActivity";

    AppCompatImageView imgBack;
    AppCompatTextView txtContinue;
    RecyclerView rvLanguage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

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
                startActivity(new Intent(getApplicationContext(), DashboardActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                finish();
            }
        });

        getLanguageList();
    }

    private void getLanguageList() {

        List<LanguageModel> languageList = new ArrayList<>();

        // US language(s) first
        languageList.add(new LanguageModel("English", "English", "en", true));

        // Asian and other languages
        languageList.add(new LanguageModel("Hindi", "हिन्दी", "hi"));
        languageList.add(new LanguageModel("Gujarati", "ગુજરાતી", "gu"));
        languageList.add(new LanguageModel("Marathi", "मराठी", "mr"));
        languageList.add(new LanguageModel("Tamil", "தமிழ்", "ta"));
        languageList.add(new LanguageModel("Telugu", "తెలుగు", "te"));
        languageList.add(new LanguageModel("Arabic", "عربي", "ar"));
        languageList.add(new LanguageModel("Hebrew", "עברית", "he"));
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
        languageList.add(new LanguageModel("French", "Français", "fr"));
        languageList.add(new LanguageModel("German", "Deutsch", "de"));
        languageList.add(new LanguageModel("Spanish", "Española", "es"));
        languageList.add(new LanguageModel("Italian", "Italiano", "it"));
        languageList.add(new LanguageModel("Portuguese", "Português", "pt"));
        languageList.add(new LanguageModel("Dutch", "Nederlands", "nl"));
        languageList.add(new LanguageModel("Polish", "Polski", "pl"));
        languageList.add(new LanguageModel("Swedish", "Svenska", "sv"));
        languageList.add(new LanguageModel("Romanian", "Română", "ro"));
        languageList.add(new LanguageModel("Czech", "Čeština", "cs"));
        languageList.add(new LanguageModel("Hungarian", "Magyar", "hu"));
        languageList.add(new LanguageModel("Finnish", "Suomi", "fi"));
        languageList.add(new LanguageModel("Catalan", "Català", "ca"));
        languageList.add(new LanguageModel("Estonian", "Eesti keel", "et"));
        languageList.add(new LanguageModel("Russian", "Русский", "ru"));


        LanguageAdapter adapter = new LanguageAdapter(languageList, language -> {
            // Handle language selection
            Log.e("LanguageSelector", "Selected: " + language.getEnglishName() + " (" + language.getCode() + ")");
        });

        rvLanguage.setAdapter(adapter);
    }
}