package com.byl.mvvm.ui.main.adapter;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;


public class FragmentPageAdapter extends FragmentStatePagerAdapter {
    private Context mContext;
    private List<Fragment> mFragments;
    private String[] mFragmentTitles;

    public FragmentPageAdapter(Context context, FragmentManager fm, List<Fragment> fragments, String[] titles) {
        super(fm);
        this.mContext = context;
        mFragments = fragments;
        mFragmentTitles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitles[position];
    }

}
