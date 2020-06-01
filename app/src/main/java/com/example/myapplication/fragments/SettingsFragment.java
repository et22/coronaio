package com.example.myapplication.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.example.myapplication.LoginActivity;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SettingsFragment extends Fragment {
    public static final String KEY_UNIT= "cellType";
    Button mSignOut;
    //firebase
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference mDatabase;
    private String mUserId;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("life", "onCreateView");
        return inflater.inflate(R.layout.activity_settings, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("life", "onViewCreated");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        assert getFragmentManager() != null;
        getFragmentManager().beginTransaction()
                .replace(R.id.settings, new SettingsFragment.myPrefFrag()).commit();
        //Toolbar toolbar = view.findViewById(R.id.app_bar);
//        view.setSupportActionBar(toolbar);
        mSignOut = view.findViewById(R.id.button_sign_out);
        // set onclick listener for signout button
        onSignOutClick();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("life", "onResume");

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("life", "onPause");
        //We check if the user's settings for either imperial or metric
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        String s = sharedPref.getString(KEY_UNIT, "idk");
        int index = Integer.parseInt(s);
        //List<String> cellTypes = Arrays.asList(getResources().getStringArray(R.array.cell_types));
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mUserId = mUser.getUid();
        mDatabase.child("users").child(mUserId).child("cellType").setValue(index);

    }

    private void onSignOutClick() {
        mSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO have to figure out how not to return to this activity if the user presses the back button
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }


    //MyPrefFrag is a helper class that opens the settings menu using a
    //static class that implements PreferenceFragmentCompat
    public static class myPrefFrag extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.settings, rootKey);
        }
    }
}
