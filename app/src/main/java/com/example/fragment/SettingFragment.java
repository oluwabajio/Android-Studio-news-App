package com.example.fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.onesignal.OneSignal;
import com.viaviapp.newsapps.AboutUsActivity;
import com.viaviapp.newsapps.MyApplication;
import com.viaviapp.newsapps.PrivacyActivity;
import com.viaviapp.newsapps.R;

public class SettingFragment extends Fragment {


    MyApplication MyApp;
    SwitchCompat notificationSwitch;
    LinearLayout lytAbout, lytPrivacy, lytMoreApp, layRateApp;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);

        MyApp = MyApplication.getAppInstance();
        notificationSwitch = rootView.findViewById(R.id.switch_notification);
        lytAbout = rootView.findViewById(R.id.lytAbout);
        lytPrivacy = rootView.findViewById(R.id.lytPrivacy);
        lytMoreApp = rootView.findViewById(R.id.lytMoreApp);
        layRateApp = rootView.findViewById(R.id.lytRateApp);

        notificationSwitch.setChecked(MyApp.getNotification());

        layRateApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("market://details?id=" + requireActivity().getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                // To count with Play market backstack, After pressing back button,
                // to taken back to our application, we need to add following flags to intent.
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + requireActivity().getPackageName())));
                }
            }
        });

        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MyApp.saveIsNotification(isChecked);
                OneSignal.setSubscription(isChecked);
            }
        });

        lytAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_ab = new Intent(requireActivity(), AboutUsActivity.class);
                startActivity(intent_ab);
            }
        });

        lytPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_pri = new Intent(requireActivity(), PrivacyActivity.class);
                startActivity(intent_pri);
            }
        });

        lytMoreApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.more_app_url))));
            }
        });
        return rootView;
    }

}
