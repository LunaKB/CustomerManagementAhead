package com.bignerdranch.android.customermanagement;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Chaz-Rae on 9/3/2016.
 * Dialog for user to choose whether to logout or not
 */
public class TextViewDialog extends DialogFragment {
    private TextView mTextView;

    public static TextViewDialog newInstance(){
        return new TextViewDialog();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstance){
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_textview, null);
        mTextView = (TextView)v.findViewById(R.id.logging_out_textview);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.logout_dialog_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_OK);
                        Toast.makeText(getActivity(), R.string.logout_toast, Toast.LENGTH_SHORT)
                                .show();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create();

    }

    private void sendResult(int resultCode){
        if(getTargetFragment() == null){
            return;
        }

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, null);
    }
}

