package com.example.kyle.patiencetraining.Util;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "score")
public class Score {
    @PrimaryKey()
    private long id;

    @ColumnInfo(name = "time")
    private long time;

    @ColumnInfo(name = "uploaded")
    private int uploaded;

    public Score(long id, long time, int uploaded) {
        this.id = id;
        this.time = time;
        this.uploaded = uploaded;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getUploaded() {
        return uploaded;
    }

    public void setUploaded(int uploaded) {
        this.uploaded = uploaded;
    }
}
