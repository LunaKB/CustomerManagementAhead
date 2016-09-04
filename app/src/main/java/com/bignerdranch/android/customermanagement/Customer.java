package com.bignerdranch.android.customermanagement;

import java.util.UUID;

/**
 * Created by Chaz-Rae on 8/24/2016.
 * Class for all customer information
 */
public class Customer {

    private String mCustomerName;
    private String mBillingInfo;
    private UUID mID;
    private int mSessionLimit;

    public Customer(){
        this(UUID.randomUUID());
    }

    public Customer(UUID id){
        mID = id;
    }

    public String getCustomerName() {
        return mCustomerName;
    }

    public void setCustomerName(String customerName) {
        mCustomerName = customerName;
    }

    public String getBillingInfo() {
        return mBillingInfo;
    }

    public void setBillingInfo(String billingInfo) {
        mBillingInfo = billingInfo;
    }

    public UUID getID() {
        return mID;
    }

    public int getSessionLimit() {
        return mSessionLimit;
    }

    public void setSessionLimit(int sessionLimit) {
        mSessionLimit = sessionLimit;
    }

    public String getPhotoFilename(){
        return "IMG_" + getID().toString() + ".jpg";
    }
}
