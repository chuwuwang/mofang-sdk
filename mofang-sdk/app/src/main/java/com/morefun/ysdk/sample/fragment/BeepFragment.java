package com.morefun.ysdk.sample.fragment;

import android.os.Bundle;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.fragment.app.Fragment;

import com.morefun.ysdk.sample.R;
import com.morefun.ysdk.sample.device.DeviceHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BeepFragment extends Fragment {
    @BindView(R.id.normal)
    RadioButton normal;

    @BindView(R.id.success)
    RadioButton success;

    @BindView(R.id.fail)
    RadioButton fail;

    @BindView(R.id.interval)
    RadioButton interval;

    @BindView(R.id.error)
    RadioButton error;

    private final String TAG = BeepFragment.class.getName();
    public static final int NORMAL = 0;
    public static final int SUCCESS = 1;
    public static final int FAIL = 2;
    public static final int INTERVAL = 3;
    public static final int ERROR = 4;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_beep, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick({R.id.btn_beep})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_beep:
                beep();
                break;
        }
    }

    private void beep() {
        int beepType;
        try {
            if (normal.isChecked()) {
                beepType = NORMAL;
            } else if (success.isChecked()) {
                beepType = SUCCESS;
            } else if (fail.isChecked()) {
                beepType = FAIL;
            } else if (interval.isChecked()) {
                beepType = INTERVAL;
            } else {
                beepType = ERROR;
            }
            DeviceHelper.getBeeper().beep(beepType);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
