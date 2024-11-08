package com.morefun.ysdk.sample.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.morefun.ysdk.sample.MyApplication;
import com.morefun.ysdk.sample.utils.ToastUtils;
import java.util.Objects;

public class PackageInstallReceiver extends BroadcastReceiver {
    private String TAG = PackageInstallReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, String.format("PackageInstallReceiver action:%s", action));
        if (action.equals(Intent.ACTION_PACKAGE_ADDED)) {
            String packageName = Objects.requireNonNull(intent.getData()).getSchemeSpecificPart();
            Log.d(TAG, String.format("PackageInstallReceiver packageName:%s", packageName));

            if (packageName.equals("com.morefun.ysdk")) {
                restartApp();
                return;
            }
        } else if (action.equals("com.morefun.scancode.broadcast")) {
            ToastUtils.show(context, intent.getStringExtra("mf_scanner_text"));
        }

    }

    public void restartApp() {
        final Intent intent = MyApplication.getInstance().getPackageManager().getLaunchIntentForPackage(MyApplication.getInstance().getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        MyApplication.getInstance().startActivity(intent);
        System.exit(0);
    }

}
