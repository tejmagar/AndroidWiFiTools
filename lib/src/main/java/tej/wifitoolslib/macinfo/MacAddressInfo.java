package tej.wifitoolslib.macinfo;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;

import tej.wifitoolslib.DeviceFinder;
import tej.wifitoolslib.NetworkInfo;
import tej.wifitoolslib.models.DeviceItem;
import tej.wifitoolslib.vendor.VendorInfo;

public class MacAddressInfo {
    /**
     * Runs command "ip n"
     * <p>Sample result:</p>
     * <pre>
     * 192.168.1.1 dev wlan0 lladdr c4:70:0b:17:79:b8 REACHABLE
     * 192.168.1.144 dev wlan0  FAILED
     * 192.168.1.35 dev wlan0  FAILED
     * 192.168.1.178 dev wlan0  FAILED
     * 192.168.1.45 dev wlan0 lladdr 0a:e0:af:b1:77:7f REACHABLE
     * ...
     * </pre>
     */
    public static void setMacAddress(Context context, List<DeviceItem> deviceItems) {
        HashMap<String, DeviceItem> deviceItemHashMap = new HashMap<>();

        for (DeviceItem deviceItem : deviceItems) {
//            Log.e("added", deviceItem.getDeviceName());
            deviceItemHashMap.put(deviceItem.getIpAddress(), deviceItem);
        }

        String currentDeviceIpAddress = NetworkInfo.getDeviceIpAddress(context);
        DeviceItem currentDeviceItem = deviceItemHashMap.get(currentDeviceIpAddress);

        if (currentDeviceItem != null) {
            String currentDeviceMacAddress = MacAddressInfo.getCurrentDeviceMacAddress(
                    currentDeviceIpAddress);
            currentDeviceItem.setMacAddress(currentDeviceMacAddress);
            currentDeviceItem.setVendorName(VendorInfo.getVendorName(context,
                    currentDeviceMacAddress));
        }

        Runtime runtime = Runtime.getRuntime();

        try {
            Process process = runtime.exec("ip n");
            process.waitFor();

            int exitCode = process.exitValue();

            if (exitCode != 0) {
                return;
            }

            InputStreamReader inputStreamReader = new InputStreamReader(process.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line, macAddress, ipAddress;
            String[] values;

            while ((line = bufferedReader.readLine()) != null) {
                values = line.split(" ");

                if (values.length == 6) {

                    /* If line starts with ip address, return macAddress
                     *  192.168.1.45 dev wlan0 lladdr 0a:e0:af:b1:77:7f REACHABLE
                     */

                    ipAddress = values[0];
                    macAddress = values[4];

                    DeviceItem deviceItem = deviceItemHashMap.get(ipAddress);

                    if (deviceItem != null) {
                        deviceItem.setMacAddress(macAddress);
                        deviceItem.setVendorName(VendorInfo.getVendorName(context,
                                deviceItem.getMacAddress()));
                    }
                }
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static String getCurrentDeviceMacAddress(String ipAddress) {
        try {
            InetAddress localIP = InetAddress.getByName(ipAddress);
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(localIP);

            if (networkInterface == null) {
                return DeviceFinder.UNKNOWN;
            }

            byte[] hardwareAddress = networkInterface.getHardwareAddress();

            if (hardwareAddress == null) {
                return DeviceFinder.UNKNOWN;
            }

            StringBuilder stringBuilder = new StringBuilder(18);
            for (byte b : hardwareAddress) {
                if (stringBuilder.length() > 0) {
                    stringBuilder.append(":");
                }

                stringBuilder.append(String.format("%02x", b));
            }

            return stringBuilder.toString();
        } catch (UnknownHostException | SocketException e) {
            e.printStackTrace();
        }

        return DeviceFinder.UNKNOWN;
    }
}
