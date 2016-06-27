package com.comslav.homeconnect;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.comslav.homeconnect.helpers.connectionHandler;
import com.jcraft.jsch.JSchException;

import java.util.concurrent.ExecutionException;

public class TrackListAdapter extends RecyclerView.Adapter<TrackListAdapter.ViewHolder> {
    private String[] mContactNameList;
    private String[] mContactHostnameList;

    public TrackListAdapter(String[] mContactNameList, String[] mContactHostnameList) {
        this.mContactNameList = mContactNameList;
        this.mContactHostnameList = mContactHostnameList;
    }

    @Override
    public TrackListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_row_layout, parent, false);
        return new ViewHolder(view, new ViewHolder.ContactViewHolderClick() {
            @Override
            public void connect(View contact) {
                final String tempContactHostname = contact.getTag().toString();
                try {
                    connectionHandler connectionHandler = new connectionHandler(tempContactHostname, "limited-user");
                    DashboardActivity.session = connectionHandler.execute().get();
                    if (DashboardActivity.session != null) {
                        Intent intent = new Intent(contact.getContext(), DashboardActivity.class);
                        contact.getContext().startActivity(intent);
                    } else {
                        Toast.makeText(contact.getContext(), "Failed", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSchException | ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.tvContactName.setText(mContactNameList[holder.getAdapterPosition()]);
        holder.tvContactHostname.setText(mContactHostnameList[holder.getAdapterPosition()]);
        holder.itemView.setTag(mContactHostnameList[holder.getAdapterPosition()]);
    }

    @Override
    public int getItemCount() {
        return mContactNameList.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvContactName;
        public TextView tvContactHostname;
        public ContactViewHolderClick mListener;

        public ViewHolder(View view, ContactViewHolderClick listener) {
            super(view);
            this.tvContactName = (TextView) view.findViewById(R.id.tvContactName);
            this.tvContactHostname = (TextView) view.findViewById(R.id.tvContactHostname);
            mListener = listener;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.connect(v);
        }

        public interface ContactViewHolderClick {
            void connect(View track);
        }
    }
}
