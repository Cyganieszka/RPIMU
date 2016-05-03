package com.mikolab.Location.nmeaFrames;

/**
 * Created by agnieszka on 30.04.2016.
 */
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
public class GPRMC extends NMEA  {



    Float utcTime = null;
    String status = null;
    String latitude = null;
    String NorS = null;
    String longitude = null;
    String EorW = null;
    Float speedOverGround = null;
    Float courseOverGround = null;
    Integer date = null;
    Float magneticVariation = null;
    String mode = null;

    public GPRMC(String nmea) {

        idx++;

        message=nmea;

        String[] tokens = nmea.split(",");

        utcTime = parseFloat(tokens[1]);
        status = stringOrNull(tokens[2]);
        latitude = stringOrNull(tokens[3]);
        NorS = stringOrNull(tokens[4]);
        longitude = stringOrNull(tokens[5]);
        EorW = stringOrNull(tokens[6]);
        speedOverGround = parseFloat(tokens[7]);
        courseOverGround = parseFloat(tokens[8]);
        date = parseInt(tokens[9]);
        magneticVariation = parseFloat(tokens[10]);
        mode = stringOrNull(tokens[11]);


    }

    public Float getUtcTime() {
        return utcTime;
    }

    public String getStatus() {
        return status;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getNorS() {
        return NorS;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getEorW() {
        return EorW;
    }

    public Float getSpeedOverGround() {
        return speedOverGround;
    }

    public Float getCourseOverGround() {
        return courseOverGround;
    }

    public Integer getDate() {
        return date;
    }

    public Float getMagneticVariation() {
        return magneticVariation;
    }

    public String getMode() {
        return mode;
    }
}