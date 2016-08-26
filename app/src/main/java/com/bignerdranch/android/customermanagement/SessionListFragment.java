package com.bignerdranch.android.customermanagement;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

/**
 * Created by Chaz-Rae on 8/24/2016.
 */
public class SessionListFragment extends Fragment {
    private RecyclerView mSessionRecyclerView;
    private SessionAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_session_list, container, false);

        mSessionRecyclerView = (RecyclerView)v.findViewById(R.id.session_recycler_view);
        mSessionRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return v;
    }

    @Override
    public void onResume(){
        super.onResume();
        updateUI();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_session_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_item_new_session:
                Session session = new Session();
                SessionListManager.get(getActivity()).addSession(session);
                Intent intent = AddSessionPagerActivity.newIntent(getActivity(), session.getId());
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateUI(){
        SessionListManager sessionListManager = SessionListManager.get(getActivity());
        List<Session> sessions = sessionListManager.getSessions();

        if(mAdapter == null) {
            mAdapter = new SessionAdapter(sessions);
            mSessionRecyclerView.setAdapter(mAdapter);
        }
        else{
            mAdapter.setSessions(sessions);
            mAdapter.notifyDataSetChanged();
        }
    }

    private class SessionHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private Session mSession;
        private ImageView mCustomerImage;
        private TextView mCustomerName;
        private TextView mSessionDate;

        public SessionHolder(View itemView){
            super(itemView);
            itemView.setOnClickListener(this);

            mCustomerImage = (ImageView)itemView.findViewById(R.id.list_item_session_imageview);
            mCustomerName = (TextView)itemView.findViewById(R.id.list_item_session_name);
            mSessionDate = (TextView)itemView.findViewById(R.id.list_item_session_timestamp);
        }

        public void bindSession(Session session){
            mSession = session;
            Customer customer = CustomerListManager.get(getActivity()).getCustomer(mSession.getCustomerID());

            if(customer != null) {
                File f = CustomerListManager.get(getActivity()).getPhotoFile(customer);
                if (f == null || !f.exists()) {
                    mCustomerImage.setImageDrawable(null);
                } else {
                    Bitmap bitmap = PictureUtils.getScaledBitmap(f.getPath(), getActivity());
                    mCustomerImage.setImageBitmap(bitmap);
                }

                mCustomerName.setText(CustomerListManager.get(getActivity()).getCustomer(mSession.getCustomerID()).getCustomerName());
            }
            mSessionDate.setText(mSession.getDate().toString());
        }

        @Override
        public void onClick(View v){
            Intent i = AddSessionPagerActivity.newIntent(getActivity(), mSession.getId());
            startActivity(i);
        }
    }

    private class SessionAdapter extends RecyclerView.Adapter<SessionHolder>{
        private List<Session> mSessions;

        public SessionAdapter(List<Session> sessions){
            mSessions = sessions;
        }

        @Override
        public SessionHolder onCreateViewHolder(ViewGroup parent, int viewType){
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View v = inflater.inflate(R.layout.list_item_session, parent, false);
            return new SessionHolder(v);
        }

        @Override
        public void onBindViewHolder(SessionHolder holder, int position){
            Session session = mSessions.get(position);
            holder.bindSession(session);
        }

        @Override
        public int getItemCount(){
            return mSessions.size();
        }

        public void setSessions(List<Session> sessions){
            mSessions = sessions;
        }
    }
}
