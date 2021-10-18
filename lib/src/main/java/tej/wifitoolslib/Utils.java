package tej.wifitoolslib;

import org.apache.commons.lang3.ArrayUtils;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;


public class Utils {
    public static String parseIpAddress(long ip) {
        try {
            byte[] byteAddress = BigInteger.valueOf(ip).toByteArray();
            ArrayUtils.reverse(byteAddress);
            return InetAddress.getByAddress(byteAddress).getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return null;
    }
}
