package com.ihs.homeconnect;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ihs.homeconnect.helpers.services;
import com.ihs.homeconnect.helpers.verticalSpaceDecorationHelper;
import com.ihs.homeconnect.helpers.xmppHandler;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

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

        servicesAdapter() {
            for (services aService : services.values()) {
                mServiceNameList.add(aService.name);
                try {
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                        mServiceIconList.add(getDrawable(getResources().getIdentifier(aService.icon, "mipmap", getPackageName())));
                    } else {
                        //noinspection deprecation
                        mServiceIconList.add(getResources().getDrawable(getResources().getIdentifier(aService.icon, "mipmap", getPackageName())));
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
                            return PixelFormat.TRANSPARENT;
                        }
                    });
                }
            }
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_row_service, parent, false);
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

        class ViewHolder extends RecyclerView.ViewHolder {
            private TextView tvServiceName;
            private ImageView ivServiceIcon;

            ViewHolder(View view) {
                super(view);
                this.tvServiceName = (TextView) view.findViewById(R.id.tvServiceName);
                this.ivServiceIcon = (ImageView) view.findViewById(R.id.ivServiceIcon);
                view.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onClick(View v) {
                        Intent intent;
                        switch (services.values()[(Integer) v.getTag()]) {
                            case dm:
                                try {
                                    session.setPortForwardingL(services.dm.lport, "127.0.0.1", services.dm.port);
                                } catch (JSchException e) {
                                    if (!e.getMessage().startsWith("PortForwardingL:")) {
                                        e.printStackTrace();
                                        break;
                                    }
                                }
                                intent = new Intent(DashboardActivity.this, DownloadManagerActivity.class);
                                startActivity(intent);
                                break;
                            case backup:
                                try {
                                    session.setPortForwardingL(services.backup.lport, "127.0.0.1", services.backup.port);
                                } catch (JSchException e) {
                                    if (!e.getMessage().startsWith("PortForwardingL:")) {
                                        e.printStackTrace();
                                        break;
                                    }
                                }
                                intent = new Intent(DashboardActivity.this, BackupActivity.class);
                                startActivity(intent);

                                break;
                            case vs:
                                try {
                                    session.setPortForwardingL(services.vs.lport, "127.0.0.1", services.vs.port);
                                } catch (JSchException e) {
                                    if (!e.getMessage().startsWith("PortForwardingL:")) {
                                        e.printStackTrace();
                                        break;
                                    }
                                }
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse("http://127.0.0.1:" + String.valueOf(services.vs.lport) + "/videocam"));
                                startActivity(i);
                                break;
                            case xmpp:
                                try {
                                    session.setPortForwardingL(services.xmpp.lport, "127.0.0.1", services.xmpp.port);
                                } catch (JSchException e) {
                                    if (!e.getMessage().startsWith("PortForwardingL:")) {
                                        e.printStackTrace();
                                        break;
                                    }
                                }
                                xmppHandler xmppHandler = new xmppHandler(DashboardActivity.this);
                                xmppHandler.execute();
                                break;
                            case power:
                                try {
                                    session.setPortForwardingL(services.power.lport, "127.0.0.1", services.power.port);
                                } catch (JSchException e) {
                                    if (!e.getMessage().startsWith("PortForwardingL:")) {
                                        e.printStackTrace();
                                        break;
                                    }
                                }
                                intent = new Intent(DashboardActivity.this, PowerActivity.class);
                                startActivity(intent);
                                break;
                            case print:
                                try {
                                    session.setPortForwardingL(services.print.lport, "127.0.0.1", services.print.port);
                                } catch (JSchException e) {
                                    if (!e.getMessage().startsWith("PortForwardingL:")) {
                                        e.printStackTrace();
                                        break;
                                    }
                                }
                                intent = new Intent(DashboardActivity.this, PrintActivity.class);
                                startActivity(intent);
                                break;
                            case sftp:
                                DownloadMediaActivity.session = session;
                                intent = new Intent(DashboardActivity.this, DownloadMediaActivity.class);
                                startActivity(intent);
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