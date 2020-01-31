package com.viaviapp.newsapps;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.adapter.CommentAdapter;
import com.example.adapter.LatestAdapter;
import com.example.fragment.LatestFragment;
import com.example.item.CommentList;
import com.example.item.ItemLatest;
import com.example.util.Constant;
import com.example.util.JsonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CommentActivity extends AppCompatActivity {

    ArrayList<CommentList> mListItem;
    public RecyclerView recyclerView;
    CommentAdapter adapter;
    private ProgressBar progressBar;
    private LinearLayout lyt_not_found;
    EditText editTextComment;
    InputMethodManager inputMethodManager;
    ImageView imageViewSend;
    LinearLayout linearLayout_all_comment;
    ImageView floatingComment;
    String Id;
    MyApplication myApplication;
    ProgressDialog pDialog;
    String strMessage;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.all_cmt));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        myApplication = MyApplication.getAppInstance();
        pDialog = new ProgressDialog(this);

        Intent intent = getIntent();
        Id = intent.getStringExtra("Id");
        mListItem = new ArrayList<>();
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        lyt_not_found = findViewById(R.id.lyt_not_found);
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.vertical_courses_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(CommentActivity.this, 1));
        recyclerView.setFocusable(false);

        editTextComment = findViewById(R.id.editText_dialogbox_comment);
         imageViewSend = findViewById(R.id.imageView_dialogBox_comment);
        linearLayout_all_comment=findViewById(R.id.linearLayout_all_comment);
        floatingComment=findViewById(R.id.fab);

        if (JsonUtils.isNetworkAvailable(CommentActivity.this)) {
            new getLatest().execute(Constant.COMMENT_URL+Id);
        }

        floatingComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearLayout_all_comment.setVisibility(View.VISIBLE);
                floatingComment.setVisibility(View.GONE);
            }
        });


        imageViewSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Constant.NEWS_ID = Id;
                if (myApplication.getIsLogin()) {
                    if (editTextComment.length() == 0) {
                        Toast.makeText(CommentActivity.this, getString(R.string.comment_require), Toast.LENGTH_SHORT).show();
                    } else {
                        if (JsonUtils.isNetworkAvailable(CommentActivity.this)) {
                            new MyTaskComment().execute(Constant.COMMENT_POST_URL + Id + "&user_name=" + myApplication.getUserName() + "&comment_text=" + editTextComment.getText().toString());
                        }
                    }
                } else {
                    final PrettyDialog dialog = new PrettyDialog(CommentActivity.this);
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
                                    Intent intent_login = new Intent(CommentActivity.this, SignInActivity.class);
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

    }
    @SuppressLint("StaticFieldLeak")
    private class getLatest extends AsyncTask<String, Void, String> {

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
                        JSONArray jsonArrayChild = objJson.getJSONArray(Constant.COMMENT_ARRAY_NAME);
                        for (int j = 0; j < jsonArrayChild.length(); j++) {
                            objJson = jsonArrayChild.getJSONObject(j);
                            CommentList objItem = new CommentList();
                            objItem.setCmt_id(objJson.getString(Constant.CMT_ID));
                            objItem.setCmt_name(objJson.getString(Constant.CMT_NAME));
                            objItem.setCmt_text(objJson.getString(Constant.CMT_TEXT));
                            mListItem.add(objItem);
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

        Collections.reverse(mListItem);
            adapter = new CommentAdapter(CommentActivity.this, mListItem);
            recyclerView.setAdapter(adapter);

            if (adapter.getItemCount() == 0) {
                lyt_not_found.setVisibility(View.VISIBLE);
            } else {
                lyt_not_found.setVisibility(View.GONE);
            }

    }

    private void showProgress(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            lyt_not_found.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
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
                            editTextComment.getText().clear();

                        }
                    });
            dialog.setCancelable(false);
            dialog.show();
        }
    }

    public void showToast(String msg) {
        Toast.makeText(CommentActivity.this, msg, Toast.LENGTH_LONG).show();
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
