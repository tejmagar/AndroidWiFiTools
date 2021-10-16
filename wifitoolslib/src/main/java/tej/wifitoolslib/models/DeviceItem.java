package tej.wifitoolslib.models;

import androidx.annotation.NonNull;

public class DeviceItem {
    private String ipAddress;
    private String deviceName;
    private String macAddress;
    private String vendorName;

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public String getVendorName() {
        return vendorName;
    }

    public boolean isDeviceNameAndIpAddressSame() {
        return deviceName.equals(ipAddress);
    }

    @Override
    @NonNull
    public String toString() {
        return "DeviceItem{" +
                "ipAddress='" + ipAddress + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", macAddress='" + macAddress + '\'' +
                ", vendorName='" + vendorName + '\'' +
                ", deviceNameAndIpAddressSame='" + isDeviceNameAndIpAddressSame() + '\'' +
                '}';
    }
}
