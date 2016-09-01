package com.bignerdranch.android.customermanagement;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
    private static final String DIALOG_SIG = "DialogSignature";
    private static final int REQUEST_CUSTOMER = 0;
    private static final int REQUEST_IMAGE_DIALOG = 1;
    private static final int REQUEST_DATE = 2;
    private static final int REQUEST_TIME = 3;
    private static final int REQUEST_SIG = 4;
    private Session mSession;
    private Customer mCustomer;

    /* Customer Info */
    private ImageView mCustomerImage;
    private int mWidth;
    private int mHeight;
    private TextView mCustomerName;
    private File mImageFile;
    private Button mChangeCustomer;

    /* Session Info */
    private Button mDate;
    private Date InstanceDate;
    private Button mTime;
    private EditText mDescription;

    /* Complete Session */
    private Button mCompleteSession;
    private File mSigFile;
    private ImageView mSignature;
    private int mSigWidth;
    private int mSigHeight;
    private Button mPayment;

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
        InstanceDate = new Date(mSession.getDate().getTime());
        mSigFile = SessionListManager.get(getActivity()).getPhotoFile(mSession);

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
        final ViewTreeObserver observer = mCustomerImage.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mWidth = mCustomerImage.getWidth();
                mHeight = mCustomerImage.getHeight();
                updatePhotoView(mWidth, mHeight);
                mCustomerImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

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
                DatePickerDialog dialog = DatePickerDialog.newInstance(InstanceDate);

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
                TimePickerDialog dialog = TimePickerDialog.newInstance(InstanceDate);

                dialog.setTargetFragment(AddSessionFragment.this, REQUEST_TIME);
                dialog.show(fragmentManager, DIALOG_TIME);
            }
        });

        mDescription = (EditText)v.findViewById(R.id.add_session_description);
        mDescription.setText(mSession.getDescription());

        /* Complete Session */
        mCompleteSession = (Button)v.findViewById(R.id.add_session_complete_button);
        mCompleteSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSignature.setVisibility(View.GONE);
                mPayment.setVisibility(View.GONE);

                FragmentManager fragmentManager = getFragmentManager();
                SessionCompleteDialog dialog = SessionCompleteDialog.newInstance(mSession.getId());

                dialog.setTargetFragment(AddSessionFragment.this, REQUEST_SIG);
                dialog.show(fragmentManager, DIALOG_SIG);
            }
        });

        mSignature = (ImageView)v.findViewById(R.id.add_session_signature_imageview);

        mPayment = (Button)v.findViewById(R.id.session_complete_submit_payment_button);
        mPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText(getSessionReport())
                        .setSubject(getString(R.string.session_report_subject))
                        .setChooserTitle(R.string.send_report)
                        .createChooserIntent();
                startActivity(i);
            }
        });

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
                updatePhotoView(mWidth, mHeight);
            }
        }
        else if(requestCode == REQUEST_IMAGE_DIALOG){}
        else if(requestCode == REQUEST_DATE){
            Date date = (Date) data.getSerializableExtra(DatePickerDialog.EXTRA_DATE);
            InstanceDate.setDate(date.getDate());
            InstanceDate.setMonth(date.getMonth());
            InstanceDate.setYear(date.getYear());
            updateDate();
        }
        else if(requestCode == REQUEST_TIME){
            Date date = (Date) data.getSerializableExtra(TimePickerDialog.EXTRA_TIME);
            InstanceDate.setHours(date.getHours());
            InstanceDate.setMinutes(date.getMinutes());
            updateTime();
        }
        else if(requestCode == REQUEST_SIG){
            int success = (int)data.getIntExtra(SessionCompleteDialog.EXTRA_SUCCESS, 0);
            if(success == 0){
                mSignature.setVisibility(View.GONE);
                mPayment.setVisibility(View.GONE);
            }
            else if(success == 1){
                mSignature.setVisibility(View.VISIBLE);
                final ViewTreeObserver sigObserver = mSignature.getViewTreeObserver();
                sigObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mSigWidth = mSignature.getWidth();
                        mSigHeight = mSignature.getHeight();
                        mSigFile = SessionListManager.get(getActivity()).getPhotoFile(mSession);
                        updateSignatureView(mSigWidth, mSigHeight);
                        mSignature.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_add_session, menu);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        /* Session Saving and Deleting */
        switch (item.getItemId()){
            case R.id.menu_item_save_session:
                mSession.setDescription(mDescription.getText().toString());
                if(mCustomer != null) {
                    mSession.setCustomerID(mCustomer.getID());
                }
                mSession.setDate(InstanceDate);
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

    private void updatePhotoView(int width, int height){
        if(mImageFile == null || !mImageFile.exists()){
            mCustomerImage.setImageDrawable(null);
        }
        else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mImageFile.getPath(), width, height);
            mCustomerImage.setImageBitmap(bitmap);
        }
    }

    private void updateSignatureView(int width, int height){
        Bitmap bitmap = PictureUtils.getScaledBitmap(mSigFile.getPath(), width, height);
        mSignature.setImageBitmap(bitmap);
        mPayment.setVisibility(View.VISIBLE);
    }

    private void updateDate() {
        String dateFormat = "EEE, MMM dd yyyy";
        String formatDate = DateFormat.format(dateFormat, InstanceDate).toString();
        String date = getString(
                R.string.date_button,
                formatDate
        );
        mDate.setText(date);
    }

    private void updateTime(){
        String hour = "";
        String timeOfDay = "";
        if(InstanceDate.getHours() == 0){
            hour = "12";
            timeOfDay = "AM";
        }
        else if(InstanceDate.getHours() == 12){
            hour = "12";
            timeOfDay = "PM";
        }
        else if(InstanceDate.getHours() > 12){
            hour = Integer.toString(InstanceDate.getHours() - 12);
            timeOfDay = "PM";
        }
        else {
            hour = Integer.toString(InstanceDate.getHours());
            timeOfDay = "AM";
        }
        String formatTime = hour + ":" + InstanceDate.getMinutes() + " " + timeOfDay;

        String time = getString(
                R.string.time_button,
                formatTime
        );
        mTime.setText(time);
    }

    private String getSessionReport(){

        String dateFormat = "EEE, MMM dd yyyy";
        String dateString = DateFormat.format(dateFormat, mSession.getDate()).toString();

        String report = getString(
                R.string.session_report,
                mCustomer.getCustomerName(),
                mCustomer.getBillingInfo(),
                dateString,
                mDescription.getText().toString());

        return report;
    }
}
