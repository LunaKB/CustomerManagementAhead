package com.bignerdranch.android.customermanagement;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.UUID;

/**
 * Created by Chaz-Rae on 8/24/2016.
 */
public class AddCustomerFragment extends Fragment {
    private static final String ARG_CUSTOMER_ID = "customer_id";
    private static final String DIALOG_IMAGE = "image_dialog";
    private static final int REQUEST_PHOTO = 0;
    private static final int REQUEST_IMAGE_DIALOG = 1;
    private Customer mCustomer;

    /* Text Fields */
    private EditText mCustomerName;
    private EditText mBillingInfo;
    private EditText mSessions;

    /* Image Taking and Saving */
    private ImageView mCustomerImage;
    private ImageButton mImageButton;
    private File mImageFile;

    /* Customer Saving and Deleting */
    private Button mSaveCustomer;
    private Button mDeleteCustomer;

    public static AddCustomerFragment newInstance(UUID customerId){
        Bundle args = new Bundle();
        args.putSerializable(ARG_CUSTOMER_ID, customerId);

        AddCustomerFragment fragment = new AddCustomerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        UUID customerId = (UUID) getArguments().getSerializable(ARG_CUSTOMER_ID);
        mCustomer = CustomerListManager.get(getActivity()).getCustomer(customerId);
        mImageFile = CustomerListManager.get(getActivity()).getPhotoFile(mCustomer);
    }

    @Override
    public void onPause(){
        super.onPause();
        CustomerListManager.get(getActivity()).updateCustomer(mCustomer);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_add_customer, container, false);
        PackageManager packageManager = getActivity().getPackageManager();

        /* Text Fields */
        mCustomerName = (EditText)v.findViewById(R.id.add_customer_name);
        mCustomerName.setText(mCustomer.getCustomerName());

        mBillingInfo = (EditText)v.findViewById(R.id.add_customer_billinginfo);
        mBillingInfo.setText(mCustomer.getBillingInfo());

        mSessions = (EditText) v.findViewById(R.id.add_customer_session_number);
        mSessions.setText(Integer.toString(mCustomer.getSessionLimit()));

        /* Image Taking and Saving */
        mImageButton = (ImageButton)v.findViewById(R.id.add_customer_imagebutton);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        boolean canTakePhoto =
                mImageFile != null && captureImage.resolveActivity(packageManager) != null;
        mImageButton.setEnabled(canTakePhoto);

        if(canTakePhoto){
            Uri uri = Uri.fromFile(mImageFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }

        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });

        mCustomerImage = (ImageView)v.findViewById(R.id.add_customer_imageview);
        updatePhotoView();
        mCustomerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                ImageViewDialog dialog = ImageViewDialog.newInstance(
                        mCustomerImage.getDrawable(), mImageFile);

                dialog.setTargetFragment(AddCustomerFragment.this, REQUEST_IMAGE_DIALOG);
                dialog.show(fragmentManager,DIALOG_IMAGE);
            }
        });

/*        mSaveCustomer = (Button)v.findViewById(R.id.add_customer_save_button);
        mSaveCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCustomer.setCustomerName(mCustomerName.getText().toString());
                mCustomer.setBillingInfo(mBillingInfo.getText().toString());
                mCustomer.setSessionLimit(Integer.valueOf(mSessions.getText().toString()));
                Toast.makeText(getActivity(), R.string.saved_toast, Toast.LENGTH_SHORT).show();
            }
        });

        mDeleteCustomer = (Button)v.findViewById(R.id.add_customer_delete_button); */

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode != Activity.RESULT_OK){
            return;
        }

        if(requestCode == REQUEST_PHOTO){
            updatePhotoView();
        }
        else if(requestCode == REQUEST_IMAGE_DIALOG){

        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_add_customer, menu);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        /* Customer Saving and Deleting */
        switch (item.getItemId()){
            case R.id.menu_item_save_customer:
                mCustomer.setCustomerName(mCustomerName.getText().toString());
                mCustomer.setBillingInfo(mBillingInfo.getText().toString());
                mCustomer.setSessionLimit(Integer.valueOf(mSessions.getText().toString()));
                Toast.makeText(getActivity(), R.string.saved_toast, Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_item_delete_customer:
                CustomerListManager.get(getActivity()).deleteCustomer(mCustomer, mImageFile);
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updatePhotoView(){
        if(mImageFile == null || !mImageFile.exists()){
            mCustomerImage.setImageDrawable(null);
        }
        else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mImageFile.getPath(), getActivity());
            mCustomerImage.setImageBitmap(bitmap);
        }
    }
}