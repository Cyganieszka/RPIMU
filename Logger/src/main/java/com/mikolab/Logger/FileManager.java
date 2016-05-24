package com.mikolab.Logger;

import com.mikolab.Location.GpsPosition;
import com.mikolab.Location.interfaces.GPSLogger;

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
public class FileManager implements GPSLogger {

    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private String mainPath="/home/pi";
    private String rootFolder="RPIMU";

    private String GPSfolder="gps";
    private String IMUfolder="imu";

    private File currentGpsFile=null;
    private File currentImuFile=null;

    private Date gpsMarker=null;
    private Date imuMarker=null;

    private String gpsFilePath;
    private String imuFilePath;


    public void init(){
        File path=new File(mainPath+File.separator+rootFolder);
        if(!path.exists()){
            path.mkdir();
        }
        File gpsPath=new File(mainPath+File.separator+rootFolder+File.separator+GPSfolder);
        if(!gpsPath.exists()){
            gpsPath.mkdir();
        }
        gpsFilePath=gpsPath.getAbsolutePath();
        File imuPath=new File(mainPath+File.separator+rootFolder+File.separator+IMUfolder);
        if(!imuPath.exists()){
            imuPath.mkdir();
        }
        imuFilePath=imuPath.getAbsolutePath();
    }

    public void saveGpsPosition(GpsPosition position) {
        File file= getCurrentGpsFile();


        BufferedWriter bw=null;
        FileWriter fw = null;
        try {
            fw = new FileWriter(file,true);
            bw = new BufferedWriter(fw);
            //bw.write(position.toJson());
            bw.write(position.toString()+"\n");
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private File getCurrentGpsFile(){
        if(currentGpsFile==null){
            gpsMarker= new Date();
            currentGpsFile=new File(gpsFilePath+File.separator+dateFormat.format(gpsMarker)+".txt");
        }else{
            Date now=new Date();
            long minutesDiff=getDateDiff(gpsMarker,now,TimeUnit.MINUTES);
            System.out.println(minutesDiff+ "diff ");
            if(minutesDiff>60){
                gpsMarker= new Date();
                currentGpsFile=new File(gpsFilePath+File.separator+dateFormat.format(gpsMarker)+".txt");
            }
        }
        if (!currentGpsFile.exists()) {
            try {
                currentGpsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return currentGpsFile;
    }



    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
    }

}