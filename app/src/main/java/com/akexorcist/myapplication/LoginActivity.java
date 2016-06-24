package com.akexorcist.myapplication;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.akexorcist.myapplication.common.BaseActivity;
import com.akexorcist.myapplication.manager.DialogManager;
import com.akexorcist.myapplication.utility.Utility;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private EditText etUsername;
    private EditText etPassword;
    private Button btnSignIn;
    private Button btnSignUp;

    // TODO Firebase Auth (1) : Declare FirebaseAuth instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindView();
        setupView();
        setupFirebaseAuth();
        checkAlreadyLoggedIn();
    }

    private void bindView() {
        etUsername = (EditText) findViewById(R.id.et_username);
        etPassword = (EditText) findViewById(R.id.et_password);
        btnSignIn = (Button) findViewById(R.id.btn_sign_in);
        btnSignUp = (Button) findViewById(R.id.btn_sign_up);
    }

    private void setupView() {
        btnSignIn.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
    }

    private void setupFirebaseAuth() {
        // TODO Firebase Auth (2) : Setup FirebaseAuth instance
    }

    private void checkAlreadyLoggedIn() {
        // TODO Firebase Auth (3) : Check current user and open chat room if current user is available
        if (true) {
            goToChatRoom();
        }
    }

    @Override
    public void onClick(View view) {
        hideKeyboard();
        if (view == btnSignIn) {
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();
            signIn(username, password);
        } else if (view == btnSignUp) {
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();
            signUp(username, password);
        }
    }

    private void signUp(@NonNull String username, @NonNull String password) {
        if (isUsernameAndPasswordValidated(username, password)) {
            showLoadingDialog();

            // TODO Firebase Auth (4) : Create user with email and password. If success, check user available then dismiss dialog and go to chat room. If failure, dismiss dialog and show error with bottom message (snackbar)
        }
    }

    private void signIn(@NonNull String username, @NonNull String password) {
        if (isUsernameAndPasswordValidated(username, password)) {
            showLoadingDialog();

            // TODO Firebase Auth (5) : Sign in with email and password. If success, check user available then dismiss dialog and go to chat room. If failure, dismiss dialog and show error with bottom message (snackbar)
        }
    }

    private void dismissLoadingDialog() {
        DialogManager.getInstance().dismissDialog();
    }

    private void showLoadingDialog() {
        DialogManager.getInstance().showLoading(this);
    }

    private void goToChatRoom() {
        openActivity(ChatRoomActivity.class);
    }

    private boolean isUsernameAndPasswordValidated(String username, String password) {
        if (Utility.isUsernameAndPasswordEmpty(username, password)) {
            showBottomMessage(R.string.please_insert_username_password);
            return false;
        }
        if (Utility.isUsernameAndPasswordLessThan6Charactor(username, password)) {
            showBottomMessage(R.string.username_password_contain_at_least_6_characters);
            return false;
        }
        return true;
    }
}
