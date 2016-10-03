//Problems : not working when I initiate chat , working otherwise
package com.ihs.homeconnect;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ihs.homeconnect.entities.ChatMessageModel;
import com.ihs.homeconnect.helpers.OrmHelper;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class XmppChatActivity extends AppCompatActivity {

    public static Chat chat;
    RecyclerView mRecyclerView;
    xmppChatAdapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    private OrmHelper ormHelper = OpenHelperManager.getHelper(this, OrmHelper.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xmpp_chat);

        final Button bSendChatMessage = (Button) findViewById(R.id.bSendMessage);
        final EditText etChatMessage = (EditText) findViewById(R.id.etChatMessage);


        mRecyclerView = (RecyclerView) findViewById(R.id.rvChatConsole);
        mLayoutManager = new LinearLayoutManager(this);

        assert mRecyclerView != null;
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new xmppChatAdapter();
        mRecyclerView.setAdapter(mAdapter);

        chat.addMessageListener(new ChatMessageListener() {
            @Override
            public void processMessage(Chat chat, Message message) {
                mAdapter.addMessage(message.getBody(), false);
            }
        });

        if (bSendChatMessage != null && etChatMessage != null)
            bSendChatMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String message = etChatMessage.getText().toString();
                                chat.sendMessage(message);
                                mAdapter.addMessage(message, true);
                                etChatMessage.setText("");
                            } catch (SmackException.NotConnectedException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
    }

    @Override
    public void onBackPressed() {
        chat.close();
        super.onBackPressed();
    }

    private class xmppChatAdapter extends RecyclerView.Adapter<xmppChatAdapter.ViewHolder> {
        List<ChatMessageModel> chatEntries = new ArrayList<>();
        Dao<ChatMessageModel, Integer> chatMessages;

        xmppChatAdapter() {
            updateAdapter();
        }

        void updateAdapter() {
            try {
                chatMessages = ormHelper.getDao(ChatMessageModel.class);
                chatEntries = chatMessages.queryForEq("sender", chat.getParticipant());
                notifyDataSetChanged();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        void addMessage(String messageString, boolean chatMe) {
            if (!messageString.equals("")) {
                ChatMessageModel chatMessage = new ChatMessageModel(chat.getParticipant(), messageString, false, chatMe);
                chatEntries.add(chatMessage);
                try {
                    chatMessages.create(chatMessage);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount());
                notifyItemInserted(getItemCount());
            }
        }

        @Override
        public xmppChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_row_chat, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(xmppChatAdapter.ViewHolder holder, int position) {
            if (chatEntries.get(holder.getAdapterPosition()).isSelf_flag()) {
                holder.tvChatMessageLocal.setText(chatEntries.get(holder.getAdapterPosition()).getBody());
                holder.tvChatMessageRemote.setText("");
            } else {
                holder.tvChatMessageRemote.setText(chatEntries.get(holder.getAdapterPosition()).getBody());
                holder.tvChatMessageLocal.setText("");
            }
        }

        @Override
        public int getItemCount() {
            return chatEntries.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView tvChatMessageLocal;
            TextView tvChatMessageRemote;

            ViewHolder(View view) {
                super(view);
                this.tvChatMessageLocal = (TextView) view.findViewById(R.id.tvChatMessageLocal);
                this.tvChatMessageRemote = (TextView) view.findViewById(R.id.tvChatMessageRemote);
                view.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {

            }
        }
    }
}
