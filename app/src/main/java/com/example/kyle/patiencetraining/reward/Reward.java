package com.example.kyle.patiencetraining.reward;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "reward")
public class Reward implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "price")
    private float price;
    @ColumnInfo(name = "start")
    private long start;
    @ColumnInfo(name = "finish")
    private long finish;
    @ColumnInfo(name = "link")
    private String link;
    @ColumnInfo(name = "imageLink")
    private String imageLink;
    @ColumnInfo(name = "imagePath")
    private String imagePath;
    @ColumnInfo(name = "notificationSet")
    private boolean notificationSet;
    @ColumnInfo(name = "notificationJobId")
    private int notificationJobId;
    @ColumnInfo(name = "finished")
    private boolean finished;

    public Reward(String name, float price, long start, long finish, String link, String imagePath, boolean notificationSet) {
        this.name = name;
        this.price = price;
        this.start = start;
        this.finish = finish;
        this.link = link;
        this.imagePath = imagePath;
        this.notificationSet = notificationSet;
        notificationJobId = new Date(start).hashCode();
        finished = false;
        imageLink = "";
    }

    protected Reward(Parcel in) {
        id = in.readLong();
        name = in.readString();
        price = in.readFloat();
        start = in.readLong();
        finish = in.readLong();
        link = in.readString();
        imageLink = in.readString();
        imagePath = in.readString();
        notificationSet = in.readByte() != 0;
        notificationJobId = in.readInt();
        finished = in.readByte() != 0;
    }

    public static final Creator<Reward> CREATOR = new Creator<Reward>() {
        @Override
        public Reward createFromParcel(Parcel in) {
            return new Reward(in);
        }

        @Override
        public Reward[] newArray(int size) {
            return new Reward[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getFinish() {
        return finish;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public boolean isNotificationSet() {
        return notificationSet;
    }

    public void setNotificationSet(boolean notificationSet) {
        this.notificationSet = notificationSet;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(name);
        parcel.writeFloat(price);
        parcel.writeLong(start);
        parcel.writeLong(finish);
        parcel.writeString(link);
        parcel.writeString(imageLink);
        parcel.writeString(imagePath);
        parcel.writeByte((byte) (notificationSet ? 1 : 0));
        parcel.writeInt(notificationJobId);
        parcel.writeByte((byte) (finished ? 1 : 0));
    }

    public int getNotificationJobId() {
        return notificationJobId;
    }

    void setNotificationJobId(int notificationJobId) {
        this.notificationJobId = notificationJobId;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }
}
