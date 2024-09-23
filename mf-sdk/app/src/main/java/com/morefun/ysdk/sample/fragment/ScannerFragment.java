package com.morefun.ysdk.sample.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.morefun.yapi.device.reader.mag.MagCardInfoEntity;
import com.morefun.yapi.device.scanner.InnerScanner;
import com.morefun.yapi.device.scanner.OnScannedListener;
import com.morefun.yapi.device.scanner.ScannerConfig;
import com.morefun.yapi.device.scanner.ZebraParam;
import com.morefun.ysdk.sample.MyApplication;
import com.morefun.ysdk.sample.R;
import com.morefun.ysdk.sample.device.DeviceHelper;
import com.morefun.ysdk.sample.receiver.PackageInstallReceiver;
import com.morefun.ysdk.sample.utils.DialogUtils;
import com.morefun.ysdk.sample.utils.ToastUtils;
import com.morefun.ysdk.sample.zxing.CaptureActivity;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ScannerFragment extends Fragment {
    private final String TAG = ScannerFragment.class.getName();

    @BindView(R.id.rb_deke_camera)
    RadioButton rb_deke_camera;

    @BindView(R.id.rb_infrared)
    RadioButton rb_infrared;

    @BindView(R.id.rb_zebra)
    RadioButton rb_zebra;

    @BindView(R.id.rb_zxing)
    RadioButton rb_zxing;

    @BindView(R.id.rb_hd_camera)
    RadioButton rb_hd_camera;
    private boolean bStop = false;
    private CountDownLatch countDownLatch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scanner, null);
        ButterKnife.bind(this, view);
        registerBroadcast();
        return view;
    }

    @OnClick({R.id.btn_scan, R.id.btn_keep_scan})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_scan:
                scanner();
                break;
            case R.id.btn_keep_scan:
                keepScanner();
                break;
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        try {
            bStop = true;
            countDownLatch.countDown();
            DeviceHelper.getInnerScanner().stopScan();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            getActivity().unregisterReceiver(mReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initZxingScanner() {
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);

        integrator.setCaptureActivity(CaptureActivity.class)
                .setDesiredBarcodeFormats(IntentIntegrator.PDF_417,
                        IntentIntegrator.QR_CODE,
                        IntentIntegrator.CODE_39,
                        IntentIntegrator.CODE_128)
                .setPrompt("")
                .setCameraId(0)
                .setBeepEnabled(true)
                .setBarcodeImageEnabled(false)
                .initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                DialogUtils.showAlertDialog(getActivity(), "Cancel");
            } else {
                DialogUtils.showAlertDialog(getActivity(), result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void scanner() {
        try {
            if (rb_zxing.isChecked()) {
                initZxingScanner();
                return;
            }

            DeviceHelper.getInnerScanner().stopScan();

            final InnerScanner innerScanner = DeviceHelper.getInnerScanner();
            Bundle bundle = new Bundle();

            int scannerType = 1;
            if (rb_deke_camera.isChecked()) {
                //1 FRONT, 2 BACK
                bundle.putInt(ScannerConfig.CAMERA_TYPE, 1);
                scannerType = 1;
            } else if (rb_hd_camera.isChecked()) {
                scannerType = 2;
            } else if (rb_infrared.isChecked()) {
                scannerType = 3;
            } else if (rb_zebra.isChecked()) {
                scannerType = 0;
                //You can filter the scanner code by adding the following parameters
                ArrayList<ZebraParam> list = new ArrayList<>();
                //Add log
                //list.add(new ZebraParam((short) -2, (byte) 1));
                // enables MRZ for MRD documents
                list.add(new ZebraParam((short) 685, (byte) 11));
                // enables PDF417 for Driver license
                list.add(new ZebraParam((short) 15, (byte) 1));
                list.add(new ZebraParam((short) 277, (byte) 1));
                //bundle.putParcelableArrayList(ScannerConfig.ZEBRA_PARAM, list);
            }

            bundle.putInt(ScannerConfig.COMM_SCANNER_TYPE, scannerType);

            innerScanner.initScanner(bundle);
            innerScanner.startScan(60, new OnScannedListener.Stub() {
                @Override
                public void onScanResult(final int retCode, final byte[] scanResult) throws RemoteException {
                    if (retCode == 0) {
                        Log.d(TAG, "onScanResult:" + new String(scanResult));
                        ToastUtils.show(MyApplication.getInstance(), new String(scanResult));
                    } else {
                        ToastUtils.show(MyApplication.getInstance(), "Error code:" + retCode);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void keepScanner() {
        bStop = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true && !bStop) {
                    try {
                        Log.d(TAG, "keepScanner>>>");
                        countDownLatch = new CountDownLatch(1);

                        DeviceHelper.getInnerScanner().stopScan();

                        final InnerScanner innerScanner = DeviceHelper.getInnerScanner();
                        Bundle bundle = new Bundle();

                        bundle.putInt(ScannerConfig.COMM_SCANNER_TYPE, rb_infrared.isChecked() ? 3 : 0);

                        innerScanner.initScanner(bundle);
                        innerScanner.startScan(10, new OnScannedListener.Stub() {
                            @Override
                            public void onScanResult(final int retCode, final byte[] scanResult) throws RemoteException {
                                Log.d(TAG, "onScanResult:" + new String(scanResult));
                                if (retCode == 0) {
                                    ToastUtils.show(getActivity(), new String(scanResult));
                                }
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                                countDownLatch.countDown();
                            }
                        });
                        countDownLatch.await();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    private ScannerReceiver mReceiver = new ScannerReceiver();

    public class ScannerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("com.morefun.scancode.broadcast")) {
                ToastUtils.show(context, intent.getStringExtra("mf_scanner_text"));
            }
        }
    }

    private void registerBroadcast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.morefun.scancode.broadcast");

        getActivity().registerReceiver(mReceiver, intentFilter);
    }

}
