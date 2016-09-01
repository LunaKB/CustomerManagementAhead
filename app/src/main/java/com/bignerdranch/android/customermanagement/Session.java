package com.bignerdranch.android.customermanagement;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Chaz-Rae on 8/24/2016.
 */
public class Session {
    private UUID mCustomerID = UUID.fromString("f17fdff3-c273-4ee2-adbd-147141d123e8");
    private Date mDate;
    private String mDescription;
    private UUID mId;

    public Session(){
        this(UUID.randomUUID());
    }

    public Session(UUID id){
        mId = id;
        mDate = new Date();
    }

    public UUID getId() {
        return mId;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public void setDay(int day, int month, int year){
        mDate.setDate(day);
        mDate.setMonth(month);
        mDate.setYear(year);
    }

    public void setTime(int hour, int minute){
        mDate.setHours(hour);
        mDate.setMinutes(minute);
        mDate.setSeconds(0);
    }

    public UUID getCustomerID() {
        return mCustomerID;
    }

    public void setCustomerID(UUID customerID) {
        mCustomerID = customerID;
    }

    public String getPhotoFilename(){
        return "IMG_" + getId().toString() + ".jpg";
    }
}
