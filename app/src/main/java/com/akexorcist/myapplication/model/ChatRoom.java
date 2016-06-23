package com.akexorcist.myapplication.model;

import java.util.List;

/**
 * Created by Akexorcist on 6/20/2016 AD.
 */

public class ChatRoom {
    List<MessageItem> messageItemList;

    public ChatRoom() {
    }

    public ChatRoom(List<MessageItem> messageItemList) {
        this.messageItemList = messageItemList;
    }

    public List<MessageItem> getMessageItemList() {
        return messageItemList;
    }

    public void setMessageItemList(List<MessageItem> messageItemList) {
        this.messageItemList = messageItemList;
    }

    public void addMessageItem(MessageItem messageItem) {
        if(messageItemList != null) {
            messageItemList.add(messageItem);
        }
    }
}
