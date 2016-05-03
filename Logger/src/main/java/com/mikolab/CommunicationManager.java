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

        System.out.print("Init ");
        switch (dev){
            case GPS:
                System.out.print("GPS \n");
                gps.init();
                break;
            case IMU:
                System.out.print("IMU \n");
                imu.init();
                break;
        }
        return true;
    }

    public boolean startLogging(Device dev){

        System.out.print("Start Logging: ");
        switch (dev){
            case GPS:
                gps.startLogging();
                System.out.print("GPS \n");
                break;
            case IMU:
                imu.startLogging();
                System.out.print("IMU \n");
                break;
        }
        return true;
    }

    public boolean stopLogging(Device dev){

        System.out.print("Stop Logging: ");
        switch (dev){
            case GPS:
                gps.stopLogging();
                System.out.print("GPS \n");
                break;
            case IMU:
                imu.stopLogging();
                System.out.print("IMU \n");
                break;
        }

        return  true;
    }


}
