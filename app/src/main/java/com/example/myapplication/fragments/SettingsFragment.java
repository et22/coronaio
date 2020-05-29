package com.example.myapplication.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceFragmentCompat;

import com.example.myapplication.R;
import com.example.myapplication.SettingsActivity;

public class SettingsFragment extends Fragment {
    public static final String KEY_UNIT= "unitpref";

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
                .replace(R.id.settings, new SettingsActivity.myPrefFrag()).commit();
//        Toolbar toolbar = view.findViewById(R.id.app_bar);
//        view.setSupportActionBar(toolbar);
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
