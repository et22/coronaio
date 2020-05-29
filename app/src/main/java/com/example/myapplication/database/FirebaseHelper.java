package com.example.myapplication.database;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseHelper {
    public static String TAG = "firebase2";

    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private String mUserId;

    public FirebaseHelper(){
        mAuth = FirebaseAuth.getInstance();
    }

    public String addNewUser(String email, String password, final String userName, final int cellType, Activity activity){
        final String[] result = new String[1];
        mAuth.createUserWithEmailAndPassword(email,
                password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, save the user's username and cellType
                            Log.d(TAG, "createUserWithEmail:success");
                            mFirebaseUser = mAuth.getCurrentUser();
                            mDatabase = FirebaseDatabase.getInstance().getReference();

                            mUserId = mFirebaseUser.getUid();

                            mDatabase.child("users").child(mUserId).child("userName").push().setValue(userName);
                            mDatabase.child("users").child(mUserId).child("cellType").push().setValue(cellType);

                            Log.d(TAG, "createUserWithEmail:failure", task.getException());
                            result[0] = "success";
                        } else {
                            // If sign in fails, send the message to the user
                            result[0] = task.getException().getMessage();
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        }
                    }
                });
        return result[0];
    }
    public boolean isSignedIn(String email, String password, Activity activity){
        final boolean[] isSuccess = new boolean[1];
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            isSuccess[0] = true;
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            isSuccess[0] = false;

                        }
                    }
                });
        return isSuccess[0];
    }

}
