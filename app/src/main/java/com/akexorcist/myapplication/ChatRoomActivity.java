package com.akexorcist.myapplication;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.akexorcist.myapplication.adpter.MessageAdapter;
import com.akexorcist.myapplication.common.BaseActivity;
import com.akexorcist.myapplication.constant.FirebaseKey;
import com.akexorcist.myapplication.manager.EventTrackerManager;
import com.akexorcist.myapplication.manager.SoundManager;
import com.akexorcist.myapplication.manager.VibrationManager;
import com.akexorcist.myapplication.model.ChatRoom;
import com.akexorcist.myapplication.model.MessageItem;
import com.akexorcist.myapplication.utility.Utility;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomActivity extends BaseActivity implements View.OnClickListener, MessageAdapter.OnMessageItemLongClickListener {
    private TextView tvUserName;
    private EditText etMessage;
    private ImageButton btnSendMessage;
    private RecyclerView rvMessage;
    private ProgressBar pbLoading;
    private MessageAdapter messageAdapter;
    private ChatRoom chatRoom;
    private DatabaseReference messageDatabaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        checkUserAuthentication();
        bindView();
        setupView();
        setupRealtimeDatabase();
        setupRemoteConfig();
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
    }

    private void setupRealtimeDatabase() {
        DatabaseReference rootDatabaseReference = FirebaseDatabase.getInstance().getReference();
        messageDatabaseReference = rootDatabaseReference.child(FirebaseKey.CHAT_DATABASE_REFERENCE_KEY);
        messageDatabaseReference.addValueEventListener(messageValueEventListener);
    }

    private void setupRemoteConfig() {
        FirebaseRemoteConfigSettings firebaseRemoteConfigSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(false)
                .build();

        FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        firebaseRemoteConfig.setConfigSettings(firebaseRemoteConfigSettings);
        firebaseRemoteConfig.setDefaults(R.xml.remote_config_default);
        firebaseRemoteConfig.fetch(0).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                FirebaseRemoteConfig.getInstance().activateFetched();
                updateSpecialUserFeature();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        messageDatabaseReference.removeEventListener(messageValueEventListener);
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

    private ValueEventListener messageValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            ChatRoom chatRoom = dataSnapshot.getValue(ChatRoom.class);
            updateChatList(chatRoom);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            showPopupMessage(R.string.something_error_in_realtime_database);
        }
    };

    private void updateChatList(ChatRoom chatRoom) {
        if (chatRoom == null) {
            chatRoom = new ChatRoom();
            chatRoom.setMessageItemList(new ArrayList<MessageItem>());
        }
        if (this.chatRoom == null) {
            setupChatList(chatRoom);
            updateSpecialUserFeature();
            this.chatRoom = chatRoom;
        } else {
            this.chatRoom.setMessageItemList(chatRoom.getMessageItemList());
        }
        refreshMessageItem();
        playMessageIncomingEffect();
        scrollChatListToLastChat();
        hideLoading();
    }

    private void sendMessage(String message) {
        if (Utility.isMessageValidated(message)) {
            clearMessageBox();
            hideKeyboard();
            addNewMessageToMessageList(message);
        }
    }

    private void addNewMessageToMessageList(String message) {
        if (chatRoom != null) {
            MessageItem messageItem = new MessageItem(message, getCurrentUserEmail());
            chatRoom.addMessageItem(messageItem);
            updateMessageDatabaseReference(chatRoom);
        }
    }

    private void updateMessageDatabaseReference(ChatRoom chatRoom) {
        messageDatabaseReference.setValue(chatRoom);
    }

    private void removeMessageItemByPosition(int position) {
        List<MessageItem> messageItemList = chatRoom.getMessageItemList();
        if (messageItemList != null && messageItemList.size() > position) {
            messageItemList.remove(position);
            updateMessageDatabaseReference(chatRoom);
            showLoading();
        }
    }

    private void signOut() {
        EventTrackerManager.onLogout(this, getCurrentUserEmail());
        goToLogin();
        FirebaseAuth.getInstance().signOut();
    }

    private void goToLogin() {
        openActivity(LoginActivity.class);
    }

    private void clearMessageBox() {
        if (etMessage != null) {
            etMessage.setText("");
        }
    }

    private void scrollChatListToLastChat() {
        rvMessage.smoothScrollToPosition(rvMessage.getAdapter().getItemCount());
    }

    private void checkUserAuthentication() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            showPopupMessage(R.string.please_sign_in);
            finish();
        }
    }

    private String getCurrentUserEmail() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            return firebaseUser.getEmail();
        }
        return "";
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

    private void setupChatList(ChatRoom chatRoom) {
        messageAdapter = new MessageAdapter(chatRoom, getCurrentUserEmail());
        messageAdapter.setOnItemLongClickListener(this);
        rvMessage.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvMessage.setAdapter(messageAdapter);
    }

    private void refreshMessageItem() {
        messageAdapter.notifyDataSetChanged();
    }

    private void updateSpecialUserFeature() {
        setSpecialUser(FirebaseRemoteConfig.getInstance().getBoolean(FirebaseKey.SPECIAL_USER_ENABLE));
    }

    private void setSpecialUser(boolean isSpecialUser) {
        if (messageAdapter != null) {
            Log.e("Check", "is Special : " + isSpecialUser);
            messageAdapter.setSpecialUser(isSpecialUser);
            messageAdapter.notifyDataSetChanged();
        }
    }

    private void playMessageIncomingEffect() {
        String filePath = "effect/incoming_message.mp3";
        SoundManager.getInstance().play(SoundManager.getAssetFileDescriptor(this, filePath), null);
        VibrationManager.vibrate(this);
    }
}
