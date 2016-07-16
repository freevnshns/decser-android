package com.ihs.homeconnect;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ihs.homeconnect.helpers.services;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.File;
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
        Button bLoadFromServer = (Button) findViewById(R.id.bLoadFromServer);
        assert bLoadFromServer != null;
        bLoadFromServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, DownloadMediaActivity.class);
                DownloadMediaActivity.session = session;
                startActivity(intent);
            }
        });
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
            holder.itemView.setTag(position);
            holder.tvServiceName.setText(mServicesList.get(position));
            holder.ivServiceIcon.setImageResource(R.drawable.ic_temp);

        }

        @Override
        public int getItemCount() {
            return mServicesList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView tvServiceName;
            private ImageView ivServiceIcon;

            public ViewHolder(View view) {
                super(view);
                this.tvServiceName = (TextView) view.findViewById(R.id.tvServiceName);
                this.ivServiceIcon = (ImageView) view.findViewById(R.id.ivServiceIcon);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent;
                        PackageManager packageManager = getPackageManager();
                        switch (services.values()[(Integer) v.getTag()]) {
                            case DownloadsManager:
                                DownloadManagerActivity.session = session;
                                intent = new Intent(DashboardActivity.this, DownloadManagerActivity.class);
                                startActivity(intent);
                                break;
                            case Backup:
                                try {
                                    packageManager.getPackageInfo("com.owncloud.android", PackageManager.GET_ACTIVITIES);
                                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.owncloud.android");
                                    try {
                                        session.setPortForwardingL(services.Backup.port + 9000, "127.0.0.1", services.Backup.port);
                                        startActivity(launchIntent);
                                    } catch (JSchException e) {
                                        e.printStackTrace();
                                    }
                                } catch (PackageManager.NameNotFoundException e) {
                                    BroadcastReceiver onComplete = new BroadcastReceiver() {
                                        @Override
                                        public void onReceive(Context context, Intent intent) {
                                            Intent promptInstall = new Intent(Intent.ACTION_VIEW)
                                                    .setDataAndType(Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.getDownloadCacheDirectory().getAbsolutePath()) + "/com.owncloud.android_20000001.apk")),
                                                            "application/vnd.android.package-archive");
                                            startActivity(promptInstall);
                                            unregisterReceiver(this);
                                        }
                                    };
                                    String url = "https://f-droid.org/repo/com.owncloud.android_20000001.apk";
                                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                                    request.setDescription("com.owncloud.android_20000001.apk");
                                    request.setTitle("ownCloud Backup App");
                                    request.setDestinationInExternalPublicDir(Environment.getDownloadCacheDirectory().getAbsolutePath(), "com.owncloud.android_20000001.apk");
                                    DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                                    manager.enqueue(request);
                                    registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                                }
                                break;
                            case HomeBase:
//                                TODO Launch Server Dashboard
//                                intent = new Intent(DashboardActivity.this, DownloadManagerActivity.class);
                                break;
                            case VideoSurveillance:
                                intent = new Intent(DashboardActivity.this, VideoSurveillanceActivity.class);
                                startActivity(intent);
                                break;
                            case Printing:
                                try {
                                    packageManager.getPackageInfo("com.blackspruce.lpd", PackageManager.GET_ACTIVITIES);
                                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.blackspruce.lpd");
                                    try {
                                        session.setPortForwardingL(services.Printing.port + 9000, "127.0.0.1", services.Printing.port);
                                        startActivity(launchIntent);
                                    } catch (JSchException e) {
                                        e.printStackTrace();
                                    }
                                } catch (PackageManager.NameNotFoundException e) {
                                    try {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.blackspruce.lpd")));
                                    } catch (android.content.ActivityNotFoundException a) {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + "com.blackspruce.lpd")));
                                    }
                                }
                                break;
                            default:
                                break;
                        }
                    }
                });
            }
        }
    }
}