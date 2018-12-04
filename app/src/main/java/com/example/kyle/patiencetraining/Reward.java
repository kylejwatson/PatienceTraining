package com.example.kyle.patiencetraining;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Reward implements Parcelable {
    private String name;
    private float price;
    private Date start;
    private Date finish;
    private String link;
    private Uri imagePath;
    private boolean notificationSet;
    private int notificationJobId;

    public Reward(String name, float price, Date start, Date finish, String link, Uri imagePath, boolean notificationSet) {
        this.name = name;
        this.price = price;
        this.start = start;
        this.finish = finish;
        this.link = link;
        this.imagePath = imagePath;
        this.notificationSet = notificationSet;
    }

    protected Reward(Parcel in) {
        name = in.readString();
        price = in.readFloat();
        start = new Date(in.readLong());
        finish = new Date(in.readLong());
        link = in.readString();
        imagePath = in.readParcelable(Uri.class.getClassLoader());
        notificationSet = in.readByte() != 0;
        notificationJobId = in.readInt();
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

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getFinish() {
        return finish;
    }

    public void setFinish(Date finish) {
        this.finish = finish;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Uri getImagePath() {
        return imagePath;
    }

    public void setImagePath(Uri imagePath) {
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
        parcel.writeString(name);
        parcel.writeFloat(price);
        parcel.writeLong(start.getTime());
        parcel.writeLong(finish.getTime());
        parcel.writeString(link);
        parcel.writeParcelable(imagePath,i);
        parcel.writeByte((byte) (notificationSet ? 1 : 0));
        parcel.writeInt(notificationJobId);
    }

    public int getNotificationJobId() {
        return notificationJobId;
    }

    public void setNotificationJobId(int notificationJobId) {
        this.notificationJobId = notificationJobId;
    }
}
