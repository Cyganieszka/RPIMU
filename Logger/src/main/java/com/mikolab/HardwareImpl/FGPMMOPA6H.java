package com.mikolab.HardwareImpl;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.mikolab.HardwareInterfaces.GPSInterface;
import com.mikolab.Location.NMEA_TYPE;
import com.mikolab.Location.interfaces.GPSListener;

/**
 * Created by User on 2016-04-26.
 */
public class FGPMMOPA6H implements GPSInterface {

    ///
    //#define PMTK_SET_BAUD_57600 "$PMTK251,57600*2C"
    //#define PMTK_SET_NMEA_UPDATE_5HZ  "$PMTK220,200*2C"
    //#define PMTK_API_SET_FIX_CTL_5HZ  "$PMTK300,200,0,0,0,0*2F"

    ///
     SerialPort serial;
    final GPSListener gpsListener;
    final boolean isFixed=false;

    public FGPMMOPA6H(GPSListener gpsListener) {
        this.gpsListener = gpsListener;
    }

    String com1="$PMTK251,57600*2C\r\n$PMTK220,100*2F\r\n$PMTK314,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0*35\r\n";
    String com2="";
    String com3="";



    public boolean init() {

        serial = SerialPort.getCommPorts()[0];
        serial.openPort();
        serial.setBaudRate(57600);
        serial.addDataListener(new SerialPortDataListener() {
            StringBuilder sb=new StringBuilder();
            @Override
            public int getListeningEvents() { return SerialPort.LISTENING_EVENT_DATA_AVAILABLE; }
            @Override
            public void serialEvent(SerialPortEvent event)
            {
                if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
                    return;
                byte[] newData = new byte[serial.bytesAvailable()];
                serial.readBytes(newData, newData.length);

                for (int i = 0; i < newData.length; ++i) {
                    if ((char) newData[i] == '\n') {
                        sb.append((char) newData[i]);
                        parseMessage(sb.toString());
                        sb=new StringBuilder();
                    } else {
                        sb.append((char) newData[i]);
                    }
                }
            }
        });



        serial.writeBytes(com1.getBytes(),com1.getBytes().length);
        return true;


    }

    SerialPortDataListener listener = new SerialPortDataListener() {
        @Override
        public int getListeningEvents() { return SerialPort.LISTENING_EVENT_DATA_WRITTEN; }
        @Override
        public void serialEvent(SerialPortEvent event)
        {
            if (event.getEventType() == SerialPort.LISTENING_EVENT_DATA_WRITTEN)
                System.out.println("All bytes were successfully transmitted!");
        }
    };

    public boolean startLogging() {

        serial.addDataListener(listener);
        return true;
    }

    public boolean stopLogging() {
        serial.removeDataListener();
        return true;
    }

    public boolean isFixed() {
        return isFixed;
    }






        String lastSentence="";

    private void parseMessage(String message){
       // System.out.print(message+"\n");

        int idx=0;


        String lines[] = message.split("\r?\n");
        if(lines.length==0)return;

        String firstLine=lines[idx];


        if(!firstLine.contains("$") ){
            if(firstLine.length()>0 && lastSentence.length()>0){
                nmeaReceived(lastSentence+firstLine);
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


    }

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
           System.out.print("Something is wrong!! unknown nmea frame!!" + "\n");

        }
    }

}
