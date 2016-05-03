package com.mikolab.Logger;

import com.mikolab.Location.GpsPosition;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by agnieszka on 03.05.2016.
 */
public class FileManager implements DataLogger {

    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    private String mainPath="/home/pi";
    private String rootFolder="RPIMU";

    private String GPSfolder="gps";
    private String IMUfolder="imu";

    private File currentGpsFile=null;
    private File currentImuFile=null;

    private Date gpsMarker=null;
    private Date imuMarker=null;

    public void init(){
        File path=new File(mainPath+File.separator+rootFolder);
        if(!path.exists()){
            path.mkdir();
        }
        File gpsPath=new File(path.getAbsolutePath()+File.separator+GPSfolder);
        if(!gpsPath.exists()){
            gpsPath.mkdir();
        }
        File imuPath=new File(path.getAbsolutePath()+File.separator+IMUfolder);
        if(!imuPath.exists()){
            imuPath.mkdir();
        }
    }

    public void saveGpsPosition(GpsPosition position) {
        File file= getCurrentGpsFile();

        BufferedWriter bw=null;
        FileWriter fw = null;
        try {
            fw = new FileWriter(file.getAbsoluteFile());
            bw = new BufferedWriter(fw);
            bw.write(position.toJson());
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private File getCurrentGpsFile(){
        if(currentGpsFile==null){
            gpsMarker= new Date();
            currentGpsFile=new File(GPSfolder+File.separator+dateFormat.format(gpsMarker));
        }else{
            Date now=new Date();
            if(getDateDiff(now,gpsMarker,TimeUnit.MINUTES)>60){
                gpsMarker= new Date();
                currentGpsFile=new File(GPSfolder+File.separator+dateFormat.format(gpsMarker));
            }
        }
        return currentGpsFile;
    }



    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
    }

}