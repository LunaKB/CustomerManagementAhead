package com.bignerdranch.android.customermanagement;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * Created by Chaz-Rae on 8/24/2016.
 * Fragment for user to choose customer
 * to view.
 *
 *  Uses Picasso for image handling
 *   http://square.github.io/picasso/
 */
public class CustomerListFragment extends Fragment {
    private static final int REQUEST_LOGOUT = 0;
    private static final String DIALOG_LOGOUT = "logout";
    private RecyclerView mCustomerRecyclerView;
    private CustomerAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_customer_list, container, false);

        mCustomerRecyclerView = (RecyclerView)v.findViewById(R.id.customer_recycler_view);
        mCustomerRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return v;
    }

    @Override
    public void onResume(){
        super.onResume();
        updateUI();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_customer_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_item_new_customer:
                Customer customer = new Customer();
                CustomerListManager.get(getActivity()).addCustomer(customer);
                Intent intent = AddCustomerPagerActivity.newIntent(getActivity(), customer.getID());
                startActivity(intent);
                return true;
            case R.id.menu_item_log_out:
                FragmentManager fragmentManager = getFragmentManager();
                TextViewDialog dialog = TextViewDialog.newInstance();

                dialog.setTargetFragment(CustomerListFragment.this, REQUEST_LOGOUT);
                dialog.show(fragmentManager, DIALOG_LOGOUT);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode != Activity.RESULT_OK){
            return;
        }

        if(requestCode == REQUEST_LOGOUT){
            getActivity().finish();
            Intent i = new Intent(getActivity(), LoginActivity.class);
            startActivity(i);
        }
    }

    private void updateUI(){
        CustomerListManager customerListManager = CustomerListManager.get(getActivity());
        List<Customer> customers = customerListManager.getCustomers();

        if(mAdapter == null) {
            mAdapter = new CustomerAdapter(customers);
            mCustomerRecyclerView.setAdapter(mAdapter);
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
            Intent i = AddCustomerPagerActivity.newIntent(getActivity(), mCustomer.getID());
            startActivity(i);
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
