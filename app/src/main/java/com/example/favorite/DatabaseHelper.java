package com.example.favorite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.item.ItemLatest;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "newsapp.db";
    public static final String TABLE_FAVOURITE_NAME = "favourite";

    public static final String KEY_ID = "id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_DESC = "desc";
    public static final String KEY_DATE = "date";
    public static final String KEY_VIEW = "view";
    public static final String KEY_TYPE = "type";
    public static final String KEY_PLAY_ID = "playid";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_FAVOURITE_TABLE = "CREATE TABLE " + TABLE_FAVOURITE_NAME + "("
                + KEY_ID + " INTEGER,"
                + KEY_TITLE + " TEXT,"
                + KEY_IMAGE + " TEXT,"
                + KEY_DESC + " TEXT,"
                + KEY_DATE + " TEXT,"
                + KEY_VIEW + " TEXT,"
                + KEY_TYPE + " TEXT,"
                + KEY_PLAY_ID + " TEXT"
                + ")";
        db.execSQL(CREATE_FAVOURITE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVOURITE_NAME);
        // Create tables again
        onCreate(db);
    }

    public boolean getFavouriteById(String story_id) {
        boolean count = false;
        SQLiteDatabase db = this.getWritableDatabase();
        String[] args = new String[]{story_id};
        Cursor cursor = db.rawQuery("SELECT id FROM favourite WHERE id=? ", args);
        if (cursor.moveToFirst()) {
            count = true;
        }
        cursor.close();
        db.close();
        return count;
    }

    public void removeFavouriteById(String _id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM  favourite " + " WHERE " + KEY_ID + " = " + _id);
        db.close();
    }

    public long addFavourite(String TableName, ContentValues contentvalues, String s1) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.insert(TableName, s1, contentvalues);
    }

    public ArrayList<ItemLatest> getFavourite() {
        ArrayList<ItemLatest> chapterList = new ArrayList<>();
        String selectQuery = "SELECT *  FROM "
                + TABLE_FAVOURITE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                ItemLatest contact = new ItemLatest();
                contact.setNewsId(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ID)));
                contact.setNewsTitle(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TITLE)));
                contact.setNewsImage(cursor.getString(cursor.getColumnIndexOrThrow(KEY_IMAGE)));
                contact.setNewsDesc(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESC)));
                contact.setNewsDate(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DATE)));
                contact.setNewsView(cursor.getString(cursor.getColumnIndexOrThrow(KEY_VIEW)));
                contact.setNewsType(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TYPE)));
                contact.setNewsVideoId(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PLAY_ID)));

                chapterList.add(contact);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return chapterList;
    }
}
