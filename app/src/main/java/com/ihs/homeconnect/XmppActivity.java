package com.ihs.homeconnect;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ihs.homeconnect.helpers.verticalSpaceDecorationHelper;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

public class XmppActivity extends AppCompatActivity {

    public static AbstractXMPPConnection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xmpp);
        ChatManager chatManager = ChatManager.getInstanceFor(connection);
        chatManager.addChatListener(new ChatManagerListener() {
            @Override
            public void chatCreated(Chat chat, boolean createdLocally) {
                if (!createdLocally) {
//                    add a listener
                }
            }
        });


        RecyclerView mRecyclerView;
        RecyclerView.Adapter mAdapter;
        RecyclerView.LayoutManager mLayoutManager;

        mRecyclerView = (RecyclerView) findViewById(R.id.rvXmppContacts);
        mLayoutManager = new LinearLayoutManager(this);

        assert mRecyclerView != null;
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        Roster roster = Roster.getInstanceFor(connection);
        mAdapter = new xmppRosterAdapter(roster.getEntries());
        mRecyclerView.setAdapter(mAdapter);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            mRecyclerView.addItemDecoration(new verticalSpaceDecorationHelper(this));
        }
//        Button bLoadFromServer = (Button) findViewById(R.id.bLoadFromServer);
//        assert bLoadFromServer != null;
//        bLoadFromServer.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(DashboardActivity.this, DownloadMediaActivity.class);
//                DownloadMediaActivity.session = session;
//                startActivity(intent);
//            }
//        });
    }

    @Override
    public void onBackPressed() {
        if (connection != null)
            connection.disconnect();
        super.onBackPressed();
    }

    private class xmppRosterAdapter extends RecyclerView.Adapter<xmppRosterAdapter.ViewHolder> {
        public ArrayList<String> rosterEntries;

        public xmppRosterAdapter(Set rosterSet) {
            rosterEntries = new ArrayList<>(rosterSet.size());
            for (RosterEntry entry : (Collection<RosterEntry>) rosterSet) {
                rosterEntries.add(entry.getUser());
            }
        }

        @Override
        public xmppRosterAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_row_layout, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(xmppRosterAdapter.ViewHolder holder, int position) {
            holder.tvRosterName.setText(rosterEntries.get(position));
            holder.itemView.setTag("duas@sinecos.local");
        }

        @Override
        public int getItemCount() {
            return rosterEntries.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public TextView tvRosterName;

            public ViewHolder(View view) {
                super(view);
                this.tvRosterName = (TextView) view.findViewById(R.id.tvContactName);
                view.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                XmppChatActivity.connection = connection;
                XmppChatActivity.chatReceipient = "duas@sinecos.local";
                Intent intent = new Intent(XmppActivity.this, XmppChatActivity.class);
                startActivity(intent);
            }
        }
    }

}
