package com.morefun.ysdk.sample.fragment;

import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.morefun.yapi.device.tpm.TpmManager;
import com.morefun.ysdk.sample.MyApplication;
import com.morefun.ysdk.sample.R;
import com.morefun.ysdk.sample.device.DeviceHelper;
import com.morefun.ysdk.sample.utils.BytesUtil;
import com.morefun.ysdk.sample.utils.ToastUtils;
import com.morefun.ysdk.sample.view.ClearEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TpmFragment extends Fragment {
    private final String TAG = TpmFragment.class.getName();
    private TpmManager tpmManager;

    @BindView(R.id.et_data)
    ClearEditText editText;
    @BindView(R.id.tv_tip)
    TextView recv;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tpm, null);
        ButterKnife.bind(this, view);
        try {
            tpmManager = DeviceHelper.getTpmManager();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        return view;
    }


    @OnClick({R.id.btn_send})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send:
                String cmd = editText.getText().toString();
                if (!cmd.isEmpty()) {
                    tpmExchange(BytesUtil.hexString2Bytes(cmd));
                } else {
                    ToastUtils.show(MyApplication.getInstance(), "Empty cmd!");
                }
                break;
        }
    }

    private void tpmExchange(byte[] cmd) {
        byte[] resp = new byte[0];
        try {
            resp = tpmManager.tpmExchange(cmd);
            if (resp != null) {
                Log.d(TAG, "tpmExchange: " +  BytesUtil.bytes2HexString(resp));
                recv.setText(BytesUtil.bytes2HexString(resp));
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
