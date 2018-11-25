package com.example.kyle.patiencetraining;

import java.util.Date;

public class Reward {
    private String name;
    private float price;
    private Date start;
    private Date finish;
    private String link;
    private String imagePath;
    private boolean notificationSet;

    public Reward(String name, float price, Date start, Date finish, String link, String imagePath, boolean notificationSet) {
        this.name = name;
        this.price = price;
        this.start = start;
        this.finish = finish;
        this.link = link;
        this.imagePath = imagePath;
        this.notificationSet = notificationSet;
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
}
