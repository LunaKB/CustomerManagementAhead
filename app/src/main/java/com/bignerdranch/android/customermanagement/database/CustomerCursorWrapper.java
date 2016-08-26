package com.bignerdranch.android.customermanagement.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.bignerdranch.android.customermanagement.Customer;
import com.bignerdranch.android.customermanagement.database.CustomerDbSchema.CustomerTable;

import java.util.UUID;

/**
 * Created by Chaz-Rae on 8/24/2016.
 */
public class CustomerCursorWrapper extends CursorWrapper{
    public CustomerCursorWrapper(Cursor cursor){
        super(cursor);
    }

    public Customer getCustomer(){
        String uuidString = getString(getColumnIndex(CustomerTable.Cols.UUID));
        String name = getString(getColumnIndex(CustomerTable.Cols.NAME));
        String billing = getString(getColumnIndex(CustomerTable.Cols.BILLING));
        int sessions = getInt(getColumnIndex(CustomerTable.Cols.SESSIONS));

        Customer c = new Customer(UUID.fromString(uuidString));

        c.setCustomerName(name);
        c.setBillingInfo(billing);
        c.setSessionLimit(sessions);

        return c;
    }
}
