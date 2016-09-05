package com.bignerdranch.android.customermanagement;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Chaz-Rae on 8/24/2016.
 * Activity to show CustomerListFragment
 */
public class CustomerListActivity extends SingleFragmentActivity
        implements CustomerListFragment.Callbacks, AddCustomerFragment.Callbacks{
    private static Boolean hasDetail;

    @Override
    protected Fragment createFragment(){
        return new CustomerListFragment();
    }

    @Override
    protected int getLayoutResId(){
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onCustomerSelected(Customer customer){
        if(findViewById(R.id.detail_fragment_container) == null){
            hasDetail = false;
            Intent intent = AddCustomerPagerActivity.newIntent(this, customer.getID());
            startActivity(intent);
        }
        else {
            hasDetail = true;
            Fragment newDetail = AddCustomerFragment.newInstance(customer.getID());

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();
        }
    }

    @Override
    public void onCustomerUpdated(Customer customer){
        CustomerListFragment listFragment = (CustomerListFragment)
                getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);
        listFragment.updateUI();
    }

    public static Boolean getHasDetail(){
        return hasDetail;
    }
}
