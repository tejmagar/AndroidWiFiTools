package tej.wifitoolslib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

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
     *
     * @return macAddress
     */
    public static String getMacAddressFromIp(String ipAddress) {
        if (ipAddress.equals(Utils.getCurrentDeviceIpAddress())) {
            return getCurrentDeviceMacAddress(ipAddress);
        }

        Runtime runtime = Runtime.getRuntime();
        
        try {
            Process process = runtime.exec("ip n");
            process.waitFor();
            InputStreamReader inputStreamReader = new InputStreamReader(process.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line, macAddress;
            String[] values;
            
            while ((line = bufferedReader.readLine()) != null) {
                values = line.split(" ");

                if (values.length == 6) {

                    /* If line starts with ip address, return macAddress
                     *  192.168.1.45 dev wlan0 lladdr 0a:e0:af:b1:77:7f REACHABLE
                    */
                    
                    if (line.startsWith(ipAddress)) {
                        macAddress = values[4]; 
                        return macAddress;
                    }
                    
                }
            }
            
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return Constants.UNKNOWN;
    }

    public static String getCurrentDeviceMacAddress(String ipAddress) {
        try {
            InetAddress localIP = InetAddress.getByName(ipAddress);
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(localIP);

            if (networkInterface == null) {
                return Constants.UNKNOWN;
            }

            byte[] hardwareAddress = networkInterface.getHardwareAddress();

            if (hardwareAddress == null) {
                return Constants.UNKNOWN;
            }

            StringBuilder stringBuilder = new StringBuilder(18);
            for (byte b : hardwareAddress) {
                if (stringBuilder.length() > 0) {
                    stringBuilder.append(Constants.MAC_SEPARATOR);
                }

                stringBuilder.append(String.format("%02x", b));
            }

            return stringBuilder.toString();
        } catch (UnknownHostException | SocketException e) {
            e.printStackTrace();
        }

        return Constants.UNKNOWN;
    }
}
