package com.mikolab.Location;

import com.mikolab.Location.nmeaFrames.GPGGA;
import com.mikolab.Location.nmeaFrames.GPRMC;
import com.mikolab.Location.nmeaFrames.GPVTG;
import com.mikolab.Location.nmeaFrames.NMEA;

import java.util.ArrayList;

/**
 * Created by User on 2016-01-11.
 */
public class GpsPosition {

    String type="";

    static int idx=0;

    long id=-1;

    ArrayList<NMEA> frames=new ArrayList<NMEA>();

    public Float utcTime = 0.0f;
    public Integer date = 0;
    //public long timestamp = 0;

    public Float lat = 0.0f;
    public Float lon = 0.0f;
    public Boolean fixed = false;
    public Float course = 0.0f;
    public Float altitude = 0.0f;
    public Float speed = 0.0f;

    public boolean hasPosition(){
        return lat!=null && lon !=0;
    }

    public String toString() {
        return String.format("%d, %f, %f, %f, %f, %f, %f",date, utcTime, lat, lon, course, altitude, speed);
    }
    public String toJson() {
        return "";
    }

    public void print(){

        System.out.println("----------------------------------------------------------------------------");

        System.out.println(String.format("idx: %d, Position at date: %d time: %f",idx,date,utcTime));
        System.out.println(String.format("Lattitude: %f,  Longitude: %f, Altitude: %f, Speed: %f, Course: %f",lat,lon,altitude,speed,course));
        System.out.println("Assigned NMEA Frames:");
        for(NMEA n :frames){
            System.out.println(n.getMessage());
        }
        System.out.println("----------------------------------------------------------------------------");
    }

    public GpsPosition(){
        idx++;
        //System.out.println("created frame "+idx);
    }

    public boolean canAdd(GPGGA frame){

        if(this.utcTime.equals(frame.getUtcTime())){
            return true;
        }else{
            return false;
        }

    }

    public boolean canAdd(GPRMC frame){

        if(this.utcTime.equals(frame.getUtcTime())){
            return true;
        }else{
            return false;
        }

    }
    public void addFrame(GPGGA frame){
       // System.out.println("added frame "+frame.idx+" to position "+idx);
        utcTime = frame.getUtcTime();
        lat = Latitude2Decimal(frame.getLatitude(), frame.getNorS());
        lon = Longitude2Decimal(frame.getLongitude(), frame.getEorW());
        altitude = frame.getMSLAltitude();
        frames.add(frame);
    }
    public void addFrame(GPRMC frame){

     //   System.out.println("added frame "+frame.idx+" to position "+idx);
        utcTime = frame.getUtcTime();
        lat = Latitude2Decimal(frame.getLatitude(), frame.getNorS());
        lon = Longitude2Decimal(frame.getLongitude(), frame.getEorW());
        speed = frame.getSpeedOverGround();
        course = frame.getCourseOverGround();
        date=frame.getDate();
        frames.add(frame);

    }
    public void addFrame(GPVTG frame){
       // System.out.println("added frame "+frame.idx+" to position "+idx);
        frames.add(frame);
        //use later
    }

    // utils
    static float Latitude2Decimal(String lat, String NS) {
        float med = Float.parseFloat(lat.substring(2))/60.0f;
        med +=  Float.parseFloat(lat.substring(0, 2));
        if(NS.startsWith("S")) {
            med = -med;
        }
        return med;
    }

    static float Longitude2Decimal(String lon, String WE) {
        float med = Float.parseFloat(lon.substring(3))/60.0f;
        med +=  Float.parseFloat(lon.substring(0, 3));
        if(WE.startsWith("W")) {
            med = -med;
        }
        return med;
    }

}
