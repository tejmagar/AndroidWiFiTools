package tej.wifitoolslib;

import android.content.Context;
import android.net.wifi.WifiManager;

public class NetworkInfo {
    public static String getGatewayAddress(Context context) {
        WifiManager wifiManager = (WifiManager)context.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        return Utils.parseIpAddress(wifiManager.getDhcpInfo().gateway);
    }

}
