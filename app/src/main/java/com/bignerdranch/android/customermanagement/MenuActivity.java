package com.bignerdranch.android.customermanagement;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by Chaz-Rae on 8/24/2016.
 * Activity to choose either CustomerListActivity
 * or SessionListActivity.
 */
public class MenuActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new MenuFragment();
    }
}
