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

    private final Context context;
    private final OnDeviceFoundListener onDeviceFoundListener;

    private ExecutorService executorService;
    private boolean isRunning = false;
    private int timeout = 500;

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
        startPing();
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void stop() {
        executorService.shutdown();
        executorService.shutdownNow();
    }

    private void startPing() {
        executorService = Executors.newFixedThreadPool(255);

        String gatewayAddress = NetworkInfo.getGatewayAddress(context);
        int lastDotIndex = gatewayAddress.lastIndexOf(".");

        String ipPrefix = gatewayAddress.substring(0, lastDotIndex + 1);

        String ipAddressToPing;

        for (int i=0; i<255; i++) {
            ipAddressToPing = ipPrefix + i;
            executorService.execute(new Ping(ipAddressToPing));
        }

        executorService.shutdown();

        try {
            boolean wait = executorService.awaitTermination(10, TimeUnit.MINUTES);

            if (wait) {
                MacAddressInfo.setMacAddress(reachableDevices);
                onDeviceFoundListener.onFinished(this, reachableDevices);
            } else {
                onDeviceFoundListener.onFailed(this);
            }

        } catch (InterruptedException e) {
            onDeviceFoundListener.onFailed(this);
            e.printStackTrace();
        }
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
