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
        //We check if the user's settings for the selected cell type
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        String s = sharedPref.getString(KEY_UNIT, "0");
        int index = Integer.parseInt(s);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        String mUserId = mUser.getUid();
        mDatabase.child("users").child(mUserId).child("cellType").setValue(index);

    }

    private void onSignOutClick() {
        mSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                // https://riptutorial.com/android/example/17590/clear-your-current-activity-stack-and-launch-a-new-activity
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getActivity().finish();
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
