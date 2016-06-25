package com.akexorcist.myapplication;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.akexorcist.myapplication.adpter.MessageAdapter;
import com.akexorcist.myapplication.common.BaseActivity;
import com.akexorcist.myapplication.manager.EventTrackerManager;
import com.akexorcist.myapplication.model.Message;
import com.akexorcist.myapplication.model.MessageData;
import com.akexorcist.myapplication.utility.Utility;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomActivity extends BaseActivity implements View.OnClickListener, MessageAdapter.OnMessageItemLongClickListener {
    private TextView tvUserName;
    private EditText etMessage;
    private ImageButton btnSendMessage;
    private RecyclerView rvMessage;
    private ProgressBar pbLoading;
    private MessageAdapter messageAdapter;
    private List<MessageData> messageDataList;

    // TODO Firebase Auth (1) : Declare FirebaseAuth instance
    // TODO Firebase Database (1) : Declare FirebaseDatabase instance
    // TODO Firebase Database (2) : Declare DatabaseReference for message data
    // TODO Firebase Remote Config (1) : Declare FirebaseRemoteConfig instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        setupFirebaseInstance();
        bindView();
        setupView();
        setupRemoteConfig();
        setupRealtimeDatabase();
        checkUserAuthentication();
    }

    private void bindView() {
        tvUserName = (TextView) findViewById(R.id.tv_user_name);
        etMessage = (EditText) findViewById(R.id.et_message);
        btnSendMessage = (ImageButton) findViewById(R.id.btn_send_message);
        rvMessage = (RecyclerView) findViewById(R.id.rv_message);
        pbLoading = (ProgressBar) findViewById(R.id.pb_loading);
    }

    private void setupView() {
        showLoading();
        btnSendMessage.setOnClickListener(this);
        tvUserName.setText(String.format("%s %s", getString(R.string.sign_in_as), getCurrentUserEmail()));

        messageDataList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageDataList, getCurrentUserEmail());
        messageAdapter.setOnItemLongClickListener(this);
        rvMessage.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvMessage.setAdapter(messageAdapter);
    }

    private void setupFirebaseInstance() {
        // TODO Firebase Auth (2) : Setup FirebaseAuth instance
        // TODO Firebase Database (3) : Setup FirebaseDatabase instance
        // TODO Firebase Remote Config (2) : Setup FirebaseRemoteConfig instance
    }

    private void setupRealtimeDatabase() {
        // TODO Firebase Database (4) : Get reference from child with database reference key
        // TODO Firebase Database (5) : Add child event listener
        // TODO Firebase Database (6) : Add value event listener
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // TODO Firebase Database (7) : Remove child event listener
    }

    // TODO Firebase Database (8) : Create ValueEventListener and remove value event listener when onDataChange was called then hide loading dialog. If cancelled show popup message with something error in realtime database message
    // TODO Firebase Database (9) : Create ChildEventListener and call addMessageData when onChildAdded. If onChildRemoved was called, remove message by data snapshot key. If cancelled show popup message with something error in realtime database message


    private void setupRemoteConfig() {
        // TODO Firebase Remote Config (3) : Create FirebaseRemoteConfigSettings from builder and enable developer mode
        // TODO Firebase Remote Config (4) : Setup config settings and defaults with xml resource
        // TODO Firebase Remote Config (5) : Fetch config from Firebase Server. If success, Activated fetched config and update special user feature
    }

    private void addMessageData(String key, Message message) {
        MessageData messageData = new MessageData(key, message);
        messageDataList.add(messageData);
        updateMessageView();
    }

    private void removeMessageByKey(String key) {
        for (MessageData messageData : messageDataList) {
            if (key.equals(messageData.getKey())) {
                messageDataList.remove(messageData);
                updateMessageView();
                return;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat_room, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_sign_out) {
            signOut();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if (view == btnSendMessage) {
            String message = etMessage.getText().toString();
            sendMessage(message);
        }
    }

    @Override
    public void onMessageItemLongClick(int position) {
        removeMessageItemByPosition(position);
    }

    private void sendMessage(String message) {
        if (Utility.isMessageValidated(message)) {
            etMessage.setText("");
            hideKeyboard();
            addNewMessage(message);
        }
    }

    private void addNewMessage(String text) {
        Message message = new Message(text, getCurrentUserEmail());
        // TODO Firebase Database (10) : Add message with push() and set message data value
    }

    private void removeMessageItemByPosition(int position) {
        // TODO Firebase Database (11) : Get database reference from child by key then remove it with removeValue()
    }

    private void updateMessageView() {
        messageAdapter.notifyDataSetChanged();
        rvMessage.smoothScrollToPosition(rvMessage.getAdapter().getItemCount());
    }

    private void signOut() {
        EventTrackerManager.onLogout(this, getCurrentUserEmail());
        // TODO Firebase Auth (3) : Sign out
        goToLogin();
    }

    private void checkUserAuthentication() {
        // TODO Firebase Auth (4) : Get current user. If current user unavailable.
        if (true) {
            showPopupMessage(R.string.please_sign_in);
            finish();
        }
    }

    private String getCurrentUserEmail() {
        // TODO Firebase Auth (5) : Get current user email (with avoid NPE code). If current user instance unavailable, return empty string
        return "";
    }

    private void updateSpecialUserFeature() {
        // TODO Firebase Remote Config (6) : Get boolean config from special user enable key, then call setSpecialUser(special_user_enable)
    }

    private void showLoading() {
        pbLoading.setVisibility(View.VISIBLE);
        etMessage.setEnabled(false);
        btnSendMessage.setEnabled(false);
    }

    private void hideLoading() {
        pbLoading.setVisibility(View.GONE);
        etMessage.setEnabled(true);
        btnSendMessage.setEnabled(true);
    }

    private void setSpecialUser(boolean isSpecialUser) {
        if (messageAdapter != null) {
            messageAdapter.setSpecialUser(isSpecialUser);
            messageAdapter.notifyDataSetChanged();
        }
    }

    private void goToLogin() {
        openActivity(LoginActivity.class);
    }
}
