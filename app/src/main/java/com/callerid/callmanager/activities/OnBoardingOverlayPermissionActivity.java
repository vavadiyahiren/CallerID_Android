package com.callerid.callmanager.activities;

import static com.callerid.callmanager.utilities.Constant.PERMISSION_INFO_SHOW;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.text.HtmlCompat;
import androidx.viewpager.widget.ViewPager;

import com.callerid.callmanager.R;
import com.callerid.callmanager.adapters.OverlayPermissionAdapter;
import com.callerid.callmanager.models.OnboardingOverlayItem;
import com.callerid.callmanager.utilities.AppPref;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.ArrayList;
import java.util.List;

public class OnBoardingOverlayPermissionActivity extends AppCompatActivity {

    private static final int OVERLAY_PERMISSION_REQUEST_CODE = 1002;
    ViewPager viewPager;
    DotsIndicator indicator;
    AppCompatTextView txtTitle, txtAllowPermission, txtDesc, txtSkip;
    List<OnboardingOverlayItem> pages = new ArrayList<>();
    ActivityResultLauncher<Intent> permissionResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {

                if (Settings.canDrawOverlays(this)) {
                    AppPref.setBooleanPref(getApplicationContext(), PERMISSION_INFO_SHOW, true);
                    startActivity(new Intent(getApplicationContext(), LanguageActivity.class));
                    finish();
                } else {
                    viewPager.setCurrentItem(2);
                }

            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding_overlay_permission);

        viewPager = findViewById(R.id.onboardingViewPager);
        indicator = findViewById(R.id.dotIndicator);
        txtTitle = findViewById(R.id.txtTitle);
        txtDesc = findViewById(R.id.txtDesc);
        txtSkip = findViewById(R.id.txtSkip);
        txtAllowPermission = findViewById(R.id.txtAllowPermission);

        pages.add(new OnboardingOverlayItem(
                getString(R.string.almost_done),
                getString(R.string.set_up_live_caller_id_by_enabling_draw_over_other_apps_in_your_system_settings),
                R.drawable.ic_overlay_1,
                getString(R.string.why)
        ));
        pages.add(new OnboardingOverlayItem(
                getString(R.string.why_do_we_need_this_permission),
                getString(R.string.overlay_permission_text),
                R.drawable.ic_overlay_2,
                getString(R.string.privacy_policy)
        ));
        pages.add(new OnboardingOverlayItem(
                getString(R.string.one_last_step),
                getString(R.string.almost_there_allow_this_final_permission_to_enable_caller_id_and_spam_detection_),
                R.drawable.ic_overlay_3,
                getString(R.string.skip)
        ));


        txtAllowPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkOverlayPermission();
            }
        });

        OverlayPermissionAdapter adapter = new OverlayPermissionAdapter(this, pages);
        viewPager.setAdapter(adapter);
        indicator.setViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                txtTitle.setText(pages.get(position).title);
                txtDesc.setText(HtmlCompat.fromHtml(pages.get(position).description, HtmlCompat.FROM_HTML_MODE_LEGACY));
                txtSkip.setText(pages.get(position).btnText);

                if (position == 2) {
                    txtAllowPermission.setText(R.string.go_back_to_settings);
                } else {
                    txtAllowPermission.setText(R.string.enable_overlay);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        txtSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = viewPager.getCurrentItem();
                if (position == 0) {
                    viewPager.setCurrentItem(1);
                } else if (position == 1) {

                    String url = getString(R.string.privacy_policy_url);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);

                } else if (position == 2) {
                    AppPref.setBooleanPref(getApplicationContext(), PERMISSION_INFO_SHOW, true);
                    startActivity(new Intent(getApplicationContext(), LanguageActivity.class));
                    finish();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == OVERLAY_PERMISSION_REQUEST_CODE) {
            if (Settings.canDrawOverlays(this)) {
                AppPref.setBooleanPref(getApplicationContext(), PERMISSION_INFO_SHOW, true);
                startActivity(new Intent(getApplicationContext(), LanguageActivity.class));
                finish();
            }
        }
    }

    private void checkOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            permissionResultLauncher.launch(intent);
            //startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE);
        } else {
            AppPref.setBooleanPref(getApplicationContext(), PERMISSION_INFO_SHOW, true);
            startActivity(new Intent(getApplicationContext(), LanguageActivity.class));
            finish();
        }
    }

}