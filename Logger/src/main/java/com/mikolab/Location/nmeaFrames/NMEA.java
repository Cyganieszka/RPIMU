package com.mikolab.Location.nmeaFrames;

import com.mikolab.Location.NMEA_TYPE;

/**
 * Created by agnieszka on 30.04.2016.
 */
public abstract class NMEA {

    public static int idx=0;

    String message;

    public String getMessage() {
        return message;
    }

    public static NMEA_TYPE getFrameType(String msg){

        if(msg.contains("GPGGA")) return NMEA_TYPE.GPGGA;
        if(msg.contains("GPRMC")) return NMEA_TYPE.GPRMC;
        if(msg.contains("GPVTG")) return NMEA_TYPE.GPVTG;
        return null;

    }



    //helper methods for parsing

    protected Integer parseInt(String msg){
        if(isNotEmpty(msg)){
            return Integer.parseInt(msg);
        }else{
            return null;
        }
    }
    protected Float parseFloat(String msg){
        if(isNotEmpty(msg)){
            return Float.parseFloat(msg);
        }else{
            return null;
        }
    }
    protected  String stringOrNull(String msg){
        if(isNotEmpty(msg)){
            return msg;
        }else{
            return null;
        }
    }

    protected boolean isNotEmpty(String msg){

        return msg.length()>0;
    }
}
