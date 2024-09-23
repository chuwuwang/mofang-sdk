package com.morefun.ysdk.sample.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.morefun.ysdk.sample.R;
import com.morefun.ysdk.sample.device.DeviceHelper;
import com.morefun.ysdk.sample.utils.BytesUtil;
import com.morefun.ysdk.sample.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NTagCardFragment extends Fragment {
    @BindView(R.id.tv_tip)
    TextView tvTip;

    private final String TAG = NTagCardFragment.class.getName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ntag_card, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick({R.id.btn_open, R.id.btn_close, R.id.btn_read})
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
        }
    }

    private void open() {
        try {
            if (DeviceHelper.getNTagCard(null).open()) {
                ToastUtils.show(getContext(), "Open Success");
            } else {
                ToastUtils.show(getContext(), "Open Fail");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void close() {
        try {
            if (DeviceHelper.getNTagCard(null).close()) {
                ToastUtils.show(getContext(), "Close Success");
            } else {
                ToastUtils.show(getContext(), "Close Fail");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void read() {
        try {
            String rst = "03000000000000000004040201001303";
            byte[] bytes = new byte[rst.length() / 2];

            DeviceHelper.getNTagCard(null).getVersion(BytesUtil.hexString2Bytes("03000000"), bytes);

            showResult("Get Version:" + BytesUtil.bytes2HexString(bytes));
            showResult("Read Page");
            for (int i = 0; i < 30; i++) {
                byte[] rstIdx = new byte[4];
                byte[] rstPage = new byte[24];

                BytesUtil.int2bytes(i, rstIdx, 0);

                String rstIdxStr = BytesUtil.bytes2HexString(rstIdx);
                String s = "04000000" + rstIdxStr;
                DeviceHelper.getNTagCard(null).read(BytesUtil.hexString2Bytes(s), rstPage);
                showResult(String.format("RST Page: %s", BytesUtil.bytes2HexString(rstPage)));
            }
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
