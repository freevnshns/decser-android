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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ihs.homeconnect.helpers.downloadManagerHandler;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class DownloadManagerActivity extends AppCompatActivity {
    final RecyclerView.Adapter mAdapter = new DownloadsAdapter();

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
        RelativeLayout rlEmpty = (RelativeLayout) findViewById(R.id.rlEmptyDMView);
        assert rlEmpty != null;

        if (mAdapter.getItemCount() == 0) {
            mRecyclerView.setVisibility(View.GONE);
            rlEmpty.setVisibility(View.VISIBLE);
        } else {
            rlEmpty.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
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
                        downloadManagerHandler downloadManagerHandler = new downloadManagerHandler();
                        if (input_url.getText().toString().equals("")) {
                            Toast.makeText(DownloadManagerActivity.this, "Please enter a valid url", Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                String add_result = (String) downloadManagerHandler.execute("aria2.addUri", input_url.getText().toString()).get();
                                if (add_result != null) {
                                    ((DownloadsAdapter) mAdapter).getDownloads();
                                    mAdapter.notifyDataSetChanged();
                                    Toast.makeText(DownloadManagerActivity.this, "Added Successfully", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(DownloadManagerActivity.this, "Adding Failed", Toast.LENGTH_LONG).show();
                                }
                            } catch (InterruptedException | ExecutionException | ClassCastException e) {
                                e.printStackTrace();
                                Toast.makeText(DownloadManagerActivity.this, "Adding Failed", Toast.LENGTH_LONG).show();
                            }
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
            ((DownloadsAdapter) mAdapter).getDownloads();
            mAdapter.notifyDataSetChanged();
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
        ArrayList<downloadTask> downloadTaskArrayList;

        public DownloadsAdapter() {
            getDownloads();
        }

        public void getDownloads() {
            downloadTaskArrayList = new ArrayList<>();
            net.minidev.json.JSONArray downloads;
            JSONObject download;
            try {
                downloadManagerHandler dmh = new downloadManagerHandler();
                downloads = (JSONArray) dmh.execute("aria2.tellActive").get();
                for (int i = 0; i < downloads.size(); i++) {
                    download = (JSONObject) downloads.get(i);
                    downloadTaskArrayList.add(new downloadTask(((JSONObject) (((JSONArray) download.get("files")).get(0))).get("path").toString(), Integer.valueOf(download.get("completedLength").toString()), Integer.valueOf(download.get("totalLength").toString()), 0));
                }
                dmh = new downloadManagerHandler();
                downloads = (JSONArray) dmh.execute("aria2.tellWaiting", "-1", "2").get();
                for (int i = 0; i < downloads.size(); i++) {
                    download = (JSONObject) downloads.get(i);
                    downloadTaskArrayList.add(new downloadTask(((JSONObject) (((JSONArray) download.get("files")).get(0))).get("path").toString(), Integer.valueOf(download.get("completedLength").toString()), Integer.valueOf(download.get("totalLength").toString()), 1));
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
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
            if (downloadTaskArrayList.get(position).getdStatus() == 0) {
                holder.ibPlayPause.setImageResource(R.drawable.ic_pause);
            } else {
                holder.ibPlayPause.setImageResource(R.drawable.ic_start);
            }
        }

        @Override
        public int getItemCount() {
            return downloadTaskArrayList.size();
        }

        private class downloadTask {
            private String dName;
            private int dCompleted;
            private int dTotal;
            private int dStatus;//0 -> active , 1 -> paused


            public downloadTask(String dName, int dCompleted, int dTotal, int dStatus) {
                this.dName = dName;
                this.dCompleted = dCompleted;
                this.dTotal = dTotal;
                this.dStatus = dStatus;
            }

            public String getdName() {
                return dName;
            }

            public int getdCompleted() {
                return dCompleted;
            }

            public int getdTotal() {
                return dTotal;
            }

            public int getdStatus() {
                return dStatus;
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
            }
        }
    }
}
