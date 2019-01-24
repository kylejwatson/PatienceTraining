package com.example.kyle.patiencetraining.util;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UrlInfo {

    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("favicon")
    @Expose
    private String favicon;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("images")
    @Expose
    private List<UrlImage> images = null;
    @SerializedName("videos")
    @Expose
    private List<Object> videos = null;

    /**
     * No args constructor for use in serialization
     *
     */
    public UrlInfo() {
    }

    /**
     *
     * @param title
     * @param favicon
     * @param description
     * @param videos
     * @param images
     * @param url
     */
    public UrlInfo(String url, String favicon, String title, String description, List<UrlImage> images, List<Object> videos) {
        super();
        this.url = url;
        this.favicon = favicon;
        this.title = title;
        this.description = description;
        this.images = images;
        this.videos = videos;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFavicon() {
        return favicon;
    }

    public void setFavicon(String favicon) {
        this.favicon = favicon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<UrlImage> getUrlImages() {
        return images;
    }

    public void setUrlImages(List<UrlImage> images) {
        this.images = images;
    }

    public List<Object> getVideos() {
        return videos;
    }

    public void setVideos(List<Object> videos) {
        this.videos = videos;
    }

}