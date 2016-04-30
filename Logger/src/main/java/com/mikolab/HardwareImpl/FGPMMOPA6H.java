package com.mikolab.HardwareImpl;

import com.mikolab.HardwareInterfaces.GPSInterface;
import com.pi4j.io.serial.*;

/**
 * Created by User on 2016-04-26.
 */
public class FGPMMOPA6H implements GPSInterface {
    final Serial serial = SerialFactory.createInstance();

    public boolean init() {
        String port = System.getProperty("serial.port", Serial.DEFAULT_COM_PORT);
        int br = Integer.parseInt(System.getProperty("baud.rate", "9600"));

        System.out.println("Serial Communication.");
        System.out.println(" ... connect using settings: " + Integer.toString(br) +  ", N, 8, 1.");
        System.out.println(" ... data received on serial port should be displayed below.");


        serial.setMonitorInterval(1000);


        try
        {
            // open the default serial port provided on the GPIO header
            System.out.println("Opening port [" + port + ":" + Integer.toString(br) + "]");
            serial.open(port, br);
            System.out.println("Port is opened.");

//            // continuous loop to keep the program running until the user terminates the program
//            while (true)
//            {
//                // wait 100 ms before continuing
//                //Thread.sleep(100);
//            }
            return true;
        }
        catch (SerialPortException ex)
        {
            System.out.println(" ==>> SERIAL SETUP FAILED : " + ex.getMessage());
            return false;
        }

    }

    public boolean startLogging() {
        serial.addListener(listener);
        return true;
    }

    public boolean stopLogging() {
        serial.removeListener(listener);
        return true;
    }



    private SerialDataListener listener =new SerialDataListener() {
        public void dataReceived(SerialDataEvent event) {

        }
    };
}
