package com.example.myapplication;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceFragmentCompat;

/* SettingsActivity creates a settings screen that allows the user to
 * input basic privacy settings, direct them to our website, or sign out.
 */

public class SettingsActivity extends AppCompatActivity {
    public static final String KEY_UNIT= "unitpref";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Makes the toolbar visible
        //Makes the "up" button visible and changes manifest to make it function
        setContentView(R.layout.activity_settings);
        //initialize the fragment in the settings xml file
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.settings, new myPrefFrag()).commit();
        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    //MyPrefFrag is a helper class that opens the settings menu using a
    //static class that implements PreferenceFragmentCompat
    public static class myPrefFrag extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.settings, rootKey);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
