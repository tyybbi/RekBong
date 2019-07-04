package com.tyybbi.rekbong;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DBHandler extends SQLiteOpenHelper {

    public static final String TAG = "DBHandler";
    private static final int DATABASE_VERSION = 1;
    protected static final String DATABASE_NAME = "Plates.db";
    private static final String TABLE_plates = "plates";
    private static final String KEY_ID = "_id";
    private static final String KEY_PLATE = "plate";
    private static final String KEY_DATE = "datetime";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "CREATE TABLE " +
                TABLE_plates + "(" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_PLATE + " TEXT NOT NULL, " +
                KEY_DATE + " INTEGER )";

        db.execSQL(sql);
        Log.i(TAG, "sql: " + sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String sql = "DROP TABLE IF EXISTS " + TABLE_plates;
        db.execSQL(sql);

        onCreate(db);
    }

    // CRUD Operations

    public void addNewPlate(Plate plate) {
        SQLiteDatabase mDb = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(KEY_PLATE, plate.getPlate());
        cv.put(KEY_DATE, plate.getDatetime());
        mDb.insert(TABLE_plates, null, cv);

        mDb.close();
    }
    
    public Cursor readAllPlates() {
        SQLiteDatabase mDb = this.getReadableDatabase();

        String sql = "SELECT * FROM " + TABLE_plates + " ORDER BY " + KEY_ID + " DESC";
        Cursor c = mDb.rawQuery(sql, null);

        return c;
    }


    public void updatePlate(Plate plate) {
        SQLiteDatabase mDb = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        mDb.insert(TABLE_plates, null, cv);
        String[] strId = {Integer.toString(plate.getId())};
        cv.put(KEY_PLATE, plate.getPlate());
        cv.put(KEY_DATE, plate.getDatetime());

        mDb.update(TABLE_plates, cv, "WHERE _id = ? ", strId);
        mDb.close();
    }

    public void deletePlate(Plate plate) {
        SQLiteDatabase mDb = this.getWritableDatabase();
        String[] strId = {Integer.toString(plate.getId())};
        mDb.delete(TABLE_plates, "WHERE _id = ? ", strId);
        mDb.close();
    }
}
