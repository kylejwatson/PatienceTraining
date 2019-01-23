package com.example.kyle.patiencetraining.reward;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface RewardDao {
    @Query("SELECT * FROM reward")
    LiveData<List<Reward>> getAllRewards();

    @Query("SELECT * FROM reward WHERE finish >= :time")
    LiveData<List<Reward>> getRewardsAfter(long time);

    @Query("SELECT * FROM reward WHERE finish <= :time")
    LiveData<List<Reward>> getRewardsBefore(long time);

    @Insert
    void insertRewards(Reward rewards);

    @Delete
    void deleteRewards(Reward rewards);

    @Update
    void updateRewards(Reward rewards);
}
