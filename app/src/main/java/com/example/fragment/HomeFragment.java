package com.example.fragment;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adapter.HomeCatAdapter;
import com.example.adapter.HomeLatestAdapter;
import com.example.adapter.HomeTopAdapter;
import com.example.favorite.DatabaseHelper;
import com.example.item.ItemCategory;
import com.example.item.ItemLatest;
import com.example.util.Constant;
import com.example.util.EnchantedViewPager;
import com.example.util.JsonUtils;
import com.example.util.RecyclerTouchListener;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.squareup.picasso.Picasso;
import com.viaviapp.newsapps.ActivityMain;
import com.viaviapp.newsapps.NewsDetailActivity;
import com.viaviapp.newsapps.R;
import com.viaviapp.newsapps.SearchActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;


public class HomeFragment extends Fragment {

    ScrollView mScrollView;
    ProgressBar mProgressBar;
    ArrayList<ItemLatest> mSliderList;
    RecyclerView mCatView, mLatestView, mTopView;
    HomeTopAdapter homeTopAdapter;
    ArrayList<ItemLatest> mLatestList, mTopList;
    ArrayList<ItemCategory> mCatList;
    Button btnCat, btnLatest, btnTop;
    EnchantedViewPager mViewPager;
    CustomViewPagerAdapter mAdapter;
    HomeLatestAdapter homeLatestAdapter;
    HomeCatAdapter homeCatAdapter;
    CircleIndicator circleIndicator;
    int currentCount = 0;
    View home_view_1, home_view_2;
    DatabaseHelper databaseHelper;
    private InterstitialAd mInterstitial;
    private int AD_COUNT = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        databaseHelper = new DatabaseHelper(getActivity());
        mSliderList = new ArrayList<>();
        mLatestList = new ArrayList<>();
        mCatList = new ArrayList<>();
        mAdapter = new CustomViewPagerAdapter();
        mTopList = new ArrayList<>();

        mScrollView = rootView.findViewById(R.id.scrollView);
        mProgressBar = rootView.findViewById(R.id.progressBar);
        mCatView = rootView.findViewById(R.id.rv_latest_cat);
        btnCat = rootView.findViewById(R.id.btn_latest_cat);
        mViewPager = rootView.findViewById(R.id.viewPager);
        btnTop = rootView.findViewById(R.id.btn_latest_top);
        mTopView = rootView.findViewById(R.id.rv_latest_top);
        home_view_1 = rootView.findViewById(R.id.home_view_1);
        home_view_2 = rootView.findViewById(R.id.home_view_2);
        btnLatest = rootView.findViewById(R.id.btn_home_latest);
        mLatestView = rootView.findViewById(R.id.rv_latest_home);

        circleIndicator = rootView.findViewById(R.id.indicator_unselected_background);
        mViewPager.useScale();
        mViewPager.removeAlpha();

        mCatView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mCatView.setLayoutManager(layoutManager);
        mCatView.setFocusable(false);

        mTopView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager_cat = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mTopView.setLayoutManager(layoutManager_cat);
        mTopView.setFocusable(false);

        mLatestView.setHasFixedSize(true);
        mLatestView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        mLatestView.setFocusable(false);
        mLatestView.setNestedScrollingEnabled(false);

        if (JsonUtils.isNetworkAvailable(requireActivity())) {
            new Home().execute(Constant.HOME_URL);
        }

        if (getResources().getString(R.string.isRTL).equals("true")) {
            home_view_1.setBackgroundResource(R.drawable.bg_gradient_home_shadow_white_right);
            home_view_2.setBackgroundResource(R.drawable.bg_gradient_home_shadow_white_right);
        }else {
            home_view_1.setBackgroundResource(R.drawable.bg_gradient_home_shadow_white_left);
            home_view_2.setBackgroundResource(R.drawable.bg_gradient_home_shadow_white_left);
         }

