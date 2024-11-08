package com.morefun.ysdk.sample.fragment;

import android.os.Bundle;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.morefun.yapi.ServiceResult;
import com.morefun.yapi.device.pinpad.DukptCalcObj;
import com.morefun.yapi.device.reader.mag.MagCardInfoEntity;
import com.morefun.yapi.device.reader.mag.MagCardReader;
import com.morefun.yapi.device.reader.mag.OnSearchMagCardListener;
import com.morefun.ysdk.sample.R;
import com.morefun.ysdk.sample.device.DeviceHelper;
import com.morefun.ysdk.sample.utils.DialogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MagCardFragment extends Fragment {
    @BindView(R.id.tv_tip)
    TextView tvTip;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mag_card, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick({R.id.btn_readMagCard})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_readMagCard:
                readMagCard();
                break;
        }
    }

    private void readMagCard() {
        try {
            DialogUtils.showProgressDialog(getActivity(), getString(R.string.tip_please_swipe_card));

            final MagCardReader magCardReader = DeviceHelper.getMagCardReader();
            magCardReader.setIsCheckLrc(false);

            Bundle bundle = new Bundle();
            magCardReader.searchCard(new OnSearchMagCardListener.Stub() {
                @Override
                public void onSearchResult(int ret, MagCardInfoEntity magCardInfoEntity) throws RemoteException {
                    DialogUtils.dismissProgressDialog(getActivity());

                    if (ret == ServiceResult.Success) {
                        StringBuilder builder = new StringBuilder();

                        builder.append("PAN: " + magCardInfoEntity.getCardNo());
                        builder.append("\nTrack 1: " + magCardInfoEntity.getTk1());
                        builder.append("\nTrack 2: " + magCardInfoEntity.getTk2());
                        builder.append("\nTrack 3: " + magCardInfoEntity.getTk3());
                        builder.append("\nService Code: " + magCardInfoEntity.getServiceCode());

                        showResult(builder.toString());
                    } else {
                        showResult("retCode:" + ret);
                    }
                }
            }, 10, bundle);

        } catch (Exception e) {
            e.printStackTrace();
            showResult(e.getMessage());
        }
    }

    private void showResult(String msg) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvTip.setText(msg);
            }
        });
    }


}
