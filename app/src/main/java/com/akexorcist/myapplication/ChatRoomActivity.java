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
import com.akexorcist.myapplication.model.Message;
import com.akexorcist.myapplication.model.MessageData;
import com.akexorcist.myapplication.utility.Utility;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
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
    private List<MessageData> messageDataList;
    private DatabaseReference messageDatabaseReference;

    private FirebaseRemoteConfig firebaseRemoteConfig;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

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
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
    }

    private void setupRemoteConfig() {
        FirebaseRemoteConfigSettings firebaseRemoteConfigSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(true)
                .build();
        firebaseRemoteConfig.setConfigSettings(firebaseRemoteConfigSettings);
        firebaseRemoteConfig.setDefaults(R.xml.remote_config_default);
        firebaseRemoteConfig.fetch().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                firebaseRemoteConfig.activateFetched();
                updateSpecialUserFeature();
            }
        });
    }

    private void setupRealtimeDatabase() {
        messageDatabaseReference = firebaseDatabase.getReference().child(FirebaseKey.CHAT_DATABASE_REFERENCE_KEY);
        messageDatabaseReference.addValueEventListener(messageValueEventListener);
        messageDatabaseReference.addChildEventListener(messageChildEventListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        messageDatabaseReference.removeEventListener(messageChildEventListener);
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

    private void removeMessageItemByPosition(int position) {
        messageDatabaseReference.child(messageDataList.get(position).getKey()).removeValue();
    }

    private ValueEventListener messageValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            messageDatabaseReference.removeEventListener(messageValueEventListener);
            hideLoading();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            showPopupMessage(R.string.something_error_in_realtime_database);
        }
    };

    private ChildEventListener messageChildEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            String key = dataSnapshot.getKey();
            Message message = dataSnapshot.getValue(Message.class);
            MessageData messageData = new MessageData(key, message);
            messageDataList.add(messageData);
            updateMessageView();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            String key = dataSnapshot.getKey();
            for (MessageData messageData : messageDataList) {
                if (key.equals(messageData.getKey())) {
                    messageDataList.remove(messageData);
                    updateMessageView();
                    return;
                }
            }
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            showPopupMessage(R.string.something_error_in_realtime_database);
        }
    };

    private void updateMessageView() {
        messageAdapter.notifyDataSetChanged();
        rvMessage.smoothScrollToPosition(rvMessage.getAdapter().getItemCount());
    }

    private void sendMessage(String message) {
        if (Utility.isMessageValidated(message)) {
            etMessage.setText("");
            hideKeyboard();
            addNewMessage(message);
        }
    }

    private void addNewMessage(String message) {
        Message messageData = new Message(message, getCurrentUserEmail());
        messageDatabaseReference.push().setValue(messageData);
    }

    private void signOut() {
        EventTrackerManager.onLogout(this, getCurrentUserEmail());
        firebaseAuth.signOut();
        goToLogin();
    }

    private void goToLogin() {
        openActivity(LoginActivity.class);
    }

    private void checkUserAuthentication() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            showPopupMessage(R.string.please_sign_in);
            finish();
        }
    }

    private String getCurrentUserEmail() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
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

    private void updateSpecialUserFeature() {
        boolean isSpecialUser = firebaseRemoteConfig.getBoolean(FirebaseKey.SPECIAL_USER_ENABLE);
        setSpecialUser(isSpecialUser);
    }

    private void setSpecialUser(boolean isSpecialUser) {
        if (messageAdapter != null) {
            messageAdapter.setSpecialUser(isSpecialUser);
            messageAdapter.notifyDataSetChanged();
        }
    }
}
