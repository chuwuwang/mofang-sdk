package com.morefun.ysdk.sample.fragment;

import android.os.Bundle;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.fragment.app.Fragment;

import com.morefun.yapi.card.cpu.CPUCardHandler;
import com.morefun.yapi.device.reader.icc.IccCardReader;
import com.morefun.yapi.device.reader.icc.IccReaderSlot;
import com.morefun.ysdk.sample.R;
import com.morefun.ysdk.sample.device.DeviceHelper;
import com.morefun.ysdk.sample.utils.DialogUtils;
import com.morefun.ysdk.sample.utils.HexUtil;
import com.morefun.ysdk.sample.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PSAMCardFragment extends Fragment {
    private final String TAG = PSAMCardFragment.class.getName();

    @BindView(R.id.rb_psamSlot1)
    RadioButton rb_psamSlot1;

    @BindView(R.id.rb_psamSlot2)
    RadioButton rb_psamSlot2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_psam_card, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick({R.id.btn_psamCard})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_psamCard:
                psamCard();
                break;
        }
    }

    private void psamCard() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int slot = IccReaderSlot.PSAMSlOT1;
                    if (rb_psamSlot1.isChecked()) {
                        slot = IccReaderSlot.PSAMSlOT1;
                    } else if (rb_psamSlot2.isChecked()) {
                        slot = IccReaderSlot.PSAMSlOT2;
                    }
                    exchangeCmd(slot);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private void exchangeCmd(int slot) throws RemoteException {
        IccCardReader cardReader = DeviceHelper.getIccCardReader(slot);
        CPUCardHandler cpuCardHandler = DeviceHelper.getCpuCardHandler(cardReader);
        try {
            String cmd = "00A4040008A00000033301010100";

            if (cpuCardHandler == null) {
                ToastUtils.show(getContext(), "Cpu Card Handler Is Null");
                return;
            }
            DialogUtils.showProgressDialog(getActivity(), "Reading Card...");

            cpuCardHandler.setPowerOff();

            byte[] atr = new byte[64];
            int artRstLength = cpuCardHandler.setPowerOn(atr);
            if (0 == artRstLength) {
                DialogUtils.dismissProgressDialog(getActivity());
                ToastUtils.show(getContext(), "Power On Fail!");
                return;
            }

            byte[] cmdBytes = HexUtil.hexStringToByte(cmd);
            byte[] tmp = new byte[256];
            int ret = cpuCardHandler.exchangeCmd(tmp, cmdBytes, cmdBytes.length);

            DialogUtils.dismissProgressDialog(getActivity());
            if (ret > 0) {
                DialogUtils.showAlertDialog(getActivity(), HexUtil.bytesToHexString(HexUtil.subByte(tmp, 0, ret)));
            } else {
                ToastUtils.show(getContext(), "Exchange Fail:" + ret);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cpuCardHandler.setPowerOff();
        }
    }
}
