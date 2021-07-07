# Android WiFi Tools
Android library for finding connected devices on the same WiFi network. It can provide IP Addresses, device names, MAC Address and vendor names.
\
[!["Buy Me A Coffee"](https://www.buymeacoffee.com/assets/img/custom_images/orange_img.png)](https://www.buymeacoffee.com/tejmagar)

<img src="screenshot.png" width="360" height="780">

## Usage
Add the JitPack repository to your build file
Add it in your root build.gradle at the end of repositories:

```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

### Add the dependency

```
dependencies {
    implementation 'com.github.tejmagar:AndroidWiFiTools:1.0.2'
}
```

### Add Permission

```
<uses-permission android:name="android.permission.INTERNET"/>
```

### Find Connected Devices
```
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
            public void onFailed(int errorCode) {

            }
        });
        
devicesFinder.start();
```

### Set Timeout
Increasing timeout value may give you better results.

```
devicesFinder.setTimeout(5000).start();
```

### Get Mac Address from IP Address
```
String macAddress = MacAddressInfo.getMacAddressFromIp("192.168.1.1");
```
Before running this code, make sure you have already run ```deviceFinder.start();``` method.
\
Returns device Mac Address. If not found, it will return "unknown" or ```Constants.UNKOWN```

### Get current device IP Address
```
String ipAddress = devicesFinder.getCurrentDeviceIpAddress();
// or
String ipAddress = Utils.getCurrentDeviceIpAddress();
```

### Get current device Mac Address
```
String currentDeviceIpAddress = devicesFinder.getCurrentDeviceIpAddress();
String currentDeviceMacAddress = MacAddressInfo.getCurrentDeviceMacAddress(currentDeviceIpAddress);
```

### Get vendor name from Mac Address
```
String vendorName = VendorInfo.getVendorName("94:17:00:3a:f9:09");
```

returns device Mac Address. If not found, it will return "unknown" or ```Constants.UNKNOWN```

 ```VendorInfo.init(context);``` will be automatically called while starting the device finder. If not, make sure you have initialized it first.
 
