package com.comslav.homeconnect;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.comslav.homeconnect.helpers.connectionHandler;
import com.comslav.homeconnect.helpers.dbHandler;
import com.jcraft.jsch.JSchException;

public class TrackListAdapter extends RecyclerView.Adapter<TrackListAdapter.ViewHolder> {
    private String[] mContactNameList;
    private String[] mContactHostnameList;
    private Context mContext;


    public TrackListAdapter(Context mContext) {
        this.mContext = mContext;
        dbHandler dbInstance = new dbHandler(mContext, null);
        this.mContactNameList = dbInstance.getContactNameList();
        this.mContactHostnameList = dbInstance.getHostnameArray();
    }

    private void updateAdapter() {
        dbHandler dbInstance = new dbHandler(mContext, null);
        this.mContactNameList = dbInstance.getContactNameList();
        this.mContactHostnameList = dbInstance.getHostnameArray();
    }

    @Override
    public TrackListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_row_layout, parent, false);
        return new ViewHolder(view, new ViewHolder.ContactViewHolderClick() {
            @Override
            public void connect(View v) {
                final String tempContactHostname = v.getTag(R.string.TAG_CONTACT_HOSTNAME).toString();
                try {
                    connectionHandler connectionHandler = new connectionHandler(tempContactHostname, "user", mContext);
                    connectionHandler.execute();
                } catch (JSchException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void removeContact(View v) {
                final String tempContactHostname = v.getTag(R.string.TAG_CONTACT_HOSTNAME).toString();
                final int tempContactPosition = (int) v.getTag(R.string.TAG_CONTACT_POSITION);
                final dbHandler dbInstance = new dbHandler(mContext, null);
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
            }
        });
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.tvContactName.setText(mContactNameList[holder.getAdapterPosition()]);
        holder.tvContactHostname.setText(mContactHostnameList[holder.getAdapterPosition()]);
        holder.itemView.setTag(R.string.TAG_CONTACT_HOSTNAME, mContactHostnameList[holder.getAdapterPosition()]);
        holder.itemView.setTag(R.string.TAG_CONTACT_POSITION, holder.getAdapterPosition());
    }

    @Override
    public int getItemCount() {
        return mContactNameList.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public TextView tvContactName;
        public TextView tvContactHostname;
        public ContactViewHolderClick mListener;

        public ViewHolder(View view, ContactViewHolderClick listener) {
            super(view);
            this.tvContactName = (TextView) view.findViewById(R.id.tvContactName);
            this.tvContactHostname = (TextView) view.findViewById(R.id.tvContactHostname);
            mListener = listener;
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.connect(v);
        }

        @Override
        public boolean onLongClick(View v) {
            mListener.removeContact(v);
            return true;
        }

        public interface ContactViewHolderClick {
            void connect(View v);

            void removeContact(View v);
        }
    }
}
