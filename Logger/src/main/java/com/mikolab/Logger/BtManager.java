package com.mikolab.Logger;

import com.mikolab.Location.GpsPosition;
import com.mikolab.Location.interfaces.GPSLogger;

import javax.bluetooth.*;
import javax.microedition.io.*;
import java.io.*;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

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
    StreamConnection conn;
    DataInputStream din;
    DataOutputStream dout;


    private void initLocalDevice() throws BluetoothStateException {
        localDevice = LocalDevice.getLocalDevice();
        System.out.println("Address: " + localDevice.getBluetoothAddress());
        System.out.println("Name: " + localDevice.getFriendlyName());
        agent = localDevice.getDiscoveryAgent();
    }

    private void detectDevices() throws BluetoothStateException {
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
    }
    private void searchServices() throws IOException {
        System.out.println("Service Inquiry Started. ");

        //hciconfig hci0 piscan
//           runCommand("sudo hciconfig hci0 reset");//set discoverable
//            runCommand("sudo sdptool add SP");
// runCommand("sudo bluetooth --compat");
        if(localDevice.setDiscoverable(DiscoveryAgent.GIAC)) {
            System.out.println("Device set to discoverable");
        }

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

    }
    private void printSearchResult(){
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
                try {
                    System.out.println((i + 1) + ". "
                            + remoteDevice.getBluetoothAddress() + " ("
                            + remoteDevice.getFriendlyName(false) + ")");
                } catch (IOException e) {
                    System.out.println((i + 1) + ". "
                            + remoteDevice.getBluetoothAddress());
                    e.printStackTrace();
                }
            }
        }
    }

    private void connectToService(){
        for (String url : vecServices) {
            try {
                conn = (StreamConnection) Connector.open(url, Connector.READ_WRITE);
                System.out.println(url + " ----=" + conn);
                din = new DataInputStream(
                        conn.openDataInputStream());
                dout = new DataOutputStream((conn.openDataOutputStream()));

                shutdown();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void detectAndTryConnect() throws IOException {
        detectDevices();
        searchServices();
        printSearchResult();
        connectToService();
    }

    public void init() {

        try {
            initLocalDevice();
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    shutdown();
                }
            });
            startScheduleTask();
        } catch (BluetoothStateException e) {
            e.printStackTrace();
        }



//        try {
//            initLocalDevice();
//            detectDevices();
//            searchServices();
//            printSearchResult();
//            connectToService();
//
//
//
//        }catch(IOException e){
//            e.printStackTrace();
//        }

    }

    private final ScheduledExecutorService scheduler = Executors
            .newScheduledThreadPool(1);

    public void startScheduleTask() {
        /**
         * not using the taskHandle returned here, but it can be used to cancel
         * the task, or check if it's done (for recurring tasks, that's not
         * going to be very useful)
         */
        final ScheduledFuture<?> taskHandle = scheduler.scheduleAtFixedRate(
                new Runnable() {
                    public void run() {
                        try {
                                detectAndTryConnect();
                        }catch(Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }, 0, 1, TimeUnit.MINUTES);

    }



    public void shutdown() {
        System.out.println("shutdown...");
        if(scheduler != null) {
            scheduler.shutdown();
        }
    }


    public static void runCommand(String command) {


        try {
        Process proc = null;

            proc = Runtime.getRuntime().exec(command);


        BufferedReader reader =
                new BufferedReader(new InputStreamReader(proc.getInputStream()));
        String line = "";

            while((line = reader.readLine()) != null) {
                System.out.print(line + "\n");
            }

        try {
            proc.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        } catch (IOException e) {
        e.printStackTrace();
    }
    }


    public void saveGpsPosition(GpsPosition position) {
        if(dout==null)return;
        try {
            if(scheduler.isShutdown()) {
                dout.writeUTF(position.toString());
                dout.flush();
            }
        } catch (IOException e) {
            startScheduleTask();
            e.printStackTrace();
        }
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
