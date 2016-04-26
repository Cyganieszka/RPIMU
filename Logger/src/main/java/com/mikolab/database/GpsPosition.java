package com.mikolab.database;

/**
 * Created by User on 2016-01-11.
 */
public class GpsPosition {

    String type="";

    long id=-1;
    public String rawNMEA = "";

    public float utcTime = 0.0f;
    public int date = 0;
    public long timestamp = 0;

    public float lat = 0.0f;
    public float lon = 0.0f;
    public boolean fixed = false;
    public float course = 0.0f;
    public float altitude = 0.0f;
    public float speed = 0.0f;

    public String toString() {
        return String.format("POSITION: lat: %f, lon: %f, utcTime: %f, course: %f, alt: %f, vel: %f", lat, lon, utcTime,  course, altitude, speed);
    }
    public String toJson() {
        return "";
    }

    public GpsPosition(){

    }
}
