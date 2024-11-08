package com.morefun.ysdk.sample.fragment;

import android.os.Bundle;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.morefun.yapi.ServiceResult;
import com.morefun.ysdk.sample.R;
import com.morefun.ysdk.sample.device.DeviceHelper;
import com.morefun.ysdk.sample.utils.TlvData;
import com.morefun.ysdk.sample.utils.TlvDataList;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RuPayFragment extends Fragment {
    @BindView(R.id.tv_tip)
    TextView tvTip;

    private final String TAG = RuPayFragment.class.getName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rupay, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick({R.id.btn_downloadRuPayService, R.id.btn_getRuPayService, R.id.btn_clearRuPayService,
            R.id.btn_downloadPRMacqKey, R.id.btn_getRuPayPRMacqKey, R.id.btn_clearRuPayPRMacqKey})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_downloadRuPayService:
                downloadServiceData();
                break;
            case R.id.btn_getRuPayService:
                getServiceDataList();
                break;
            case R.id.btn_clearRuPayService:
                clearServiceData();
                break;
            case R.id.btn_downloadPRMacqKey:
                downloadPRMacqKey();
                break;
            case R.id.btn_getRuPayPRMacqKey:
                getPRMacqKeyList();
                break;
            case R.id.btn_clearRuPayPRMacqKey:
                clearPRMacqKey();
                break;
        }
    }

    private void downloadServiceData() {
        String[] serviceData = new String[]{"DF16021010DF24050810109500df453b09101506150101112233445566778800000100010061150406125703000000010000020000000A0101000A01020502000102010206000A01010302"};
        try {
            int ret = 0;
            for (int i = 0; i < serviceData.length; i++) {
                String tip = "Download Service Data" + String.format("(%d)", i);

                showResult(tip);
                String aid = serviceData[i];
                ret = DeviceHelper.getEmvRupayService().addServiceParam(aid);

                if (ret != ServiceResult.Success) {
                    break;
                }
                SystemClock.sleep(500);
            }
            showResult("Download Service Data " + (ret == ServiceResult.Success ? "success" : "fail"));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void clearServiceData() {
        try {
            DeviceHelper.getEmvRupayService().clearServiceParam();
            showResult("Clear Service Data Success!");
        } catch (RemoteException e) {

        }
    }

    private void getServiceDataList() {
        try {
            List<String> serviceList = DeviceHelper.getEmvRupayService().getRuPayServiceParaList();
            StringBuilder builder = new StringBuilder();

            for (int i = 0; i < serviceList.size(); i++) {
                String serviceParam = serviceList.get(i);
                TlvDataList tlvDataList = TlvDataList.fromBinary(serviceParam);
                TlvData df = tlvDataList.getTLV("DF16");
                builder.append("\n Service Id DF16:" + (df != null ? df.getValue() : ""));
                df = tlvDataList.getTLV("DF24");
                builder.append("\n TSrQ DF24:" + (df != null ? df.getValue() : ""));
                df = tlvDataList.getTLV("DF24");
                builder.append("\n TSrQ DF24:" + (df != null ? df.getValue() : ""));
                df = tlvDataList.getTLV("DF45");
                builder.append("\n SrData DF45:" + (df != null ? df.getValue() : ""));
            }
            showResult(builder.toString());

        } catch (RemoteException e) {

        } catch (NullPointerException e) {
            showResult(e.getMessage());
        }
    }

    private void downloadPRMacqKey() {
        String[] PRMacqKey = new String[]{
                "DF16021010DF5403000000DF48083E77D2912D5D1BBF"
        };
        int ret = -1;
        try {
            for (int i = 0; i < PRMacqKey.length; i++) {
                String tip = "Download PRMacqKey" + String.format("(%d)", i);

                showResult(tip);
                String aid = PRMacqKey[i];
                ret = DeviceHelper.getEmvRupayService().addPRMacqKeyParam(aid);

                if (ret != ServiceResult.Success) {
                    break;
                }
                SystemClock.sleep(500);
            }
            showResult("Download PRMacqKey " + (ret == ServiceResult.Success ? "success" : "fail"));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void clearPRMacqKey() {
        try {
            DeviceHelper.getEmvRupayService().clearPRMacqKeyParam();
            showResult("Clear PRMacqKey Success!");
        } catch (RemoteException e) {

        }

    }

    private void getPRMacqKeyList() {
        try {
            List<String> macqKeyList = DeviceHelper.getEmvRupayService().getRuPayPRMacqKeyParaList();
            StringBuilder builder = new StringBuilder();

            for (int i = 0; i < macqKeyList.size(); i++) {
                String serviceParam = macqKeyList.get(i);
                TlvDataList tlvDataList = TlvDataList.fromBinary(serviceParam);
                TlvData df = tlvDataList.getTLV("DF16");
                builder.append("\n Max Fill Volume DF16:" + (df != null ? df.getValue() : ""));
                df = tlvDataList.getTLV("DF54");
                builder.append("\n Parent Mac DF54:" + (df != null ? df.getValue() : ""));
                df = tlvDataList.getTLV("DF4E");
                builder.append("\n  DF4E:" + (df != null ? df.getValue() : ""));
                df = tlvDataList.getTLV("DF48");
                builder.append("\n UDKac KCV DF48:" + (df != null ? df.getValue() : ""));
            }
            showResult(builder.toString());

        } catch (RemoteException e) {

        } catch (NullPointerException e) {
            showResult(e.getMessage());
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
