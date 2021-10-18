package tej.wifitoolslib.vendor;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class VendorInfo {

    public static final String UNKNOWN = "UnKnown";

    private static String json = "[]";
    private static JSONArray jsonArray;

    private static void readVendorFile(Context context) {
        if (jsonArray != null) {
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

            json = stringBuilder.toString();

            bufferedReader.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static JSONArray getJsonArray(Context context) throws JSONException {
        if (jsonArray == null) {
            readVendorFile(context);
            jsonArray = new JSONArray(json);
        }

        return jsonArray;
    }

    public static String getVendorName(Context context, String macAddress) {
        try {
            JSONArray jsonArray = getJsonArray(context);

            String macAddressPrefix, vendorName;

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                macAddressPrefix = jsonObject.getString("m");
                vendorName = jsonObject.getString("n");

                if (macAddress.toLowerCase().startsWith(macAddressPrefix.toLowerCase())) {
                    return vendorName;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return UNKNOWN;
    }
}
