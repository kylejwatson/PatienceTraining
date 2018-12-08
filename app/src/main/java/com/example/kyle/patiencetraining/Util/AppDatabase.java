package com.example.kyle.patiencetraining.Util;

import android.content.Context;

import com.example.kyle.patiencetraining.Reward.Reward;
import com.example.kyle.patiencetraining.Reward.RewardDao;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Reward.class},version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract RewardDao rewardDao();

    private final static String NAME_DATABASE = "patience_training_db";

    private static AppDatabase sInstance;

    public static AppDatabase getInstance(Context context){
        if(sInstance == null)
            sInstance = Room.databaseBuilder(context,
                    AppDatabase.class,NAME_DATABASE)
                    .fallbackToDestructiveMigration().build();
        return sInstance;
    }
}