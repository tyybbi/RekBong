package com.tyybbi.rekbong;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Plates.db";
    private static final String TABLE_plates = "plates";
    private static final String KEY_ID = "_id";
    private static final String KEY_LETTER_PART = "letterpart";
    private static final String KEY_NUMBER_PART = "numberpart";
    private static final String KEY_DATE = "datetime";

    DBHandler(Context context) {
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

    void addNewPlate(Plate plate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(KEY_LETTER_PART, plate.getLetterPart());
        cv.put(KEY_NUMBER_PART, plate.getNumberPart());
        cv.put(KEY_DATE, plate.getDatetime());
        db.insert(TABLE_plates, null, cv);
    }
    
    Cursor getAllPlates(boolean reverse) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql;

        if (!reverse) {
            // Read in descending order so that the newly added plate shows up first in ListView
            sql = "SELECT * FROM " + TABLE_plates + " ORDER BY " + KEY_NUMBER_PART + " DESC";
        } else {
            // Reverse spotting order is set
            sql = "SELECT * FROM " + TABLE_plates + " ORDER BY " + KEY_NUMBER_PART;
        }

        return db.rawQuery(sql, null);
    }

    ArrayList<Integer> getAllNumberParts() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT " + KEY_NUMBER_PART + " FROM " + TABLE_plates;
        ArrayList<Integer> plateNumbers = new ArrayList<>();

        // Get plate numbers from DB and put them into ArrayList
        try {
            Cursor c = db.rawQuery(sql, null);
            c.moveToFirst();
            while (!c.isAfterLast()) {
                plateNumbers.add(c.getInt(c.getColumnIndexOrThrow(KEY_NUMBER_PART)));
                c.moveToNext();
            }
            c.close();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return plateNumbers;
    }

    Plate getPlate(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "SELECT * FROM " + TABLE_plates + " WHERE " + KEY_ID + " = " + id;
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();

        Plate plate = new Plate();
        plate.setId(c.getInt(0));
        plate.setLetterPart(c.getString(1));
        plate.setNumberPart(c.getInt(2));
        plate.setDatetime(c.getLong(3));
        c.close();

        return plate;
    }

    void updatePlate(Plate plate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        String[] strId = {Integer.toString(plate.getId())};
        cv.put(KEY_LETTER_PART, plate.getLetterPart());
        cv.put(KEY_NUMBER_PART, plate.getNumberPart());
        cv.put(KEY_DATE, plate.getDatetime());
        db.update(TABLE_plates, cv,  KEY_ID + " = ?", strId);
    }

    void deletePlate(Plate plate) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] strId = {Integer.toString(plate.getId())};
        db.delete(TABLE_plates, KEY_ID + " = ?", strId);
    }

    void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "DELETE FROM " + TABLE_plates;
        db.execSQL(sql);
    }
}
