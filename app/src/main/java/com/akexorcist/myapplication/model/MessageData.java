package com.akexorcist.myapplication.model;

/**
 * Created by Akexorcist on 6/25/2016 AD.
 */

public class MessageData {
    String key;
    Message message;

    public MessageData() {
    }

    public MessageData(String key, Message message) {
        this.key = key;
        this.message = message;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
