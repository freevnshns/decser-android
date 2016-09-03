package com.ihs.homeconnect;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
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
import android.graphics.Rect;
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
import android.widget.Button;
import android.widget.EditText;
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

    private class verticalSpaceDecorationHelper extends RecyclerView.ItemDecoration {
        private Drawable mDivider;

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public verticalSpaceDecorationHelper(Context mContext) {
            mDivider = mContext.getDrawable(R.drawable.line_divider);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);

            if (parent.getChildAdapterPosition(view) == 0) {
                return;
            }
            outRect.top = mDivider.getIntrinsicHeight();
        }

        @Override
        public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
            int dividerLeft = parent.getPaddingLeft();
            int dividerRight = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount - 1; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int dividerTop = child.getBottom() + params.bottomMargin;
                int dividerBottom = dividerTop + mDivider.getIntrinsicHeight();

                mDivider.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom);
                mDivider.draw(canvas);
            }
        }
    }

    private class servicesAdapter extends RecyclerView.Adapter<servicesAdapter.ViewHolder> {
        private ArrayList<String> mServiceNameList = new ArrayList<>();
        private ArrayList<Drawable> mServiceIconList = new ArrayList<>();


        public servicesAdapter() {
            for (services aService : services.values()) {
                mServiceNameList.add(aService.toString());
                try {
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                        mServiceIconList.add(getDrawable(getResources().getIdentifier("ic_" + aService.toString(), "drawable", getPackageName())));
                    } else {
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
                                        session.setPortForwardingL(services.backup.port + 9000, "127.0.0.1", services.backup.port);
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
                                    session.setPortForwardingL(services.homebase.port + 9000, "127.0.0.1", services.homebase.port);
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
                                        session.setPortForwardingL(services.printer.port + 9000, "127.0.0.1", services.printer.port);
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
                                    session.setPortForwardingL(services.xmpp.port + 9000, "127.0.0.1", services.xmpp.port);
                                    intent = new Intent(DashboardActivity.this, XmppActivity.class);
                                    startActivity(intent);
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