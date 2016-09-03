package com.mikolab;

/**
 * Created by User on 2016-01-11.
 */

import com.mikolab.HardwareImpl.FGPMMOPA6H;
import com.mikolab.HardwareImpl.MPU9255;
import com.mikolab.Jni.Rtimu;
import com.mikolab.Location.LocationManager;
import com.mikolab.Logger.BtManager;
import com.mikolab.Logger.FileManager;


public class Main{




    public static void main(String args[])
            throws InterruptedException, NumberFormatException
    {

        for(int i=0;i<30;i++){
            System.out.println("logging start in "+(30-i)+" seconds");
            Thread.sleep(1000);
        }


        BtManager btmanager=new BtManager();
        btmanager.init();
        FileManager fileManager= new FileManager();
        fileManager.init();

        LocationManager locationManager= new LocationManager();
        locationManager.addGPSLogger(fileManager);
        locationManager.addGPSLogger(btmanager);
        locationManager.addIMULogger(fileManager);
        locationManager.addIMULogger(btmanager);


        FGPMMOPA6H gps= new FGPMMOPA6H(locationManager);
        MPU9255 imu= new MPU9255(locationManager);

        CommunicationManager comm=new CommunicationManager(gps, imu);

        comm.initDevice(Device.GPS);
        comm.startLogging(Device.GPS);

        comm.initDevice(Device.IMU);
        comm.startLogging(Device.IMU);


        while (true)
        {
                // wait 100 ms before continuing
                Thread.sleep(1);
        }


    }







}


