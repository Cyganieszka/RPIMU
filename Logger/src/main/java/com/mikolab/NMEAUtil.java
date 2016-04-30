package com.mikolab;

import com.mikolab.database.GpsPosition;
import com.mikolab.database.NmeaParser;

/**
 * Created by User on 2016-04-26.
 */
public class NMEAUtil {

    final NmeaParser parser= new NmeaParser();

    public void printPosition(String nmea){
        nmea=nmea.replace("\n","");
        if(nmea.length()==0)return;
        System.out.print("nmea  -> "+nmea+"\n");

        GpsPosition position = parser.parse(nmea);
        if(position!=null) {
            System.out.print("parsed-> "+position+"\n");
        }

    }
}
