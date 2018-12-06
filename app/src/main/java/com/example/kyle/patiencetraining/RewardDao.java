package com.example.kyle.patiencetraining;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface RewardDao {
    @Query("SELECT * FROM reward")
    public List<Reward> getAllRewards();

    @Insert
    public void insertRewards(Reward rewards);

    @Delete
    public void deleteRewards(Reward rewards);

    @Update
    public void updateRewards(Reward rewards);
}
