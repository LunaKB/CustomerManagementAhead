package com.bignerdranch.android.customermanagement.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.bignerdranch.android.customermanagement.Session;
import com.bignerdranch.android.customermanagement.database.SessionDbSchema.SessionTable;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Chaz-Rae on 8/24/2016.
 */
public class SessionCursorWrapper extends CursorWrapper {
    public SessionCursorWrapper(Cursor cursor){
        super(cursor);
    }

    public Session getSession(){
        String uuidSessionString = getString(getColumnIndex(SessionTable.Cols.SESSION_UUID));
        String uuidCustomerString = getString(getColumnIndex(SessionTable.Cols.CUSTOMER_UUID));
        Long date = getLong(getColumnIndex(SessionTable.Cols.DATE));
        String description = getString(getColumnIndex(SessionTable.Cols.DESCRIPTION));

        Session s = new Session(UUID.fromString(uuidSessionString));
        Date d = new Date(date);

        s.setCustomerID(UUID.fromString(uuidCustomerString));
        s.setDate(d);
        s.setDescription(description);

        return s;
    }
}
