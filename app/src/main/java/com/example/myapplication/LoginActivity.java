package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.database.FirebaseHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "fire";
    private Button mSignIn, mCreateAccount;
    private EditText mEmail, mPassword;
    private FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //initialize firebase helper
        firebaseHelper = new FirebaseHelper();

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
                signInUser();
            }
        });
    }

    private void signInUser() {
        //TODO: firebase logic for checking if user is registered

        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();

        email = email.trim();
        password = password.trim();

        if (email.isEmpty() || password.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setMessage(R.string.login_error_message)
                    .setTitle(R.string.login_error_title)
                    .setPositiveButton(android.R.string.ok, null);
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            boolean test = firebaseHelper.isSignedIn(email, password, LoginActivity.this);
            if(test){

                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
            }else{
                Toast.makeText(LoginActivity.this, "Authentication failed.",
                        Toast.LENGTH_SHORT).show();
            }
        }

    }
}
