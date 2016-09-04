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

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;

import java.util.ArrayList;

public class XmppChatActivity extends AppCompatActivity {
    public static String chatReceipient;
    public static AbstractXMPPConnection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xmpp_chat);

        final Button bSendChatMessage = (Button) findViewById(R.id.bSendMessage);
        final EditText etChatMessage = (EditText) findViewById(R.id.etChatMessage);
        RecyclerView mRecyclerView;
        final RecyclerView.Adapter mAdapter;
        RecyclerView.LayoutManager mLayoutManager;

        mRecyclerView = (RecyclerView) findViewById(R.id.rvChatConsole);
        mLayoutManager = new LinearLayoutManager(this);

        assert mRecyclerView != null;
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        final xmppChatAdapter chatAdapter = new xmppChatAdapter();
        mAdapter = chatAdapter;
        mRecyclerView.setAdapter(mAdapter);

        ChatManager chatManager = ChatManager.getInstanceFor(connection);
        final Chat newChat = chatManager.createChat(chatReceipient, new ChatMessageListener() {
            @Override
            public void processMessage(Chat chat, final Message message) {
                chatAdapter.messageUpdate(message.getBody());
            }
        });
        if (bSendChatMessage != null && etChatMessage != null)
            bSendChatMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String message = etChatMessage.getText().toString();
                        newChat.sendMessage(message);
                        chatAdapter.messageUpdate(message);
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    }
                }
            });
    }

    private class xmppChatAdapter extends RecyclerView.Adapter<xmppChatAdapter.ViewHolder> {
        public ArrayList<String> chatEntries;

        public xmppChatAdapter() {
            chatEntries = new ArrayList<>();
        }

        public void messageUpdate(String messageString) {
            chatEntries.add(messageString);
            notifyDataSetChanged();
        }

        @Override
        public xmppChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_bubble_layout, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(xmppChatAdapter.ViewHolder holder, int position) {
            holder.tvChatBody.setText(chatEntries.get(position));
        }

        @Override
        public int getItemCount() {
            return chatEntries.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public TextView tvChatBody;

            public ViewHolder(View view) {
                super(view);
                this.tvChatBody = (TextView) view.findViewById(R.id.tvChatMessage);
                view.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {

            }
        }
    }
}
