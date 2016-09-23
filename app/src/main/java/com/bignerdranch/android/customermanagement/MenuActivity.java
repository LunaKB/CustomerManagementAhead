package com.bignerdranch.android.customermanagement;

import android.support.v4.app.Fragment;

/**
 * Created by Chaz-Rae on 8/24/2016.
 * Activity to choose either CustomerListActivity
 * or SessionListActivity.
 */
public class MenuActivity extends SingleFragmentActivity{

    @Override
    protected Fragment createFragment() {
        return new MenuFragment();
    }
}