package tej.wifitools;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import tej.wifitoolslib.DevicesFinder;
import tej.wifitoolslib.Utils;
import tej.wifitoolslib.interfaces.OnDeviceFindListener;
import tej.wifitoolslib.models.DeviceItem;
import tej.wifitoolslib.vendors.VendorInfo;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        DevicesFinder devicesFinder = new DevicesFinder(this, new OnDeviceFindListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onDeviceFound(DeviceItem deviceItem) {

            }

            @Override
            public void onComplete(List<DeviceItem> deviceItems) {

            }

            @Override
            public void onFailed(String message) {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });

        devicesFinder.setTimeout(5000).start();
    }
}