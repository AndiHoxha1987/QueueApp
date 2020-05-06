package com.autochthonoustech.makefor.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.autochthonoustech.makefor.ui.FindQueueFragment;
import com.autochthonoustech.makefor.ui.HistoryFragment;
import com.autochthonoustech.makefor.ui.MyQueueFragment;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {


    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0: return MyQueueFragment.newInstance(position);

            case 1: return FindQueueFragment.newInstance(position);

            case 2: return HistoryFragment.newInstance(position);

            default: return null;
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0: return "Queue";

            case 1: return "Search";

            case 2: return "History";

            default: return null;
        }
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }
}