package tej.wifitools;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import tej.wifitoolslib.DevicesFinder;
import tej.wifitoolslib.interfaces.OnDeviceFindListener;
import tej.wifitoolslib.models.DeviceItem;

public class MainActivity extends AppCompatActivity {

    private final List<String> devices = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = findViewById(R.id.listview);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, devices);
        listView.setAdapter(arrayAdapter);

        DevicesFinder devicesFinder = new DevicesFinder(this, new OnDeviceFindListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onDeviceFound(DeviceItem deviceItem) {
                String data = "Device Name: " + deviceItem.getDeviceName() + "\n" +
                        "Ip Address: " + deviceItem.getIpAddress() + "\n" +
                        "MAC Address: " + deviceItem.getMacAddress() + "\n" +
                        "Vendor Name: " + deviceItem.getVendorName();

                devices.add(data);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onComplete(List<DeviceItem> deviceItems) {

            }

            @Override
            public void onFailed(int errorCode) {

            }
        });

        devicesFinder.setTimeout(5000).start();
    }
}