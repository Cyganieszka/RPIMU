package com.mikolab.HardwareInterfaces;

/**
 * Created by User on 2016-04-26.
 */
public interface DeviceInterface {

    boolean init();
    boolean startLogging();
    boolean stopLogging();

}
