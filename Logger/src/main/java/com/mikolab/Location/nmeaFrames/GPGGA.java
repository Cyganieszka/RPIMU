package com.mikolab.Location.nmeaFrames;

/**
 * Created by agnieszka on 30.04.2016.
 */
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

public class GPGGA extends NMEA  {

    Float utcTime = null;
    String latitude = null;
    String NorS = null;
    String longitude = null;
    String EorW = null;
    Boolean fixed = null;
    Integer satellitesUsed = null;
    Float HDOP = null;
    Float MSLAltitude = null;
    String units = null;
    Float GeoidalSeparation = null;
    String units2 = null;
    Integer ageoFDiffCorr = null;

    public GPGGA(String nmea) {

        idx++;

        message=nmea;

        String[] tokens = nmea.split(",");

        utcTime = parseFloat(tokens[1]);
        fixed =parseInt(tokens[6]) > 0;
        latitude = stringOrNull(tokens[2]);
        NorS = stringOrNull(tokens[3]);
        longitude = stringOrNull(tokens[4]);
        EorW = stringOrNull(tokens[5]);
        satellitesUsed = parseInt(tokens[7]);
        HDOP = parseFloat(tokens[8]);
        MSLAltitude = parseFloat(tokens[9]);
        units = stringOrNull(tokens[10]);
        GeoidalSeparation = parseFloat(tokens[11]);
        units2 = stringOrNull(tokens[12]);
        ageoFDiffCorr = parseInt(tokens[13]);

    }

    public Float getUtcTime() {
        return utcTime;
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

    public Boolean getFixed() {
        return fixed;
    }

    public Integer getSatellitesUsed() {
        return satellitesUsed;
    }

    public Float getHDOP() {
        return HDOP;
    }

    public Float getMSLAltitude() {
        return MSLAltitude;
    }

    public String getUnits() {
        return units;
    }

    public Float getGeoidalSeparation() {
        return GeoidalSeparation;
    }

    public String getUnits2() {
        return units2;
    }

    public Integer getAgeoFDiffCorr() {
        return ageoFDiffCorr;
    }
}
