package com.morefun.ysdk.sample.fragment;

import static com.morefun.ysdk.sample.utils.BytesUtil.bytes2HexString;
import static com.morefun.ysdk.sample.utils.BytesUtil.hexString2Bytes;
import static com.morefun.ysdk.sample.utils.BytesUtil.int2bytes;

import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.morefun.yapi.card.at24cxx.IAT24CxxCard;
import com.morefun.yapi.card.industry.SL4442Card;
import com.morefun.ysdk.sample.R;
import com.morefun.ysdk.sample.device.DeviceHelper;
import com.morefun.ysdk.sample.utils.BytesUtil;
import com.morefun.ysdk.sample.utils.DialogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AT24CxxCardFragment extends Fragment {

    @BindView(R.id.et_address)
    EditText et_address;

    @BindView(R.id.et_data)
    EditText et_data;

    @BindView(R.id.et_pin)
    EditText et_pin;

    @BindView(R.id.et_type)
    EditText et_type;

    @BindView(R.id.tv_tip)
    TextView tvTip;

    @BindView(R.id.et_readLen)
    EditText et_readLen;

    private final String TAG = AT24CxxCardFragment.class.getName();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_at24cxx_card, null);
        ButterKnife.bind(this, view);

        return view;
    }

    @OnClick({R.id.btn_open, R.id.btn_close, R.id.btn_read, R.id.btn_write,
            R.id.btn_setType, R.id.btn_getType, R.id.btn_setPin, R.id.btn_getPin})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_open:
                open();
                break;
            case R.id.btn_close:
                close();
                break;
            case R.id.btn_read:
                read();
                break;
            case R.id.btn_write:
                write();
                break;
            case R.id.btn_setType:
                setType();
                break;
            case R.id.btn_getType:
                getType();
                break;
            case R.id.btn_setPin:
                setPin();
                break;
            case R.id.btn_getPin:
                getPin();
                break;
        }
    }

    private IAT24CxxCard getAT24CxxCard() {
        try {
            return DeviceHelper.getAT24CxxCard();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void open() {
        try {
            int ret = getAT24CxxCard().open();
            DialogUtils.showAlertDialog(getActivity(), ret == 0 ? "Open success" : "Open fail");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void close() {
        try {
            int ret = getAT24CxxCard().close();
            DialogUtils.showAlertDialog(getActivity(), ret == 0 ? "Close success" : "Close fail");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void read() {
        try {
            int addr = Integer.parseInt(et_address.getText().toString());
            int len = Integer.parseInt(et_readLen.getText().toString());
            byte[] result = new byte[256];
            int ret = getAT24CxxCard().read(addr, len, result);
            showResult(BytesUtil.bytes2HexString(result));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void write() {
        try {
            int addr = Integer.parseInt(et_address.getText().toString());
            byte[] data = et_data.getText().toString().getBytes();
            int len = data.length;

            int ret = getAT24CxxCard().write(addr, data, len);
            DialogUtils.showAlertDialog(getActivity(), ret == 0 ? "Write success" : "Write fail");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setType() {
        try {
            int type = Integer.parseInt(et_type.getText().toString());
            int ret = getAT24CxxCard().setType(type);
            DialogUtils.showAlertDialog(getActivity(), ret == 0 ? "Set success" : "Set fail");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getType() {
        try {
            int ret = getAT24CxxCard().getType();
            DialogUtils.showAlertDialog(getActivity(), "type: " + ret);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setPin() {
        try {
            int type = Integer.parseInt(et_pin.getText().toString());
            int ret = getAT24CxxCard().setPin(type);
            DialogUtils.showAlertDialog(getActivity(), ret == 0 ? "Set success" : "Set fail");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getPin() {
        try {
            int ret = getAT24CxxCard().getPin();
            DialogUtils.showAlertDialog(getActivity(), "Pin: " + ret);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showResult(final String text) {
        Log.d(TAG, text);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvTip.append("### " + text + "\r\n");
            }
        });
    }

}
