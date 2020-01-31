package com.viaviapp.newsapps;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adapter.CommentAdapter;
import com.example.adapter.RelatedAdapter;
import com.example.favorite.DatabaseHelper;
import com.example.item.CommentList;
import com.example.item.ItemGallery;
import com.example.item.ItemLatest;
import com.example.util.Constant;
import com.example.util.JsonUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class NewsDetailActivity extends AppCompatActivity {


    ProgressBar mProgressBar;
    ScrollView mScrollView;
    WebView webView;
    LinearLayout lyt_not_found;
    Menu menu;
    ImageView image_news, image_list_fav, image_play;
    TextView txt_date, txt_title, txt_view, no_rel, textViewNoCommentFound, text_no;
    ItemLatest objBean;
    ArrayList<ItemGallery> mGallery;
    ArrayList<ItemLatest> mListItemRelated;
    ArrayList<CommentList> mListItemComment;
    RelatedAdapter relatedAdapter;
    String Id;
    Button btn_latest_top, btn_comment;
    RecyclerView mTopView, recyclerViewComment;
    CommentAdapter commentAdapter;
    ImageView imageView_Send;
    EditText editText;
    DatabaseHelper databaseHelper;
    boolean iswhichscreen;
    MyApplication myApplication;
    ProgressDialog pDialog;
    String strMessage;
    LinearLayout adLayout;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_news_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        objBean = new ItemLatest();
        mListItemRelated = new ArrayList<>();
        mGallery = new ArrayList<>();
        mListItemComment = new ArrayList<>();
        databaseHelper = new DatabaseHelper(NewsDetailActivity.this);
        myApplication = MyApplication.getAppInstance();
        pDialog = new ProgressDialog(this);

        lyt_not_found = findViewById(R.id.lyt_not_found);
        mProgressBar = findViewById(R.id.progressBar);
        mScrollView = findViewById(R.id.scrollView);
        image_news = findViewById(R.id.image_news);
        image_list_fav = findViewById(R.id.image_fav);
        txt_date = findViewById(R.id.txt_date);
        txt_title = findViewById(R.id.txt_title);
        txt_view = findViewById(R.id.txt_view);
        image_play = findViewById(R.id.image_play);
        webView = findViewById(R.id.webView_news_details);
        btn_latest_top = findViewById(R.id.btn_latest_top);
        mTopView = findViewById(R.id.rv_latest_top);
        no_rel = findViewById(R.id.no_rel);
        btn_comment = findViewById(R.id.btn_latest_comment);
        imageView_Send = findViewById(R.id.imageView_dialogBox_comment);
        recyclerViewComment = findViewById(R.id.recyclerView_comment_scd);
        textViewNoCommentFound = findViewById(R.id.textView_noComment_scdetail);
        editText = findViewById(R.id.editText_dialogbox_comment);
        text_no = findViewById(R.id.text_no);
        adLayout = findViewById(R.id.adLayout);

        mTopView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager_cat = new LinearLayoutManager(NewsDetailActivity.this, LinearLayoutManager.HORIZONTAL, false);
        mTopView.setLayoutManager(layoutManager_cat);
        mTopView.setFocusable(false);

        recyclerViewComment.setHasFixedSize(true);
        recyclerViewComment.setLayoutManager(new GridLayoutManager(NewsDetailActivity.this, 1));
        recyclerViewComment.setFocusable(false);
        recyclerViewComment.setNestedScrollingEnabled(false);
        textViewNoCommentFound.setVisibility(View.GONE);

        Intent intent = getIntent();
        Id = intent.getStringExtra("Id");

        if (JsonUtils.isNetworkAvailable(NewsDetailActivity.this)) {
            new getDetail().execute(Constant.SINGLE_NEWS_URL + Id);
        }

        Intent intent2 = getIntent();
        iswhichscreen = intent2.getBooleanExtra("isNotification", false);
        if (!iswhichscreen) {
            if (JsonUtils.personalization_ad) {
                 JsonUtils.showPersonalizedAds(adLayout, NewsDetailActivity.this);
            } else {
                 JsonUtils.showNonPersonalizedAds(adLayout, NewsDetailActivity.this);
            }

        }

    }

    @SuppressLint("StaticFieldLeak")
    private class getDetail extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(true);
        }

        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.getJSONString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            showProgress(false);
            if (null == result || result.length() == 0) {
                lyt_not_found.setVisibility(View.VISIBLE);
            } else {
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.LATEST_ARRAY_NAME);
                    JSONObject objJson;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);
                        objBean.setNewsId(objJson.getString(Constant.LATEST_ID));
                        objBean.setNewsTitle(objJson.getString(Constant.LATEST_HEADING));
                        objBean.setNewsImage(objJson.getString(Constant.LATEST_IMAGE));
                        objBean.setNewsDesc(objJson.getString(Constant.LATEST_DESC));
                        objBean.setNewsDate(objJson.getString(Constant.LATEST_DATE));
                        objBean.setNewsView(objJson.getString(Constant.LATEST_VIEW));
                        objBean.setNewsType(objJson.getString(Constant.LATEST_TYPE));
                        objBean.setNewsVideoId(objJson.getString(Constant.LATEST_VIDEO_PLAY_ID));

                        JSONArray jsonArrayComment = objJson.getJSONArray(Constant.COMMENT_ARRAY_NAME);
                        if (jsonArrayComment.length() > 0 && !jsonArrayComment.get(0).equals("")) {
                            int k = jsonArrayComment.length() >= 3 ? 3 : jsonArrayComment.length();
                            for (int j = 0; j < k; j++) {
                                JSONObject objectComment = jsonArrayComment.getJSONObject(j);
                                CommentList commentList = new CommentList();
                                commentList.setCmt_id(objectComment.getString(Constant.CMT_ID));
                                commentList.setCmt_name(objectComment.getString(Constant.CMT_NAME));
                                commentList.setCmt_text(objectComment.getString(Constant.CMT_TEXT));
                                mListItemComment.add(commentList);
                            }
                        }

                        JSONArray jsonArrayGallery = objJson.getJSONArray(Constant.GALLERY_ARRAY_NAME);
                        if (jsonArrayGallery.length() > 0 && !jsonArrayGallery.get(0).equals("")) {
                            ItemGallery item = new ItemGallery();
                            item.setGaImage(objJson.getString(Constant.LATEST_IMAGE));
                            item.setGaType(objJson.getString(Constant.LATEST_TYPE));
                            item.setGaPlayId(objJson.getString(Constant.LATEST_VIDEO_PLAY_ID));
                            mGallery.add(item);
                            for (int l = 0; l < jsonArrayGallery.length(); l++) {
                                JSONObject objChild = jsonArrayGallery.getJSONObject(l);
                                ItemGallery item2 = new ItemGallery();
                                item2.setGaImage(objChild.getString(Constant.GALLERY_IMAGE_NAME));
                                item2.setGaPlayId("");
                                item2.setGaType("");
                                mGallery.add(item2);
                            }
                        } else {
                            ItemGallery item = new ItemGallery();
                            item.setGaImage(objJson.getString(Constant.LATEST_IMAGE));
                            item.setGaType(objJson.getString(Constant.LATEST_TYPE));
                            item.setGaPlayId(objJson.getString(Constant.LATEST_VIDEO_PLAY_ID));
                            mGallery.add(item);
                        }

                        JSONArray jsonArrayChild = objJson.getJSONArray(Constant.RELATED_ITEM_ARRAY_NAME);
                        if (jsonArrayChild.length() > 0 && !jsonArrayChild.get(0).equals("")) {
                            for (int j = 0; j < jsonArrayChild.length(); j++) {
                                JSONObject objChild = jsonArrayChild.getJSONObject(j);
                                ItemLatest item = new ItemLatest();
                                item.setNewsId(objChild.getString(Constant.LATEST_ID));
                                item.setNewsTitle(objChild.getString(Constant.LATEST_HEADING));
                                item.setNewsImage(objChild.getString(Constant.LATEST_IMAGE));
                                item.setNewsDesc(objChild.getString(Constant.LATEST_DESC));
                                item.setNewsDate(objChild.getString(Constant.LATEST_DATE));
                                item.setNewsView(objChild.getString(Constant.LATEST_VIEW));
                                item.setNewsType(objChild.getString(Constant.LATEST_TYPE));
                                item.setNewsVideoId(objChild.getString(Constant.LATEST_VIDEO_PLAY_ID));
                                mListItemRelated.add(item);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                displayData();
            }
        }
    }

    private void displayData() {

        if (objBean.getNewsType().equals("video")) {
            if (objBean.getNewsImage().isEmpty()) {
                Picasso.get().load(Constant.YOUTUBE_IMAGE_FRONT + objBean.getNewsVideoId() + Constant.YOUTUBE_SMALL_IMAGE_BACK).placeholder(R.drawable.place_holder_big).into(image_news);
            }
            image_play.setVisibility(View.VISIBLE);
        } else if (objBean.getNewsType().equals("image")) {
            Picasso.get().load(objBean.getNewsImage()).placeholder(R.drawable.place_holder_big).into(image_news);
            image_play.setVisibility(View.GONE);
        }

        image_news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constant.ConsImage = mGallery;
                Intent intent_gallery = new Intent(NewsDetailActivity.this, ImageActivity.class);
                startActivity(intent_gallery);
            }
        });


        txt_date.setText(objBean.getNewsDate());
        txt_title.setText(objBean.getNewsTitle());
        txt_view.setText(JsonUtils.Format(Integer.parseInt(objBean.getNewsView())));
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        String text = "<style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/myfonts/Poppins-Regular_0.ttf\")}body,* {font-family: MyFont; color:#7A7B7D; font-size: 13px;}img{max-width:100%;height:auto; border-radius: 3px;}</style>";
        webView.loadDataWithBaseURL("", text + "<div>" + objBean.getNewsDesc() + "</div>", "text/html", "utf-8", null);

        if (!mGallery.isEmpty()) {
            text_no.setText("1/" + mGallery.size());
        }

        if (!mListItemRelated.isEmpty()) {
            relatedAdapter = new RelatedAdapter(NewsDetailActivity.this, mListItemRelated);
            mTopView.setAdapter(relatedAdapter);
        } else {
            no_rel.setVisibility(View.VISIBLE);
        }

        if (mListItemComment.size() == 0) {
            textViewNoCommentFound.setVisibility(View.VISIBLE);
        } else {
            Collections.reverse(mListItemComment);
            commentAdapter = new CommentAdapter(NewsDetailActivity.this, mListItemComment);
            recyclerViewComment.setAdapter(commentAdapter);
        }

        if (databaseHelper.getFavouriteById(objBean.getNewsId())) {
            image_list_fav.setImageResource(R.drawable.ic_fav_hover);
        } else {
            image_list_fav.setImageResource(R.drawable.ic_fav);
        }

        image_list_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues fav = new ContentValues();
                if (databaseHelper.getFavouriteById(objBean.getNewsId())) {
                    databaseHelper.removeFavouriteById(objBean.getNewsId());
                    image_list_fav.setImageResource(R.drawable.ic_fav);
                    Toast.makeText(NewsDetailActivity.this, getString(R.string.favourite_remove), Toast.LENGTH_SHORT).show();
                } else {
                    fav.put(DatabaseHelper.KEY_ID, objBean.getNewsId());
                    fav.put(DatabaseHelper.KEY_TITLE, objBean.getNewsTitle());
                    fav.put(DatabaseHelper.KEY_IMAGE, objBean.getNewsImage());
                    fav.put(DatabaseHelper.KEY_DESC, objBean.getNewsDesc());
                    fav.put(DatabaseHelper.KEY_DATE, objBean.getNewsDate());
                    fav.put(DatabaseHelper.KEY_VIEW, objBean.getNewsView());
                    fav.put(DatabaseHelper.KEY_TYPE, objBean.getNewsType());
                    fav.put(DatabaseHelper.KEY_PLAY_ID, objBean.getNewsVideoId());
                    databaseHelper.addFavourite(DatabaseHelper.TABLE_FAVOURITE_NAME, fav, null);
                    image_list_fav.setImageResource(R.drawable.ic_fav_hover);
                    Toast.makeText(NewsDetailActivity.this, getString(R.string.favourite_add), Toast.LENGTH_SHORT).show();
                }
            }
        });

        imageView_Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constant.NEWS_ID = objBean.getNewsId();
                if (myApplication.getIsLogin()) {
                    if (editText.length() == 0) {
                        Toast.makeText(NewsDetailActivity.this, getString(R.string.comment_require), Toast.LENGTH_SHORT).show();
                    } else {
                        if (JsonUtils.isNetworkAvailable(NewsDetailActivity.this)) {
                            new MyTaskComment().execute(Constant.COMMENT_POST_URL + objBean.getNewsId() + "&user_name=" + myApplication.getUserName() + "&comment_text=" + editText.getText().toString());
                        }
                    }
                } else {
                    final PrettyDialog dialog = new PrettyDialog(NewsDetailActivity.this);
                    dialog.setTitle(getString(R.string.dialog_warning))
                            .setTitleColor(R.color.dialog_text)
                            .setMessage(getString(R.string.login_require))
                            .setMessageColor(R.color.dialog_text)
                            .setAnimationEnabled(false)
                            .setIcon(R.drawable.pdlg_icon_close, R.color.dialog_color, new PrettyDialogCallback() {
                                @Override
                                public void onClick() {
                                    dialog.dismiss();
                                }
                            })
                            .addButton(getString(R.string.dialog_ok), R.color.dialog_white_text, R.color.dialog_color, new PrettyDialogCallback() {
                                @Override
                                public void onClick() {
                                    dialog.dismiss();
                                    Intent intent_login = new Intent(NewsDetailActivity.this, SignInActivity.class);
                                    intent_login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent_login.putExtra("isfromdetail", true);
                                    intent_login.putExtra("isnewsid", Constant.NEWS_ID);
                                    startActivity(intent_login);
                                 }
                            });
                    dialog.setCancelable(false);
                    dialog.show();
                }
            }
        });

        btn_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NewsDetailActivity.this, CommentActivity.class)
                        .putExtra("Id", objBean.getNewsId()));
            }
        });
    }


    private void showProgress(boolean show) {
        if (show) {
            mProgressBar.setVisibility(View.VISIBLE);
            mScrollView.setVisibility(View.GONE);
            lyt_not_found.setVisibility(View.GONE);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mScrollView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                if (!iswhichscreen) {
                    super.onBackPressed();

                } else {
                    Intent intent = new Intent(NewsDetailActivity.this, ActivityMain.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
                break;
            case R.id.menu_detail_share:
                if (objBean.getNewsType().equals("video")) {
                    (new SaveTask(NewsDetailActivity.this)).execute(Constant.YOUTUBE_IMAGE_FRONT + objBean.getNewsVideoId() + Constant.YOUTUBE_SMALL_IMAGE_BACK);
                } else {
                    (new SaveTask(NewsDetailActivity.this)).execute(objBean.getNewsImage());
                }
                break;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
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
            if (objBean.getNewsType().equals("video")) {
                share.putExtra(Intent.EXTRA_TEXT, objBean.getNewsTitle() + "\n" + Html.fromHtml(objBean.getNewsDesc()) + "\n" + getString(R.string.news_video) + "\n" + "https://www.youtube.com/watch?v=" + objBean.getNewsVideoId() + "\n" + getString(R.string.share_message) + "\n" + "https://play.google.com/store/apps/details?id=" + getPackageName());
            } else {
                share.putExtra(Intent.EXTRA_TEXT, objBean.getNewsTitle() + "\n" + Html.fromHtml(objBean.getNewsDesc()) + "\n" + getString(R.string.share_message) + "\n" + "https://play.google.com/store/apps/details?id=" + getPackageName());
            }
            share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file.getAbsolutePath()));
            startActivity(Intent.createChooser(share, "Share Image"));
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class MyTaskComment extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.getJSONString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            dismissProgressDialog();

            if (null == result || result.length() == 0) {
                showToast(getString(R.string.no_data));

            } else {

                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.LATEST_ARRAY_NAME);
                    JSONObject objJson;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);
                        strMessage = objJson.getString(Constant.MSG);
                        Constant.GET_SUCCESS_MSG = objJson.getInt(Constant.SUCCESS);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setResult();
            }
        }
    }

    public void setResult() {

        if (Constant.GET_SUCCESS_MSG == 0) {
            final PrettyDialog dialog = new PrettyDialog(this);
            dialog.setTitle(getString(R.string.dialog_error))
                    .setTitleColor(R.color.dialog_color)
                    .setMessage(strMessage)
                    .setMessageColor(R.color.dialog_color)
                    .setAnimationEnabled(false)
                    .setIcon(R.drawable.pdlg_icon_close, R.color.dialog_color, new PrettyDialogCallback() {
                        @Override
                        public void onClick() {
                            dialog.dismiss();
                        }
                    })
                    .addButton(getString(R.string.dialog_ok), R.color.pdlg_color_white, R.color.dialog_color, new PrettyDialogCallback() {
                        @Override
                        public void onClick() {
                            dialog.dismiss();
                        }
                    });
            dialog.setCancelable(false);
            dialog.show();
        } else {
            final PrettyDialog dialog = new PrettyDialog(this);
            dialog.setTitle(getString(R.string.dialog_success))
                    .setTitleColor(R.color.dialog_color)
                    .setMessage(strMessage)
                    .setMessageColor(R.color.dialog_color)
                    .setAnimationEnabled(false)
                    .setIcon(R.drawable.pdlg_icon_success, R.color.dialog_color, new PrettyDialogCallback() {
                        @Override
                        public void onClick() {
                            dialog.dismiss();
                        }
                    })
                    .addButton(getString(R.string.dialog_ok), R.color.pdlg_color_white, R.color.dialog_color, new PrettyDialogCallback() {
                        @Override
                        public void onClick() {
                            dialog.dismiss();
                            editText.getText().clear();

                        }
                    });
            dialog.setCancelable(false);
            dialog.show();
        }
    }

    public void showToast(String msg) {
        Toast.makeText(NewsDetailActivity.this, msg, Toast.LENGTH_LONG).show();
    }

    public void showProgressDialog() {
        pDialog.setMessage(getString(R.string.loading));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();
    }

    public void dismissProgressDialog() {
        pDialog.dismiss();
    }
}
