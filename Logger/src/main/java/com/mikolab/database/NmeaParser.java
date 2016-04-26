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

    //    $GPGGA,183504.000,5016.8803,N,01840.0275,E,2,09,0.92,261.1,M,42.2,M,0000,0000*6A
    //Message ID            - $GPGGA            x
    //UTC Time              - 183504.000        x
    //Latitude              - 5016.8803         x
    //N/S Indicator         - N                 x
    //Longitude             - 01840.0275        x
    //E/W Indicator         - E                 x
    //Position Fix          - 2                 x
    //Satellites Used       - 09                x
    //HDOP                  - 0.92
    //MSL Altitude          - 261.1             x
    //Units                 - M
    //Geoidal Separation    - 42.2
    //Units                 - M
    //Age of Diff. Corr.    - 0000
    //Checksum              -

    class GPGGA implements SentenceParser {
        public boolean parse(String [] tokens, GpsPosition position) {
            if(tokens.length>9 && Integer.parseInt(tokens[7])>0) {
                position.utcTime = Float.parseFloat(tokens[1]);
                position.lat = Latitude2Decimal(tokens[2], tokens[3]);
                position.lon = Longitude2Decimal(tokens[4], tokens[5]);
                position.altitude = Float.parseFloat(tokens[9]);
                fixOK=true;
                return true;
            }
            fixOK=false;
            return false;

        }
    }

    //   $GPRMC,183504.000,A,5016.8803,N,01840.0275,E,0.03,330.96,230416,    , ,D*62

    //Message ID            - $GPRMC            x
    //UTC Time              - 183504.000        d
    //Status                - A                 x
    //Latitude              - 5016.8803         d
    //N/S Indicator         - N                 d
    //Longitude             - 01840.0275        d
    //E/W Indicator         - E                 d
    //Speed over Ground     - 0.03
    //Course over Ground    - 330.96            d
    //Date                  - 230416            x
    //Magnetic Variation    -
    //Mode                  -
    //Checksum              -
    class GPRMC implements SentenceParser {
        public boolean parse(String [] tokens, GpsPosition position) {
            if(fixOK) {
                position.utcTime = Float.parseFloat(tokens[1]);
                position.lat = Latitude2Decimal(tokens[3], tokens[4]);
                position.lon = Longitude2Decimal(tokens[5], tokens[6]);
                position.speed = Float.parseFloat(tokens[7]);
                position.course = Float.parseFloat(tokens[8]);
            }
            return true;
        }
    }

    //   $GPVTG,276.03,T,,M,0.03,N,0.05,K,D*3E
    //Message ID            - $GPVTG            x
    //Course                - 276.03            d
    //Reference             - T
    //Course                -
    //Reference             - M
    //Speed                 - 0.03
    //Units                 - N
    //Speed                 - 0.05              x
    //Units                 - K
    //Mode                  - D
    //Checksum              -
    class GPVTG implements SentenceParser {
        public boolean parse(String [] tokens, GpsPosition position) {
            if(fixOK) {
//                position.course = Float.parseFloat(tokens[3]);
            }
            return true;
        }
    }





    static GpsPosition position;

    private final Map<String, SentenceParser> sentenceParsers = new HashMap<String, SentenceParser>();

    public NmeaParser() {
        sentenceParsers.put("GPGGA", new GPGGA());
        sentenceParsers.put("GPRMC", new GPRMC());
        //only really good GPS devices have this sentence but ...
        sentenceParsers.put("GPVTG", new GPVTG());
    }

    enum NMEA_TYPE{
        GPGGA,
        GPRMC,
        GPVTG
    }
    private NMEA_TYPE getTypeNMEA(String[] tokens){

        if(tokens[0].equals("GPGGA")) return NMEA_TYPE.GPGGA;
        if(tokens[0].equals("GPRMC")) return NMEA_TYPE.GPRMC;
        if(tokens[0].equals("GPVTG")) return NMEA_TYPE.GPVTG;
        return null;

    }

    private float getTimestampNMEA(String[] tokens){

        NMEA_TYPE type=getTypeNMEA(tokens);
        switch (type){
            case GPGGA:
                return 0;
            case GPRMC:
                return 0;
            case GPVTG:
                return 0;
            default:
                return 0;

        }

    }

    GpsPosition lastPosition;

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
            }else{
                return null;
            }
            return position;
        }else{
            return null;
            //todo lines not always starting with $ sign
        }


    }

}
