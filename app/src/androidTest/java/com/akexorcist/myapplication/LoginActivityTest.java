package com.akexorcist.myapplication;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by Akexorcist on 6/24/2016 AD.
 */

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {
    LoginActivity activity;

    @Rule
    public final ActivityTestRule<LoginActivity> activityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Before
    public void initActivity() {
        activity = activityTestRule.getActivity();
    }

    @Test
    public void validateUsernameAndPassword_Success() {
        String username = "Akexorcist";
        String password = "1234567";
        boolean isValidate = activity.isUsernameAndPasswordValidated(username, password);
        Assert.assertTrue(isValidate);
    }

    @Test
    public void validateUsernameAndPassword_PasswordFailure() {
        String username = "Akexorcist";
        String password = "";
        boolean isValidate = activity.isUsernameAndPasswordValidated(username, password);
        Assert.assertFalse(isValidate);
    }

    @Test
    public void validateUsernameAndPassword_UsernameFailure() {
        String username = "Ake";
        String password = "123456789";
        boolean isValidate = activity.isUsernameAndPasswordValidated(username, password);
        Assert.assertFalse(isValidate);
    }

    @Test
    public void validateUsernameAndPassword_UsernameAndPasswordFailure() {
        String username = "Ake";
        String password = "321";
        boolean isValidate = activity.isUsernameAndPasswordValidated(username, password);
        Assert.assertFalse(isValidate);
    }
}