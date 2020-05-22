package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    private Button mSignIn, mCreateAccount;
    private EditText mEmail, mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //initializing ui components
        mSignIn = findViewById(R.id.button_sign_in);
        mCreateAccount = findViewById(R.id.button_create_account);
        mEmail = findViewById(R.id.enter_email);
        mPassword = findViewById(R.id.enter_password);

        //hide action bar
        getSupportActionBar().hide();

        //set on click listeners for sign in and register buttons
        onSignInClick();
        onRegisterClick();
    }

    private void onRegisterClick() {
        mCreateAccount.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void onSignInClick() {
        mSignIn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(userIsRegistered()){
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private boolean userIsRegistered() {
        //TODO: add firebase logic for checking if user is registered
        return true;
    }
}
