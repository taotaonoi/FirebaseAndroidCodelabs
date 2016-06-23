package com.akexorcist.myapplication;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.akexorcist.myapplication.common.BaseActivity;
import com.akexorcist.myapplication.manager.DialogManager;
import com.akexorcist.myapplication.utility.Utility;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private EditText etUsername;
    private EditText etPassword;
    private Button btnSignIn;
    private Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindView();
        setupView();
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

    private void checkAlreadyLoggedIn() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
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
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(username, password)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            FirebaseUser user = authResult.getUser();
                            if (user != null) {
                                dismissLoadingDialog();
                                goToChatRoom();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dismissLoadingDialog();
                            showBottomMessage(e.getMessage());
                        }
                    });
        }
    }

    private void signIn(@NonNull String username, @NonNull String password) {
        if (isUsernameAndPasswordValidated(username, password)) {
            showLoadingDialog();
            FirebaseAuth.getInstance().signInWithEmailAndPassword(username, password)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            FirebaseUser user = authResult.getUser();
                            if (user != null) {
                                dismissLoadingDialog();
                                goToChatRoom();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dismissLoadingDialog();
                            showBottomMessage(e.getMessage());
                        }
                    });
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
