package com.morefun.ysdk.sample.utils;

import android.content.Context;
import android.os.PowerManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class WakeLockUtil {
    private static PowerManager.WakeLock wakeLock;
    /**
     * 点亮屏幕
     *
     * @param
     */
    @Nullable
    public static PowerManager.WakeLock acquireWakeLock(@NonNull Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (pm == null)
            return null;
        wakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |
                        PowerManager.FULL_WAKE_LOCK |
                        PowerManager.ON_AFTER_RELEASE,
                context.getClass().getName());
        wakeLock.acquire();
        return wakeLock;
    }

    public static void release() {
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
    }
}