package com.bignerdranch.android.customermanagement;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import java.util.List;
import java.util.UUID;

/**
 * Created by Chaz-Rae on 8/24/2016.
 * Activity to slide left or right to view
 * other AddCustomerFragments
 *
 * Coded from Android Programming: The Big Nerd Ranch Guide 2nd Edition
 */
public class AddCustomerPagerActivity extends AppCompatActivity
        implements AddCustomerFragment.Callbacks{
    private static final String EXTRA_CUSTOMER_ID =
            "com.bignerdranch.android.customermanagement.customer_id";

    private ViewPager mViewPager;
    private List<Customer> mCustomers;

    @Override
    public void onCustomerUpdated(Customer customer){

    }

    public static Intent newIntent(Context packageContext, UUID customerId){
        Intent intent = new Intent(packageContext, AddCustomerPagerActivity.class);
        intent.putExtra(EXTRA_CUSTOMER_ID, customerId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpager);
        UUID customerId = (UUID) getIntent().getSerializableExtra(EXTRA_CUSTOMER_ID);

        mViewPager = (ViewPager)findViewById(R.id.activity_viewpager);
        mCustomers = CustomerListManager.get(this).getCustomers();

        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Customer customer = mCustomers.get(position);
                return AddCustomerFragment.newInstance(customer.getID());
            }

            @Override
            public int getCount() {
                return mCustomers.size();
            }
        });

        for(int i = 0; i < mCustomers.size(); i++){
            if(mCustomers.get(i).getID().equals(customerId)){
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }
}
