package com.mikolab.HardwareInterfaces;

import com.mikolab.Location.NMEA_TYPE;

/**
 * Created by User on 2016-04-26.
 */
public interface GPSListener {

    void nmeaFrameReceived(NMEA_TYPE type, String frame);
}
