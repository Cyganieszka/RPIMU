package com.mikolab.HardwareImpl;

import com.mikolab.HardwareInterfaces.IMUInterface;
import com.mikolab.Jni.Rtimu;
import com.mikolab.Location.ImuPosition;
import com.mikolab.Location.interfaces.IMUListener;

/**
 * Created by User on 2016-04-26.
 */

public class MPU9255 implements IMUInterface {

    IMUListener listener;
    Rtimu rtimu;
    TestThread thread;

    public MPU9255(IMUListener listener){
        this.listener=listener;
    }
    public boolean init() {
        rtimu=new Rtimu();
        rtimu.init();
        return true;
    }

    public boolean startLogging() {
        TestThread thread= new TestThread(rtimu,listener);
        thread.start();
        return true;
    }

    public boolean stopLogging() {
        thread.stopExecuting();
        return true;
    }


    static class TestThread extends Thread{

        Rtimu rtimu;
        private volatile boolean execute;
        IMUListener listener;

        public TestThread(Rtimu imu, IMUListener listener){
            this.rtimu=imu;
            this.listener=listener;

            System.out.println("init, poll intervall "+rtimu.getPollInterval());
        }

        @Override
        public void run() {
            double[] values;
            this.execute = true;
            int count=0;
            long timestamp=System.currentTimeMillis();
            while (this.execute) {
                try {
                    this.sleep(rtimu.getPollInterval()/1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    this.execute = false;
                }
                values = rtimu.getReadings();
                //System.out.println(values[0]+" "+values[1]+" "+values[2]+" "+values[3]+" "+values[4]);
                if(values[0]!=0){
                    count++;
                    listener.valuesReceived(values);

                }
                if((System.currentTimeMillis()-timestamp)>1000){
                    System.out.println(String.valueOf(count)+"/sekundę       ------");
                    count=0;
                    timestamp=System.currentTimeMillis();
                }
            }
        }
        public void stopExecuting() {
            this.execute = false;
        }
    }



}
