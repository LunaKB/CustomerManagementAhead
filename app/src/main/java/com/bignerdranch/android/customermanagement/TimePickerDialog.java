package com.bignerdranch.android.customermanagement;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Chaz-Rae on 8/24/2016.
 * Dialog for user to pick time.
 */
public class TimePickerDialog extends DialogFragment {
    private final static String ARG_TIME = "time";
    public final static String EXTRA_TIME =
            "com.bignerdranch.android.customermanagement.time";
    private TimePicker mTimePicker;

    public static TimePickerDialog newInstance(Date date){
        Bundle args = new Bundle();
        args.putSerializable(ARG_TIME, date);

        TimePickerDialog fragment = new TimePickerDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstance){
        Date date = (Date)getArguments().getSerializable(ARG_TIME);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time, null);

        mTimePicker = (TimePicker)v.findViewById(R.id.dialog_time_picker);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.time_picker_dialog)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        int hour = mTimePicker.getCurrentHour();
                        int minute = mTimePicker.getCurrentMinute();
                        Date date = new GregorianCalendar(0, 0, 0, hour, minute).getTime();
                        sendResult(Activity.RESULT_OK, date);
                    }
                })
                .create();
    }

    private void sendResult(int resultCode, Date date){
        if(getTargetFragment() == null){
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_TIME, date);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
