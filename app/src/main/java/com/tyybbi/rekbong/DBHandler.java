package com.tyybbi.rekbong;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    protected static final String DATABASE_NAME = "Plates.db";
    private static final String TABLE_plates = "plates";
    private static final String KEY_ID = "_id";
    private static final String KEY_LETTER_PART = "letterpart";
    private static final String KEY_NUMBER_PART = "numberpart";
    private static final String KEY_DATE = "datetime";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "CREATE TABLE " +
                TABLE_plates + "(" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_LETTER_PART + " TEXT, " +
                KEY_NUMBER_PART + " INTEGER NOT NULL, " +
                KEY_DATE + " INTEGER )";

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String sql = "DROP TABLE IF EXISTS " + TABLE_plates;
        db.execSQL(sql);

        onCreate(db);
    }

    // CRUD Operations

    public void addNewPlate(Plate plate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(KEY_LETTER_PART, plate.getLetterPart());
        cv.put(KEY_NUMBER_PART, plate.getNumberPart());
        cv.put(KEY_DATE, plate.getDatetime());
        db.insert(TABLE_plates, null, cv);

        db.close();
    }
    
    public Cursor getAllPlates(boolean reverse) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql;

        if (!reverse) {
            // Read in descending order so that the newly added plate shows up first in ListView
            sql = "SELECT * FROM " + TABLE_plates + " ORDER BY " + KEY_NUMBER_PART + " DESC";
        } else {
            // Reverse spotting order, ie. from 999 to 1
            sql = "SELECT * FROM " + TABLE_plates + " ORDER BY " + KEY_NUMBER_PART;
        }
        Cursor c = db.rawQuery(sql, null);

        return c;
    }

    public Plate getPlate(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "SELECT * FROM " + TABLE_plates + " WHERE " + KEY_ID + " = " + id;
        Cursor c = db.rawQuery(sql, null);
        if (c != null)
            c.moveToFirst();

        Plate plate = new Plate();
        plate.setId(c.getInt(0));
        plate.setLetterPart(c.getString(1));
        plate.setNumberPart(c.getInt(2));
        plate.setDatetime(c.getLong(3));

        return plate;
    }

    public void updatePlate(Plate plate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        String[] strId = {Integer.toString(plate.getId())};
        cv.put(KEY_LETTER_PART, plate.getLetterPart());
        cv.put(KEY_NUMBER_PART, plate.getNumberPart());
        cv.put(KEY_DATE, plate.getDatetime());

        db.update(TABLE_plates, cv,  KEY_ID + " = ?", strId);
        db.close();
    }

    public void deletePlate(Plate plate) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] strId = {Integer.toString(plate.getId())};
        db.delete(TABLE_plates, KEY_ID + " = ?", strId);
        db.close();
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "DELETE FROM " + TABLE_plates;
        db.execSQL(sql);
    }
}
