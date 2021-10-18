package tej.wifitoolslib.interfaces;

import java.util.List;

import tej.wifitoolslib.DeviceFinder;
import tej.wifitoolslib.models.DeviceItem;

public interface OnDeviceFoundListener {
    void onStart(DeviceFinder deviceFinder);
    void onFinished(DeviceFinder deviceFinder, List<DeviceItem> deviceItems);
    void onFailed(DeviceFinder deviceFinder, int errorCode);
}
