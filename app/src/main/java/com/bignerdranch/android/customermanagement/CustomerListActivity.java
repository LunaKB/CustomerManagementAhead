package com.bignerdranch.android.customermanagement;

import android.support.v4.app.Fragment;

/**
 * Created by Chaz-Rae on 8/24/2016.
 * Activity to show CustomerListFragment
 */
public class CustomerListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment(){
        return new CustomerListFragment();
    }
}
