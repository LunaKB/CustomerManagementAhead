package com.bignerdranch.android.customermanagement;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.List;
import java.util.UUID;

/**
 * Created by Chaz-Rae on 8/24/2016.
 * Activity to slide left or right to view
 * other AddSessionFragments
 *
 * Coded from Android Programming: The Big Nerd Ranch Guide 2nd Edition
 */
public class AddSessionPagerActivity extends AppCompatActivity {
    private static final String EXTRA_SESSION_ID =
            "com.bignerdranch.android.customermanagement.session_id";

    private ViewPager mViewPager;
    private List<Session> mSessions;

    public static Intent newIntent(Context packageContext, UUID sessionId){
        Intent intent = new Intent(packageContext, AddSessionPagerActivity.class);
        intent.putExtra(EXTRA_SESSION_ID, sessionId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_session_pager);
        UUID sessionId = (UUID) getIntent().getSerializableExtra(EXTRA_SESSION_ID);

        mViewPager = (ViewPager)findViewById(R.id.activity_session_pager_viewpager);
        mSessions = SessionListManager.get(this).getSessions();

        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Session session = mSessions.get(position);
                return AddSessionFragment.newInstance(session.getId());
            }

            @Override
            public int getCount() {
                return mSessions.size();
            }
        });

        for(int i = 0; i < mSessions.size(); i++){
            if(mSessions.get(i).getId().equals(sessionId)){
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }
}
