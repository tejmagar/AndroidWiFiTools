package tej.wifitools;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import tej.wifitoolslib.DeviceFinder;
import tej.wifitoolslib.interfaces.OnDeviceFoundListener;
import tej.wifitoolslib.models.DeviceItem;

public class MainActivity extends AppCompatActivity {

    private final List<String> devices = new ArrayList<>();
    private long start, end;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = findViewById(R.id.listview);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, devices);
        listView.setAdapter(arrayAdapter);

        DeviceFinder devicesFinder = new DeviceFinder(this, new OnDeviceFoundListener() {

            @Override
            public void onStart(DeviceFinder deviceFinder) {
                start = System.currentTimeMillis();
            }

            @Override
            public void onFinished(DeviceFinder deviceFinder, List<DeviceItem> deviceItems) {
                end = System.currentTimeMillis();

                float time = (end - start)/1000f;
                Toast.makeText(getApplicationContext(), "Scan finished in " + time
                        + " seconds", Toast.LENGTH_SHORT).show();

                for (DeviceItem deviceItem : deviceItems) {
                    String data = "Device Name: " + deviceItem.getDeviceName() + "\n" +
                            "Ip Address: " + deviceItem.getIpAddress() + "\n" +
                            "MAC Address: " + deviceItem.getMacAddress() + "\n" +
                            "Vendor Name: " + deviceItem.getVendorName();

                    devices.add(data);
                }

                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed(DeviceFinder deviceFinder, int errorCode) {

            }
        });

        devicesFinder.setTimeout(500).start();
    }
}