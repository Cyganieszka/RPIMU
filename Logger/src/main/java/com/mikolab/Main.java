package com.mikolab;

/**
 * Created by User on 2016-01-11.
 */

import com.mikolab.HardwareImpl.FGPMMOPA6H;
import com.mikolab.HardwareImpl.MPU9255;
import com.mikolab.Location.LocationManager;
import com.mikolab.Logger.FileManager;


public class Main{




    public static void main(String args[])
            throws InterruptedException, NumberFormatException
    {

        FileManager fileManager= new FileManager();
        fileManager.init();
        LocationManager locationManager= new LocationManager();

        locationManager.setDataLogger(fileManager);

        FGPMMOPA6H gps= new FGPMMOPA6H(locationManager);
        MPU9255 imu= new MPU9255();

        CommunicationManager comm=new CommunicationManager(gps, imu);

        comm.initDevice(Device.GPS);
        comm.startLogging(Device.GPS);

        while (true)
        {
                // wait 100 ms before continuing
                Thread.sleep(10);
        }
    }







}


