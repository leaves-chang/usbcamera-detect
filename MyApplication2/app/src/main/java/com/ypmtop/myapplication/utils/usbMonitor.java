package com.ypmtop.myapplication.utils;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;



import java.util.HashMap;
import java.util.Iterator;



public class usbMonitor extends BroadcastReceiver {

    private final String TAG = "usbMonitor";
    private Context mContext;
    public static final int USB_0XEF_SUBCLASS_2 = 2;
    public static final int MAX_USB_DEVICES = 10;
    PendingIntent pendingIntent;
    public static final UsbDevice[] uvcDevLists = new UsbDevice[MAX_USB_DEVICES];
    UsbDevice currentDevice;

    private static final String ACTION_USB_PERMISSION =
            "com.ypmtop.myapplication.utils.usbMonitor.USB_PERMISSION";


    public usbMonitor(Context context) {
        mContext = context;

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "action:" + action);
 /*
        if (ACTION_USB_PERMISSION.equals(action)) {
            Log.d(TAG, "cll enter. ");
            synchronized (this) {
                UsbManager manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
                StringBuilder usbSb = new StringBuilder();
                HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
                Log.d(TAG, "cll enter 11. ");
                for(UsbDevice usbDevice : deviceList.values()) {
                    Log.d(TAG, "cll enter. 111");
                    usbSb.append("Model    : " + usbDevice.getDeviceName() + "\n");
                    usbSb.append(" Id      : " + usbDevice.getDeviceId() + "\n");
                    usbSb.append(" Class   : " + usbDevice.getDeviceClass() + "\n");
                    usbSb.append(" Prod.Id : " + usbDevice.getProductId() + "\n");
                    usbSb.append(" Vendor.Id : " + usbDevice.getVendorId() + "\n");
                    int iCount = usbDevice.getInterfaceCount();
                    for (int i = 0; i < iCount; i++) {
                        UsbInterface usbInterface = usbDevice.getInterface(i);
                        usbSb.append("    Interface " + i + " :\n");
                        usbSb.append("     Class: " + usbInterface.getInterfaceClass() + "\n");
                    }
                    Log.d(TAG, "usb info" + usbSb.toString());
                }

//                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
//                    if(device != null){
//                        //call method to set up device communication
//                    }
//                }
//                else {
//                    Log.d(TAG, "permission denied for device " + device);
//                }
            }
        }
*/

        if (ACTION_USB_PERMISSION.equals(action)) {
            synchronized (this) {
                mContext.unregisterReceiver(this);
                UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)
                        && currentDevice.equals(device)) {
                    //TODO 授权成功，操作USB设备
                    Log.d(TAG, "cll ok.");
                }else{
                    //用户点击拒绝了
                    Log.d(TAG, "cll ng.");
                }
                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                    if(device != null){
                        //call method to set up device communication
                    }
                }
                else {
                    Log.d(TAG, "permission denied for device " + device);
                }
            }
        }
        if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action) ||
                UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {

            UsbManager manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
            StringBuilder usbSb = new StringBuilder();
            HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
            Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
            for(UsbDevice usbDevice : deviceList.values()) {
                usbSb.append("Model    : " + usbDevice.getDeviceName() + "\n");
                usbSb.append(" Id      : " + usbDevice.getDeviceId() + "\n");
                usbSb.append(" Class   : " + usbDevice.getDeviceClass() + "\n");
                usbSb.append(" Sub Class   : " + usbDevice.getDeviceSubclass() + "\n");
                usbSb.append(" Protocol    : " + usbDevice.getDeviceProtocol() + "\n");
                usbSb.append(" Prod.Id : " + usbDevice.getProductId() + "\n");
                usbSb.append(" Vendor.Id : " + usbDevice.getVendorId() + "\n");
                usbSb.append(" ManufacturerName : " + usbDevice.getManufacturerName() + "\n");
                usbSb.append(" ProductName : " + usbDevice.getProductName() + "\n");
                usbSb.append(" SerialName : " + usbDevice.getSerialNumber() + "\n");
                int iCount = usbDevice.getInterfaceCount();
                for (int i = 0; i < iCount; i++) {
                    UsbInterface usbInterface = usbDevice.getInterface(i);
                    usbSb.append("    Interface " + i + " :\n");
                    usbSb.append("     Class: " + usbInterface.getInterfaceClass() + "\n");
                }
                Log.d(TAG, usbSb.toString());
            }
            if(UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                while (deviceIterator.hasNext()) {
                    UsbDevice device = deviceIterator.next();
                    if (isUsbCamera(device)) {
                        Log.d(TAG, "uvc requestPermission.");
                        manager.requestPermission(device, pendingIntent);
                    }

                }
            }
        }


    }

    public void registerUsbReceiver(Context context) {

        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        context.registerReceiver(this, filter);
        pendingIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
    }



    public boolean isUsbCamera(UsbDevice usbDevice) {
        boolean result = false;
        if (usbDevice != null) {
            if (UsbConstants.USB_CLASS_MISC == usbDevice.getDeviceClass() && USB_0XEF_SUBCLASS_2 == usbDevice.getDeviceSubclass()) {
                int iCount = usbDevice.getInterfaceCount();
                for (int i = 0; i < iCount; i++) {
                    UsbInterface usbInterface = usbDevice.getInterface(i);
                    if (usbInterface.getInterfaceClass() == UsbConstants.USB_CLASS_VIDEO) {
                        currentDevice = usbDevice;
                        result = true;
                        break;
                    }
                }
            } else if (UsbConstants.USB_CLASS_VIDEO == usbDevice.getDeviceClass()) {
                currentDevice = usbDevice;
                result = true;
            }
        }
        Log.d(TAG, "isUsbCamera: " + result);
        return result;
    }

    private void getUsbPermission(Context context, UsbDevice mUSBDevice) {
        UsbManager mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
       // mContext.registerReceiver(mUsbReceiver, filter);
        mUsbManager.requestPermission(mUSBDevice, pendingIntent);
    }


}
