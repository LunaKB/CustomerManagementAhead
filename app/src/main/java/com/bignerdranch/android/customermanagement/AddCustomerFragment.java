package com.bignerdranch.android.customermanagement;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;
import java.util.UUID;

/*
 * Created by Chaz-Rae on 8/24/2016.
 * Fragment to view and save or delete customer image,
 * name, billing information, and number of sessions.
 *
 * Uses Picasso for image handling
 *  http://square.github.io/picasso/
 */
public class AddCustomerFragment extends Fragment {
    private static final String ARG_CUSTOMER_ID = "customer_id";
    private static final String DIALOG_IMAGE = "image_dialog";
    private static final int REQUEST_PHOTO = 0;
    private static final int REQUEST_IMAGE_DIALOG = 1;
    private Customer mCustomer;
    private Callbacks mCallbacks;

    /* Text Fields */
    private EditText mCustomerName;
    private EditText mBillingInfo;
    private EditText mSessions;

    /* Image Taking and Saving */
    private ImageView mCustomerImage;
    private int mWidth;
    private int mHeight;
    private ImageButton mImageButton;
    private File mImageFile;

    /**
    * Required interface for hosting activities
    */
    public interface Callbacks{
        void onCustomerUpdated(Customer customer);
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach(){
        super.onDetach();
        mCallbacks = null;
    }

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
        final ViewTreeObserver observer = mCustomerImage.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mWidth = mCustomerImage.getWidth();
                mHeight = mCustomerImage.getHeight();
                Picasso.with(getActivity())
                        .load(mImageFile)
                        .resize(mWidth, mHeight)
                        .into(mCustomerImage);

                mCustomerImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

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

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode != Activity.RESULT_OK){
            return;
        }

        try {
            if (requestCode == REQUEST_PHOTO) {
              Picasso.with(getActivity()).invalidate(mImageFile);
                Picasso.with(getActivity())
                        .load(mImageFile)
                        .resize(mWidth, mHeight)
                        .into(mCustomerImage);
            } else if (requestCode == REQUEST_IMAGE_DIALOG) {
            /* Do nothing */
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_add_customer, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        /* Customer Saving and Deleting */
        switch (item.getItemId()){
            case R.id.menu_item_save_customer:
                mCustomer.setCustomerName(mCustomerName.getText().toString());
                mCustomer.setBillingInfo(mBillingInfo.getText().toString());
                mCustomer.setSessionLimit(Integer.valueOf(mSessions.getText().toString()));
                Toast.makeText(getActivity(), R.string.saved_toast, Toast.LENGTH_SHORT).show();
                updateCustomer();
                return true;
            case R.id.menu_item_delete_customer:
                CustomerListManager.get(getActivity()).deleteCustomer(mCustomer, mImageFile);
                Toast.makeText((getActivity()), R.string.deleted_toast, Toast.LENGTH_SHORT).show();
                if(!CustomerListActivity.getHasDetail()) {
                    getActivity().finish();
                }
                else{
                    onCustomerDeleted(CustomerListFragment.sAdapter, CustomerListFragment.sRecyclerView);
                    updateCustomer();
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .remove(this)
                            .commit();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateCustomer(){
        CustomerListManager.get(getActivity()).updateCustomer(mCustomer);
        mCallbacks.onCustomerUpdated(mCustomer);
    }

    private void onCustomerDeleted(CustomerListFragment.CustomerAdapter adapter, RecyclerView recyclerView){
        CustomerListManager customerListManager = CustomerListManager.get(getActivity());
        List<Customer> customers = customerListManager.getCustomers();

        if(adapter == null) {
            adapter = new CustomerListFragment(). new CustomerAdapter(customers);
            recyclerView.setAdapter(adapter);
        }
        else{
            adapter.setCustomers(customers);
            adapter.notifyDataSetChanged();
        }
    }
}
