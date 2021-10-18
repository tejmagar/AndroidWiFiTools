package tej.wifitoolslib.interfaces;

import java.util.List;

import tej.wifitoolslib.DeviceFinder;
import tej.wifitoolslib.models.DeviceItem;

public interface OnDeviceFoundListener {
    void onStart(DeviceFinder deviceFinder);
    void onProgress(DeviceFinder deviceFinder, int progress);
    void onFinished(DeviceFinder deviceFinder, List<DeviceItem> deviceItems);
    void onFailed(DeviceFinder deviceFinder);
}
