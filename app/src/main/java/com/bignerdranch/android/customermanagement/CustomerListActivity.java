package com.bignerdranch.android.customermanagement;

import android.support.v4.app.Fragment;

/**
 * Created by Chaz-Rae on 8/24/2016.
 */
public class CustomerListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment(){
        return new CustomerListFragment();
    }
}
