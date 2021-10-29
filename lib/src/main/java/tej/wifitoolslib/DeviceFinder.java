package tej.wifitoolslib;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

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
    public static final int WIFI_NOT_CONNECTED = 2;
    public static final int OTHERS = 3;

    private final Context context;
    private final OnDeviceFoundListener onDeviceFoundListener;

    private ExecutorService executorService;
    private boolean isRunning = false;
    private int timeout = 500;
    private boolean stopRequested = false;

    private final List<DeviceItem> reachableDevices = new ArrayList<>();

    private final Handler handler;

    public DeviceFinder(Context context, OnDeviceFoundListener onDeviceFoundListener) {
        this.context = context;
        this.onDeviceFoundListener = onDeviceFoundListener;

        handler = new Handler(Looper.getMainLooper());
    }

    public DeviceFinder setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public void start() {
        isRunning = true;
        stopRequested = false;

        reachableDevices.clear();

        new Thread(this::startPing).start();
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void stop() {
        stopRequested = true;
        executorService.shutdownNow();

        if (isRunning) {
            sendFailedEvent(ERROR_USER_STOPPED);
        }

        isRunning = false;
    }

    private void sendStartEvent() {
        handler.post(() -> onDeviceFoundListener.onStart(this));
    }

    private void sendFailedEvent(int errorCode) {
        handler.post(() -> onDeviceFoundListener.onFailed(this, errorCode));
    }

    private void sendFinishedEvent(List<DeviceItem> deviceItems) {
        handler.post(() -> onDeviceFoundListener.onFinished(this, deviceItems));
    }

    private static boolean isBelowAndroidR() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.R;
    }

    private void startPing() {
        if (!NetworkInfo.isWifiConnected(context)) {
            isRunning = false;
            sendFailedEvent(WIFI_NOT_CONNECTED);
            return;
        }

        executorService = Executors.newFixedThreadPool(255);
        sendStartEvent();

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

            if (stopRequested) {
                sendFailedEvent(ERROR_USER_STOPPED);
            } else {
                sendFailedEvent(OTHERS);
            }

            return;
        }

        executorService.shutdown();

        try {
            boolean wait = executorService.awaitTermination(10, TimeUnit.MINUTES);

            if (wait) {
                if (isBelowAndroidR()) {
                    MacAddressInfo.setMacAddress(context, reachableDevices);
                }
                sendFinishedEvent(reachableDevices);
            } else {
                sendFailedEvent(OTHERS);
            }

        } catch (InterruptedException e) {
            if (stopRequested) {
                sendFailedEvent(ERROR_USER_STOPPED);
            } else {
                sendFailedEvent(OTHERS);
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
