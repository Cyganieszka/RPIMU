package com.mikolab.database;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by User on 2016-01-11.
 */
public class NmeaParser {


    interface SentenceParser {
        boolean parse(String [] tokens, GpsPosition position);
    }

    static boolean fixOK=false;

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

    // parsers
    class GPGGA implements SentenceParser {
        public boolean parse(String [] tokens, GpsPosition position) {
            if(tokens.length>9 && Integer.parseInt(tokens[7])>0) {
                position.time = Float.parseFloat(tokens[1]);
                position.lat = Latitude2Decimal(tokens[2], tokens[3]);
                position.lon = Longitude2Decimal(tokens[4], tokens[5]);
                position.quality = Integer.parseInt(tokens[6]);
                position.altitude = Float.parseFloat(tokens[9]);
                fixOK=true;
                return true;
            }
            fixOK=false;
            return false;

        }
    }

    class GPGGL implements SentenceParser {
        public boolean parse(String [] tokens, GpsPosition position) {
            if(fixOK) {
                position.lat = Latitude2Decimal(tokens[1], tokens[2]);
                position.lon = Longitude2Decimal(tokens[3], tokens[4]);
                position.time = Float.parseFloat(tokens[5]);
            }
            return true;
        }
    }

    class GPRMC implements SentenceParser {
        public boolean parse(String [] tokens, GpsPosition position) {
            if(fixOK) {
                position.time = Float.parseFloat(tokens[1]);
                position.lat = Latitude2Decimal(tokens[3], tokens[4]);
                position.lon = Longitude2Decimal(tokens[5], tokens[6]);
                position.velocity = Float.parseFloat(tokens[7]);
                position.dir = Float.parseFloat(tokens[8]);
            }
            return true;
        }
    }

    class GPVTG implements SentenceParser {
        public boolean parse(String [] tokens, GpsPosition position) {
            if(fixOK) {
                position.dir = Float.parseFloat(tokens[3]);
            }
            return true;
        }
    }

    class GPRMZ implements SentenceParser {
        public boolean parse(String [] tokens, GpsPosition position) {
            if(fixOK) {
                position.altitude = Float.parseFloat(tokens[1]);
            }
            return true;
        }
    }



    static GpsPosition position;

    private final Map<String, SentenceParser> sentenceParsers = new HashMap<String, SentenceParser>();

    public NmeaParser() {
        sentenceParsers.put("GPGGA", new GPGGA());
        sentenceParsers.put("GPGGL", new GPGGL());
        sentenceParsers.put("GPRMC", new GPRMC());
        sentenceParsers.put("GPRMZ", new GPRMZ());
        //only really good GPS devices have this sentence but ...
        sentenceParsers.put("GPVTG", new GPVTG());
    }

    public static GpsPosition getEmptyPosition(){
        return new GpsPosition();
    }

    public GpsPosition parse(String line) {

        if(line.startsWith("$")) {
            position = getEmptyPosition();
            String nmea = line.substring(1);
            position.rawNMEA=nmea;
            String[] tokens = nmea.split(",");
            String type = tokens[0];

            if(sentenceParsers.containsKey(type)) {
                sentenceParsers.get(type).parse(tokens, position);
            }
            if(fixOK)position.updatefix();
            return position;
        }else{
            return null;
            //todo lines not always starting with $ sign
        }


    }

}
