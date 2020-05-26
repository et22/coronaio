package com.example.myapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.myapplication.adapters.BNViewPagerAdapter;
import com.example.myapplication.views.CoronaAnimationView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    BottomNavigationView bottomNav;
    ViewPager viewPager;
    ArrayList<Fragment> fragments;
    BNViewPagerAdapter bnViewPagerAdapter;
    private String TAG = "fragments";

    //animation
    private CoronaAnimationView mAnimationView;


    //ui components
    private Button mPlayFFA, mPlaySolo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setContentView(R.layout.activity_home);
        mAnimationView = (CoronaAnimationView) findViewById(R.id.animated_view);

        //initializing ui components
        mPlayFFA = findViewById(R.id.button_play_ffa);
        mPlaySolo = findViewById(R.id.button_play_solo);

        //hide action bar
        getSupportActionBar().hide();

        //set action bar time
        /*ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            this.setTitle("Home");
        }*/

        //creating onclicks for ffa and solo
        onClickFFA();
        onClickSolo();
        /*
        bottomNav = findViewById(R.id.navigation);
        viewPager = findViewById(R.id.view_pager);

        fragments = new ArrayList<>();
        fragments.add(new StartFragment());
        fragments.add(new StatsFragment());
        fragments.add(new SettingsFragment());

        bnViewPagerAdapter = new BNViewPagerAdapter(this.getSupportFragmentManager(), fragments);
        viewPager.setAdapter(bnViewPagerAdapter);

        viewPager.addOnPageChangeListener(viewPagerListener);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        */
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAnimationView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAnimationView.pause();
    }
    private void onClickSolo() {
        mPlaySolo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void onClickFFA() {
        mPlayFFA.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });
    }

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

                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            };

}