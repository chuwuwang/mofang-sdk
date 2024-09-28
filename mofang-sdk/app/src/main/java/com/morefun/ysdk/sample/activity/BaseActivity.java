package com.morefun.ysdk.sample.activity;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.morefun.yapi.device.printer.MultipleAppPrinter;
import com.morefun.ysdk.sample.device.DeviceHelper;


public abstract class BaseActivity extends Activity {

    protected MultipleAppPrinter printerMul;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            printerMul = DeviceHelper.getPrinter();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    protected void showResult(final TextView textView, final String text) {
        Log.d("BaseActivity", text);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.append(text + "\r\n");
            }
        });
    }

    protected void btnToggle(Button btn,boolean clickable) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btn.setEnabled(clickable);
            }
        });
    }

    protected void clearText(final TextView textView) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText("");
            }
        });
    }


    protected abstract void setButtonName();

    protected boolean checkAppInstalled(String pkgName) {
        if (pkgName == null || pkgName.isEmpty()) {
            return false;
        }
        PackageInfo packageInfo;
        try {
            packageInfo = getPackageManager().getPackageInfo(pkgName, 0);
            return packageInfo != null;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
}