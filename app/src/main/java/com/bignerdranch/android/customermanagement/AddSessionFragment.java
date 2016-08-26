package com.bignerdranch.android.customermanagement;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Chaz-Rae on 8/24/2016.
 */
public class AddSessionFragment extends Fragment {
    private static final String ARG_SESSION_ID = "session_id";
    private static final String DIALOG_CUSTOMER = "customer";
    private static final String DIALOG_IMAGE = "image_dialog";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final int REQUEST_CUSTOMER = 0;
    private static final int REQUEST_IMAGE_DIALOG = 1;
    private static final int REQUEST_DATE = 2;
    private static final int REQUEST_TIME = 3;
    private Session mSession;
    private Customer mCustomer;

    /* Customer Info */
    private ImageView mCustomerImage;
    private TextView mCustomerName;
    private File mImageFile;
    private Button mChangeCustomer;

    /* Session Info */
    private Button mDate;
    private Button mTime;
    private EditText mDescription;

    public static AddSessionFragment newInstance(UUID sessionId){
        Bundle args = new Bundle();
        args.putSerializable(ARG_SESSION_ID, sessionId);

        AddSessionFragment fragment = new AddSessionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        UUID sessionId = (UUID) getArguments().getSerializable(ARG_SESSION_ID);
        mSession = SessionListManager.get(getActivity()).getSession(sessionId);
        mCustomer = CustomerListManager.get(getActivity()).getCustomer(mSession.getCustomerID());

        if(mCustomer != null) {
            mImageFile = CustomerListManager.get(getActivity()).getPhotoFile(mCustomer);
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        SessionListManager.get(getActivity()).updateSession(mSession);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_add_session, null);

        /* Customer Info */
        mCustomerImage = (ImageView)v.findViewById(R.id.add_session_imageview);
        updatePhotoView();
        mCustomerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                ImageViewDialog dialog = ImageViewDialog.newInstance(
                        mCustomerImage.getDrawable(), mImageFile);

                dialog.setTargetFragment(AddSessionFragment.this, REQUEST_IMAGE_DIALOG);
                dialog.show(fragmentManager,DIALOG_IMAGE);
            }
        });

        mCustomerName = (TextView)v.findViewById(R.id.add_session_customer_name);
        if(mCustomer != null) {
            mCustomerName.setText(mCustomer.getCustomerName());
        }

        mChangeCustomer = (Button)v.findViewById(R.id.add_session_change_customer_button);
        mChangeCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                CustomerListDialog dialog = CustomerListDialog.newInstance();

                dialog.setTargetFragment(AddSessionFragment.this,REQUEST_CUSTOMER);
                dialog.show(fragmentManager, DIALOG_CUSTOMER);
            }
        });

        /* Session Info */
        mDate = (Button)v.findViewById(R.id.add_session_date_button);
        updateDate();
        mDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                DatePickerDialog dialog = DatePickerDialog.newInstance(mSession.getDate());

                dialog.setTargetFragment(AddSessionFragment.this, REQUEST_DATE);
                dialog.show(fragmentManager, DIALOG_DATE);
            }
        });

        mTime = (Button)v.findViewById(R.id.add_session_time_button);
        updateTime();
        mTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                TimePickerDialog dialog = TimePickerDialog.newInstance(mSession.getDate());

                dialog.setTargetFragment(AddSessionFragment.this, REQUEST_TIME);
                dialog.show(fragmentManager, DIALOG_TIME);
            }
        });

        mDescription = (EditText)v.findViewById(R.id.add_session_description);
        mDescription.setText(mSession.getDescription());

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode != Activity.RESULT_OK){
            return;
        }
        if(requestCode == REQUEST_CUSTOMER){
            int noCustomer = data.getIntExtra(CustomerListDialog.EXTRA_NO_CUSTOMER, 1);
            UUID customerId = (UUID)data.getSerializableExtra(CustomerListDialog.EXTRA_CUSTOMER);

            if(noCustomer == 0){
                mCustomer = CustomerListManager.get(getActivity()).getCustomer(customerId);
                mCustomerName.setText(mCustomer.getCustomerName());
                mImageFile = CustomerListManager.get(getActivity()).getPhotoFile(mCustomer);
                updatePhotoView();
            }
        }
        else if(requestCode == REQUEST_IMAGE_DIALOG){

        }
        else if(requestCode == REQUEST_DATE){
            Date date = (Date) data.getSerializableExtra(DatePickerDialog.EXTRA_DATE);
            mSession.setDay(date.getDate(), date.getMonth(), date.getYear());
            updateDate();
        }
        else if(requestCode == REQUEST_TIME){
            Date date = (Date) data.getSerializableExtra(TimePickerDialog.EXTRA_TIME);
            mSession.setTime(date.getHours(), date.getMinutes());
            updateTime();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_add_session, menu);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        /* Customer Saving and Deleting */
        switch (item.getItemId()){
            case R.id.menu_item_save_session:
                mSession.setDescription(mDescription.getText().toString());
                if(mCustomer != null) {
                    mSession.setCustomerID(mCustomer.getID());
                }
                Toast.makeText(getActivity(), R.string.saved_toast, Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_item_delete_session:
                SessionListManager.get(getActivity()).deleteSession(mSession);
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

    private void updateDate() {
        Date date = mSession.getDate();
        String formatDate = DateFormat.getDateFormat(getActivity()).format(date);
        mDate.setText(formatDate);
    }

    private void updateTime(){
        Date date = mSession.getDate();
        String formatTime = date.getHours() + ":" + date.getMinutes();
        mTime.setText(formatTime);
    }
}
