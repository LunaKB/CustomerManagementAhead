package com.bignerdranch.android.customermanagement;

import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * Created by Chaz-Rae on 8/24/2016.
 * Activity to show SessionListFragment
 */
public class SessionListActivity extends SingleFragmentActivity
        implements SessionListFragment.Callbacks, AddSessionFragment.Callbacks{
    private static Boolean hasDetail;

    @Override
    protected Fragment createFragment(){
        return new SessionListFragment();
    }

    @Override
    protected int getLayoutResId(){
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onSessionSelected(Session session){
        if(findViewById(R.id.detail_fragment_container) == null){
            hasDetail = false;
            Intent intent = AddSessionPagerActivity.newIntent(this, session.getId());
            startActivity(intent);
        }
        else {
            hasDetail = true;
            Fragment newDetail = AddSessionFragment.newInstance(session.getId());

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();
        }
    }

    @Override
    public void onSessionUpdated(Session session){
        SessionListFragment listFragment = (SessionListFragment)
                getSupportFragmentManager()
                        .findFragmentById(R.id.fragment_container);
        listFragment.updateUI();
    }

    public static Boolean getHasDetail(){
        return hasDetail;
    }
}
