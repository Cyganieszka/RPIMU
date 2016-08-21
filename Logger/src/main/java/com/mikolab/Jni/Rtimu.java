package com.mikolab.Jni;


/**
 * Created by agnieszka on 07.08.2016.
 */
public class Rtimu {

    static{
        System.loadLibrary("Rtimu");
    }

    public native void init();
    public native int getPollInterval();
    public native double[] getReadings();


}
