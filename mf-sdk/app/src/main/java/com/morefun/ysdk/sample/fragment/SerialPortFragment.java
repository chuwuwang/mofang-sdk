package com.morefun.ysdk.sample.fragment;

import android.os.Bundle;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.morefun.yapi.ServiceResult;
import com.morefun.yapi.device.serialport.SerialPortDriver;
import com.morefun.ysdk.sample.R;
import com.morefun.ysdk.sample.device.DeviceHelper;
import com.morefun.ysdk.sample.utils.BytesUtil;
import com.morefun.ysdk.sample.utils.ToastUtils;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SerialPortFragment extends Fragment {
    private final String TAG = SerialPortFragment.class.getName();

    @BindView(R.id.et_data)
    EditText etData;

    @BindView(R.id.et_recv)
    EditText etReceive;

    private SerialPortDriver serialPortDriver;
    private boolean isOpen;
    private boolean bStop = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_serial_port, null);
        ButterKnife.bind(this, view);
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
    }


    private void open() {
        try {
            int port = 4;

            serialPortDriver = DeviceHelper.getSerialPortDriver(port);
            int connect = serialPortDriver.connect("115200,N,8,1");

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
            serialPortDriver.disconnect();
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
            int send = serialPortDriver.send(data, data.length);
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

                        int read = serialPortDriver.recv(recvBytes, recvBytes.length, 1_000);
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

}
