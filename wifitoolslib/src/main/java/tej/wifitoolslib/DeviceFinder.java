package tej.wifitoolslib;

import android.content.Context;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import tej.wifitoolslib.interfaces.OnDeviceFoundListener;
import tej.wifitoolslib.macinfo.MacAddressInfo;
import tej.wifitoolslib.models.DeviceItem;

public class DeviceFinder {
    public static final String UNKNOWN = "UnKnown";
    public static final int ERROR_USER_STOPPED = 0;
    public static final int UNKNOWN_ERROR = 1;

    private final Context context;
    private final OnDeviceFoundListener onDeviceFoundListener;

    private ExecutorService executorService;
    private boolean isRunning = false;
    private int timeout = 500;
    private boolean stopRequest = false;

    private final List<DeviceItem> reachableDevices = new ArrayList<>();

    public DeviceFinder(Context context, OnDeviceFoundListener onDeviceFoundListener) {
        this.context = context;
        this.onDeviceFoundListener = onDeviceFoundListener;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void start() {
        isRunning = true;
        stopRequest = false;
        startPing();
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void stop() {
        stopRequest = true;
        executorService.shutdownNow();
        isRunning = false;
    }

    private void startPing() {
        if (!NetworkInfo.isWifiConnected(context)) {
            return;
        }

        executorService = Executors.newFixedThreadPool(255);
        onDeviceFoundListener.onStart(this);

        String gatewayAddress = NetworkInfo.getGatewayAddress(context);
        int lastDotIndex = gatewayAddress.lastIndexOf(".");

        String ipPrefix = gatewayAddress.substring(0, lastDotIndex + 1);

        String ipAddressToPing;

        try {
            for (int i = 0; i < 255; i++) {
                ipAddressToPing = ipPrefix + (i + 1);
                executorService.execute(new Ping(ipAddressToPing));
            }
        } catch (Exception e) {
            e.printStackTrace();

            if (stopRequest) {
                onDeviceFoundListener.onFailed(this, ERROR_USER_STOPPED);
            } else {
                onDeviceFoundListener.onFailed(this, UNKNOWN_ERROR);
            }

            return;
        }

        executorService.shutdown();

        try {
            boolean wait = executorService.awaitTermination(10, TimeUnit.MINUTES);

            if (wait) {
                MacAddressInfo.setMacAddress(context, reachableDevices);
                onDeviceFoundListener.onFinished(this, reachableDevices);
            } else {
                onDeviceFoundListener.onFailed(this, UNKNOWN_ERROR);
            }

        } catch (InterruptedException e) {
            if (stopRequest) {
                onDeviceFoundListener.onFailed(this, ERROR_USER_STOPPED);
            } else {
                onDeviceFoundListener.onFailed(this, UNKNOWN_ERROR);
            }

            e.printStackTrace();
        }

        isRunning = false;
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

                if (Thread.currentThread().isInterrupted()) {
                    return;
                }

                if (inetAddress.isReachable(timeout)) {
                    DeviceItem deviceItem = new DeviceItem();
                    // Vendor name and mac address still not set
                    deviceItem.setIpAddress(ipAddress);
                    deviceItem.setDeviceName(inetAddress.getHostName());
                    reachableDevices.add(deviceItem);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
