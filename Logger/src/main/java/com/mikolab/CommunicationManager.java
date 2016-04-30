package com.mikolab;

import com.mikolab.HardwareInterfaces.GPSInterface;
import com.mikolab.HardwareInterfaces.IMUInterface;

/**
 * Created by User on 2016-04-26.
 */
public class CommunicationManager {


    private final GPSInterface gps;
    private final IMUInterface imu;

    // constructor
    public CommunicationManager(GPSInterface gps, IMUInterface imu){
        this.gps=gps;
        this.imu=imu;
    }


    public boolean initDevice(Device dev){

        switch (dev){
            case GPS:
                gps.init();
                break;
            case IMU:
                imu.init();
                break;
        }
        return true;
    }

    public boolean startLogging(Device dev){

        switch (dev){
            case GPS:
                gps.startLogging();
                break;
            case IMU:
                imu.startLogging();
                break;
        }
        return true;
    }

    public boolean stopLogging(Device dev){

        switch (dev){
            case GPS:
                gps.stopLogging();
                break;
            case IMU:
                imu.stopLogging();
                break;
        }

        return  true;
    }


}
