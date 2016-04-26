package com.mikolab;

/**
 * Created by User on 2016-01-11.
 */

import com.mikolab.HardwareImpl.FGPMMOPA6H;
import com.mikolab.HardwareImpl.MPU9255andBPM180;
import com.mikolab.database.GpsPosition;
import com.mikolab.database.NmeaParser;
import com.pi4j.io.serial.*;


public class Main{



    static NMEAUtil util= new NMEAUtil();

    public static void main(String args[])
            throws InterruptedException, NumberFormatException
    {


        FGPMMOPA6H gps= new FGPMMOPA6H();
        MPU9255andBPM180 imu= new MPU9255andBPM180();
        CommunicationManager comm=new CommunicationManager(gps, imu);

        comm.initDevice(Device.GPS);

        while (true)
        {
                // wait 100 ms before continuing
                Thread.sleep(10);
        }
    }
    static String lastSentence="";

    static SerialDataListener listener = new SerialDataListener() {
        public void dataReceived(SerialDataEvent event) {
            String message = event.getData();
            System.out.print("-----------------------------------------------------------------------------\n");

            System.out.print("message \n\n");
            System.out.print(message+"\n");
            System.out.print("seperate Frames \n\n");


            int startIndex=message.indexOf('\n',0);
            if(startIndex==-1) return;
            int endIndex=message.indexOf('\n',startIndex+1);

            String nmea;

            if(startIndex>0){
                nmea=message.substring(0,startIndex+1);
                if(nmea.contains("$")){
                    util.printPosition(nmea);
                }else
               if(lastSentence.length()>0) {
                   util.printPosition(lastSentence+"|+|"+nmea);
                   lastSentence="";
               }
            }

            while(message.substring(startIndex,message.length()-1).contains("*") && endIndex > -1){
                nmea=message.substring(startIndex,endIndex-1);
                util.printPosition(nmea);
                int prev=message.indexOf('\n',endIndex+1);
                startIndex=endIndex;
                endIndex = prev;
            }

            if(endIndex!=-1) {
                if(lastSentence.length()>0)System.out.print("Something is wrong!!"+"\n");
                lastSentence = message.substring(startIndex - 1, message.length()).replace("\n", "");
            }




        }
    };





}


