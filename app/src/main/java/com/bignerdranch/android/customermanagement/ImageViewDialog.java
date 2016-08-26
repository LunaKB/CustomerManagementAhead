package com.bignerdranch.android.customermanagement;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

/**
 * Created by Chaz-Rae on 8/24/2016.
 */
public class ImageViewDialog extends DialogFragment{
    private static final String ARG_BOOLEAN = "boolean";
    private static final String ARG_FILE = "file";
    private ImageView mImageView;

    public static ImageViewDialog newInstance(Drawable drawable, File path){
        Bundle args = new Bundle();
        if(drawable == null){
            args.putBoolean(ARG_BOOLEAN, true);
        }
        else{
            args.putBoolean(ARG_BOOLEAN, false);
        }

        args.putSerializable(ARG_FILE, path);

        ImageViewDialog fragment = new ImageViewDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        File path = (File)getArguments().getSerializable(ARG_FILE);
        Boolean bool = (Boolean)getArguments().getBoolean(ARG_BOOLEAN);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_imageview, null);
        mImageView = (ImageView)v.findViewById(R.id.dialog_imageview);

        if(bool){
            mImageView.setImageDrawable(null);
            return new AlertDialog.Builder(getActivity())
                    .setView(v)
                    .setTitle(R.string.no_image)
                    .setPositiveButton(android.R.string.ok, null)
                    .create();
        }
        else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(path.getPath(), getActivity());
            mImageView.setImageBitmap(bitmap);
        }

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.yes_image)
                .setPositiveButton(android.R.string.ok, null)
                .create();
    }
}
