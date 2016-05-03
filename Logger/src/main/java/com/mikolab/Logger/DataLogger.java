package com.mikolab.Logger;

import com.mikolab.Location.GpsPosition;

/**
 * Created by agnieszka on 03.05.2016.
 */
public interface DataLogger {
    void saveGpsPosition(GpsPosition position);
    //void saveImuData(ImuData);
}
