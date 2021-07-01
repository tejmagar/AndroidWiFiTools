package tej.wifitoolslib.vendors;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import tej.wifitoolslib.Constants;

public class VendorInfo {
    private static boolean initialized = false;
    private static String vendors;

    /**
     * Loads vendors name from "assets/vendors.json" file
     * @param context Context
     */

    public static void init(Context context) {
        if (initialized) {
            return;
        }

        try {
            InputStream inputStream = context.getAssets().open("vendors.json");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            vendors = stringBuilder.toString();
            initialized = true;

            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * @param macAddress Mac Address of device
     * @return vendorName
     */

    public static String getVendorName(String macAddress) {
        if (!initialized) {
            return Constants.UNKNOWN;
        }

        try {
            JSONArray jsonArray = new JSONArray(vendors);
            JSONObject object;
            String mac_prefix;
            String vendorName;

            for (int i = 0; i < jsonArray.length(); i++) {
                object = jsonArray.getJSONObject(i);
                mac_prefix = object.getString("m");
                vendorName = object.getString("n");

                if (macAddress.toLowerCase().startsWith(mac_prefix.toLowerCase())) {
                    return vendorName;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return Constants.UNKNOWN;
    }
}
