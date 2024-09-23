package com.morefun.ysdk.sample.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.morefun.yapi.card.industry.IndustryCardHandler;
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

public class FelicaFragment extends Fragment {
    @BindView(R.id.tv_tip)
    TextView tvTip;

    @BindView(R.id.et_thresholdA)
    EditText et_thresholdA;

    @BindView(R.id.et_configA)
    EditText et_configA;

    @BindView(R.id.et_thresholdB)
    EditText et_thresholdB;

    @BindView(R.id.et_configB)
    EditText et_configB;

    @BindView(R.id.et_gsp)
    EditText et_gsp;

    @BindView(R.id.et_powerType)
    EditText et_powerType;

    @BindView(R.id.et_testTimes)
    EditText et_testTimes;

    private final String TAG = FelicaFragment.class.getName();

    private boolean bStop = true;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_felica, null);
        ButterKnife.bind(this, view);

        return view;
    }

    @OnClick({R.id.btn_powerOn, R.id.btn_powerOff, R.id.btn_setRFLevel,
            R.id.btn_setPower, R.id.btn_start, R.id.btn_test1, R.id.btn_test2})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_powerOn:
                powerOn();
                break;
            case R.id.btn_powerOff:
                powerOff();
                break;
            case R.id.btn_setRFLevel:
                setRFLevel();
                break;
            case R.id.btn_setPower:
                setPower();
                break;
            case R.id.btn_start:
                start();
                break;
            case R.id.btn_test1:
                test1();
                break;
            case R.id.btn_test2:
                test2();
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private IndustryCardHandler getFeilcaHandler() {
        try {
            IccCardReader cardReader = DeviceHelper.getIccCardReader(IccReaderSlot.RFSlOT);
            return DeviceHelper.getIndustryCardHandler(cardReader);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void powerOn() {
        try {
            boolean b = getFeilcaHandler().setPowerOn(new byte[]{0x00, 0x00});
            if (b) {
                DialogUtils.showAlertDialog(getActivity(), "Power On Success");
            } else {
                DialogUtils.showAlertDialog(getActivity(), "Power On Fail");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void powerOff() {
        try {
            getFeilcaHandler().setPowerOff();
            DialogUtils.showAlertDialog(getActivity(), "Power Off Success");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setPower() {
        try {
            int type = Integer.parseInt(et_powerType.getText().toString());
            getFeilcaHandler().setPower(type);
            DialogUtils.showAlertDialog(getActivity(), "Set Power Success");
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.show(getContext(), e.getMessage());
        }
    }

    private void setRFLevel() {
        try {
            int thresholdA = Integer.parseInt(et_thresholdA.getText().toString());
            int cfgA = Integer.parseInt(et_configA.getText().toString());
            int thresholdB = Integer.parseInt(et_thresholdB.getText().toString());
            int cfgB = Integer.parseInt(et_configB.getText().toString());
            int gsp = Integer.parseInt(et_gsp.getText().toString());

            getFeilcaHandler().setRFLevel(thresholdA, cfgA, thresholdB, cfgB, gsp);
            DialogUtils.showAlertDialog(getActivity(), "Set RF Level Success");
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.show(getContext(), e.getMessage());
        }
    }

    private void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (getFeilcaHandler() == null) {
                        return;
                    }
                    DialogUtils.showProgressDialog(getActivity(), "POLL...");

                    byte[] result = new byte[256];
                    byte[] ucPollCmd = new byte[]{0x6, 0x00, (byte) 0xFF, (byte) 0xFF, 0x0, 0x00};
                    byte[] ucReadCmd = new byte[]{0x16, 0x06, 0x01, 0x2e, 0x45, 0x76, (byte) 0xba, (byte) 0xc5, 0x41, 0x2c, 0x01, 0x0b, 0x00, 0x04, (byte) 0x80, 0x01, (byte) 0x80, 0x02, (byte) 0x80, 0x05, (byte) 0x80, (byte) 0x82};

                    Log.i(TAG, "Felica POLL Start");
                    int ret = getFeilcaHandler().exchangeCmd(result, ucPollCmd, ucPollCmd.length);
                    Log.i(TAG, "Felica POLL End");

                    if (ret > 0 && result != null) {
                        int pos = 2;
                        System.arraycopy(result, 2, ucReadCmd, 2, 8);
                        pos += 8;
                        System.arraycopy(ucReadCmd, pos, ucReadCmd, pos, ucReadCmd.length - pos);
                        Log.d(TAG, "cmd:" + HexUtil.bytesToHexString(ucReadCmd));

                        Log.i(TAG, "Cmd Start");
                        DialogUtils.setProgressMessage(getActivity(), "Exchange...");
                        ret = getFeilcaHandler().exchangeCmd(result, ucReadCmd, ucReadCmd.length);
                        Log.i(TAG, "Read Result:" + HexUtil.bytesToHexString(HexUtil.subByte(result, 0, ret)));

                        DialogUtils.dismissProgressDialog(getActivity());
                        if (ret > 0) {
                            byte[] buffer = new byte[ret];
                            System.arraycopy(result, 0, buffer, 0, ret);
                            showResult("Read Result:" + HexUtil.bytesToHexString(buffer));
                            DialogUtils.showAlertDialog(getActivity(), HexUtil.bytesToHexString(buffer));
                        }
                    } else {
                        DialogUtils.dismissProgressDialog(getActivity());
                        DialogUtils.showAlertDialog(getActivity(), "POLL FAIL!");
                    }
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                    showResult(e.toString());
                }
            }
        }).start();
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void ignoreBatteryOptimization() {
        Intent intent = new Intent();
        String packageName = getContext().getPackageName();
        PowerManager pm = (PowerManager) getContext().getSystemService(Context.POWER_SERVICE);
        if (pm != null && !pm.isIgnoringBatteryOptimizations(packageName)) {
            intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + packageName));
            startActivity(intent);
        }
    }

    private void test1() {
        bStop = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int count = Integer.parseInt(et_testTimes.getText().toString());
                    if (getFeilcaHandler() == null) {
                        return;
                    }

                    DialogUtils.showProgressDialog(getActivity(), String.format("Test [0]..."));
                    for (int i = 0; i < count; i++) {
                        if (bStop) {
                            return;
                        }
                        DialogUtils.setProgressMessage(getActivity(), String.format("Test [%d]...", i));
                        boolean b = getFeilcaHandler().setPowerOn(new byte[]{0x00, 0x00});
                        if (b) {
                            Thread.sleep(300);
                            byte[] result = new byte[256];
                            byte[] ucPollCmd = new byte[]{0x6, 0x00, (byte) 0xFF, (byte) 0xFF, 0x0, 0x00};
                            byte[] ucReadCmd = new byte[]{0x16, 0x06, 0x01, 0x2e, 0x45, 0x76, (byte) 0xba, (byte) 0xc5, 0x41, 0x2c, 0x01, 0x0b, 0x00, 0x04, (byte) 0x80, 0x01, (byte) 0x80, 0x02, (byte) 0x80, 0x05, (byte) 0x80, (byte) 0x82};

                            int ret = getFeilcaHandler().exchangeCmd(result, ucPollCmd, ucPollCmd.length);

                            if (ret > 0 && result != null) {
                                int pos = 2;
                                System.arraycopy(result, 2, ucReadCmd, 2, 8);
                                pos += 8;
                                System.arraycopy(ucReadCmd, pos, ucReadCmd, pos, ucReadCmd.length - pos);
                                Log.d(TAG, "cmd:" + HexUtil.bytesToHexString(ucReadCmd));

                                Log.i(TAG, "Cmd Start");
                                ret = getFeilcaHandler().exchangeCmd(result, ucReadCmd, ucReadCmd.length);
                                Log.i(TAG, "Read Result:" + HexUtil.bytesToHexString(HexUtil.subByte(result, 0, ret)));

                                if (ret > 0) {
                                    byte[] buffer = new byte[ret];
                                    System.arraycopy(result, 0, buffer, 0, ret);
                                    //showResult(String.format("[%d] Read Result: %s", i, HexUtil.bytesToHexString(buffer)));
                                }
                            } else {
                                DialogUtils.dismissProgressDialog(getActivity());
                                DialogUtils.showAlertDialog(getActivity(), "POLL FAIL!");
                                return;
                            }
                        } else {
                            DialogUtils.dismissProgressDialog(getActivity());
                            DialogUtils.showAlertDialog(getActivity(), "Power on fail!");
                            return;
                        }
                        getFeilcaHandler().setPowerOff();
                        Thread.sleep(300);
                    }
                    DialogUtils.dismissProgressDialog(getActivity());
                    DialogUtils.showAlertDialog(getActivity(), "Test Finish");

                } catch (Exception e) {
                    e.printStackTrace();
                    showResult(e.toString());
                }
            }
        }).start();
    }

    private void test2() {
        bStop = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int count = Integer.parseInt(et_testTimes.getText().toString());
                    if (getFeilcaHandler() == null) {
                        return;
                    }

                    boolean b = getFeilcaHandler().setPowerOn(new byte[]{0x00, 0x00});
                    Thread.sleep(300);
                    DialogUtils.showProgressDialog(getActivity(), String.format("Test [0]..."));
                    for (int i = 0; i < count; i++) {
                        if (bStop) {
                            return;
                        }

                        DialogUtils.setProgressMessage(getActivity(), String.format("Test [%d]...", i));
                        if (b) {

                            byte[] result = new byte[256];
                            byte[] ucPollCmd = new byte[]{0x6, 0x00, (byte) 0xFF, (byte) 0xFF, 0x0, 0x00};
                            byte[] ucReadCmd = new byte[]{0x16, 0x06, 0x01, 0x2e, 0x45, 0x76, (byte) 0xba, (byte) 0xc5, 0x41, 0x2c, 0x01, 0x0b, 0x00, 0x04, (byte) 0x80, 0x01, (byte) 0x80, 0x02, (byte) 0x80, 0x05, (byte) 0x80, (byte) 0x82};

                            int ret = getFeilcaHandler().exchangeCmd(result, ucPollCmd, ucPollCmd.length);

                            if (ret > 0 && result != null) {
                                int pos = 2;
                                System.arraycopy(result, 2, ucReadCmd, 2, 8);
                                pos += 8;
                                System.arraycopy(ucReadCmd, pos, ucReadCmd, pos, ucReadCmd.length - pos);
                                Log.d(TAG, "cmd:" + HexUtil.bytesToHexString(ucReadCmd));

                                Log.i(TAG, "Cmd Start");
                                ret = getFeilcaHandler().exchangeCmd(result, ucReadCmd, ucReadCmd.length);
                                Log.i(TAG, "Read Result:" + HexUtil.bytesToHexString(HexUtil.subByte(result, 0, ret)));

                                if (ret > 0) {
                                    byte[] buffer = new byte[ret];
                                    System.arraycopy(result, 0, buffer, 0, ret);
                                    //showResult(String.format("[%d] Read Result: %s", i, HexUtil.bytesToHexString(buffer)));
                                }
                            } else {
                                DialogUtils.dismissProgressDialog(getActivity());
                                DialogUtils.showAlertDialog(getActivity(), "POLL FAIL!");
                                return;
                            }
                        } else {
                            DialogUtils.dismissProgressDialog(getActivity());
                            DialogUtils.showAlertDialog(getActivity(), "Power on fail!");
                            return;
                        }
                        Thread.sleep(300);
                    }
                    getFeilcaHandler().setPowerOff();

                    DialogUtils.dismissProgressDialog(getActivity());
                    DialogUtils.showAlertDialog(getActivity(), "Test Finish");
                } catch (Exception e) {
                    e.printStackTrace();
                    showResult(e.toString());
                }
            }
        }).start();
    }
}
