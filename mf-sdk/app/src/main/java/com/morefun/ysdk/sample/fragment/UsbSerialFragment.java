package com.morefun.ysdk.sample.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.morefun.yapi.ServiceResult;
import com.morefun.yapi.device.serialport.SerialPort;
import com.morefun.yapi.device.serialport.SerialPortDriver;
import com.morefun.ysdk.sample.R;
import com.morefun.ysdk.sample.device.DeviceHelper;
import com.morefun.ysdk.sample.utils.BytesUtil;
import com.morefun.ysdk.sample.utils.ToastUtils;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UsbSerialFragment extends Fragment {
    private final String TAG = UsbSerialFragment.class.getName();

    @BindView(R.id.et_usbName)
    EditText etName;

    @BindView(R.id.et_data)
    EditText etData;

    @BindView(R.id.et_recv)
    EditText etReceive;

    private SerialPort usbSerial;
    private boolean isOpen;
    private boolean bStop = true;

    private USBReceiver mUsbReceiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_usb_serial, null);
        ButterKnife.bind(this, view);
        registerReceiver();
        return view;
    }

    @OnClick({R.id.btn_open, R.id.btn_send, R.id.btn_close})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_open:
                open();
                break;
            case R.id.btn_send:
                send();
                break;
            case R.id.btn_close:
                close();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        close();
        unRegisterReceiver();
    }


    private void open() {
        try {
            int baudRate = 115200;
            int dataBits = 8;
            int stopBits = 1;
            int parity = 0;

            usbSerial = DeviceHelper.getUsbSerialPort("dev/" + etName.getText().toString());
            int connect = usbSerial.openAndInit(baudRate, dataBits, stopBits, parity);

            if (connect == ServiceResult.Success) {
                ToastUtils.show(getActivity(), "Open Serial Port Success");
                isOpen = true;
                recv();
            } else {
                ToastUtils.show(getActivity(), "Open Serial Port Fail");
            }
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.show(getActivity(), e.getMessage());
        }
    }


    private void close() {
        try {
            usbSerial.close();
            isOpen = false;
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void send() {
        try {
            String sendMessage = etData.getText().toString();
            if (TextUtils.isEmpty(sendMessage) || !isOpen) {
                return;
            }

            byte[] data = sendMessage.getBytes();
            int send = usbSerial.write(data, data.length, 0);
            if (send == 0) {
                ToastUtils.show(getActivity(), "Send Success");
            } else {
                ToastUtils.show(getActivity(), "Send Fail");
            }
        } catch (Exception e) {
            ToastUtils.show(getActivity(), e.getMessage());
        }

    }

    private void recv() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isOpen) {
                    try {
                        byte[] recvBytes = new byte[1024];

                        int read = usbSerial.read(recvBytes, recvBytes.length, 1_000);
                        recvBytes = Arrays.copyOfRange(recvBytes, 0, read);

                        if (read > 0) {
                            showReceive(BytesUtil.bytes2HexString(recvBytes));
                        }

                        SystemClock.sleep(20);
                    } catch (RemoteException e) {

                    }
                }
            }
        }).start();
    }

    private void showReceive(String msg) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                etReceive.setText(msg);
            }
        });
    }

    public void registerReceiver() {
        try {
            IntentFilter filter = new IntentFilter();
            filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
            filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
            mUsbReceiver = new USBReceiver();
            getActivity().registerReceiver(mUsbReceiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void unRegisterReceiver() {
        if (mUsbReceiver != null) {
            try {
                getActivity().unregisterReceiver(mUsbReceiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mUsbReceiver = null;
    }

    protected class USBReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            switch (intent.getAction()) {
                case UsbManager.ACTION_USB_DEVICE_ATTACHED: // Plug in the USB device
                    break;
                case UsbManager.ACTION_USB_DEVICE_DETACHED: // Unplug the USB device
                    close();
                    break;
                default:
                    break;
            }
        }
    }

}
