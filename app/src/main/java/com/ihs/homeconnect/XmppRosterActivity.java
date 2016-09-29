package com.ihs.homeconnect;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ihs.homeconnect.helpers.VerticalSpaceDecorationHelper;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;

import java.util.ArrayList;

public class XmppRosterActivity extends AppCompatActivity {
    XmppService xmppService;
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    ChatManager chatManager;

    private ServiceConnection xmppServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            XmppService.PacketBinder packetBinder = (XmppService.PacketBinder) iBinder;
            xmppService = packetBinder.getService();


            ArrayList<rosterEntry> roster_sender = new ArrayList<>();

            AbstractXMPPConnection connection = xmppService.getAbstractXMPPConnection();
            Roster roster = Roster.getInstanceFor(connection);
            chatManager = ChatManager.getInstanceFor(connection);


            for (RosterEntry entry : roster.getEntries()) {
                roster_sender.add(new rosterEntry(entry.getUser(), false));
            }
            mAdapter = new xmppRosterAdapter(roster_sender);
            mRecyclerView.setAdapter(mAdapter);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, XmppService.class);
        bindService(intent, xmppServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(xmppServiceConnection);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xmpp);

        mRecyclerView = (RecyclerView) findViewById(R.id.rvXmppContacts);
        mLayoutManager = new LinearLayoutManager(this);

        assert mRecyclerView != null;
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            mRecyclerView.addItemDecoration(new VerticalSpaceDecorationHelper(this));
        }
    }

    private class rosterEntry {
        String rosterName;
        boolean newMessage;

        rosterEntry(String rosterName, boolean newMessage) {
            this.rosterName = rosterName;
            this.newMessage = newMessage;
        }
    }

    private class xmppRosterAdapter extends RecyclerView.Adapter<xmppRosterAdapter.ViewHolder> {

        ArrayList<rosterEntry> rosterEntries;

        xmppRosterAdapter(ArrayList<rosterEntry> rosterEntries) {
            this.rosterEntries = rosterEntries;
        }

        @Override
        public xmppRosterAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_row_contact, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(xmppRosterAdapter.ViewHolder holder, int position) {
            if (rosterEntries.get(position).newMessage) {
                holder.itemView.setBackgroundColor(Color.parseColor("#ff9900"));
//                BAD UX TODO improve the foll. statement to be executed after click
                rosterEntries.get(position).newMessage = false;
            } else
                holder.itemView.setBackgroundColor(Color.TRANSPARENT);
//            Strip domain name if req.
            holder.tvRosterName.setText(rosterEntries.get(position).rosterName);
            holder.itemView.setTag(rosterEntries.get(position).rosterName);
        }

        @Override
        public int getItemCount() {
            return rosterEntries.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView tvRosterName;

            ViewHolder(View view) {
                super(view);
                this.tvRosterName = (TextView) view.findViewById(R.id.tvContactName);
                view.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                XmppChatActivity.chat = chatManager.createChat(v.getTag().toString());
                v.setBackgroundColor(Color.TRANSPARENT);
                Intent intent = new Intent(XmppRosterActivity.this, XmppChatActivity.class);
                startActivity(intent);
            }
        }
    }
}
