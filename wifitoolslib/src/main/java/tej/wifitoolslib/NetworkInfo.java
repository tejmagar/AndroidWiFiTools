package tej.wifitoolslib;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;

public class NetworkInfo {
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
    }

    public static DhcpInfo getDhcpInfo(Context context) {
        WifiManager wifiManager = (WifiManager)context.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        return wifiManager.getDhcpInfo();
    }

    public static String getGatewayAddress(Context context) {

        return Utils.parseIpAddress(getDhcpInfo(context).gateway);
    }

    public static String getDeviceIpAddress(Context context) {
        return Utils.parseIpAddress(getDhcpInfo(context).ipAddress);
    }

}
