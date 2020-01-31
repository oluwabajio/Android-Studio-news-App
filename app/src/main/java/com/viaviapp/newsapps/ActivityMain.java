package com.viaviapp.newsapps;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.fragment.CategoryFragment;
import com.example.fragment.FavoriteFragment;
import com.example.fragment.HomeFragment;
import com.example.fragment.LatestFragment;
import com.example.fragment.MostViewFragment;
import com.example.fragment.SettingFragment;
import com.example.item.ItemAbout;
import com.example.util.Constant;
import com.example.util.JsonUtils;
import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ActivityMain extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    private FragmentManager fragmentManager;
    ArrayList<ItemAbout> mListItem;
    JsonUtils jsonUtils;
    final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 102;
    String[] PERMISSIONS = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
    LinearLayout adLayout;
    private ConsentForm form;
    MyApplication MyApp;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.post(new Runnable() {
            @Override
            public void run() {
                Drawable d = ResourcesCompat.getDrawable(getResources(), R.drawable.d_slidemenu, null);
                toolbar.setNavigationIcon(d);
            }
        });
        MyApp = MyApplication.getAppInstance();
        jsonUtils = new JsonUtils(this);
        jsonUtils.forceRTLIfSupported(getWindow());

        mDrawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        View hView = navigationView.inflateHeaderView(R.layout.nav_header);
        adLayout = findViewById(R.id.adLayout);
        fragmentManager = getSupportFragmentManager();

        mListItem = new ArrayList<>();

        if (JsonUtils.isNetworkAvailable(ActivityMain.this)) {
            new MyTaskDev().execute(Constant.ABOUT_US_URL);
        } else {
            showToast(getString(R.string.network_msg));
        }

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        mDrawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }
        HomeFragment homeFragment = new HomeFragment();
        loadFrag(homeFragment, getString(R.string.menu_home), fragmentManager);

    }

    private void setupDrawerContent(final NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        int id = menuItem.getItemId();
                        switch (id) {
                            case R.id.nav_home:
                                HomeFragment homeFragment = new HomeFragment();
                                loadFrag(homeFragment, getString(R.string.menu_home), fragmentManager);
                                mDrawerLayout.closeDrawers();
                                break;
                            case R.id.nav_latest:
                                LatestFragment latestFragment = new LatestFragment();
                                loadFrag(latestFragment, getString(R.string.menu_latest), fragmentManager);
                                mDrawerLayout.closeDrawers();
                                break;
                            case R.id.nav_cat:
                                CategoryFragment categoryFragment = new CategoryFragment();
                                loadFrag(categoryFragment, getString(R.string.menu_category), fragmentManager);
                                mDrawerLayout.closeDrawers();
                                break;
                            case R.id.nav_most:
                                MostViewFragment mostViewFragment = new MostViewFragment();
                                loadFrag(mostViewFragment, getString(R.string.menu_most), fragmentManager);
                                mDrawerLayout.closeDrawers();
                                break;

                            case R.id.nav_fav:
                                FavoriteFragment favoriteFragment = new FavoriteFragment();
                                loadFrag(favoriteFragment, getString(R.string.menu_favorite), fragmentManager);
                                mDrawerLayout.closeDrawers();
                                break;
                            case R.id.nav_profile:
                                Intent intent_profile = new Intent(ActivityMain.this, ProfileActivity.class);
                                startActivity(intent_profile);
                                mDrawerLayout.closeDrawers();
                                break;
                            case R.id.nav_setting:
                                SettingFragment settingFragment = new SettingFragment();
                                loadFrag(settingFragment, getString(R.string.menu_setting), fragmentManager);
                                mDrawerLayout.closeDrawers();
                                break;

                            case R.id.nav_share:
                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message) + "\n" + "https://play.google.com/store/apps/details?id=" + getPackageName());
                                sendIntent.setType("text/plain");
                                startActivity(sendIntent);
                                mDrawerLayout.closeDrawers();
                                break;
                            case R.id.nav_login:
                                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                                return true;
                            case R.id.nav_logout:
                                Logout();
                                return true;
                        }
                        return true;
                    }
                });

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }

        if (MyApp.getIsLogin()) {
            navigationView.getMenu().findItem(R.id.nav_login).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_logout).setVisible(true);
        } else {
            navigationView.getMenu().findItem(R.id.nav_login).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_logout).setVisible(false);
        }

        if (!MyApp.getIsLogin()) {
            navigationView.getMenu().findItem(R.id.nav_profile).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_logout).setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("StaticFieldLeak")
    private class MyTaskDev extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.getJSONString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (null == result || result.length() == 0) {
                showToast(getString(R.string.no_data_found));
            } else {

                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.LATEST_ARRAY_NAME);
                    JSONObject objJson;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);

                        ItemAbout itemAbout = new ItemAbout();

                        itemAbout.setappDevelop(objJson.getString(Constant.APP_DEVELOP));
                        itemAbout.setappBannerId(objJson.getString(Constant.ADS_BANNER_ID));
                        itemAbout.setappFullId(objJson.getString(Constant.ADS_FULL_ID));
                        itemAbout.setappBannerOn(objJson.getString(Constant.ADS_BANNER_ON_OFF));
                        itemAbout.setappFullOn(objJson.getString(Constant.ADS_FULL_ON_OFF));
                        itemAbout.setappFullPub(objJson.getString(Constant.ADS_PUB_ID));
                        itemAbout.setappFullAdsClick(objJson.getString(Constant.ADS_CLICK));
                        mListItem.add(itemAbout);


                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setResult();
            }
        }
    }

    private void setResult() {

        ItemAbout itemAbout = mListItem.get(0);

        Constant.SAVE_ADS_BANNER_ID = itemAbout.getappBannerId();
        Constant.SAVE_ADS_FULL_ID = itemAbout.getappFullId();
        Constant.SAVE_ADS_BANNER_ON_OFF = itemAbout.getappBannerOn();
        Constant.SAVE_ADS_FULL_ON_OFF = itemAbout.getappFullOn();
        Constant.SAVE_ADS_PUB_ID = itemAbout.getappFullPub();
        Constant.SAVE_ADS_CLICK = itemAbout.getappFullAdsClick();

        checkForConsent();
    }

    public void showToast(String msg) {
        Toast.makeText(ActivityMain.this, msg, Toast.LENGTH_LONG).show();
    }

    public void highLightNavigation(int position) {
        navigationView.getMenu().getItem(position).setChecked(true);

    }

    public void loadFrag(Fragment f1, String name, FragmentManager fm) {
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
        FragmentTransaction ft = fm.beginTransaction();
        //  ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.fragment1, f1, name);
        ft.commit();
        setToolbarTitle(name);
    }

    public void setToolbarTitle(String Title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(Title);
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else if (fragmentManager.getBackStackEntryCount() != 0) {
            String tag = fragmentManager.getFragments().get(fragmentManager.getBackStackEntryCount() - 1).getTag();
            setToolbarTitle(tag);
            super.onBackPressed();
        } else {
            AlertDialog.Builder alert = new AlertDialog.Builder(ActivityMain.this);
            alert.setTitle(getString(R.string.app_name));
            alert.setIcon(R.mipmap.app_icon);
            alert.setMessage(getString(R.string.exit_msg));
            alert.setPositiveButton(getString(R.string.exit_yes),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            finish();
                        }
                    });
            alert.setNegativeButton(getString(R.string.exit_no), new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                }
            });
            alert.show();
        }

    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        boolean canUseExternalStorage = false;

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    canUseExternalStorage = true;
                }
                if (!canUseExternalStorage) {
                    Toast.makeText(ActivityMain.this, getString(R.string.permission_request), Toast.LENGTH_SHORT).show();
                } else {
                    // user now provided permission
                    // perform function for what you want to achieve
                    Log.i("Permission", "granted");
                }
            }
        }
    }

    public void checkForConsent() {

        // ConsentInformation.getInstance(ActivityMain.this).addTestDevice("65C855CE481F45A609DAC8C6E8951D53");
        // Geography appears as in EEA for test devices.
        //  ConsentInformation.getInstance(ActivityMain.this).setDebugGeography(DebugGeography.DEBUG_GEOGRAPHY_EEA);
        // Geography appears as not in EEA for debug devices.
        ConsentInformation consentInformation = ConsentInformation.getInstance(ActivityMain.this);
        String[] publisherIds = {Constant.SAVE_ADS_PUB_ID};
        consentInformation.requestConsentInfoUpdate(publisherIds, new ConsentInfoUpdateListener() {
            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus) {
                Log.d("consentStatus", consentStatus.toString());
                // User's consent status successfully updated.
                switch (consentStatus) {
                    case PERSONALIZED:
                        JsonUtils.personalization_ad = true;
                        JsonUtils.showPersonalizedAds(adLayout, ActivityMain.this);
                        break;
                    case NON_PERSONALIZED:
                        JsonUtils.personalization_ad = false;
                        JsonUtils.showNonPersonalizedAds(adLayout, ActivityMain.this);
                        break;
                    case UNKNOWN:
                        if (ConsentInformation.getInstance(getBaseContext())
                                .isRequestLocationInEeaOrUnknown()) {
                            requestConsent();
                        } else {
                            JsonUtils.personalization_ad = true;
                            JsonUtils.showPersonalizedAds(adLayout, ActivityMain.this);
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFailedToUpdateConsentInfo(String errorDescription) {
                // User's consent status failed to update.
            }
        });

    }


    public void requestConsent() {
        URL privacyUrl = null;
        try {
            // TODO: Replace with your app's privacy policy URL.
            privacyUrl = new URL(getString(R.string.gdpr_privacy_link));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            // Handle error.
        }
        form = new ConsentForm.Builder(ActivityMain.this, privacyUrl)
                .withListener(new ConsentFormListener() {
                    @Override
                    public void onConsentFormLoaded() {
                        showForm();
                        // Consent form loaded successfully.
                    }

                    @Override
                    public void onConsentFormOpened() {
                        // Consent form was displayed.
                    }

                    @Override
                    public void onConsentFormClosed(ConsentStatus consentStatus, Boolean userPrefersAdFree) {
                        Log.d("consentStatus_form", consentStatus.toString());
                        switch (consentStatus) {
                            case PERSONALIZED:
                                JsonUtils.personalization_ad = true;
                                JsonUtils.showPersonalizedAds(adLayout, ActivityMain.this);
                                break;
                            case NON_PERSONALIZED:
                                JsonUtils.personalization_ad = false;
                                JsonUtils.showNonPersonalizedAds(adLayout, ActivityMain.this);
                                break;
                            case UNKNOWN:
                                JsonUtils.personalization_ad = false;
                                JsonUtils.showNonPersonalizedAds(adLayout, ActivityMain.this);
                        }
                    }

                    @Override
                    public void onConsentFormError(String errorDescription) {
                        Log.d("errorDescription", errorDescription);
                    }
                })
                .withPersonalizedAdsOption()
                .withNonPersonalizedAdsOption()
                .build();
        form.load();
    }

    private void showForm() {
        if (form != null) {
            form.show();
        }
    }

    private void Logout() {
        new AlertDialog.Builder(ActivityMain.this)
                .setTitle(getString(R.string.menu_logout))
                .setMessage(getString(R.string.logout_msg))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        MyApp.saveIsLogin(false);
                        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                //  .setIcon(R.drawable.ic_logout)
                .show();
    }
}