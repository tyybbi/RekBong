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
    private static final String KEY_ID = "id";
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

    public void addNewPlate(String plate, long date) {
        SQLiteDatabase mDb = this.getWritableDatabase();
        Log.i(TAG, "after getWrit");
        ContentValues contentValues = new ContentValues();

        contentValues.put(KEY_PLATE, plate);
        contentValues.put(KEY_DATE, date);
        mDb.insert(TABLE_plates, null, contentValues);

        Log.i(TAG, "plate, date: " + plate + date);

        mDb.close();
    }

    public ArrayList<Plate> readAllPlates() {
        ArrayList<Plate> mList = new ArrayList<>();
        SQLiteDatabase mDb = this.getReadableDatabase();

        String sql = "SELECT * FROM " + TABLE_plates;
        Cursor c = mDb.rawQuery(sql, null);

        c.moveToFirst();
        while (c.isAfterLast() == false) {
            Plate lPlate = new Plate();
            lPlate.id = Integer.parseInt(c.getString(c.getColumnIndex(KEY_ID)));
            lPlate.plate = c.getString(c.getColumnIndex(KEY_PLATE));
            lPlate.datetime = c.getLong(c.getColumnIndex(KEY_DATE));
            mList.add(lPlate);
            c.moveToNext();
        }

        mDb.close();

        return mList;
    }


    public void updatePlate() {
        // TODO
    }

    public void deletePlate() {
        // TODO
    }
}
