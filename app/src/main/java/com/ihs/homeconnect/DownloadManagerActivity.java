package com.ihs.homeconnect;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ihs.homeconnect.helpers.services;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DownloadManagerActivity extends AppCompatActivity {

    final RecyclerView.Adapter mAdapter = new DownloadsAdapter();
    RequestQueue rpcQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_manager);

        RecyclerView mRecyclerView;

        RecyclerView.LayoutManager mLayoutManager;

        mRecyclerView = (RecyclerView) findViewById(R.id.rvDownloadsList);
        mLayoutManager = new LinearLayoutManager(this);


        assert mRecyclerView != null;
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            mRecyclerView.addItemDecoration(new panelSpaceDecorationHelper(this));
        }
        FloatingActionButton fabAddNewUri = (FloatingActionButton) findViewById(R.id.fabAddDownloadUri);
        assert fabAddNewUri != null;
        fabAddNewUri.setImageResource(R.drawable.ic_add);
        fabAddNewUri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DownloadManagerActivity.this);
                builder.setTitle("Add a download");
                final EditText input_url = new EditText(getApplicationContext());
                input_url.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
                input_url.setTextColor(Color.BLACK);
                input_url.setHint("Paste the download url");
                builder.setView(input_url);
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (input_url.getText().toString().equals("")) {
                            Toast.makeText(DownloadManagerActivity.this, "Please enter a valid url", Toast.LENGTH_SHORT).show();
                        } else {
                            ((DownloadsAdapter) mAdapter).rpcMethods("aria2.addUri", input_url.getText().toString());
                        }
                    }
                });
                builder.show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_generic_refresh_helper, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.refresh) {
            ((DownloadsAdapter) mAdapter).rpcMethods("aria2.tellActive");
            ((DownloadsAdapter) mAdapter).rpcMethods("aria2.tellWaiting", "-1", "2");
        }
        return super.onOptionsItemSelected(item);
    }

    private class panelSpaceDecorationHelper extends RecyclerView.ItemDecoration {
        private Drawable mDivider;

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public panelSpaceDecorationHelper(Context mContext) {
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

    private class DownloadsAdapter extends RecyclerView.Adapter<DownloadsAdapter.ViewHolder> {
        public dtArrayList downloadTaskArrayList = new dtArrayList();

        public DownloadsAdapter() {
            rpcMethods("aria2.tellActive");
            rpcMethods("aria2.tellWaiting", "-1", "2");
        }

        void rpcMethods(final String... params) {
            Cache cache = new DiskBasedCache(Environment.getDownloadCacheDirectory());
            Network network = new BasicNetwork(new HurlStack());
            rpcQueue = new RequestQueue(cache, network);
            JSONObject rpcRequest = new JSONObject();
            try {
                rpcRequest.accumulate("jsonrpc", "2.0");
                rpcRequest.accumulate("method", params[0]);
                if (params.length == 2) {
                    if (params[0].equals("aria2.addUri")) {
                        JSONArray uris = new JSONArray();
                        JSONArray uri = new JSONArray();
                        uri.put(params[1]);
                        uris.put(uri);
                        rpcRequest.accumulate("params", uris);
                    } else {
                        JSONArray par = new JSONArray();
                        par.put(params[1]);
                        rpcRequest.accumulate("params", par);
                    }
                }
                if (params.length == 3) {
                    JSONArray parm = new JSONArray();
                    parm.put(Integer.valueOf(params[1]));
                    parm.put(Integer.valueOf(params[2]));
                    rpcRequest.accumulate("params", parm);
                }
                rpcRequest.accumulate("id", "aria2c");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "http://127.0.0.1:" + String.valueOf(services.dm.lport) + "/jsonrpc", rpcRequest, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONObject download;
                        JSONArray downloads;
                        if (params.length == 2) {
                            System.out.print("Handle success add or pause here here");
                            rpcMethods("aria2.tellActive");
                            rpcMethods("aria2.tellWaiting", "-1", "2");
                        } else {
                            downloads = (JSONArray) response.get("result");
                            for (int i = 0; i < downloads.length(); i++) {
                                download = (JSONObject) downloads.get(i);
                                String tempDownloadName = download.getJSONArray("files").getJSONObject(0).get("path").toString();
                                if (tempDownloadName.equals("")) {
                                    tempDownloadName = download.getJSONArray("files").getJSONObject(0).getJSONArray("uris").getJSONObject(0).get("uri").toString();
                                }
                                downloadTask dlt = new downloadTask(tempDownloadName, Integer.valueOf(download.get("completedLength").toString()), Integer.valueOf(download.get("totalLength").toString()), String.valueOf(download.get("status")), String.valueOf(download.get("gid")));
                                if (downloadTaskArrayList.contains(dlt)) {
                                    downloadTaskArrayList.update(dlt);
                                } else {
                                    downloadTaskArrayList.add(dlt);
                                }
                            }
                            notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });
            rpcQueue.start();
            rpcQueue.add(jsonObjectRequest);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_row_downloads, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.tvDownloadName.setText(downloadTaskArrayList.get(position).getdName());
            holder.pbDownloadCompletion.setMax(downloadTaskArrayList.get(position).getdTotal());
            holder.pbDownloadCompletion.setProgress(downloadTaskArrayList.get(position).getdCompleted());
            holder.ibPlayPause.setTag(R.id.TAG_DOWNLOAD_GID, downloadTaskArrayList.get(position).getdGid());
            if (downloadTaskArrayList.get(position).getdStatus().equals("active")) {
                holder.ibPlayPause.setImageResource(R.drawable.ic_pause);
                holder.ibPlayPause.setTag(R.id.TAG_DOWNLOAD_STATUS, 0);
            } else {
                holder.ibPlayPause.setImageResource(R.drawable.ic_start);
                holder.ibPlayPause.setTag(R.id.TAG_DOWNLOAD_STATUS, 1);
            }
        }

        @Override
        public int getItemCount() {
            return downloadTaskArrayList.size();
        }

        public class dtArrayList extends ArrayList<downloadTask> {
            @Override
            public boolean contains(Object o) {
                boolean flag = false;
                for (downloadTask dt :
                        this) {
                    if (((downloadTask) o).getdGid().equals(dt.getdGid())) {
                        flag = true;
                        break;
                    }
                }
                return flag;
            }

            public void update(downloadTask dtn) {
                for (downloadTask dt :
                        this) {
                    if (dtn.getdGid().equals(dt.getdGid())) {
                        dt.setdCompleted(dtn.getdCompleted());
                        dt.setdStatus(dtn.getdStatus());
                        dt.setdTotal(dtn.getdTotal());
                        break;
                    }
                }

            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView tvDownloadName;
            private ProgressBar pbDownloadCompletion;
            private ImageButton ibPlayPause;

            public ViewHolder(View view) {
                super(view);
                this.tvDownloadName = (TextView) view.findViewById(R.id.tvDownloadName);
                this.pbDownloadCompletion = (ProgressBar) view.findViewById(R.id.pbDownloadCompletion);
                this.pbDownloadCompletion.setIndeterminate(false);
                this.pbDownloadCompletion.getProgressDrawable().setColorFilter(Color.parseColor("#ff9900"), PorterDuff.Mode.SRC_IN);
                this.ibPlayPause = (ImageButton) view.findViewById(R.id.ibPlayPause);
                this.ibPlayPause.setBackgroundColor(Color.TRANSPARENT);
//                TODO fix layout of the row because it uses dps' which is very bad
                this.ibPlayPause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v.getTag(R.id.TAG_DOWNLOAD_STATUS).equals(1)) {
                            rpcMethods("aria2.unpause", v.getTag(R.id.TAG_DOWNLOAD_GID).toString());
                        } else {
                            rpcMethods("aria2.pause", v.getTag(R.id.TAG_DOWNLOAD_GID).toString());
                        }
                    }
                });
            }
        }
    }

    private class downloadTask {
        private String dName;
        private String dGid;
        private String dStatus;
        private int dCompleted;
        private int dTotal;


        public downloadTask(String dName, int dCompleted, int dTotal, String dStatus, String dGid) {
            this.dName = dName;
            this.dCompleted = dCompleted;
            this.dTotal = dTotal;
            this.dStatus = dStatus;
            this.dGid = dGid;
        }

        public String getdName() {
            return dName;
        }

        public int getdCompleted() {
            return dCompleted;
        }

        public void setdCompleted(int dCompleted) {
            this.dCompleted = dCompleted;
        }

        public int getdTotal() {
            return dTotal;
        }

        public void setdTotal(int dTotal) {
            this.dTotal = dTotal;
        }

        public String getdStatus() {
            return dStatus;
        }

        public void setdStatus(String dStatus) {
            this.dStatus = dStatus;
        }

        public String getdGid() {
            return dGid;
        }
    }
}