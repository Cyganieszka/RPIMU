package com.mikolab.Jni;



public class Rtimu {

    static{
        System.loadLibrary("Rtimu");
    }

    public native void init();
    public native int getPollInterval();
    public native double[] getReadings();


}
