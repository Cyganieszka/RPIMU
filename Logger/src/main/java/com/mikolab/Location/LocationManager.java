package com.mikolab.Location;

import com.mikolab.HardwareInterfaces.GPSListener;
import com.mikolab.Location.nmeaFrames.GPGGA;
import com.mikolab.Location.nmeaFrames.GPRMC;
import com.mikolab.Location.nmeaFrames.GPVTG;
import com.mikolab.Logger.DataLogger;


/**
 * Created by agnieszka on 30.04.2016.
 */
public class LocationManager implements GPSListener{



    GpsPosition current= new GpsPosition();

    DataLogger dataLogger;

    private void newPosition(){
        if(current!=null){
            savePosition(current);
        }
        current=new GpsPosition();
    }

    public void setDataLogger(DataLogger dataLogger){
        this.dataLogger=dataLogger;
    }

    public void nmeaFrameReceived(NMEA_TYPE type, String frame) {



       // System.out.println(type + frame);
        switch (type){
            case GPGGA:
                GPGGA gpgga=new GPGGA(frame);
                if(current.canAdd(gpgga)) {
                    current.addFrame(gpgga);
                }else{
                    newPosition();
                    current.addFrame(gpgga);
                }
                break;
            case GPRMC:
                GPRMC gprmc=new GPRMC(frame);
                if(current.canAdd(gprmc)) {
                    current.addFrame(gprmc);
                }else{
                    newPosition();
                    current.addFrame(gprmc);
                }
                break;
            case GPVTG:
                if(current!=null)
                    current.addFrame(new GPVTG(frame));
                break;
        }
    }

    private void savePosition(GpsPosition position){

        dataLogger.saveGpsPosition(position);
        position.print();

}
}
