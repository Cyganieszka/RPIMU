package com.mikolab.Logger;

import com.mikolab.Location.GpsPosition;
import com.mikolab.Location.interfaces.GPSLogger;

import javax.bluetooth.*;
import javax.microedition.io.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Vector;

/**
 * Created by agnieszka on 17.05.2016.
 */
public class BtManager implements GPSLogger, DiscoveryListener {

    // object used for waiting
    private static Object lock = new Object();
    // vector containing the devices discovered
    private static Vector<RemoteDevice> vecDevices = new Vector<RemoteDevice>();
    private static Vector<String> vecServices = new Vector<String>();

    // display local device address and name
    LocalDevice localDevice;
    DiscoveryAgent agent;

    public void init() {

        try {
            localDevice = LocalDevice.getLocalDevice();
            System.out.println("Address: " + localDevice.getBluetoothAddress());
            System.out.println("Name: " + localDevice.getFriendlyName());
            agent = localDevice.getDiscoveryAgent();
            System.out.println("Starting device inquiry...");
            agent.startInquiry(DiscoveryAgent.GIAC, this);

            try {
                synchronized (lock) {
                    lock.wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("Device Inquiry Completed. ");
            System.out.println("Service Inquiry Started. ");

            UUID uuids[] = new UUID[1];
            uuids[0] = new UUID("0000111100001000800000805f9b34fb", false);

            for (RemoteDevice rd : vecDevices) {
                System.out.println("From: " + rd.getFriendlyName(false));
                agent.searchServices(null, uuids, rd, this);
                try {
                    synchronized (lock) {
                        lock.wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // print all devices in vecDevices
            int deviceCount = vecDevices.size();

            if (deviceCount <= 0) {
                System.out.println("No Devices Found .");
            } else {
                // print bluetooth device addresses and names in the format [ No.
                // address (name) ]
                System.out.println("Bluetooth Devices: ");
                for (int i = 0; i < deviceCount; i++) {
                    RemoteDevice remoteDevice = (RemoteDevice) vecDevices
                            .elementAt(i);
                    System.out.println((i + 1) + ". "
                            + remoteDevice.getBluetoothAddress() + " ("
                            + remoteDevice.getFriendlyName(false) + ")");
                }
            }

            // System.out.println("SR: " + sr.toString());
            for (String url : vecServices) {
                try {
//                String url = sr.getConnectionURL(
//                        ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
                    StreamConnection conn = (StreamConnection) Connector.open(url, Connector.READ_WRITE);
                    System.out.println(url + " ----=" + conn);
                    DataInputStream din = new DataInputStream(
                            conn.openDataInputStream());
                    synchronized (lock) {
                        try {
                            lock.wait(10);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    while (din.available() != 0) {
                        System.out.print(din.readChar());
                    }
                    System.out.println();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }

    }

    public void saveGpsPosition(GpsPosition position) {

    }


    /**
     * This call back method will be called for each discovered bluetooth
     * devices.
     */
    public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
        System.out.println("Device discovered: "
                + btDevice.getBluetoothAddress());
        // add the device to the vector
        if (!vecDevices.contains(btDevice)) {
            vecDevices.addElement(btDevice);
        }
    }

    // no need to implement this method since services are not being discovered
    public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
        for (ServiceRecord sr : servRecord) {
            vecServices.add(sr.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false));
        }
    }

    // no need to implement this method since services are not being discovered
    public void serviceSearchCompleted(int transID, int respCode) {
        System.out.println("Service search completed - code: " + respCode);
        synchronized (lock) {
            lock.notify();
        }
    }

    /**
     * This callback method will be called when the device discovery is
     * completed.
     */
    public void inquiryCompleted(int discType) {
        switch (discType) {
            case DiscoveryListener.INQUIRY_COMPLETED:
                System.out.println("INQUIRY_COMPLETED");
                break;

            case DiscoveryListener.INQUIRY_TERMINATED:
                System.out.println("INQUIRY_TERMINATED");
                break;

            case DiscoveryListener.INQUIRY_ERROR:
                System.out.println("INQUIRY_ERROR");
                break;

            default:
                System.out.println("Unknown Response Code");
                break;
        }
        synchronized (lock) {
            lock.notify();
        }
    }// end method
}
