package com.akexorcist.myapplication.adpter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.akexorcist.myapplication.R;
import com.akexorcist.myapplication.adpter.holder.OtherMessageViewHolder;
import com.akexorcist.myapplication.adpter.holder.UserMessageViewHolder;
import com.akexorcist.myapplication.model.ChatRoom;
import com.akexorcist.myapplication.model.MessageItem;

/**
 * Created by Akexorcist on 6/20/2016 AD.
 */

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_OTHER = 0;
    private static final int TYPE_USER = 1;
    private ChatRoom chatRoom;
    private String currentUser;
    private OnMessageItemLongClickListener messageItemLongClickListener;

    public MessageAdapter(ChatRoom chatRoom, String currentUser) {
        this.chatRoom = chatRoom;
        this.currentUser = currentUser;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_USER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_user_message_item, parent, false);
            return new UserMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_other_message_item, parent, false);
            return new OtherMessageViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        String user = chatRoom.getMessageItemList().get(position).getUser();
        if (user.equalsIgnoreCase(currentUser)) {
            return TYPE_USER;
        }
        return TYPE_OTHER;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        MessageItem messageItem = chatRoom.getMessageItemList().get(position);
        if (holder instanceof UserMessageViewHolder) {
            UserMessageViewHolder userMessageViewHolder = (UserMessageViewHolder) holder;
            userMessageViewHolder.tvUserName.setText(R.string.you);
            userMessageViewHolder.tvMessage.setText(messageItem.getText());
        } else if (holder instanceof OtherMessageViewHolder) {
            OtherMessageViewHolder otherMessageViewHolder = (OtherMessageViewHolder) holder;
            otherMessageViewHolder.tvUserName.setText(messageItem.getUser());
            otherMessageViewHolder.tvMessage.setText(messageItem.getText());
        }
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (messageItemLongClickListener != null) {
                    messageItemLongClickListener.onMessageItemLongClick(position);
                }
                return true;
            }
        });
    }

    public void setOnItemLongClickListener(OnMessageItemLongClickListener listener) {
        messageItemLongClickListener = listener;
    }

    @Override
    public int getItemCount() {
        return chatRoom.getMessageItemList().size();
    }

    public interface OnMessageItemLongClickListener {
        void onMessageItemLongClick(int position);
    }
}
