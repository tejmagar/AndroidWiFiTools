package tej.wifitoolslib.models;

import tej.wifitoolslib.DeviceFinder;

public class DeviceItem {
    private String ipAddress = DeviceFinder.UNKNOWN;
    private String macAddress = DeviceFinder.UNKNOWN;
    private String deviceName = DeviceFinder.UNKNOWN;
    private String vendorName = DeviceFinder.UNKNOWN;

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public boolean isIpAddressAndDeviceNameSame() {
        return ipAddress.equals(deviceName);
    }
}
