package com.ihs.homeconnect;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ihs.homeconnect.helpers.services;
import com.jcraft.jsch.Session;

import java.util.ArrayList;

public class DashboardActivity extends AppCompatActivity {
    public static Session session = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
        RecyclerView mRecyclerView;
        RecyclerView.Adapter mAdapter;
        RecyclerView.LayoutManager mLayoutManager;

        mRecyclerView = (RecyclerView) findViewById(R.id.rvDashboard);
        mLayoutManager = new GridLayoutManager(this, 2);

        assert mRecyclerView != null;
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new servicesAdapter();
        mRecyclerView.setAdapter(mAdapter);

//        final dbHandler dbInstance;
//        dbInstance = new dbHandler(this, null);
//        final ArrayList<String> installedServices = dbInstance.getServices(1);
//        ArrayAdapter<String> dashAdapter = new ArrayAdapter<>(this, R.layout.activity_dashboard, R.id.tvDashTemp, installedServices);
//        final ListView listView = (ListView) findViewById(R.id.lvDashOptions);
//        listView.setAdapter(dashAdapter);
//        listView.setClickable(true);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                for (services ser : services.values()) {
//                    if (ser.toString().equals(installedServices.get(position))) {
//                        try {
//                            session.setPortForwardingL(ser.port, "127.0.0.1", ser.port);
//                            launchService(9000 + ser.port, ser.toString());
//                        } catch (JSchException e) {
//                            if (e.getMessage().startsWith("PortForwardingL"))
//                                launchService(9000 + ser.port, ser.toString());
//                            else {
//                                loggingHandler loggingHandler = new loggingHandler();
//                                loggingHandler.addLog(e.getMessage());
//                            }
//                        }
//                    }
//                }
//            }
//        });
//    }
//
//        public void launchService ( int port, String type){
//            Intent intent;
//            if (type.equals("Torrent")) {
//                intent = new Intent(this, DownloadManagerActivity.class);
//            } else {
//                intent = new Intent(Intent.ACTION_VIEW);
//                String url = "http://127.0.0.1:" + port;
//                intent.setData(Uri.parse(url));
//            }
//            startActivity(intent);
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dash, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.serverMediaDownload) {
            Intent intent = new Intent(this, DownloadMediaActivity.class);
            DownloadMediaActivity.session = session;
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (session.isConnected())
            session.disconnect();
        finish();
    }

    private class servicesAdapter extends RecyclerView.Adapter<servicesAdapter.ViewHolder> {
        private ArrayList<String> mServicesList = new ArrayList<>();


        public servicesAdapter() {
            for (services aService : services.values()) {
                mServicesList.add(aService.toString());
            }
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_column_layout, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.tvServiceName.setText(mServicesList.get(position));
        }

        @Override
        public int getItemCount() {
            return mServicesList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView tvServiceName;

            public ViewHolder(View view) {
                super(view);
                this.tvServiceName = (TextView) view.findViewById(R.id.tvServiceName);
            }
        }
    }
}