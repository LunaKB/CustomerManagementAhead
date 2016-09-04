package com.bignerdranch.android.customermanagement;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;
import java.util.UUID;

/**
 * Created by Chaz-Rae on 8/24/2016.
 * Dialog for user to choose a customer
 * from the list of existing customers.
 * Handling for case of no customer chosen included.
 *
 * Uses Picasso for image handling
 *  http://square.github.io/picasso/
 */
public class CustomerListDialog extends DialogFragment {
    public static final String EXTRA_CUSTOMER =
            "com.bignerdranch.android.customermanagement.customer";
    public static final String EXTRA_NO_CUSTOMER =
            "com.bignerdranch.android.customermanagement.nocustomer";
    private RecyclerView mRecyclerView;
    private CustomerAdapter mAdapter;
    private Customer mChosenCustomer = null;

    public static CustomerListDialog newInstance(){
        CustomerListDialog fragment = new CustomerListDialog();
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_customer_list, null);
        mRecyclerView = (RecyclerView)v.findViewById(R.id.dialog_customer_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.customer_list_dialog)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_OK, mChosenCustomer);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }

    private void sendResult(int resultCode, Customer customer){
        if(getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        if(customer == null){
            intent.putExtra(EXTRA_NO_CUSTOMER, 1);
            intent.putExtra(EXTRA_CUSTOMER, UUID.randomUUID());        }
        else {
            intent.putExtra(EXTRA_NO_CUSTOMER, 0);
            intent.putExtra(EXTRA_CUSTOMER, customer.getID());
        }
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }

    private void updateUI(){
        CustomerListManager customerListManager = CustomerListManager.get(getActivity());
        List<Customer> customers = customerListManager.getCustomers();

        if(mAdapter == null) {
            mAdapter = new CustomerAdapter(customers);
            mRecyclerView.setAdapter(mAdapter);
        }
        else{
            mAdapter.setCustomers(customers);
            mAdapter.notifyDataSetChanged();
        }
    }

    private class CustomerHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private Customer mCustomer;
        private ImageView mCustomerImage;
        private TextView mCustomerName;
        private TextView mCustomerSessions;

        public CustomerHolder(View itemView){
            super(itemView);
            itemView.setOnClickListener(this);

            mCustomerImage = (ImageView)itemView.findViewById(R.id.list_item_customer_imageview);
            mCustomerName = (TextView)itemView.findViewById(R.id.list_item_customer_name);
            mCustomerSessions = (TextView)itemView.findViewById(R.id.list_item_customer_session_number);
        }

        public void bindCustomer(Customer customer){
            mCustomer = customer;

            final File f = CustomerListManager.get(getActivity()).getPhotoFile(mCustomer);
            if(f == null || !f.exists()){
                mCustomerImage.setImageDrawable(null);
            }
            else {
                final ViewTreeObserver observer = mCustomerImage.getViewTreeObserver();
                observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        int width = mCustomerImage.getWidth();
                        int height = mCustomerImage.getHeight();
                        Picasso.with(getActivity())
                                .load(f)
                                .resize(width, height)
                                .into(mCustomerImage);
                        mCustomerImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
                observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        int width = mCustomerImage.getWidth();
                        int height = mCustomerImage.getHeight();
                        Picasso.with(getActivity())
                                .load(f)
                                .resize(width, height)
                                .into(mCustomerImage);
                        mCustomerImage.getViewTreeObserver().removeOnPreDrawListener(this);
                        return true;
                    }
                });
            }

            mCustomerName.setText(mCustomer.getCustomerName());
            mCustomerSessions.setText(Integer.toString(mCustomer.getSessionLimit()));
        }

        @Override
        public void onClick(View v){
            mChosenCustomer = mCustomer;
            Toast.makeText(getActivity(), mCustomer.getCustomerName() + " chosen", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private class CustomerAdapter extends RecyclerView.Adapter<CustomerHolder>{
        private List<Customer> mCustomers;

        public CustomerAdapter(List<Customer> customers){
            mCustomers = customers;
        }

        @Override
        public CustomerHolder onCreateViewHolder(ViewGroup parent, int viewType){
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View v = inflater.inflate(R.layout.list_item_customer, parent, false);
            return new CustomerHolder(v);
        }

        @Override
        public void onBindViewHolder(CustomerHolder holder, int position){
            Customer customer = mCustomers.get(position);
            holder.bindCustomer(customer);
        }

        @Override
        public int getItemCount(){
            return mCustomers.size();
        }

        public void setCustomers(List<Customer> customers){
            mCustomers = customers;
        }
    }
}
