package com.example.group25_hw06;

/**
 * Assignment #: HW06
 * File Name: Group25_HW06 MainActivity.java
 * Full Name: Kristin Pflug
 */

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements LoginFragment.LoginFragmentListener, RegisterFragment.RegisterFragmentListener, ForumsFragment.ForumFragmentListener, CreateForumFragment.CreateForumFragmentListener {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() == null){
            getSupportFragmentManager().beginTransaction().add(R.id.rootView, new LoginFragment()).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.rootView, new ForumsFragment()).commit();
        }
    }

    @Override
    public void goToForumsFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.rootView, new ForumsFragment()).commit();
    }

    @Override
    public void goToCreateAccountFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.rootView, new RegisterFragment()).commit();
    }

    @Override
    public void cancelToLogin() {
        getSupportFragmentManager().beginTransaction().replace(R.id.rootView, new LoginFragment()).commit();
    }

    @Override
    public void logout() {
        getSupportFragmentManager().beginTransaction().replace(R.id.rootView, new LoginFragment()).commit();
    }

    @Override
    public void goToCreateForum() {
        getSupportFragmentManager().beginTransaction().replace(R.id.rootView, new CreateForumFragment()).addToBackStack(null).commit();
    }

    @Override
    public void goToForumDetails(String forumID) {
        getSupportFragmentManager().beginTransaction().replace(R.id.rootView, ForumDetailsFragment.newInstance(forumID)).addToBackStack(null).commit();
    }

    @Override
    public void cancelToForums() {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void submitNewForum() {
        getSupportFragmentManager().popBackStack();
    }
}