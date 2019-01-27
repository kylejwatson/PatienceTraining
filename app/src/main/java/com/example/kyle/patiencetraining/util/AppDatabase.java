package com.example.kyle.patiencetraining.util;

import android.content.Context;

import com.example.kyle.patiencetraining.reward.Reward;
import com.example.kyle.patiencetraining.reward.RewardDao;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Reward.class,Score.class},version = 3)
public abstract class AppDatabase extends RoomDatabase {
    public abstract RewardDao rewardDao();
    public abstract ScoreDao scoreDao();

    private final static String NAME_DATABASE = "patience_training_db";
    private static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE reward "
                    + " ADD COLUMN imageLink STRING");
        }
    };

    private static AppDatabase sInstance;

    public static AppDatabase getInstance(Context context){
        if(sInstance == null)
            sInstance = Room.databaseBuilder(context,
                    AppDatabase.class,NAME_DATABASE)
                    .addMigrations(MIGRATION_2_3)
                    .build();
        return sInstance;
    }
}