package com.mikolab.Location.nmeaFrames;

/**
 * Created by agnieszka on 30.04.2016.
 */
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
public class GPVTG extends NMEA  {

    // I know null is not nice
    Float course = null;
    String reference = null;
    Float course2 = null;
    String reference2 = null;
    Float speed = null;
    String units = null;
    Float speed2 = null;
    String units2 = null;
    String mode = null;

    public  GPVTG(String nmea) {

        idx++;

        message=nmea;

        String[] tokens = nmea.split(",");

        course = parseFloat(tokens[1]);
        reference = stringOrNull(tokens[2]);
        course2 = parseFloat(tokens[3]);
        reference2 = stringOrNull(tokens[4]);
        speed = parseFloat(tokens[5]);
        units = stringOrNull(tokens[6]);
        speed2 = parseFloat(tokens[7]);
        units2 = stringOrNull(tokens[8]);
        mode = stringOrNull(tokens[9]);

    }

    public Float getCourse() {
        return course;
    }

    public String getReference() {
        return reference;
    }

    public Float getCourse2() {
        return course2;
    }

    public String getReference2() {
        return reference2;
    }

    public Float getSpeed() {
        return speed;
    }

    public String getUnits() {
        return units;
    }

    public Float getSpeed2() {
        return speed2;
    }

    public String getUnits2() {
        return units2;
    }

    public String getMode() {
        return mode;
    }
}