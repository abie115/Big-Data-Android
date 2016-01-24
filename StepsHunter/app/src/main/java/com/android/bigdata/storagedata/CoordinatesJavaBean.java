package com.android.bigdata.storagedata;

import com.google.gson.annotations.SerializedName;

public class CoordinatesJavaBean {

    @SerializedName("time")
    private String time;

    @SerializedName("lat")
    private Double latitude;

    @SerializedName("lon")
    private Double longitude;

    public CoordinatesJavaBean() {
    }

    public CoordinatesJavaBean(String time, Double latitude, Double longitude) {
        setLatitude(latitude);
        setLongitude(longitude);
        setTime(time);
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

}
