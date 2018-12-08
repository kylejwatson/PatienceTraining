package com.example.kyle.patiencetraining.MainUI;

import android.os.Bundle;

import com.example.kyle.patiencetraining.Reward.LockedReward.LockedFragment;
import com.example.kyle.patiencetraining.Reward.UnlockedReward.UnlockedFragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class SectionsPagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;
    private long rewardID;
    public static final String ARG_REWARD_ID = "click_reward_argument";

    SectionsPagerAdapter(FragmentManager fm, int NumOfTabs, long rewardID) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.rewardID = rewardID;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new LockedFragment();
            case 1:
                Bundle args = new Bundle();
                args.putLong(ARG_REWARD_ID, rewardID);
                Fragment fragment = new UnlockedFragment();
                fragment.setArguments(args);
                return fragment;
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
