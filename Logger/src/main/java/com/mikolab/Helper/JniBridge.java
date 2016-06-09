package com.mikolab.Helper;

/**
 * Created by agnieszka on 29.05.2016.
 */
public class JniBridge {
    public native void sayHi(String who, int times); //1

    static { System.loadLibrary("RPIMU-jni"); } //2
}
