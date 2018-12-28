package com.example.kyle.patiencetraining.Util;


import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface ScoreDao {
    @Query("SELECT * FROM score")
    List<Score> getAllScores();

    @Query("SELECT * FROM score WHERE uploaded = 0")
    List<Score> getLocalScores();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertScore(Score... scores);

    @Update
    void updateScores(Score... scores);
}
