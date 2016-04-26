package com.mikolab;

/**
 * Created by User on 2016-01-11.
 */

import com.mikolab.database.GpsPosition;
import com.mikolab.database.NmeaParser;
import com.pi4j.io.serial.*;


public class Main{

    public static final NmeaParser parser= new NmeaParser();

    public static void main(String args[])
            throws InterruptedException, NumberFormatException
    {
        String port = System.getProperty("serial.port", Serial.DEFAULT_COM_PORT);
        int br = Integer.parseInt(System.getProperty("baud.rate", "9600"));

        System.out.println("Serial Communication.");
        System.out.println(" ... connect using settings: " + Integer.toString(br) +  ", N, 8, 1.");
        System.out.println(" ... data received on serial port should be displayed below.");

        // create an instance of the serial communications class
        final Serial serial = SerialFactory.createInstance();
        serial.setMonitorInterval(1000);


        // create and register the serial data listener
        serial.addListener(listener);

        try
        {
            // open the default serial port provided on the GPIO header
            System.out.println("Opening port [" + port + ":" + Integer.toString(br) + "]");
            serial.open(port, br);
            System.out.println("Port is opened.");

            // continuous loop to keep the program running until the user terminates the program
            while (true)
            {
                // wait 100 ms before continuing
                //Thread.sleep(100);
            }
        }
        catch (SerialPortException ex)
        {
            System.out.println(" ==>> SERIAL SETUP FAILED : " + ex.getMessage());
            return;
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
                    printPosition(nmea);
                }else
               if(lastSentence.length()>0) {
                   printPosition(lastSentence+"|+|"+nmea);
                   lastSentence="";
               }
            }

            while(message.substring(startIndex,message.length()-1).contains("*") && endIndex > -1){
                nmea=message.substring(startIndex,endIndex-1);
                printPosition(nmea);
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

    static void printPosition(String nmea){
        nmea=nmea.replace("\n","");
        if(nmea.length()==0)return;
        System.out.print("nmea  -> "+nmea+"\n");

        GpsPosition position = parser.parse(nmea);
        if(position!=null) {
            System.out.print("parsed-> "+position+"\n");
        }



    }



}


