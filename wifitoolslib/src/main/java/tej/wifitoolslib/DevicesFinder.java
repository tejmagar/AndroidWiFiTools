package tej.wifitoolslib;

import android.app.Activity;
import android.content.Context;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import tej.wifitoolslib.interfaces.OnDeviceFindListener;
import tej.wifitoolslib.models.DeviceItem;
import tej.wifitoolslib.vendors.VendorInfo;

public class DevicesFinder {
    private final Context context;
    private final OnDeviceFindListener deviceFindListener;
    private int timeout = 1000;
    private boolean isRunning = false;

    private String currentDeviceIpAddress;

    // Error Codes
    public static final int IP_ADDRESS_NULL = 0;
    public static final int INVALID_TIMEOUT = 1;
    public static final int UNKNOWN_ERROR = 2;

    private final List<DeviceItem> reachableDevices = new ArrayList<>();

    public DevicesFinder(Context context, OnDeviceFindListener onDeviceFindListener) {
        this.context = context;
        this.deviceFindListener = onDeviceFindListener;
    }

    public DevicesFinder setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public int getTimeout() {
        return timeout;
    }

    public void start() {
        if (isRunning) {
            return;
        }

        reachableDevices.clear();

        if (timeout == 0) {
            deviceFindListener.onFailed(INVALID_TIMEOUT);
            return;
        }

        isRunning = true;
        deviceFindListener.onStart();

        currentDeviceIpAddress = Utils.getCurrentDeviceIpAddress();

        if (currentDeviceIpAddress == null) {
            isRunning = false;
            deviceFindListener.onFailed(IP_ADDRESS_NULL);
            return;
        }

        new Thread(() -> {
            // Initializing vendor info
            VendorInfo.init(context);

            // Scans IP Address ranging from 192.168.*.1 to 192.168.*.255
            ExecutorService executorService = Executors.newFixedThreadPool(255);

            int lastDotIndex;
            String newIpAddressPrefix;
            String newIpAddress;

            for (int i=0; i<255; i++) {
                /*
                 * Sample IP Address for current device
                 * 192.168.1.45
                 */

                lastDotIndex = currentDeviceIpAddress.lastIndexOf(".");
                newIpAddressPrefix = currentDeviceIpAddress.substring(0, lastDotIndex + 1);
                newIpAddress = newIpAddressPrefix + (i + 1);

                executorService.execute(new Ping(newIpAddress));
            }

            executorService.shutdown();

            try {
                boolean success = executorService.awaitTermination(10, TimeUnit.MINUTES);

                isRunning = false;

                if (success) {
                    ((Activity)context).runOnUiThread(() ->
                            deviceFindListener.onComplete(reachableDevices));

                    return;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            isRunning = false;

            ((Activity)context).runOnUiThread(() -> deviceFindListener.onFailed(UNKNOWN_ERROR));

        }).start();
    }

    class Ping implements Runnable {

        private final String ipAddress;

        public Ping(String ipAddress) {
            this.ipAddress = ipAddress;
        }

        @Override
        public void run() {
            try {
                InetAddress inetAddress = InetAddress.getByName(ipAddress);

                if (inetAddress.isReachable(timeout)) {
                    String deviceName = inetAddress.getHostName();
                    String macAddress = MacAddressInfo.getMacAddressFromIp(ipAddress);
                    String vendorName = VendorInfo.getVendorName(macAddress);

                    DeviceItem deviceItem = new DeviceItem(ipAddress, deviceName, macAddress,
                            vendorName);
                    reachableDevices.add(deviceItem);

                    ((Activity)context).runOnUiThread(() ->
                            deviceFindListener.onDeviceFound(deviceItem));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
