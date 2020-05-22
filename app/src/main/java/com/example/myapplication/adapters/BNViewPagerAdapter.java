package com.example.myapplication.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

/* BNViewPager Adapter is a simple implementation of FragmentPagerAdapter
 * that allows the user to swith between the start and history fragments.
 */
public class BNViewPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> fragments;

    public BNViewPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments){
            super(fm);
            this.fragments = fragments;
        }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

}
