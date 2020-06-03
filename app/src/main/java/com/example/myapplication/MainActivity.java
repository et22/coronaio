package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.myapplication.adapters.BNViewPagerAdapter;
import com.example.myapplication.fragments.HomeFragment;
import com.example.myapplication.fragments.SettingsFragment;
import com.example.myapplication.fragments.StatsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1;
    BottomNavigationView bottomNav;
    ViewPager viewPager;
    ArrayList<Fragment> fragments;
    BNViewPagerAdapter bnViewPagerAdapter;
    private String TAG = "fragments";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //hide action bar
        getSupportActionBar().hide();
        //bottomNav comp
        bottomNav = findViewById(R.id.navigation);
        bottomNav.setItemIconTintList(null);
        viewPager = findViewById(R.id.view_pager);

        fragments = new ArrayList<>();
        fragments.add(new HomeFragment(getWindow()));
        fragments.add(new StatsFragment());
        fragments.add(new SettingsFragment());

        bnViewPagerAdapter = new BNViewPagerAdapter(this.getSupportFragmentManager(), fragments);
        viewPager.setAdapter(bnViewPagerAdapter);

        viewPager.addOnPageChangeListener(viewPagerListener);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        if (!checkPermission())
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);

    }
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            finish();
        }
    }
    private boolean checkPermission(){
        int allowed = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (allowed == PackageManager.PERMISSION_GRANTED) return true;
        else return false;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()) {
                        case R.id.nav_start:
                            viewPager.setCurrentItem(0);
                            // true means that we want to select the clicked item
                            return true;
                        case R.id.nav_history:
                            viewPager.setCurrentItem(1);
                            return true;

                        case R.id.nav_settings:
                            viewPager.setCurrentItem(2);
                    }
                    return false;
                }
            };

    //https://stackoverflow.com/questions/58128491/how-to-combine-bottomnavigationview-and-viewpager
    private ViewPager.OnPageChangeListener viewPagerListener =
            new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    switch (position) {
                        case 0:
                            bottomNav.getMenu().findItem(R.id.nav_start).setChecked(true);
                            break;
                        case 1:
                            bottomNav.getMenu().findItem(R.id.nav_history).setChecked(true);
                            break;
                        case 2:
                            bottomNav.getMenu().findItem(R.id.nav_settings).setChecked(true);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            };
}
