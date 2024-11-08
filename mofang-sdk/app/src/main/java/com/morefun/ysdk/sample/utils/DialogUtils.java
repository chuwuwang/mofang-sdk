package com.morefun.ysdk.sample.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;

import com.morefun.ysdk.sample.R;

import java.util.Objects;


public class DialogUtils {
    private static ProgressDialog pd;
    private static AlertDialog dialog;

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

    public interface OnSelectListener {
        void onSelect(int position);
    }

    public static void showDropdownDialog(Context context, String title, int textArrayResId, OnSelectListener listener) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(context));
        builder.setTitle(title);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Objects.requireNonNull(context),
                textArrayResId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final Spinner spinner = new Spinner(Objects.requireNonNull(context));
        spinner.setAdapter(adapter);
        spinner.setPadding(26, 16, 26, 16);
        builder.setView(spinner);
        builder.setPositiveButton("CONFIRM", (dialogInterface, i) -> {
            listener.onSelect(spinner.getSelectedItemPosition());
            dialogInterface.dismiss();
        }).setNegativeButton("CANCEL",
                (dialogInterface, i) -> dialogInterface.dismiss());
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spinner.setSelection(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                spinner.setSelection(0);
            }
        });
        dialog = builder.create();
        dialog.show();
    }
}
