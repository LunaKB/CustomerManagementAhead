package com.bignerdranch.android.customermanagement;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.bignerdranch.android.customermanagement.database.SessionBaseHelper;
import com.bignerdranch.android.customermanagement.database.SessionCursorWrapper;
import com.bignerdranch.android.customermanagement.database.SessionDbSchema.SessionTable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Chaz-Rae on 8/24/2016.
 * Class to manage session list using SQLite database
 */
public class SessionListManager {
    private static SessionListManager sSessionList;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static SessionListManager get(Context context){
        if(sSessionList == null){
            sSessionList = new SessionListManager(context);
        }
        return sSessionList;
    }

    /* Constructor */
    private SessionListManager(Context context){
        mContext = context.getApplicationContext();
        mDatabase = new SessionBaseHelper(mContext).getWritableDatabase();
    }

    /* Add, delete, update */
    public void addSession(Session session){
        ContentValues values = getContentValues(session);
        mDatabase.insert(SessionTable.NAME, null, values);
    }

    public void deleteSession(Session session, File file){
        mDatabase.delete(SessionTable.NAME, SessionTable.Cols.SESSION_UUID + " = ?",
                new String[] {session.getId().toString()});
        if(file.exists()){
            file.delete();
        }
    }

    public void updateSession(Session session){
        String uuidString = session.getId().toString();
        ContentValues values = getContentValues(session);

        mDatabase.update(SessionTable.NAME, values,
                SessionTable.Cols.SESSION_UUID + " = ?", new String[]{uuidString});
    }

    /* Getters */
    public List<Session> getSessions(){
        List<Session> sessions = new ArrayList<>();

        SessionCursorWrapper cursor = querySessions(null, null);

        try{
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                sessions.add(cursor.getSession());
                cursor.moveToNext();
            }
        }
        finally{
            cursor.close();
        }

        return sessions;
    }

    public Session getSession(UUID id){
        SessionCursorWrapper cursor = querySessions(
                SessionTable.Cols.SESSION_UUID + " = ?",
                new String[] {id.toString()}
        );

        try{
            if(cursor.getCount() == 0){
                return null;
            }

            cursor.moveToFirst();
            return cursor.getSession();
        }
        finally {
            cursor.close();
        }
    }

    public File getPhotoFile(Session session){
        File externalFilesDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if(externalFilesDir == null){
            return null;
        }

        return new File(externalFilesDir, session.getPhotoFilename());
    }

    /* SQLiteDatabase Methods */
    private static ContentValues getContentValues(Session session){
        ContentValues values = new ContentValues();
        values.put(SessionTable.Cols.SESSION_UUID, session.getId().toString());
        values.put(SessionTable.Cols.CUSTOMER_UUID, session.getCustomerID().toString());
        values.put(SessionTable.Cols.DATE, session.getDate().getTime());
        values.put(SessionTable.Cols.DESCRIPTION, session.getDescription());

        return values;
    }

    private SessionCursorWrapper querySessions(String whereClause, String[] whereArgs){
        Cursor cursor = mDatabase.query(
                SessionTable.NAME,
                null, // Columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null // orderBy
        );

        return new SessionCursorWrapper(cursor);
    }
}
