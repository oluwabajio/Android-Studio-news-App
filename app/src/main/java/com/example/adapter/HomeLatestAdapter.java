package com.example.adapter;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.favorite.DatabaseHelper;
import com.example.item.ItemLatest;
import com.example.util.Constant;
import com.example.util.JsonUtils;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.squareup.picasso.Picasso;
import com.viaviapp.newsapps.NewsDetailActivity;
import com.viaviapp.newsapps.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class HomeLatestAdapter extends RecyclerView.Adapter<HomeLatestAdapter.ItemRowHolder> {

    private ArrayList<ItemLatest> dataList;
    private Context mContext;
    private DatabaseHelper databaseHelper;
    private String s_title, s_image, s_desc, s_type, s_play_id;
    private InterstitialAd mInterstitial;
    private int AD_COUNT = 0;

    public HomeLatestAdapter(Context context, ArrayList<ItemLatest> dataList) {
        this.dataList = dataList;
        this.mContext = context;
        databaseHelper = new DatabaseHelper(mContext);
    }

    @Override
    public ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_home_latest_item, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(final ItemRowHolder holder, final int position) {
        final ItemLatest singleItem = dataList.get(position);

        if (singleItem.getNewsType().equals("video")) {
            if (singleItem.getNewsImage().isEmpty()) {
                Picasso.get().load(Constant.YOUTUBE_IMAGE_FRONT + singleItem.getNewsVideoId() + Constant.YOUTUBE_SMALL_IMAGE_BACK).placeholder(R.drawable.place_holder_big).into(holder.image_news);
            }
            holder.image_play.setVisibility(View.VISIBLE);
        } else if (singleItem.getNewsType().equals("image")) {
            Picasso.get().load(singleItem.getNewsImage()).placeholder(R.drawable.place_holder_big).into(holder.image_news);
            holder.image_play.setVisibility(View.GONE);
        }
        holder.txt_date.setText(singleItem.getNewsDate());
        holder.txt_title.setText(singleItem.getNewsTitle());
        holder.txt_desc.setText(Html.fromHtml(singleItem.getNewsDesc()));
        holder.txt_view.setText(JsonUtils.Format(Integer.parseInt(singleItem.getNewsView())));

        holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constant.SAVE_ADS_FULL_ON_OFF.equals("true")) {
                    AD_COUNT++;
                    if (AD_COUNT == Integer.parseInt(Constant.SAVE_ADS_CLICK)) {
                        AD_COUNT = 0;
                        mInterstitial = new InterstitialAd(mContext);
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
                                Intent intent = new Intent(mContext, NewsDetailActivity.class);
                                intent.putExtra("Id", singleItem.getNewsId());
                                mContext.startActivity(intent);

                            }

                            @Override
                            public void onAdFailedToLoad(int errorCode) {
                                Intent intent = new Intent(mContext, NewsDetailActivity.class);
                                intent.putExtra("Id", singleItem.getNewsId());
                                mContext.startActivity(intent);
                            }
                        });
                    } else {
                        Intent intent = new Intent(mContext, NewsDetailActivity.class);
                        intent.putExtra("Id", singleItem.getNewsId());
                        mContext.startActivity(intent);
                    }
                } else {
                    Intent intent = new Intent(mContext, NewsDetailActivity.class);
                    intent.putExtra("Id", singleItem.getNewsId());
                    mContext.startActivity(intent);
                }
            }
        });

        holder.lyt_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                s_title = singleItem.getNewsTitle();
                s_desc = singleItem.getNewsDesc();
                s_image = singleItem.getNewsImage();
                s_play_id = singleItem.getNewsVideoId();
                s_type = singleItem.getNewsType();

                if (s_type.equals("video")) {
                    (new SaveTask(mContext)).execute(Constant.YOUTUBE_IMAGE_FRONT + s_play_id + Constant.YOUTUBE_SMALL_IMAGE_BACK);
                } else {
                    (new SaveTask(mContext)).execute(s_image);
                }
             }
        });

        if (databaseHelper.getFavouriteById(singleItem.getNewsId())) {
            holder.image_list_fav.setImageResource(R.drawable.ic_fav_hover);
        } else {
            holder.image_list_fav.setImageResource(R.drawable.ic_fav);
        }

        holder.image_list_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues fav = new ContentValues();
                if (databaseHelper.getFavouriteById(singleItem.getNewsId())) {
                    databaseHelper.removeFavouriteById(singleItem.getNewsId());
                    holder.image_list_fav.setImageResource(R.drawable.ic_fav);
                    Toast.makeText(mContext, mContext.getString(R.string.favourite_remove), Toast.LENGTH_SHORT).show();
                } else {
                    fav.put(DatabaseHelper.KEY_ID, singleItem.getNewsId());
                    fav.put(DatabaseHelper.KEY_TITLE, singleItem.getNewsTitle());
                    fav.put(DatabaseHelper.KEY_IMAGE, singleItem.getNewsImage());
                    fav.put(DatabaseHelper.KEY_DESC, singleItem.getNewsDesc());
                    fav.put(DatabaseHelper.KEY_DATE, singleItem.getNewsDate());
                    fav.put(DatabaseHelper.KEY_VIEW, singleItem.getNewsView());
                    fav.put(DatabaseHelper.KEY_TYPE, singleItem.getNewsType());
                    fav.put(DatabaseHelper.KEY_PLAY_ID, singleItem.getNewsVideoId());
                    databaseHelper.addFavourite(DatabaseHelper.TABLE_FAVOURITE_NAME, fav, null);
                    holder.image_list_fav.setImageResource(R.drawable.ic_fav_hover);
                    Toast.makeText(mContext, mContext.getString(R.string.favourite_add), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    public class ItemRowHolder extends RecyclerView.ViewHolder {
        private ImageView image_news, image_list_fav, image_play;
        private TextView txt_date, txt_title, txt_view, txt_desc;
        private LinearLayout lyt_share;
        private RelativeLayout lyt_parent;

        private ItemRowHolder(View itemView) {
            super(itemView);
            image_news = itemView.findViewById(R.id.image_news);
            lyt_parent = itemView.findViewById(R.id.rootLayout);
            image_list_fav = itemView.findViewById(R.id.image_fav);
            txt_date = itemView.findViewById(R.id.txt_date);
            txt_title = itemView.findViewById(R.id.txt_title);
            txt_view = itemView.findViewById(R.id.txt_view);
            txt_desc = itemView.findViewById(R.id.txt_desc);
            lyt_share = itemView.findViewById(R.id.lay_share);
            image_play = itemView.findViewById(R.id.image_play);

        }
    }

    @SuppressLint("StaticFieldLeak")
    public class SaveTask extends AsyncTask<String, String, String> {
        private Context context;
        URL myFileUrl;
        Bitmap bmImg = null;
        File file;

        private SaveTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub

            try {

                myFileUrl = new URL(args[0]);

                HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();
                bmImg = BitmapFactory.decodeStream(is);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {

                String path = myFileUrl.getPath();
                String idStr = path.substring(path.lastIndexOf('/') + 1);
                File filepath = Environment.getExternalStorageDirectory();
                File dir = new File(filepath.getAbsolutePath() + Constant.DOWNLOAD_FOLDER_PATH);
                dir.mkdirs();
                String fileName = idStr;
                file = new File(dir, fileName);
                FileOutputStream fos = new FileOutputStream(file);
                bmImg.compress(Bitmap.CompressFormat.JPEG, 75, fos);
                fos.flush();
                fos.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(String args) {
            // TODO Auto-generated method stub

            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("image/jpeg");
            if (s_type.equals("video")) {
                share.putExtra(Intent.EXTRA_TEXT, s_title + "\n" + Html.fromHtml(s_desc) + "\n" + mContext.getString(R.string.news_video) + "\n" + "https://www.youtube.com/watch?v=" + s_play_id + "\n" + mContext.getString(R.string.share_message) + "\n" + "https://play.google.com/store/apps/details?id=" + mContext.getPackageName());
            } else {
                share.putExtra(Intent.EXTRA_TEXT, s_title + "\n" + Html.fromHtml(s_desc) + "\n" + mContext.getString(R.string.share_message) + "\n" + "https://play.google.com/store/apps/details?id=" + mContext.getPackageName());
            }
            share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file.getAbsolutePath()));
            mContext.startActivity(Intent.createChooser(share, "Share Image"));
        }
    }

}
