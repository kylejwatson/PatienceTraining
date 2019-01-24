package com.example.kyle.patiencetraining.util;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UrlImage {

    @SerializedName("src")
    @Expose
    private String src;
    @SerializedName("size")
    @Expose
    private List<Integer> size = null;
    @SerializedName("type")
    @Expose
    private String type;

    /**
     * No args constructor for use in serialization
     *
     */
    public UrlImage() {
    }

    /**
     *
     * @param src
     * @param type
     * @param size
     */
    public UrlImage(String src, List<Integer> size, String type) {
        super();
        this.src = src;
        this.size = size;
        this.type = type;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public List<Integer> getSize() {
        return size;
    }

    public void setSize(List<Integer> size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
