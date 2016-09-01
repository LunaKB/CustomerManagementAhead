package com.bignerdranch.android.customermanagement;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.github.gcacace.signaturepad.views.SignaturePad;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by Chaz-Rae on 8/30/2016.
 */
public class SessionCompleteDialog extends DialogFragment{
    private final static String ARG_ID = "id";
    public final static String EXTRA_SUCCESS =
            "com.bignerdranch.android.customermanagement.success";
    private SignaturePad mSignaturePad;
    private Bitmap mSignature;

    public static SessionCompleteDialog newInstance(UUID id){
        Bundle args = new Bundle();
        args.putSerializable(ARG_ID, id);

        SessionCompleteDialog fragment = new SessionCompleteDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstance){
        UUID id = (UUID)getArguments().getSerializable(ARG_ID);
        final Session session = SessionListManager.get(getActivity()).getSession(id);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_session_complete, null);
        mSignaturePad = (SignaturePad)v.findViewById(R.id.session_complete_signature_pad);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle("Sign Here")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mSignature = mSignaturePad.getSignatureBitmap();
                        saveBitmap(session);
                        if(SessionListManager.get(getActivity()).getPhotoFile(session) == null){
                            sendResult(Activity.RESULT_OK, 0);
                        }
                        else{
                            sendResult(Activity.RESULT_OK, 1);
                        }
                    }
                })
                .create();
    }

    private void sendResult(int resultCode, int success){
        if(getTargetFragment() == null){
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_SUCCESS, success);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }

    private void saveBitmap(Session session){
        OutputStream outStream = null;
        File file = SessionListManager.get(getActivity()).getPhotoFile(session);
        String filename = file.toString();

        if(file.exists()){
            file.delete();
            file = new File(filename);
        }

        try {
            outStream = new FileOutputStream(file);

            mSignature.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();

        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
}
