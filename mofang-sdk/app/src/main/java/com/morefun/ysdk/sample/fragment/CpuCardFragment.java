package com.morefun.ysdk.sample.fragment;

import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.morefun.yapi.ServiceResult;
import com.morefun.yapi.card.cpu.APDUCmd;
import com.morefun.yapi.card.cpu.CPUCardHandler;
import com.morefun.yapi.device.reader.icc.ICCSearchResult;
import com.morefun.yapi.device.reader.icc.IccCardReader;
import com.morefun.yapi.device.reader.icc.IccCardType;
import com.morefun.yapi.device.reader.icc.IccReaderSlot;
import com.morefun.yapi.device.reader.icc.OnSearchIccCardListener;
import com.morefun.ysdk.sample.R;
import com.morefun.ysdk.sample.device.DeviceHelper;
import com.morefun.ysdk.sample.utils.BytesUtil;
import com.morefun.ysdk.sample.utils.DialogUtils;
import com.morefun.ysdk.sample.utils.HexUtil;
import com.morefun.ysdk.sample.utils.ToastUtils;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CpuCardFragment extends Fragment {
    @BindView(R.id.tv_tip)
    TextView tvTip;

    @BindView(R.id.et_timeout)
    EditText etTimeout;

    @BindView(R.id.et_data)
    EditText etData;

    private final String TAG = CpuCardFragment.class.getName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cpu_card, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick({R.id.btn_cpuCard, R.id.btn_typeACardOpen, R.id.btn_typeACardClose, R.id.btn_typeAExchange})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_cpuCard:
                searchCpuCard(new String[]{IccCardType.CPUCARD});
                break;
            case R.id.btn_typeACardOpen:
                typeACardOpen();
                break;
            case R.id.btn_typeACardClose:
                typeACardClose();
                break;
            case R.id.btn_typeAExchange:
                typeAExchange();
                break;
        }
    }

    private void typeACardOpen() {
        if (TextUtils.isEmpty(etData.getText().toString())) {
            ToastUtils.show(getActivity(), "Please Input Timeout");
            return;
        }

        DialogUtils.showProgressDialog(getActivity(), getString(R.string.tip_dip_tap_card));
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int timeout = Integer.parseInt(etTimeout.getText().toString());
                    int ret = DeviceHelper.getCpuTypeAHandler().open(timeout);
                    DialogUtils.dismissProgressDialog(getActivity());
                    if (ret != 0) {
                        DialogUtils.showAlertDialog(getActivity(), "Open Fail");
                        return;
                    }
                    DialogUtils.showAlertDialog(getActivity(), "Open Success");
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                    DialogUtils.dismissProgressDialog(getActivity());
                    ToastUtils.show(getContext(), e.getMessage());
                }
            }
        }).start();
    }

    private void typeACardClose() {
        try {
            DeviceHelper.getCpuTypeAHandler().close();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void typeAExchange() {
        if (TextUtils.isEmpty(etData.getText().toString())) {
            ToastUtils.show(getActivity(), "Data Is Empty");
            return;
        }

        DialogUtils.showProgressDialog(getActivity(), "TypeA Card Exchange...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    byte[] result = new byte[128];
                    byte[] cmd = BytesUtil.hexString2Bytes(etData.getText().toString());

                    int ret = DeviceHelper.getCpuTypeAHandler().exchangeCmd(result, cmd, cmd.length);

                    DialogUtils.dismissProgressDialog(getActivity());
                    if (ret > 0) {
                        DialogUtils.showAlertDialog(getActivity(), "Exchange Success");
                        return;
                    }
                    DialogUtils.showAlertDialog(getActivity(), "Exchange Fail");
                } catch (Exception e) {
                    e.printStackTrace();
                    DialogUtils.dismissProgressDialog(getActivity());
                    DialogUtils.showAlertDialog(getActivity(), e.getMessage());
                }
            }
        }).start();
    }

    private void searchCpuCard(final String[] cardType) {
        DialogUtils.showProgressDialog(getActivity(), getString(R.string.tip_dip_tap_card));
        try {
            final IccCardReader icReader = DeviceHelper.getIccCardReader(IccReaderSlot.ICSlOT1);
            final IccCardReader rfReader = DeviceHelper.getIccCardReader(IccReaderSlot.RFSlOT);

            OnSearchIccCardListener.Stub listener = new OnSearchIccCardListener.Stub() {
                @Override
                public void onSearchResult(int retCode, Bundle bundle) throws RemoteException {

                    icReader.stopSearch();
                    rfReader.stopSearch();

                    if (ServiceResult.Success == retCode) {
                        String cardType = bundle.getString(ICCSearchResult.CARDTYPE);
                        if (IccCardType.CPUCARD.equals(cardType)) {
                            int slot = bundle.getInt(ICCSearchResult.CARDOTHER);
                            exchangeAPDU(slot);
                        }
                    } else {
                        DialogUtils.dismissProgressDialog(getActivity());
                        DialogUtils.showAlertDialog(getActivity(), "Search Card Fail!");
                    }
                }
            };

            icReader.searchCard(listener, 10, cardType);
            rfReader.searchCard(listener, 10, cardType);
        } catch (RemoteException e) {
            e.printStackTrace();
            showResult(e.getMessage());
        }
    }

    private void exchangeAPDU(int slot) throws RemoteException {
        DialogUtils.setProgressMessage(getActivity(), "Exchange...");
        IccCardReader cardReader = DeviceHelper.getIccCardReader(slot);
        CPUCardHandler cpuCardHandler = DeviceHelper.getCpuCardHandler(cardReader);

        try {
            String cmd = "00A40400";
            String data = "325041592E5359532E4444463031";
            byte le = 0x00;

            if (cpuCardHandler == null) {
                DialogUtils.dismissProgressDialog(getActivity());
                DialogUtils.showAlertDialog(getActivity(), "Cpu Card Handler Is Null!");
                return;
            }
            byte[] atr = new byte[16];
            if (0 == cpuCardHandler.setPowerOn(atr)) {
                DialogUtils.dismissProgressDialog(getActivity());
                DialogUtils.showAlertDialog(getActivity(), "Power On Fail!");
                return;
            }

            byte[] cmdBytes = HexUtil.hexStringToByte(cmd);
            byte[] dataArray = HexUtil.hexStringToByte(data);
            byte[] tmp = new byte[256];

            System.arraycopy(dataArray, 0, tmp, 0, dataArray.length);

            APDUCmd apduCmd = new APDUCmd();
            apduCmd.setCla(cmdBytes[0]);
            apduCmd.setIns(cmdBytes[1]);
            apduCmd.setP1(cmdBytes[2]);
            apduCmd.setP2(cmdBytes[3]);
            apduCmd.setLc(dataArray.length);
            apduCmd.setDataIn(tmp);
            apduCmd.setLe(le);

            int ret = cpuCardHandler.exchangeAPDUCmd(apduCmd);
            cpuCardHandler.setPowerOff();

            DialogUtils.dismissProgressDialog(getActivity());
            if (ret == ServiceResult.Success) {
                StringBuilder builder = new StringBuilder();
                if (!Arrays.equals(atr, new byte[16])) {
                    builder.append("ATR: " + HexUtil.bytesToHexString(atr)).append("\n");
                }
                builder.append("DATA: " + HexUtil.bytesToHexString(BytesUtil.subBytes(apduCmd.getDataOut(), 0, apduCmd.getDataOutLen())));

                DialogUtils.showAlertDialog(getActivity(), builder.toString());
            } else {
                DialogUtils.showAlertDialog(getActivity(), "Exchange Fail!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            DialogUtils.dismissProgressDialog(getActivity());
            DialogUtils.showAlertDialog(getActivity(), "Exchange Fail!");
        } finally {
            cpuCardHandler.setPowerOff();
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
