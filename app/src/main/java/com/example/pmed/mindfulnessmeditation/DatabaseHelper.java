package com.example.pmed.mindfulnessmeditation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;

/**
 * Created by Jacky Sitzman on 3/7/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "subjects.db";
    private static final String TABLE_NAME = "subjects";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_UNAME = "uname";
    private static final String COLUMN_PASS = "pass";
    private static final String COLUMN_1 = "data1";
    private static final String COLUMN_2 = "data2";
    private static final String COLUMN_3 = "data3";

    private SQLiteDatabase db;

    private static final String TABLE_CREATE = "create table subjects (id integer primary key not null, " +
            "uname text not null, pass text not null)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
        this.db = db;
    }


    public void insertSubjects(Subjects s) {
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        String query = "select * from " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        int count = cursor.getCount();
        //s.setId(Integer.toString(count));

        //values.put(COLUMN_ID, s.getId()); //subject id number
        values.put(COLUMN_UNAME, s.getUname());
        values.put(COLUMN_PASS, s.getPass());

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public String searchPass(String uname) {
        db = this.getReadableDatabase();
        String query = "select * from " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        String a, b;
        b = "not found";

        if(cursor.moveToFirst()) {
            do {
                a = cursor.getString(1);
                b = cursor.getString(2);
                if(a.equals(uname)) {
                    b = cursor.getString(2);
                    break;
                }
            }
            while(cursor.moveToNext());
        }
        return b;
    }


    /*public String globalPassword() {
        String globalPassword = "";
        String query = "select * from subjects where isAdmin = true";
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()) {
            globalPassword = cursor.getString(4);
        }

        return globalPassword;
    }*/

    public Cursor getData1() {
        Cursor cursor;
        String[] projections = {COLUMN_1, COLUMN_2, COLUMN_3};
        cursor = db.query(TABLE_NAME,projections,null,null,null,null,null);
        return cursor;
    }

    public Cursor getInformation(SQLiteDatabase db) {
        Cursor cursor;
        String[] projections = {COLUMN_ID, COLUMN_UNAME, COLUMN_PASS};
        cursor = db.query(TABLE_NAME,projections,null,null,null,null,null);
        return cursor;
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(query);
        this.onCreate(db);
    }
}
