package tej.wifitoolslib.interfaces;

import java.util.List;

import tej.wifitoolslib.models.DeviceItem;

public interface OnDeviceFindListener {
    void onStart();
    void onDeviceFound(DeviceItem deviceItem);
    void onComplete(List<DeviceItem> deviceItems);
    void onFailed(int errorCode);
}
