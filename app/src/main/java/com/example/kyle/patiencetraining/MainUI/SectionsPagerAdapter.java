package com.example.kyle.patiencetraining.MainUI;

import com.example.kyle.patiencetraining.Reward.LockedReward.LockedFragment;
import com.example.kyle.patiencetraining.Reward.UnlockedReward.UnlockedFragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class SectionsPagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;

    SectionsPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new LockedFragment();
            case 1:
                return new UnlockedFragment();
            case 2:
                return new LeaderboardFragment();
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
