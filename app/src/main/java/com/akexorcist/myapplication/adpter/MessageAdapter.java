package com.akexorcist.myapplication.adpter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.akexorcist.myapplication.R;
import com.akexorcist.myapplication.adpter.holder.OtherMessageViewHolder;
import com.akexorcist.myapplication.adpter.holder.UserMessageViewHolder;
import com.akexorcist.myapplication.model.Message;
import com.akexorcist.myapplication.model.MessageData;

import java.util.List;

/**
 * Created by Akexorcist on 6/20/2016 AD.
 */

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_OTHER = 0;
    private static final int TYPE_USER = 1;
    private List<MessageData> messageDataList;
    ;
    private String currentUser;
    private boolean isSpecialUser = false;
    private OnMessageItemLongClickListener messageItemLongClickListener;

    public MessageAdapter(List<MessageData> messageDataList, String currentUser) {
        this.messageDataList = messageDataList;
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
        String user = messageDataList.get(position).getMessage().getUser();
        if (user.equalsIgnoreCase(currentUser)) {
            return TYPE_USER;
        }
        return TYPE_OTHER;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        Message message = messageDataList.get(position).getMessage();
        if (holder instanceof UserMessageViewHolder) {
            UserMessageViewHolder userMessageViewHolder = (UserMessageViewHolder) holder;
            if (isSpecialUser) {
                int specialColor = userMessageViewHolder.itemView.getResources().getColor(R.color.colorAmber);
                userMessageViewHolder.tvUserName.setTextColor(specialColor);
            }
            userMessageViewHolder.tvUserName.setText(R.string.you);
            userMessageViewHolder.tvMessage.setText(message.getText());
        } else if (holder instanceof OtherMessageViewHolder) {
            OtherMessageViewHolder otherMessageViewHolder = (OtherMessageViewHolder) holder;
            otherMessageViewHolder.tvUserName.setText(message.getUser());
            otherMessageViewHolder.tvMessage.setText(message.getText());
        }
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (messageItemLongClickListener != null) {
                    messageItemLongClickListener.onMessageItemLongClick(holder.getAdapterPosition());
                }
                return true;
            }
        });
    }

    public void setOnItemLongClickListener(OnMessageItemLongClickListener listener) {
        messageItemLongClickListener = listener;
    }

    public void setSpecialUser(boolean isSpecialUser) {
        this.isSpecialUser = isSpecialUser;
    }

    @Override
    public int getItemCount() {
        return messageDataList.size();
    }

    public interface OnMessageItemLongClickListener {
        void onMessageItemLongClick(int position);
    }
}
