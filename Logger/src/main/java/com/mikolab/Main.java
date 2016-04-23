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
                // wait 1 second before continuing
                Thread.sleep(1000);
            }
        }
        catch (SerialPortException ex)
        {
            System.out.println(" ==>> SERIAL SETUP FAILED : " + ex.getMessage());
            return;
        }
    }

    static SerialDataListener listener = new SerialDataListener() {
        @Override
        public void dataReceived(SerialDataEvent event) {
            //System.out.print(/*"Read:\n" + */ event.getData());

            new Thread(new Runnable() {
                public void run() {
                    GpsPosition position = parser.parse(event.getData());
                    if(position!=null) {
                        System.out.println(position.toString());
                    }
                }
            }).start();


        }
    };



}


