package com.morefun.ysdk.sample.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import com.morefun.ysdk.sample.R;


public class DialogUtils {
    private static ProgressDialog pd;

    public static void showProgressDialog(Activity activity, String title) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pd = new ProgressDialog(activity);
                pd.setMessage(title);
                pd.setCancelable(false);
                pd.show();
            }
        });
    }

    public static void setProgressMessage(Activity activity, String msg) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (pd != null) {
                    pd.setMessage(msg);
                }
            }
        });
    }

    public static void dismissProgressDialog(Activity activity) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (pd != null) {
                    pd.dismiss();
                }
            }
        });
    }

    public static void showAlertDialog(Activity activity, String msg) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(activity.getString(R.string.tip_prompt));
                builder.setMessage(msg);
                builder.setCancelable(true);
                builder.setPositiveButton(activity.getString(R.string.tip_confirm), null);
                builder.show();
            }
        });
    }

    public static void showAlertDialog(Activity activity, String msg, OnClickListener listener) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(activity.getString(R.string.tip_prompt));
                builder.setMessage(msg);
                builder.setCancelable(false);
                builder.setNegativeButton(activity.getString(R.string.tip_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (listener != null) {
                            listener.onCancel();
                        }
                    }
                });
                builder.setPositiveButton(activity.getString(R.string.tip_confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (listener != null) {
                            listener.onConfirm();
                        }
                    }
                });
                builder.show();
            }
        });
    }

    public interface OnClickListener {
        void onConfirm();

        void onCancel();
    }
}
