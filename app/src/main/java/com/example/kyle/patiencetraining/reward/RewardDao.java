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

    @Query("SELECT * FROM reward WHERE finished = 0")
    LiveData<List<Reward>> getRewardsAfter();

    @Query("SELECT * FROM reward WHERE finished = 1")
    LiveData<List<Reward>> getRewardsBefore();

    @Query("SELECT * FROM reward WHERE finished = 0")
    List<Reward> getRewardsToSort();

    @Query("SELECT * FROM reward WHERE id = :id")
    Reward getReward(long id);

    @Insert
    void insertRewards(Reward rewards);

    @Delete
    void deleteRewards(Reward rewards);

    @Update
    void updateRewards(Reward rewards);
}
