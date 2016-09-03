package com.mikolab.Logger;

import com.mikolab.Location.GpsPosition;
import com.mikolab.Location.ImuPosition;
import com.mikolab.Location.interfaces.GPSLogger;
import com.mikolab.Location.interfaces.IMULogger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by agnieszka on 03.05.2016.
 */
public class FileManager implements GPSLogger,IMULogger,Runnable {

    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DateFormat timeFormat = new SimpleDateFormat("HH:mm");
    DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private String mainPath="/home/pi";
    private String rootFolder="RPIMU";

    Date stamp=new Date();

    private File gpsFile=null;
    private File imuFile=null;

    BlockingQueue<GpsPosition> gpsToSend= new ArrayBlockingQueue<GpsPosition>(100);
    BlockingQueue<ImuPosition> imuToSend= new ArrayBlockingQueue<ImuPosition>(100);


    @Override
    public void run() {
        while(true){
            if(imuToSend.size()>0){
                try {
                    saveImu(imuToSend.take());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if(gpsToSend.size()>0){
                try {
                    saveGps(gpsToSend.take());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void init(){
        File path=new File(mainPath+File.separator+rootFolder);
        if(!path.exists()){
            path.mkdir();
        }
        createNewFiles();
        stamp=new Date();

        Thread thread = new Thread(this);
        thread.start();
    }

    String todayFolderPath=null;
    private String getToadyFolder(){
        if(todayFolderPath==null || dayChanged(new Date(),stamp)) {
            File todayFolder = new File(mainPath + File.separator + rootFolder + File.separator + dateFormat.format(new Date()));
            if (!todayFolder.exists()) {
                todayFolder.mkdir();
            }
            todayFolderPath=todayFolder.getAbsolutePath();
        }
        return todayFolderPath;
    }
    String todaySubFolderPath=null;
    private String getToadySubFolder(){
        if(todaySubFolderPath==null || hourChanged(new Date(),stamp)) {
            File todayFolder = new File(getToadyFolder() + File.separator + timeFormat.format(new Date()));
            if (!todayFolder.exists()) {
                todayFolder.mkdir();
            }
            todaySubFolderPath = todayFolder.getAbsolutePath();
        }
        return todaySubFolderPath;
    }

    private void createNewFiles(){
        Date date=new Date();
        gpsFile=new File(getToadySubFolder()+File.separator+"GPS_"+dateTimeFormat.format(date));
        imuFile=new File(getToadySubFolder()+File.separator+"IMU_"+dateTimeFormat.format(date));

        try {
            gpsFile.createNewFile();
            imuFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveGpsPosition(GpsPosition position) {
        try {
            gpsToSend.put(position);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveImuPosition(ImuPosition position) {
        try {
            imuToSend.put(position);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    public void saveGps(GpsPosition position) {
        if(position.lat!=0) {
            File file = getCurrentGpsFile();


            BufferedWriter bw = null;
            FileWriter fw = null;
            try {
                fw = new FileWriter(file, true);
                bw = new BufferedWriter(fw);
                bw.write(position.toString() + "\n");
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void saveImu(ImuPosition position) {
        File file= getCurrentImuFile();


        BufferedWriter bw=null;
        FileWriter fw = null;
        try {
            fw = new FileWriter(file,true);
            bw = new BufferedWriter(fw);
            bw.write(position.toString()+"\n");
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private boolean dayChanged(Date date1, Date date2){
        Calendar now = Calendar.getInstance();
        now.setTime(date1);
        Calendar then = Calendar.getInstance();
        then.setTime(date2);

        return now.get(Calendar.DAY_OF_MONTH)!=then.get(Calendar.DAY_OF_MONTH);
    }

    private boolean hourChanged(Date date1, Date date2){
        Calendar now = Calendar.getInstance();
        now.setTime(date1);
        Calendar then = Calendar.getInstance();
        then.setTime(date2);

        return now.get(Calendar.HOUR_OF_DAY)!=then.get(Calendar.HOUR_OF_DAY);
    }

    private void createnewFilesIfNeeded(){
        Date now=new Date();
        long minutesDiff=getDateDiff(stamp,now,TimeUnit.MINUTES);

        if(minutesDiff>10){
            createNewFiles();
            stamp=new Date();
        }
    }

    private File getCurrentGpsFile(){
        createnewFilesIfNeeded();
        return gpsFile;
    }


    private File getCurrentImuFile(){
        createnewFilesIfNeeded();
        return imuFile;
    }




    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
    }

}