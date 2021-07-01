package tej.wifitoolslib.models;

import androidx.annotation.NonNull;

public class DeviceItem {
    private final String ipAddress;
    private final String deviceName;
    private final String macAddress;
    private final String vendorName;

    public DeviceItem(String ipAddress, String deviceName, String macAddress, String vendorName) {
        this.ipAddress = ipAddress;
        this.deviceName = deviceName;
        this.macAddress = macAddress;
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