        btnLatest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ((ActivityMain) requireActivity()).highLightNavigation(1);
                String categoryName = getString(R.string.menu_latest);
                FragmentManager fm = getFragmentManager();
                LatestFragment f1 = new LatestFragment();
                assert fm != null;
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.fragment1, f1, categoryName);
                ft.commit();
                ((ActivityMain) requireActivity()).setToolbarTitle(categoryName);
            }
        });

        btnTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ActivityMain) requireActivity()).highLightNavigation(2);
                String categoryName = getString(R.string.menu_most);
                FragmentManager fm = getFragmentManager();
                MostViewFragment f1 = new MostViewFragment();
                assert fm != null;
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.fragment1, f1, categoryName);
                ft.commit();
                ((ActivityMain) requireActivity()).setToolbarTitle(categoryName);
            }
        });

        btnCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ActivityMain) requireActivity()).highLightNavigation(3);
                String categoryName = getString(R.string.home_category);
                FragmentManager fm = getFragmentManager();
                CategoryFragment f1 = new CategoryFragment();
                assert fm != null;
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.fragment1, f1, categoryName);
                ft.commit();
                ((ActivityMain) requireActivity()).setToolbarTitle(categoryName);
            }
        });

        mCatView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), mCatView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                if (Constant.SAVE_ADS_FULL_ON_OFF.equals("true")) {
                    AD_COUNT++;
                    if (AD_COUNT == Integer.parseInt(Constant.SAVE_ADS_CLICK)) {
                        AD_COUNT = 0;
                        mInterstitial = new InterstitialAd(requireActivity());
                        mInterstitial.setAdUnitId(Constant.SAVE_ADS_FULL_ID);
                        AdRequest adRequest;
                        if (JsonUtils.personalization_ad) {
                            adRequest = new AdRequest.Builder()
                                    .build();
                        } else {
                            Bundle extras = new Bundle();
                            extras.putString("npa", "1");
                            adRequest = new AdRequest.Builder()
                                    .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                                    .build();
                        }
                        mInterstitial.loadAd(adRequest);
                        mInterstitial.setAdListener(new AdListener() {
                            @Override
                            public void onAdLoaded() {
                                // TODO Auto-generated method stub
                                super.onAdLoaded();
                                if (mInterstitial.isLoaded()) {
                                    mInterstitial.show();
                                }
                            }

                            public void onAdClosed() {
                                String categoryName = mCatList.get(position).getCategoryName();
                                Bundle bundle = new Bundle();
                                bundle.putString("name", categoryName);
                                bundle.putString("Id", mCatList.get(position).getCategoryId());

                                FragmentManager fm = getFragmentManager();
                                CategoryListFragment categoryItemFragment = new CategoryListFragment();
                                categoryItemFragment.setArguments(bundle);
                                assert fm != null;
                                FragmentTransaction ft = fm.beginTransaction();
                                ft.hide(HomeFragment.this);
                                ft.add(R.id.fragment1, categoryItemFragment, categoryName);
                                ft.addToBackStack(categoryName);
                                ft.commit();
                                ((ActivityMain) requireActivity()).setToolbarTitle(categoryName);

                            }

                            @Override
                            public void onAdFailedToLoad(int errorCode) {
                                String categoryName = mCatList.get(position).getCategoryName();
                                Bundle bundle = new Bundle();
                                bundle.putString("name", categoryName);
                                bundle.putString("Id", mCatList.get(position).getCategoryId());

                                FragmentManager fm = getFragmentManager();
                                CategoryListFragment categoryItemFragment = new CategoryListFragment();
                                categoryItemFragment.setArguments(bundle);
                                assert fm != null;
                                FragmentTransaction ft = fm.beginTransaction();
                                ft.hide(HomeFragment.this);
                                ft.add(R.id.fragment1, categoryItemFragment, categoryName);
                                ft.addToBackStack(categoryName);
                                ft.commit();
                                ((ActivityMain) requireActivity()).setToolbarTitle(categoryName);
                            }
                        });
                    } else {
                        String categoryName = mCatList.get(position).getCategoryName();
                        Bundle bundle = new Bundle();
                        bundle.putString("name", categoryName);
                        bundle.putString("Id", mCatList.get(position).getCategoryId());

                        FragmentManager fm = getFragmentManager();
                        CategoryListFragment categoryItemFragment = new CategoryListFragment();
                        categoryItemFragment.setArguments(bundle);
                        assert fm != null;
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.hide(HomeFragment.this);
                        ft.add(R.id.fragment1, categoryItemFragment, categoryName);
                        ft.addToBackStack(categoryName);
                        ft.commit();
                        ((ActivityMain) requireActivity()).setToolbarTitle(categoryName);
                    }
                } else {
                    String categoryName = mCatList.get(position).getCategoryName();
                    Bundle bundle = new Bundle();
                    bundle.putString("name", categoryName);
                    bundle.putString("Id", mCatList.get(position).getCategoryId());

                    FragmentManager fm = getFragmentManager();
                    CategoryListFragment categoryItemFragment = new CategoryListFragment();
                    categoryItemFragment.setArguments(bundle);
                    assert fm != null;
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.hide(HomeFragment.this);
                    ft.add(R.id.fragment1, categoryItemFragment, categoryName);
                    ft.addToBackStack(categoryName);
                    ft.commit();
                    ((ActivityMain) requireActivity()).setToolbarTitle(categoryName);
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        setHasOptionsMenu(true);
        return rootView;
    }

    @SuppressLint("StaticFieldLeak")
    private class Home extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
            mScrollView.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.getJSONString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mProgressBar.setVisibility(View.GONE);
            mScrollView.setVisibility(View.VISIBLE);
            if (null == result || result.length() == 0) {
                showToast(getString(R.string.no_data));
            } else {

                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONObject jsonArray = mainJson.getJSONObject(Constant.LATEST_ARRAY_NAME);

                    JSONArray jsonSlider = jsonArray.getJSONArray(Constant.HOME_FEATURED_ARRAY);
                    JSONObject objJsonSlider;
                    for (int i = 0; i < jsonSlider.length(); i++) {
                        objJsonSlider = jsonSlider.getJSONObject(i);
                        ItemLatest objItem = new ItemLatest();
                        objItem.setNewsId(objJsonSlider.getString(Constant.LATEST_ID));
                        objItem.setNewsTitle(objJsonSlider.getString(Constant.LATEST_HEADING));
                        objItem.setNewsImage(objJsonSlider.getString(Constant.LATEST_IMAGE));
                        objItem.setNewsDesc(objJsonSlider.getString(Constant.LATEST_DESC));
                        objItem.setNewsDate(objJsonSlider.getString(Constant.LATEST_DATE));
                        objItem.setNewsView(objJsonSlider.getString(Constant.LATEST_VIEW));
                        objItem.setNewsType(objJsonSlider.getString(Constant.LATEST_TYPE));
                        objItem.setNewsVideoId(objJsonSlider.getString(Constant.LATEST_VIDEO_PLAY_ID));
                        mSliderList.add(objItem);
                    }

                    JSONArray jsonPopular = jsonArray.getJSONArray(Constant.HOME_TOP_ARRAY);
                    JSONObject objJson;
                    for (int i = 0; i < jsonPopular.length(); i++) {
                        objJson = jsonPopular.getJSONObject(i);
                        ItemLatest objItem = new ItemLatest();
                        objItem.setNewsId(objJson.getString(Constant.LATEST_ID));
                        objItem.setNewsTitle(objJson.getString(Constant.LATEST_HEADING));
                        objItem.setNewsImage(objJson.getString(Constant.LATEST_IMAGE));
                        objItem.setNewsDesc(objJson.getString(Constant.LATEST_DESC));
                        objItem.setNewsDate(objJson.getString(Constant.LATEST_DATE));
                        objItem.setNewsView(objJson.getString(Constant.LATEST_VIEW));
                        objItem.setNewsType(objJson.getString(Constant.LATEST_TYPE));
                        objItem.setNewsVideoId(objJson.getString(Constant.LATEST_VIDEO_PLAY_ID));
                        mTopList.add(objItem);
                    }

                    JSONArray jsonPopularLa = jsonArray.getJSONArray(Constant.HOME_LATEST_ARRAY);
                    JSONObject objJsonLa;
                    for (int i = 0; i < jsonPopularLa.length(); i++) {
                        objJsonLa = jsonPopularLa.getJSONObject(i);
                        ItemLatest objItem = new ItemLatest();
                        objItem.setNewsId(objJsonLa.getString(Constant.LATEST_ID));
                        objItem.setNewsTitle(objJsonLa.getString(Constant.LATEST_HEADING));
                        objItem.setNewsImage(objJsonLa.getString(Constant.LATEST_IMAGE));
                        objItem.setNewsDesc(objJsonLa.getString(Constant.LATEST_DESC));
                        objItem.setNewsDate(objJsonLa.getString(Constant.LATEST_DATE));
                        objItem.setNewsView(objJsonLa.getString(Constant.LATEST_VIEW));
                        objItem.setNewsType(objJsonLa.getString(Constant.LATEST_TYPE));
                        objItem.setNewsVideoId(objJsonLa.getString(Constant.LATEST_VIDEO_PLAY_ID));
                        mLatestList.add(objItem);
                    }


                    JSONArray jsonLatest = jsonArray.getJSONArray(Constant.HOME_CAT_ARRAY);
                    JSONObject objJsonCat;
                    for (int i = 0; i < jsonLatest.length(); i++) {
                        objJsonCat = jsonLatest.getJSONObject(i);
                        ItemCategory objItem = new ItemCategory();
                        objItem.setCategoryId(objJsonCat.getString(Constant.CATEGORY_CID));
                        objItem.setCategoryName(objJsonCat.getString(Constant.CATEGORY_NAME));
                        objItem.setCategoryImageBig(objJsonCat.getString(Constant.CATEGORY_IMAGE));
                        objItem.setCategoryImageSmall(objJsonCat.getString(Constant.CATEGORY_IMAGE_THUMB));
                        mCatList.add(objItem);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setResult();
            }
        }
    }

    private void setResult() {
        if (getActivity() != null) {
            homeTopAdapter = new HomeTopAdapter(getActivity(), mTopList);
            mTopView.setAdapter(homeTopAdapter);

            homeCatAdapter = new HomeCatAdapter(getActivity(), mCatList);
            mCatView.setAdapter(homeCatAdapter);

            homeLatestAdapter = new HomeLatestAdapter(getActivity(), mLatestList);
            mLatestView.setAdapter(homeLatestAdapter);

            if (!mSliderList.isEmpty()) {
                mViewPager.setAdapter(mAdapter);
                circleIndicator.setViewPager(mViewPager);
                autoPlay(mViewPager);
            }

        }
    }

    private class CustomViewPagerAdapter extends PagerAdapter {
        private LayoutInflater inflater;

        private CustomViewPagerAdapter() {
            // TODO Auto-generated constructor stub
            inflater = requireActivity().getLayoutInflater();
        }

        @Override
        public int getCount() {
            return mSliderList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View imageLayout = inflater.inflate(R.layout.row_slider_item, container, false);
            assert imageLayout != null;
            ImageView image = imageLayout.findViewById(R.id.image);
            final ImageView image_fav = imageLayout.findViewById(R.id.image_fav);
            TextView text_title = imageLayout.findViewById(R.id.txt_title);
            TextView text_desc = imageLayout.findViewById(R.id.txt_desc);
            TextView text_date = imageLayout.findViewById(R.id.txt_date);
            TextView text_view = imageLayout.findViewById(R.id.txt_view);
            LinearLayout lytParent = imageLayout.findViewById(R.id.rootLayout);
            LinearLayout lytShare = imageLayout.findViewById(R.id.lay_share);
            ImageView image_play = imageLayout.findViewById(R.id.image_play);

            if (mSliderList.get(position).getNewsType().equals("video")) {
                if (mSliderList.get(position).getNewsImage().isEmpty()) {
                    Picasso.get().load(Constant.YOUTUBE_IMAGE_FRONT + mSliderList.get(position).getNewsVideoId() + Constant.YOUTUBE_SMALL_IMAGE_BACK).placeholder(R.drawable.place_holder_big).into(image);
                }
                image_play.setVisibility(View.VISIBLE);
            } else if (mSliderList.get(position).getNewsType().equals("image")) {
                Picasso.get().load(mSliderList.get(position).getNewsImage()).placeholder(R.drawable.place_holder_big).into(image);
                image_play.setVisibility(View.GONE);
            }
            text_title.setText(mSliderList.get(position).getNewsTitle());
            text_desc.setText(Html.fromHtml(mSliderList.get(position).getNewsDesc()));
            text_date.setText(mSliderList.get(position).getNewsDate());
            text_view.setText(JsonUtils.Format(Integer.parseInt(mSliderList.get(position).getNewsView())));

            imageLayout.setTag(EnchantedViewPager.ENCHANTED_VIEWPAGER_POSITION + position);
            lytParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Constant.SAVE_ADS_FULL_ON_OFF.equals("true")) {
                        AD_COUNT++;
                        if (AD_COUNT == Integer.parseInt(Constant.SAVE_ADS_CLICK)) {
                            AD_COUNT = 0;
                            mInterstitial = new InterstitialAd(requireActivity());
                            mInterstitial.setAdUnitId(Constant.SAVE_ADS_FULL_ID);
                            AdRequest adRequest;
                            if (JsonUtils.personalization_ad) {
                                adRequest = new AdRequest.Builder()
                                        .build();
                            } else {
                                Bundle extras = new Bundle();
                                extras.putString("npa", "1");
                                adRequest = new AdRequest.Builder()
                                        .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                                        .build();
                            }
                            mInterstitial.loadAd(adRequest);
                            mInterstitial.setAdListener(new AdListener() {
                                @Override
                                public void onAdLoaded() {
                                    // TODO Auto-generated method stub
                                    super.onAdLoaded();
                                    if (mInterstitial.isLoaded()) {
                                        mInterstitial.show();
                                    }
                                }

                                public void onAdClosed() {
                                    Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
                                    intent.putExtra("Id", mSliderList.get(position).getNewsId());
                                    startActivity(intent);

                                }

                                @Override
                                public void onAdFailedToLoad(int errorCode) {
                                    Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
                                    intent.putExtra("Id", mSliderList.get(position).getNewsId());
                                    startActivity(intent);
                                }
                            });
                        } else {
                            Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
                            intent.putExtra("Id", mSliderList.get(position).getNewsId());
                            startActivity(intent);
                        }
                    } else {
                        Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
                        intent.putExtra("Id", mSliderList.get(position).getNewsId());
                        startActivity(intent);
                    }
                }
            });

            lytShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            if (databaseHelper.getFavouriteById(mSliderList.get(position).getNewsId())) {
                image_fav.setImageResource(R.drawable.ic_fav_hover);
            } else {
                image_fav.setImageResource(R.drawable.ic_fav);
            }

            image_fav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ContentValues fav = new ContentValues();
                    if (databaseHelper.getFavouriteById(mSliderList.get(position).getNewsId())) {
                        databaseHelper.removeFavouriteById(mSliderList.get(position).getNewsId());
                        image_fav.setImageResource(R.drawable.ic_fav);
                        Toast.makeText(getActivity(), getString(R.string.favourite_remove), Toast.LENGTH_SHORT).show();
                    } else {
                        fav.put(DatabaseHelper.KEY_ID, mSliderList.get(position).getNewsDate());
                        fav.put(DatabaseHelper.KEY_TITLE, mSliderList.get(position).getNewsTitle());
                        fav.put(DatabaseHelper.KEY_IMAGE, mSliderList.get(position).getNewsImage());
                        fav.put(DatabaseHelper.KEY_DESC, mSliderList.get(position).getNewsDesc());
                        fav.put(DatabaseHelper.KEY_DATE, mSliderList.get(position).getNewsDate());
                        fav.put(DatabaseHelper.KEY_VIEW, mSliderList.get(position).getNewsView());
                        fav.put(DatabaseHelper.KEY_TYPE, mSliderList.get(position).getNewsType());
                        fav.put(DatabaseHelper.KEY_PLAY_ID, mSliderList.get(position).getNewsVideoId());
                        databaseHelper.addFavourite(DatabaseHelper.TABLE_FAVOURITE_NAME, fav, null);
                        image_fav.setImageResource(R.drawable.ic_fav_hover);
                        Toast.makeText(getActivity(), getString(R.string.favourite_add), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            container.addView(imageLayout, 0);
            return imageLayout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            (container).removeView((View) object);
        }
    }

    public void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
    }

    private void autoPlay(final ViewPager viewPager) {

        viewPager.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mAdapter != null && viewPager.getAdapter().getCount() > 0) {
                        int position = currentCount % mAdapter.getCount();
                        currentCount++;
                        viewPager.setCurrentItem(position);
                        autoPlay(viewPager);
                    }
                } catch (Exception e) {
                    Log.e("TAG", "auto scroll pager error.", e);
                }
            }
        }, 2500);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);

        final SearchView searchView = (SearchView) menu.findItem(R.id.search)
                .getActionView();

        final MenuItem searchMenuItem = menu.findItem(R.id.search);
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                if (!hasFocus) {
                    searchMenuItem.collapseActionView();
                    searchView.setQuery("", false);
                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String newText) {
                // TODO Auto-generated method stub


                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                intent.putExtra("search", query);
                startActivity(intent);
                searchView.clearFocus();
                return false;
            }
        });

    }
}

