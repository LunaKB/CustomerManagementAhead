package com.bignerdranch.android.customermanagement;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by Chaz-Rae on 9/12/2016.
 * Activity to choose either CustomerListActivity
 * or SessionListActivity.
 */
public class MenuFragment extends Fragment {
    private static final int REQUEST_LOGOUT = 0;
    private static final String DIALOG_LOGOUT = "logout";
    private LinearLayout mSessions;
    private LinearLayout mCustomers;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_menu, container, false);

        mSessions = (LinearLayout)v.findViewById(R.id.menu_button_sessions);
        mSessions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), SessionListActivity.class);
                startActivity(i);
            }
        });

        mCustomers = (LinearLayout)v.findViewById(R.id.menu_button_customers);
        mCustomers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), CustomerListActivity.class);
                startActivity(i);
            }
        });

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_item_log_out:
                FragmentManager fragmentManager = getFragmentManager();
                TextViewDialog dialog = TextViewDialog.newInstance();

                dialog.setTargetFragment(MenuFragment.this, REQUEST_LOGOUT);
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
}
