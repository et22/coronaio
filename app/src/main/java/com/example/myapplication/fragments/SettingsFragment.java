package com.example.myapplication.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceFragmentCompat;

import com.example.myapplication.LoginActivity;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsFragment extends Fragment {
    Button mSignOut;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        assert getFragmentManager() != null;
        getFragmentManager().beginTransaction()
                .replace(R.id.settings, new SettingsFragment.myPrefFrag()).commit();
        //Toolbar toolbar = view.findViewById(R.id.app_bar);
//        view.setSupportActionBar(toolbar);
        mSignOut = view.findViewById(R.id.button_sign_out);
        // set onclick listener for signout button
        onSignOutClick();
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
