package com.ihs.homeconnect;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ihs.homeconnect.helpers.services;
import com.ihs.homeconnect.helpers.verticalSpaceDecorationHelper;
import com.ihs.homeconnect.helpers.xmppHandler;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.File;
import java.util.ArrayList;

public class DashboardActivity extends AppCompatActivity {
    public static Session session = null;
    public static String connected_hostname = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (session == null) {
            onBackPressed();
        }
        setContentView(R.layout.activity_dashboard);
        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
        RecyclerView mRecyclerView;
        RecyclerView.Adapter mAdapter;
        RecyclerView.LayoutManager mLayoutManager;

        mRecyclerView = (RecyclerView) findViewById(R.id.rvDashboard);
        mLayoutManager = new LinearLayoutManager(this);

        assert mRecyclerView != null;
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new servicesAdapter();
        mRecyclerView.setAdapter(mAdapter);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            mRecyclerView.addItemDecoration(new verticalSpaceDecorationHelper(this));
        }
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

        if (id == R.id.share_key) {
            Intent intent = new Intent(this, ShareKeyActivity.class);
            ShareKeyActivity.session = session;
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
        private ArrayList<String> mServiceNameList = new ArrayList<>();
        private ArrayList<Drawable> mServiceIconList = new ArrayList<>();


        public servicesAdapter() {
            for (services aService : services.values()) {
                mServiceNameList.add(aService.name);
                try {
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                        mServiceIconList.add(getDrawable(getResources().getIdentifier("ic_" + aService.toString(), "drawable", getPackageName())));
                    } else {
                        //noinspection deprecation
                        mServiceIconList.add(getResources().getDrawable(getResources().getIdentifier("ic_" + aService.toString(), "drawable", getPackageName())));
                    }
                } catch (Exception e) {
                    mServiceIconList.add(new Drawable() {
                        @Override
                        public void draw(Canvas canvas) {

                        }

                        @Override
                        public void setAlpha(int alpha) {

                        }

                        @Override
                        public void setColorFilter(ColorFilter colorFilter) {

                        }

                        @Override
                        public int getOpacity() {
                            return 0;
                        }
                    });
                }
            }
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_row_layout, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.itemView.setTag(position);
            holder.tvServiceName.setText(mServiceNameList.get(position));
            holder.ivServiceIcon.setBackground(mServiceIconList.get(position));
        }

        @Override
        public int getItemCount() {
            return mServiceNameList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView tvServiceName;
            private ImageView ivServiceIcon;

            public ViewHolder(View view) {
                super(view);
                this.tvServiceName = (TextView) view.findViewById(R.id.tvServiceName);
                this.ivServiceIcon = (ImageView) view.findViewById(R.id.ivServiceIcon);
                view.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onClick(View v) {
                        Intent intent;
                        PackageManager packageManager = getPackageManager();
                        switch (services.values()[(Integer) v.getTag()]) {
                            case dm:
                                DownloadManagerActivity.session = session;
                                intent = new Intent(DashboardActivity.this, DownloadManagerActivity.class);
                                startActivity(intent);
                                break;
                            case backup:
                                try {
                                    packageManager.getPackageInfo("com.owncloud.android", PackageManager.GET_ACTIVITIES);
                                    final Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.owncloud.android");
                                    try {
                                        session.setPortForwardingL(services.backup.lport, "127.0.0.1", services.backup.port);
                                    } catch (JSchException e) {
                                        e.printStackTrace();
                                    } finally {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
                                        builder.setTitle("Please copy the following url and paste it at the next screen to setup backup.");
                                        final EditText input_url = new EditText(getApplicationContext());
                                        input_url.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
                                        input_url.setTextColor(Color.BLACK);
                                        input_url.setText("http://127.0.0.1:9080/owncloud");
                                        builder.setView(input_url);
                                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                startActivity(launchIntent);
                                            }
                                        });
                                        builder.show();
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
                                    request.setTitle("ownCloud backup App");
                                    request.setDestinationInExternalPublicDir(Environment.getDownloadCacheDirectory().getAbsolutePath(), "com.owncloud.android_20000001.apk");
                                    DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                                    manager.enqueue(request);
                                    registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                                }
                                break;
                            case homebase:
//                                TODO Launch Server Dashboard
                                intent = new Intent(Intent.ACTION_VIEW);
                                try {
                                    session.setPortForwardingL(services.homebase.lport, "127.0.0.1", services.homebase.port);
                                } catch (JSchException e) {
                                    e.printStackTrace();
                                }
                                intent.setData(Uri.parse("http://127.0.0.1:9080/"));
                                startActivity(intent);
                                break;
                            case vs:
                                try {
                                    session.setPortForwardingL(services.vs.port, "127.0.0.1", services.vs.port);
                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse("http://127.0.0.1:" + String.valueOf(services.vs.port) + "/"));
                                    startActivity(i);
                                } catch (JSchException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case printer:
                                try {
                                    packageManager.getPackageInfo("com.blackspruce.lpd", PackageManager.GET_ACTIVITIES);
                                    final Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.blackspruce.lpd");
                                    try {
                                        session.setPortForwardingL(services.printer.lport, "127.0.0.1", services.printer.port);
                                    } catch (JSchException e) {
                                        e.printStackTrace();
                                    } finally {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
                                        builder.setTitle("Please copy the following url and paste it at the setup screen to setup print.");
                                        final EditText input_url = new EditText(getApplicationContext());
                                        input_url.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
                                        input_url.setTextColor(Color.BLACK);
                                        input_url.setText("http://127.0.0.1:9631/");
                                        builder.setView(input_url);
                                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                startActivity(launchIntent);
                                            }
                                        });
                                        builder.show();
                                    }
                                } catch (PackageManager.NameNotFoundException e) {
                                    try {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.blackspruce.lpd")));
                                    } catch (android.content.ActivityNotFoundException a) {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + "com.blackspruce.lpd")));
                                    }
                                }
                                break;
                            case xmpp:
                                try {
                                    session.setPortForwardingL(services.xmpp.lport, "127.0.0.1", services.xmpp.port);
                                    xmppHandler xmppHandler = new xmppHandler(DashboardActivity.this);
                                    xmppHandler.execute();
                                } catch (JSchException e) {
                                    e.printStackTrace();
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