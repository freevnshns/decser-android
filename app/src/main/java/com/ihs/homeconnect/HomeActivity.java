package com.ihs.homeconnect;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ihs.homeconnect.helpers.DbHandler;
import com.ihs.homeconnect.helpers.VerticalSpaceDecorationHelper;
import com.ihs.homeconnect.helpers.connectionHandler;
import com.jcraft.jsch.JSchException;

public class HomeActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.splashScreen);
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_home);

        DbHandler dbInstance = new DbHandler(this, null);
        if (dbInstance.getUserRegistration() == 0) {
            Intent intent = new Intent(this, RegistrationActivity.class);
            startActivity(intent);
            finish();
        }

        RecyclerView mRecyclerView;
        RecyclerView.Adapter mAdapter;
        RecyclerView.LayoutManager mLayoutManager;


        mRecyclerView = (RecyclerView) findViewById(R.id.rvContactList);
        mLayoutManager = new LinearLayoutManager(this);

        assert mRecyclerView != null;
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new ContactListAdapter(HomeActivity.this);

        mRecyclerView.setAdapter(mAdapter);
        RelativeLayout rlEmpty = (RelativeLayout) findViewById(R.id.rlEmptyHomeView);
        assert rlEmpty != null;
        if (mAdapter.getItemCount() == 0) {
            mRecyclerView.setVisibility(View.GONE);
            rlEmpty.setVisibility(View.VISIBLE);
        } else {
            rlEmpty.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            mRecyclerView.addItemDecoration(new VerticalSpaceDecorationHelper(this));
        }
        FloatingActionButton floatingActionUploadButton = (FloatingActionButton) findViewById(R.id.fabAddContact);
        assert floatingActionUploadButton != null;
        floatingActionUploadButton.setImageResource(R.drawable.ic_add);
        floatingActionUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AddContactActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        if (id == R.id.action_request_key) {
//            Intent intent = new Intent(this, RequestKeyActivity.class);
//            startActivity(intent);
//        }
//        return super.onOptionsItemSelected(item);
//    }

    private class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ViewHolder> {
        String[] mContactNameList;
        String[] mContactHostnameList;
        Context mContext;


        ContactListAdapter(Context mContext) {
            this.mContext = mContext;
            DbHandler dbInstance = new DbHandler(mContext, null);
            this.mContactNameList = dbInstance.getContactNameList();
            this.mContactHostnameList = dbInstance.getHostnameArray();
        }

        void updateAdapter() {
            DbHandler dbInstance = new DbHandler(mContext, null);
            this.mContactNameList = dbInstance.getContactNameList();
            this.mContactHostnameList = dbInstance.getHostnameArray();
        }

        @Override
        public ContactListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_row_contact, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.tvContactName.setText(mContactNameList[holder.getAdapterPosition()]);
            holder.tvContactHostname.setText(mContactHostnameList[holder.getAdapterPosition()]);
            holder.itemView.setTag(R.id.TAG_CONTACT_HOSTNAME, mContactHostnameList[holder.getAdapterPosition()]);
            holder.itemView.setTag(R.id.TAG_CONTACT_POSITION, holder.getAdapterPosition());
        }

        @Override
        public int getItemCount() {
            return mContactNameList.length;
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
            TextView tvContactName;
            TextView tvContactHostname;

            ViewHolder(View view) {
                super(view);
                this.tvContactName = (TextView) view.findViewById(R.id.tvContactName);
                this.tvContactHostname = (TextView) view.findViewById(R.id.tvContactHostname);
                view.setOnClickListener(this);
                view.setOnLongClickListener(this);
            }

            @Override
            public void onClick(View v) {
                final String tempContactHostname = v.getTag(R.id.TAG_CONTACT_HOSTNAME).toString();
                try {
                    String access_lvl = new DbHandler(mContext, null).getAccessType(tempContactHostname);
                    connectionHandler connectionHandler = new connectionHandler(tempContactHostname, access_lvl,
                            mContext);
                    connectionHandler.execute();
                } catch (JSchException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public boolean onLongClick(View v) {
                final String tempContactHostname = v.getTag(R.id.TAG_CONTACT_HOSTNAME).toString();
                final int tempContactPosition = (int) v.getTag(R.id.TAG_CONTACT_POSITION);
                final DbHandler dbInstance = new DbHandler(mContext, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Delete Contact").setMessage("Are you sure you want to delete this contact ?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (dbInstance.deleteContact(tempContactHostname)) {
                            updateAdapter();
                            notifyItemRemoved(tempContactPosition);
                            notifyItemRangeChanged(tempContactPosition, getItemCount());
                        } else
                            Toast.makeText(mContext, "0", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
        }
    }
}
