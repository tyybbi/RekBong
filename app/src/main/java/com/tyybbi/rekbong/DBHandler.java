package com.tyybbi.rekbong;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHandler extends SQLiteOpenHelper {

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

        String sql = "CREATE TABLE IF NOT EXISTS " +
                TABLE_plates + "(" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_PLATE + "TEXT NOT NULL, " +
                KEY_DATE + "INTEGER ) ";

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String sql = "DROP TABLE IF EXISTS " + TABLE_plates;
        db.execSQL(sql);

        onCreate(db);
    }

    // CRUD Operations

    public void addNewPlate(String KEY_PLATE, long KEY_DATE) {
        SQLiteDatabase mDb = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        // TODO
    }

    public ArrayList<Plate> readAllPlates() {
        ArrayList<Plate> mList = new ArrayList<>();
        SQLiteDatabase mDb = this.getReadableDatabase();

        String sql = "SELECT * FROM " + TABLE_plates;
        Cursor c = mDb.rawQuery(sql, null);

        return mList;
    }

    public void updatePlate() {
        // TODO
    }

    public void deletePlate() {
        // TODO
    }
}
