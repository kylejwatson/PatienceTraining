package com.example.kyle.patiencetraining;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class SectionsPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    public RewardFragment lockedFragment;
    public RewardFragment unlockedFragment;

    public SectionsPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        lockedFragment = new LockedFragment();
        unlockedFragment = new UnlockedFragment();
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new LockedFragment();
            case 1:
                return new UnlockedFragment();
            case 2:
                //leaderboard fragment
            default:
                return null;
        }
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
