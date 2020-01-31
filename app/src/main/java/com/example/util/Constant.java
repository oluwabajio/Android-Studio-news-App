package com.example.util;

import com.example.item.ItemGallery;
import com.viaviapp.newsapps.BuildConfig;
import java.io.Serializable;
import java.util.ArrayList;

public class Constant implements Serializable {

    private static final long serialVersionUID = 1L;

    //server url
    public static final String SERVER_URL= BuildConfig.server_url;

    public static final String IMAGE_URL =SERVER_URL+"images/";

    public static final String HOME_URL =SERVER_URL+"api.php?home";

    public static final String LATEST_URL =SERVER_URL+"api.php?latest";

    public static final String CATEGORY_URL =SERVER_URL+"api.php?cat_list";

    public static final String CATEGORY_ITEM_URL =SERVER_URL+"api.php?cat_id=";

    public static final String SINGLE_NEWS_URL = SERVER_URL+"api.php?news_id=";

    public static final String MOST_VIEW_NEWS_URL = SERVER_URL+"api.php?most_view_news";

    public static final String SEARCH_URL = SERVER_URL+"api.php?search=";

    public static final String COMMENT_URL = SERVER_URL+"api.php?news_id=";

    public static final String COMMENT_POST_URL = SERVER_URL+"api_comment.php?news_id=";

    public static final String ABOUT_US_URL = SERVER_URL+"api.php";

    public static final String LOGIN_URL = SERVER_URL+"api.php?users_login&email=";

    public static final String REGISTER_URL = SERVER_URL+"api.php?user_register&name=";

    public static final String FORGOT_URL = SERVER_URL+"api.php?forgot_pass&email=";

    public static final String PROFILE_URL = SERVER_URL+"api.php?user_profile&id=";

    public static final String PROFILE_UPDATE_URL = SERVER_URL+"api.php?user_profile_update&user_id=";

    public static final String YOUTUBE_IMAGE_FRONT="http://img.youtube.com/vi/";
    public static final String YOUTUBE_SMALL_IMAGE_BACK="/hqdefault.jpg";

    public static final String LATEST_ARRAY_NAME="NEWS_APP";
    public static final String HOME_FEATURED_ARRAY="featured_news";
    public static final String HOME_TOP_ARRAY="top_10_news";
    public static final String HOME_LATEST_ARRAY="latest_news";
    public static final String HOME_CAT_ARRAY="category_list";
    public static final String GALLERY_ARRAY_NAME="galley_image";
    public static final String RELATED_ITEM_ARRAY_NAME="related_news";
    public static final String COMMENT_ARRAY_NAME="user_comments";

    public static final String LATEST_ID="id";
    public static final String LATEST_HEADING="news_title";
    public static final String LATEST_DESC="news_description";
    public static final String LATEST_IMAGE="news_image_b";
    public static final String LATEST_IMAGE_SMALL="news_image_s";
    public static final String LATEST_DATE="news_date";
    public static final String LATEST_VIEW="news_views";
    public static final String LATEST_VIDEO_URL="video_url";
    public static final String LATEST_TYPE="news_type";
    public static final String LATEST_VIDEO_PLAY_ID="video_id";
    public static final String GALLERY_IMAGE_NAME="image_name";

     public static final String CATEGORY_NAME="category_name";
    public static final String CATEGORY_CID="cid";
    public static final String CATEGORY_IMAGE="category_image";
    public static final String CATEGORY_IMAGE_THUMB="category_image_thumb";

    public static final String CMT_ID="news_id";
    public static final String CMT_NAME="user_name";
    public static final String CMT_TEXT="comment_text";

    public static final String APP_NAME="app_name";
    public static final String APP_IMAGE="app_logo";
    public static final String APP_VERSION="app_version";
    public static final String APP_AUTHOR="app_author";
    public static final String APP_CONTACT="app_contact";
    public static final String APP_EMAIL="app_email";
    public static final String APP_WEBSITE="app_website";
    public static final String APP_DESC="app_description";
    public static final String APP_PRIVACY="app_privacy_policy";
    public static final String APP_DEVELOP="app_developed_by";
    public static final String APP_PACKAGE_NAME="package_name";

    public static int GET_SUCCESS_MSG;
    public static final String MSG = "msg";
    public static final String SUCCESS = "success";
     public static final String USER_NAME = "name";
    public static final String USER_ID = "user_id";
    public static final String USER_EMAIL = "email";
    public static final String USER_PHONE = "phone";
    public static String NEWS_ID;

    public static final String ADS_BANNER_ID="banner_ad_id";
    public static final String ADS_FULL_ID="interstital_ad_id";
    public static final String ADS_BANNER_ON_OFF="banner_ad";
    public static final String ADS_FULL_ON_OFF="interstital_ad";
    public static final String ADS_PUB_ID="publisher_id";
    public static final String ADS_CLICK="interstital_ad_click";
    public static String SAVE_ADS_BANNER_ID,SAVE_ADS_FULL_ID,SAVE_ADS_BANNER_ON_OFF,SAVE_ADS_FULL_ON_OFF,SAVE_ADS_PUB_ID,SAVE_ADS_CLICK;

    public static final String DOWNLOAD_FOLDER_PATH="/NewsApp/";
    public static ArrayList<ItemGallery> ConsImage= new ArrayList<ItemGallery>();
}