package com.bignerdranch.android.customermanagement;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * Created by Chaz-Rae on 8/24/2016.
 */
public class MenuActivity extends AppCompatActivity {
    private LinearLayout mSessions;
    private LinearLayout mCustomers;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        mSessions = (LinearLayout) findViewById(R.id.menu_button_sessions);
        mSessions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MenuActivity.this, SessionListActivity.class);
                startActivity(i);
            }
        });

        mCustomers = (LinearLayout) findViewById(R.id.menu_button_customers);
        mCustomers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MenuActivity.this, CustomerListActivity.class);
                startActivity(i);
            }
        });
    }
}
