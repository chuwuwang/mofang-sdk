package com.morefun.ysdk.sample;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.morefun.yapi.engine.DeviceServiceEngine;
import com.morefun.ysdk.sample.device.DeviceHelper;
import com.morefun.ysdk.sample.receiver.PackageInstallReceiver;

public class MyApplication extends Application {

    private final String TAG = MyApplication.class.getName();
    private final String SERVICE_ACTION = "com.morefun.ysdk.service";
    private final String SERVICE_PACKAGE = "com.morefun.ysdk";
    private static MyApplication instance;
    private DeviceServiceEngine deviceServiceEngine = null;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        bindDeviceService();
        registerBroadcast();
    }

    public static Application getInstance() {
        return instance;
    }

    public DeviceServiceEngine getDeviceService() {
        return deviceServiceEngine;
    }

    public void bindDeviceService() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (null != deviceServiceEngine) {
                        return;
                    }

                    Intent intent = new Intent();
                    intent.setAction(SERVICE_ACTION);
                    intent.setPackage(SERVICE_PACKAGE);
                    Log.e(TAG, "======bindService======");
                    bindService(intent, connection, Context.BIND_AUTO_CREATE);

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            deviceServiceEngine = null;
            Log.e(TAG, "======onServiceDisconnected======");
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            deviceServiceEngine = DeviceServiceEngine.Stub.asInterface(service);
            Log.d(TAG, "======onServiceConnected======");

            try {
                DeviceHelper.reset();
                DeviceHelper.initDevices(MyApplication.this);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            linkToDeath(service);
        }

        private void linkToDeath(IBinder service) {
            try {
                service.linkToDeath(new IBinder.DeathRecipient() {
                    @Override
                    public void binderDied() {
                        Log.d(TAG, "======binderDied======");
                        deviceServiceEngine = null;
                        bindDeviceService();
                    }
                }, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };



    private void registerBroadcast() {
        Log.i("MyApplication", "registerBroadcast");

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        intentFilter.addAction(Intent.ACTION_BOOT_COMPLETED);
        intentFilter.addAction("com.morefun.scancode.broadcast");

        intentFilter.addDataScheme("package");

        registerReceiver(new PackageInstallReceiver(), intentFilter);
    }
}
