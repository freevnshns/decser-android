package com.ihs.homeconnect;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ihs.homeconnect.helpers.jsonrpcHandler;
import com.ihs.homeconnect.helpers.services;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class DownloadManagerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_manager);

        RecyclerView mRecyclerView;
        RecyclerView.Adapter mAdapter = null;
        RecyclerView.LayoutManager mLayoutManager;

        mRecyclerView = (RecyclerView) findViewById(R.id.rvDownloadsList);
        mLayoutManager = new LinearLayoutManager(this);
        jsonrpcHandler jsonrpcHandler = new jsonrpcHandler();
        try {
            JSONObject rpc_result = jsonrpcHandler.execute("http://127.0.0.1:" + String.valueOf(services.dm.port) + "/jsonrpc", "aria2.tellActive").get();
            mAdapter = new DownloadsAdapter(rpc_result);
        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Download Manger failed to start on your server please restart the server", Toast.LENGTH_LONG).show();
            onBackPressed();
        }
        assert mRecyclerView != null;
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
        RelativeLayout rlEmpty = (RelativeLayout) findViewById(R.id.rlEmptyDMView);
        assert rlEmpty != null;
        assert mAdapter != null;
        if (mAdapter.getItemCount() == 0) {
            mRecyclerView.setVisibility(View.GONE);
            rlEmpty.setVisibility(View.VISIBLE);
        } else {
            rlEmpty.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            mRecyclerView.addItemDecoration(new verticalSpaceDecorationHelper(this));
        }
        FloatingActionButton fabAddNewUri = (FloatingActionButton) findViewById(R.id.fabAddDownloadUri);
        assert fabAddNewUri != null;
        fabAddNewUri.setImageResource(R.drawable.ic_add_contact);
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
                        jsonrpcHandler jsonrpcHandler = new jsonrpcHandler();
                        if (input_url.getText().toString().equals("")) {
                            Toast.makeText(DownloadManagerActivity.this, "Please enter a valid url", Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                JSONObject result = jsonrpcHandler.execute("http://127.0.0.1:" + String.valueOf(services.dm.port) + "/jsonrpc", "aria2.addUri", input_url.getText().toString()).get();
                                if (result.has("error"))
                                    Toast.makeText(DownloadManagerActivity.this, "Adding Failed", Toast.LENGTH_LONG).show();
                                else
                                    Toast.makeText(DownloadManagerActivity.this, "Added Successfully", Toast.LENGTH_LONG).show();
                            } catch (InterruptedException | ExecutionException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                builder.show();
            }
        });
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

    private class DownloadsAdapter extends RecyclerView.Adapter<DownloadsAdapter.ViewHolder> {
        private ArrayList<String> mDownloads = new ArrayList<>();
        private ArrayList<String> mDownloadsPercentage = new ArrayList<>();

        public DownloadsAdapter(JSONObject rpc_result) throws JSONException {
            JSONArray mDownloadsList = rpc_result.getJSONArray("result");
            for (int i = 0; i < mDownloadsList.length(); i++) {
                mDownloads.add(mDownloadsList.getJSONObject(i).getJSONArray("files").getJSONObject(0).get("path").toString());
                mDownloadsPercentage.add(String.valueOf((Float.valueOf(mDownloadsList.getJSONObject(i).get("completedLength").toString()) / Float.valueOf(mDownloadsList.getJSONObject(i).get("totalLength").toString())) * 100) + "%");
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.downloads_row_layout, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.tvDownloadName.setText(mDownloads.get(position));
            holder.tvCompletionPercentage.setText(mDownloadsPercentage.get(position));
        }

        @Override
        public int getItemCount() {
            return mDownloads.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView tvDownloadName;
            private TextView tvCompletionPercentage;

            public ViewHolder(View view) {
                super(view);
                this.tvDownloadName = (TextView) view.findViewById(R.id.tvDownloadName);
                this.tvCompletionPercentage = (TextView) view.findViewById(R.id.tvCompletionPercentage);
            }
        }
    }
}
