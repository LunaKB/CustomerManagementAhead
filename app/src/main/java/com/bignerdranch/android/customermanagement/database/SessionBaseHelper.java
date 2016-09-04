package com.bignerdranch.android.customermanagement.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bignerdranch.android.customermanagement.database.SessionDbSchema.SessionTable;

/**
 * Created by Chaz-Rae on 8/24/2016.
 * Session database creator
 *
 * Coded from Android Programming: The Big Nerd Ranch Guide 2nd Edition
 */
public class SessionBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "sessionBase.db";

    public SessionBaseHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("create table " + SessionTable.NAME+ "(" +
                "_id integer primary key autoincrement, " +
                SessionTable.Cols.SESSION_UUID + ", " +
                SessionTable.Cols.CUSTOMER_UUID + ", " +
                SessionTable.Cols.DATE + ", " +
                SessionTable.Cols.DESCRIPTION + ")"
        );
    }

    @Override
    public void  onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }
}
