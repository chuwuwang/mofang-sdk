package com.morefun.ysdk.sample.fragment;

import android.os.Bundle;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.morefun.ysdk.sample.R;
import com.morefun.ysdk.sample.device.DeviceHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LedFragment extends Fragment {
    private final String TAG = LedFragment.class.getName();

    @BindView(R.id.led1)
    CheckBox led1;

    @BindView(R.id.led2)
    CheckBox led2;

    @BindView(R.id.led3)
    CheckBox led3;

    @BindView(R.id.led4)
    CheckBox led4;

    @BindView(R.id.iv_blue)
    ImageView iv_blue;

    @BindView(R.id.iv_green)
    ImageView iv_green;

    @BindView(R.id.iv_yellow)
    ImageView iv_yellow;

    @BindView(R.id.iv_red)
    ImageView iv_red;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_led, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick({R.id.btn_hardware_led, R.id.btn_soft_led})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_hardware_led:
                hardwareLed();
                break;
            case R.id.btn_soft_led:
                softLed();
                break;
        }
    }

    private void hardwareLed() {
        try {
            DeviceHelper.getLedDriver().PowerLed(led1.isChecked(), led2.isChecked(), led3.isChecked(), led4.isChecked());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void softLed() {
        setBlueLed(led1.isChecked());
        setYellowLed(led2.isChecked());
        setGreenLed(led3.isChecked());
        setRedLed(led4.isChecked());
    }

    private void setBlueLed(boolean isOn) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                iv_blue.setBackgroundResource(isOn ? R.drawable.blue_led_on : R.drawable.blue_led_off);
            }
        });
    }

    private void setYellowLed(boolean isOn) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                iv_yellow.setBackgroundResource(isOn ? R.drawable.yellow_led_on : R.drawable.yellow_led_off);
            }
        });
    }

    private void setGreenLed(boolean isOn) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                iv_green.setBackgroundResource(isOn ? R.drawable.green_led_on : R.drawable.green_led_off);
            }
        });
    }

    private void setRedLed(boolean isOn) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                iv_red.setBackgroundResource(isOn ? R.drawable.red_led_on : R.drawable.red_led_off);
            }
        });
    }

}
