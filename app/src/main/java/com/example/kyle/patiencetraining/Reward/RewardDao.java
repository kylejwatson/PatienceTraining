package com.example.kyle.patiencetraining.Reward;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface RewardDao {
    @Query("SELECT * FROM reward")
    List<Reward> getAllRewards();

    @Insert
    void insertRewards(Reward rewards);

    @Delete
    void deleteRewards(Reward rewards);

    @Update
    void updateRewards(Reward rewards);
}
