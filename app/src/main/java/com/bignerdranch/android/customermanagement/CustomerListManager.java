package com.bignerdranch.android.customermanagement;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.bignerdranch.android.customermanagement.database.CustomerBaseHelper;
import com.bignerdranch.android.customermanagement.database.CustomerCursorWrapper;
import com.bignerdranch.android.customermanagement.database.CustomerDbSchema.CustomerTable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Chaz-Rae on 8/24/2016.
 */
public class CustomerListManager {
    private static CustomerListManager sCustomerList;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static CustomerListManager get(Context context){
        if(sCustomerList == null){
            sCustomerList = new CustomerListManager(context);
        }
        return sCustomerList;
    }

    /* Constuctor */
    private CustomerListManager(Context context){
        mContext = context.getApplicationContext();
        mDatabase = new CustomerBaseHelper(mContext).getWritableDatabase();

    }

    /* Add, delete, update */
    public void addCustomer(Customer customer){
        ContentValues values = getContentValues(customer);
        mDatabase.insert(CustomerTable.NAME, null, values);
    }

    public void deleteCustomer(Customer customer, File file){
        mDatabase.delete(CustomerTable.NAME, CustomerTable.Cols.UUID + " = ?",
                new String[] {customer.getID().toString()});
        if(file.exists()){
            file.delete();
        }
    }

    public void updateCustomer(Customer customer){
        String uuidString = customer.getID().toString();
        ContentValues values = getContentValues(customer);

        mDatabase.update(CustomerTable.NAME, values,
                CustomerTable.Cols.UUID + " = ?", new String[]{uuidString});
    }

    /* Getters */
    public List<Customer> getCustomers(){
        List<Customer> customers = new ArrayList<>();

        CustomerCursorWrapper cursor = queryCustomers(null, null);

        try{
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                customers.add(cursor.getCustomer());
                cursor.moveToNext();
            }
        }
        finally{
            cursor.close();
        }

        return customers;
    }

    public Customer getCustomer(UUID id){
        CustomerCursorWrapper cursor = queryCustomers(
                CustomerTable.Cols.UUID + " = ?",
                new String[] {id.toString()}
        );

        try{
            if(cursor.getCount() == 0){
                return null;
            }

            cursor.moveToFirst();
            return cursor.getCustomer();
        }
        finally {
            cursor.close();
        }
    }

    public File getPhotoFile(Customer customer){
        File externalFilesDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if(externalFilesDir == null){
            return null;
        }

        return new File(externalFilesDir, customer.getPhotoFilename());
    }

    /* SQLiteDatabase Methods */
    private static ContentValues getContentValues(Customer customer){
        ContentValues values = new ContentValues();
        values.put(CustomerTable.Cols.UUID, customer.getID().toString());
        values.put(CustomerTable.Cols.NAME, customer.getCustomerName());
        values.put(CustomerTable.Cols.BILLING, customer.getBillingInfo());
        values.put(CustomerTable.Cols.SESSIONS, Integer.toString(customer.getSessionLimit()));

        return values;
    }

    private CustomerCursorWrapper queryCustomers(String whereClause, String[] whereArgs){
        Cursor cursor = mDatabase.query(
                CustomerTable.NAME,
                null, // Columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null // orderBy
        );

        return new CustomerCursorWrapper(cursor);
    }
}
