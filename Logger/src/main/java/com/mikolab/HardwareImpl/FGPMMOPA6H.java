package com.mikolab.HardwareImpl;

import com.mikolab.HardwareInterfaces.GPSInterface;
import com.mikolab.HardwareInterfaces.GPSListener;
import com.mikolab.Location.NMEA_TYPE;
import com.pi4j.io.serial.*;

/**
 * Created by User on 2016-04-26.
 */
public class FGPMMOPA6H implements GPSInterface {
    final Serial serial = SerialFactory.createInstance();
    final GPSListener gpsListener;
    final boolean isFixed=false;

    public FGPMMOPA6H(GPSListener gpsListener) {
        this.gpsListener = gpsListener;
    }


    public boolean init() {
        String port = System.getProperty("serial.port", Serial.DEFAULT_COM_PORT);
        int br = Integer.parseInt(System.getProperty("baud.rate", "9600"));

        System.out.println("Serial Communication.");
        System.out.println(" ... connect using settings: " + Integer.toString(br) +  ", N, 8, 1.");

        serial.setMonitorInterval(1000);

        try
        {
            // open the default serial port provided on the GPIO header
            System.out.println("Opening port [" + port + ":" + Integer.toString(br) + "]");
            serial.open(port, br);
            System.out.println("Port is opened.");

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

    public boolean isFixed() {
        return isFixed;
    }



//    private SerialDataListener listener =new SerialDataListener() {
//        public void dataReceived(SerialDataEvent event) {
//            gpsListener.gpsData(event.getData());// change seperate listeners on different frames
//        }
//    };


    String lastSentence="";

    SerialDataListener listener = new SerialDataListener() { // todo clean later
        public void dataReceived(SerialDataEvent event) {
            String message = event.getData();
//            System.out.print("-----------------------------------------------------------------------------\n");
//
//            System.out.print("message \n\n");
//            System.out.print(message+"\n");
//            System.out.print("seperate Frames \n\n");

            int idx=0;


            String lines[] = message.split("\r?\n");

            String firstLine=lines[idx];


            if(!firstLine.contains("$") ){
                if(firstLine.length()>0 && lastSentence.length()>0){
                    nmeaReceived(lastSentence+firstLine);
                    //System.out.print(lastSentence+firstLine+"\n");
                }
                idx=1;
            }

            for(;idx<lines.length;idx++){
                //System.out.print(lines[idx]+"\n");
                if(lines[idx].matches(".+[*]\\w{2}")){
                    nmeaReceived(lines[idx]);
                }else{
                    lastSentence=lines[idx];
                    break;
                }
            }

            /////
//
//            int startIndex = message.indexOf('\n', 0);
//            if (startIndex == -1) return;
//            int endIndex = message.indexOf('\n', startIndex + 1);
//
//            String nmea;
//
//            if (startIndex > 0) {
//                nmea = message.substring(0, startIndex + 1);
//                if (nmea.contains("$")) {
//                    nmeaReceived(nmea);
//
//
//                } else if (lastSentence.length() > 0) {
//                    nmeaReceived(lastSentence+nmea);
//                    lastSentence = "";
//                }
//            }
//
//            while (message.substring(startIndex, message.length() - 1).contains("*") && endIndex > -1) {
//                nmea = message.substring(startIndex, endIndex - 1);
//                nmeaReceived(nmea);
//                int prev = message.indexOf('\n', endIndex + 1);
//                startIndex = endIndex;
//                endIndex = prev;
//            }
//
//            if (endIndex != -1) {
//                if (lastSentence.length() > 0) System.out.print("Something is wrong!!" + "\n");
//                lastSentence = message.substring(startIndex - 1, message.length()).replace("\n", "");
//            }

        }
    };

    //GPGGA,
    //GPRMC,
    //GPVTG

    private void nmeaReceived(String nmea){// todo move to nmea parser?


        String data=nmea.substring(nmea.indexOf('$')+1);
        if(nmea.contains("GPGGA")){
            gpsListener.nmeaFrameReceived(NMEA_TYPE.GPGGA,data);
        }else if(nmea.contains("GPRMC")){
            gpsListener.nmeaFrameReceived(NMEA_TYPE.GPRMC,data);
        }else if(nmea.contains("GPVTG")){
            gpsListener.nmeaFrameReceived(NMEA_TYPE.GPVTG,data);
        }else {
            //todo check other types
           // System.out.print("Something is wrong!! unknown nmea frame!!" + "\n");

        }
    }

}
