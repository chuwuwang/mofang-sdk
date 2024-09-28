package com.morefun.ysdk.sample.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.telephony.CellIdentityCdma;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.leon.lfilepickerlibrary.LFilePicker;
import com.leon.lfilepickerlibrary.utils.Constant;
import com.morefun.yapi.engine.DeviceInfoConstrants;
import com.morefun.yapi.engine.OnUninstallAppListener;
import com.morefun.ysdk.sample.MyApplication;
import com.morefun.ysdk.sample.R;
import com.morefun.ysdk.sample.activity.MainActivity;
import com.morefun.ysdk.sample.device.DeviceHelper;
import com.morefun.ysdk.sample.utils.BytesUtil;
import com.morefun.ysdk.sample.utils.DialogUtils;
import com.morefun.ysdk.sample.utils.FileUtil;
import com.morefun.ysdk.sample.utils.TlvData;
import com.morefun.ysdk.sample.utils.ToastUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeviceFragment extends Fragment {
    @BindView(R.id.tv_tip)
    TextView tvTip;

    private final int REQUEST_CODE = 0;
    private final String TAG = DeviceFragment.class.getName();
    private String mFilePath;
    private Fragment mFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device, null);
        ButterKnife.bind(this, view);
        mFragment = this;
        return view;
    }

    @OnClick({R.id.btn_deviceInfo, R.id.btn_install, R.id.btn_uninstall, R.id.btn_chooseFile,
            R.id.btn_ota, R.id.btn_enable_home_key, R.id.btn_disable_home_key, R.id.btn_enable_status_bar, R.id.btn_disable_status_bar,
            R.id.btn_wifi_off, R.id.btn_wifi_on, R.id.btn_sim1, R.id.btn_sim2,
            R.id.btn_force_sleep, R.id.btn_sleep_time, R.id.btn_enable_full_key, R.id.btn_disable_full_key,
            R.id.btn_switch_device, R.id.btn_switch_host, R.id.btn_reboot, R.id.btn_power_off,
            R.id.btn_autoStartAppEnable, R.id.btn_autoStartAppDisable, R.id.btn_kioskEnable, R.id.btn_kioskDisable, R.id.btn_setLauncher,
            R.id.btn_enableNaviBar, R.id.btn_disableNaviBar,R.id.btn_auxLcd})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_deviceInfo:
                getDeviceInfo();
                break;
            case R.id.btn_chooseFile:
                showFileChooser(REQUEST_CODE);
                break;
            case R.id.btn_install:
                install();
                break;
            case R.id.btn_uninstall:
                uninstall();
                break;
            case R.id.btn_ota:
                otaUpgrade();
                break;
            case R.id.btn_enable_home_key:
                enableHomeKey();
                break;
            case R.id.btn_disable_home_key:
                disableHomeKey();
                break;
            case R.id.btn_enable_status_bar:
                enableStatusBar();
                break;
            case R.id.btn_disable_status_bar:
                disableStatusBar();
                break;
            case R.id.btn_wifi_off:
                wifiOff();
                break;
            case R.id.btn_wifi_on:
                wifiOn();
                break;
            case R.id.btn_sim1:
                sim1();
                break;
            case R.id.btn_sim2:
                sim2();
                break;
            case R.id.btn_force_sleep:
                forceSleep();
                break;
            case R.id.btn_sleep_time:
                sleepTime();
                break;
            case R.id.btn_enable_full_key:
                enableFullKeyMode();
                break;
            case R.id.btn_disable_full_key:
                disableFullKeyMode();
                break;
            case R.id.btn_switch_device:
                switchToDevice();
                break;
            case R.id.btn_switch_host:
                switchToHost();
                break;
            case R.id.btn_power_off:
                powerOff();
                break;
            case R.id.btn_reboot:
                reboot();
                break;
            case R.id.btn_autoStartAppEnable:
                autoStartAppEnable();
                break;
            case R.id.btn_autoStartAppDisable:
                autoStartAppDisable();
                break;
            case R.id.btn_kioskEnable:
                enableKioskMode();
                break;
            case R.id.btn_kioskDisable:
                disableKioskMode();
                break;
            case R.id.btn_setLauncher:
                setLauncher();
                break;
            case R.id.btn_enableNaviBar:
                enableNaviBar();
                break;
            case R.id.btn_disableNaviBar:
                disableNaviBar();
                break;
            case R.id.btn_auxLcd:
                showAuxLcdDialog(getActivity());
                break;
        }
    }

    private void getDeviceInfo() {
        try {
            String[] telInfo = telephonyCellLocation(getContext());

            Bundle devInfo = DeviceHelper.getDeviceService().getDevInfo();
            String vendor = devInfo.getString(DeviceInfoConstrants.COMMOM_VENDOR);
            String model = devInfo.getString(DeviceInfoConstrants.COMMOM_MODEL_EX);
            String osVer = devInfo.getString(DeviceInfoConstrants.COMMOM_OS_VER);
            String sn = devInfo.getString(DeviceInfoConstrants.COMMOM_SN);
            String versionCode = devInfo.getString(DeviceInfoConstrants.COMMON_SERVICE_VER);
            String sp = devInfo.getString(DeviceInfoConstrants.COMMOM_HARDWARE);
            String hardwareVer = devInfo.getString(DeviceInfoConstrants.COMMOM_HARDWARE_VER);
            String imei = devInfo.getString(DeviceInfoConstrants.IMEI);
            String imsi = devInfo.getString(DeviceInfoConstrants.IMSI);
            String iccid = devInfo.getString(DeviceInfoConstrants.ICCID);
            String imei2 = devInfo.getString(DeviceInfoConstrants.IMEI2);
            String imsi2 = devInfo.getString(DeviceInfoConstrants.IMSI2);
            String iccid2 = devInfo.getString(DeviceInfoConstrants.ICCID2);
            StringBuilder builder = new StringBuilder();

            builder.append("Manufacture:" + vendor + "\n");
            builder.append("Model:" + model + "\n");
            builder.append("System:" + osVer + "\n");
            builder.append("Sn:" + sn + "\n");
            builder.append("Sp:" + sp + "\n");
            builder.append("Version Code:" + versionCode + "\n");
            builder.append("Hardware Version:" + hardwareVer + "\n");
            if (!TextUtils.isEmpty(imei)) {
                builder.append("IMEI:" + imei + "\n");
            }

            if (!TextUtils.isEmpty(imei2)) {
                builder.append("IMEI2:" + imei2 + "\n");
            }

            if (!TextUtils.isEmpty(imsi)) {
                builder.append("IMSI:" + imsi + "\n");
            }

            if (!TextUtils.isEmpty(imsi2)) {
                builder.append("IMSI2:" + imsi2 + "\n");
            }

            if (!TextUtils.isEmpty(iccid)) {
                builder.append("ICCID:" + iccid + "\n");
            }

            if (!TextUtils.isEmpty(iccid2)) {
                builder.append("ICCID2:" + iccid2 + "\n");
            }
            builder.append("App Key:" + devInfo.getString("AppKey") + "\n");
            builder.append("Pub Key:" + devInfo.getString("PubKey") + "\n");

            String mcc = String.format("%03d", Integer.parseInt(telInfo[3]));
            String mnc = String.format("%02d", Integer.parseInt(telInfo[2]));
            String lac = String.format("%d", Integer.parseInt(telInfo[1]));
            String cid = String.format("%d", Integer.parseInt(telInfo[0]));

            builder.append("MCC:" + mcc + "\n");
            builder.append("MNC:" + mnc + "\n");
            builder.append("LAC:" + lac + "\n");
            builder.append("CID:" + cid + "\n");

            DialogUtils.showAlertDialog(getActivity(), builder.toString());
        } catch (Exception e) {
        }
    }

    private void showFileChooser(int requestCode) {
        new LFilePicker()
                .withSupportFragment(mFragment)
                .withRequestCode(requestCode)
                .withTitle("Please Choose APK")
                .withMutilyMode(false)
                .withStartPath(Environment.getExternalStorageDirectory().getPath())//指定初始显示路径
                .withNotFoundBooks("Select at least one file")
                .withChooseMode(true)
                .withFileFilter(new String[]{".apk", ".zip"})
                .start();
    }

    private void install() {
        if (mFilePath == null) {
            ToastUtils.show(getContext(), "Please Choose An APK");
            return;
        }

        try {
            Log.i(TAG, String.format("File Path:%s", mFilePath));
            DeviceHelper.getDeviceService().installApp(mFilePath, "", "");
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    private void uninstall() {
        try {
            String packageName = "Name of your application package";

            DeviceHelper.getDeviceService().uninstallApp(packageName, new OnUninstallAppListener.Stub() {
                @Override
                public void onUninstallAppResult(int code) throws RemoteException {
                    Log.e(TAG, String.format("onUninstallAppResult:%d", code));
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    private void otaUpgrade() {
        if (mFilePath == null) {
            ToastUtils.show(getContext(), "Please choose an OTA package");
            return;
        }
        Intent intent = new Intent("com.morefun.upgrade");
        intent.putExtra("filepath", mFilePath);
        getActivity().sendBroadcast(intent);
    }

    private void disableHomeKey() {
        try {
            Bundle bundle = new Bundle();
            bundle.putBoolean(DeviceInfoConstrants.DISABLE_HOME, true);
            int ret = DeviceHelper.getDeviceService().setProperties(bundle);
            DialogUtils.showAlertDialog(getActivity(), ret == 0 ? "Success" : "Fail");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void enableHomeKey() {
        try {
            Bundle bundle = new Bundle();
            bundle.putBoolean(DeviceInfoConstrants.ENABLE_HOME, true);
            int ret = DeviceHelper.getDeviceService().setProperties(bundle);
            DialogUtils.showAlertDialog(getActivity(), ret == 0 ? "Success" : "Fail");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void disableStatusBar() {
        try {
            Bundle bundle = new Bundle();
            bundle.putBoolean(DeviceInfoConstrants.DISABLE_STATUS_BAR, true);
            int ret = DeviceHelper.getDeviceService().setProperties(bundle);
            DialogUtils.showAlertDialog(getActivity(), ret == 0 ? "Success" : "Fail");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void enableStatusBar() {
        try {
            Bundle bundle = new Bundle();
            bundle.putBoolean(DeviceInfoConstrants.ENABLE_STATUS_BAR, true);
            int ret = DeviceHelper.getDeviceService().setProperties(bundle);
            DialogUtils.showAlertDialog(getActivity(), ret == 0 ? "Success" : "Fail");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void reboot() {
        try {
            Bundle bundle = new Bundle();
            bundle.putBoolean(DeviceInfoConstrants.REBOOT, true);
            int ret = DeviceHelper.getDeviceService().setProperties(bundle);
            DialogUtils.showAlertDialog(getActivity(), ret == 0 ? "Success" : "Fail");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void powerOff() {
        try {
            Bundle bundle = new Bundle();
            bundle.putBoolean(DeviceInfoConstrants.POWER_OFF, true);
            int ret = DeviceHelper.getDeviceService().setProperties(bundle);
            DialogUtils.showAlertDialog(getActivity(), ret == 0 ? "Success" : "Fail");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    private final String ACTION_USB_HOST = "com.morefun.usbhost";
    private final String ACTION_USB_DEVICE = "com.morefun.usbdevice";

    private void switchToHost() {
        Intent intent = new Intent(ACTION_USB_HOST);
        getActivity().sendBroadcast(intent);
    }

    private void switchToDevice() {
        Intent intent = new Intent(ACTION_USB_DEVICE);
        getActivity().sendBroadcast(intent);
    }

    private void wifiOn() {
        try {
            Bundle bundle = new Bundle();
            bundle.putInt(DeviceInfoConstrants.SETTING_WIFI_SWITCH, 1);
            int ret = DeviceHelper.getDeviceService().setProperties(bundle);
            DialogUtils.showAlertDialog(getActivity(), ret == 0 ? "Success" : "Fail");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void wifiOff() {
        try {
            Bundle bundle = new Bundle();
            bundle.putInt(DeviceInfoConstrants.SETTING_WIFI_SWITCH, 0);
            int ret = DeviceHelper.getDeviceService().setProperties(bundle);
            DialogUtils.showAlertDialog(getActivity(), ret == 0 ? "Success" : "Fail");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void sim1() {
        try {
            Bundle bundle = new Bundle();
            bundle.putInt(DeviceInfoConstrants.SETTING_SIM_SLOT, 0);
            int ret = DeviceHelper.getDeviceService().setProperties(bundle);
            DialogUtils.showAlertDialog(getActivity(), ret == 0 ? "Success" : "Fail");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void sim2() {
        try {
            Bundle bundle = new Bundle();
            bundle.putInt(DeviceInfoConstrants.SETTING_SIM_SLOT, 1);
            int ret = DeviceHelper.getDeviceService().setProperties(bundle);
            DialogUtils.showAlertDialog(getActivity(), ret == 0 ? "Success" : "Fail");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void forceSleep() {
        try {
            Bundle bundle = new Bundle();
            bundle.putBoolean(DeviceInfoConstrants.FORCE_SLEEP, true);
            int ret = DeviceHelper.getDeviceService().setProperties(bundle);
            DialogUtils.showAlertDialog(getActivity(), ret == 0 ? "Success" : "Fail");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void sleepTime() {
        try {
            Bundle bundle = new Bundle();
            bundle.putInt(DeviceInfoConstrants.SLEEP_TIME, 60 * 1000);
            int ret = DeviceHelper.getDeviceService().setProperties(bundle);
            DialogUtils.showAlertDialog(getActivity(), ret == 0 ? "Success" : "Fail");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void enableFullKeyMode() {
        try {
            Bundle bundle = new Bundle();
            bundle.putBoolean(DeviceInfoConstrants.ENABLE_FULL_KEY_MODE, true);
            int ret = DeviceHelper.getDeviceService().setProperties(bundle);
            DialogUtils.showAlertDialog(getActivity(), ret == 0 ? "Success" : "Fail");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void disableFullKeyMode() {
        try {
            Bundle bundle = new Bundle();
            bundle.putBoolean(DeviceInfoConstrants.DISABLE_FULL_KEY_MODE, true);
            int ret = DeviceHelper.getDeviceService().setProperties(bundle);
            DialogUtils.showAlertDialog(getActivity(), ret == 0 ? "Success" : "Fail");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void autoStartAppEnable() {
        try {
            Bundle bundle = new Bundle();
            bundle.putBoolean(DeviceInfoConstrants.AUTO_START_APP_ENABLE, true);
            bundle.putString(DeviceInfoConstrants.AUTO_START_APP_PACKAGE, "com.morefun.ysdk.sample");

            int ret = DeviceHelper.getDeviceService().setProperties(bundle);
            DialogUtils.showAlertDialog(getActivity(), ret == 0 ? "Success" : "Fail");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void autoStartAppDisable() {
        try {
            Bundle bundle = new Bundle();
            bundle.putBoolean(DeviceInfoConstrants.AUTO_START_APP_DISABLE, true);

            int ret = DeviceHelper.getDeviceService().setProperties(bundle);
            DialogUtils.showAlertDialog(getActivity(), ret == 0 ? "Success" : "Fail");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Long press the power button and enter the password to exit kioskMode
     * The next time the application is launched, it will still enter kioskMode
     */
    private void enableKioskMode() {
        try {
            Bundle bundle = new Bundle();
            bundle.putBoolean(DeviceInfoConstrants.KIOSK_MODE_ENABLE, true);
            bundle.putString(DeviceInfoConstrants.KIOSK_MODE_PACKAGE, "com.morefun.ysdk.sample");
            bundle.putString(DeviceInfoConstrants.KIOSK_MODE_PASSWORD, "123456");

            int ret = DeviceHelper.getDeviceService().setProperties(bundle);
            restartApp();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Long press the power button and enter the empty password to exit kioskMode
     * The next time the application is launched, it will not enter kioskMode
     */
    private void disableKioskMode() {
        try {
            Bundle bundle = new Bundle();
            bundle.putBoolean(DeviceInfoConstrants.KIOSK_MODE_DISABLE, true);
            bundle.putString(DeviceInfoConstrants.KIOSK_MODE_PACKAGE, "com.morefun.ysdk.sample");

            int ret = DeviceHelper.getDeviceService().setProperties(bundle);
            getActivity().finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * In AndroidManifest.xml, it is necessary to declare the activity as a launcher
     * <category android:name="android.intent.category.HOME"/>
     * <category android:name="android.intent.category.DEFAULT"/>
     */
    private void setLauncher() {
        try {
            Bundle bundle = new Bundle();
            bundle.putBoolean(DeviceInfoConstrants.SET_LAUNCHER, true);
            bundle.putString(DeviceInfoConstrants.SET_LAUNCHER_PACKAGE, "com.morefun.ysdk.sample");
            bundle.putString(DeviceInfoConstrants.SET_LAUNCHER_ACTIVITY, "com.morefun.ysdk.sample.activity.MainActivity");

            int ret = DeviceHelper.getDeviceService().setProperties(bundle);
            DialogUtils.showAlertDialog(getActivity(), ret == 0 ? "Success" : "Fail");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            List<String> list = data.getStringArrayListExtra(Constant.RESULT_INFO);
            if (requestCode == REQUEST_CODE) {
                mFilePath = list.get(0);
                tvTip.setText("File Path:" + mFilePath + "\n");
            }
        }
    }

    @SuppressLint("MissingPermission")
    private String[] telephonyCellLocation(Context context) {
        String[] ret = new String[4];
        ret[0] = "0";
        ret[1] = "0";
        ret[2] = "0";
        ret[3] = "0";
        try {
            TelephonyManager tel = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            List<CellInfo> cellInfos = tel.getAllCellInfo();
            if (cellInfos != null) {
                for (CellInfo cellInfo : cellInfos) {
                    if (cellInfo instanceof CellInfoGsm) {
                        CellIdentityGsm cellIdentityGsm = ((CellInfoGsm) cellInfo).getCellIdentity();
                        int cid = cellIdentityGsm.getCid();
                        int lac = cellIdentityGsm.getLac();
                        int mcc = cellIdentityGsm.getMcc();
                        int mnc = cellIdentityGsm.getMnc();
                        Log.d(TAG, "cid:" + cid + " lac:" + lac + " mcc:" + mcc + " mnc:" + mnc);
                        ret[0] = String.valueOf(cid);
                        ret[1] = String.valueOf(lac);
                        ret[2] = String.valueOf(mnc);
                        ret[3] = String.valueOf(mcc);
                        return ret;
                    } else if (cellInfo instanceof CellInfoCdma) {
                        CellIdentityCdma cellIdentityCdma = ((CellInfoCdma) cellInfo).getCellIdentity();
                        int cid = cellIdentityCdma.getBasestationId();
                        int lac = cellIdentityCdma.getSystemId();
                        int mcc = cellIdentityCdma.getNetworkId();
                        int mnc = cellIdentityCdma.getSystemId();
                        Log.d(TAG, "cid:" + cid + " lac:" + lac + " mcc:" + mcc + " mnc:" + mnc);
                        ret[0] = String.valueOf(cid);
                        ret[1] = String.valueOf(lac);
                        ret[2] = String.valueOf(mnc);
                        ret[3] = String.valueOf(mcc);
                        return ret;
                    } else if (cellInfo instanceof CellInfoLte) {
                        CellIdentityLte cellIdentityLte = ((CellInfoLte) cellInfo).getCellIdentity();
                        int ci = cellIdentityLte.getCi();
                        int tac = cellIdentityLte.getTac();
                        int mcc = cellIdentityLte.getMcc();
                        int mnc = cellIdentityLte.getMnc();
                        Log.d(TAG, "cid:" + ci + " lac:" + tac + " mcc:" + mcc + " mnc:" + mnc);
                        ret[0] = String.valueOf(ci);
                        ret[1] = String.valueOf(tac);
                        ret[2] = String.valueOf(mnc);
                        ret[3] = String.valueOf(mcc);
                        return ret;
                    } else if (cellInfo instanceof CellInfoWcdma) {
                        CellIdentityWcdma cellIdentityWcdma = ((CellInfoWcdma) cellInfo).getCellIdentity();
                        int cid = cellIdentityWcdma.getCid();
                        int lac = cellIdentityWcdma.getLac();
                        int mcc = cellIdentityWcdma.getMcc();
                        int mnc = cellIdentityWcdma.getMnc();
                        Log.d(TAG, "cid:" + cid + " lac:" + lac + " mcc:" + mcc + " mnc:" + mnc);
                        ret[0] = String.valueOf(cid);
                        ret[1] = String.valueOf(lac);
                        ret[2] = String.valueOf(mnc);
                        ret[3] = String.valueOf(mcc);
                        return ret;
                    }
                }
            } else {
                Log.d(TAG, "cellList is null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    private void restartApp() {
        final Intent intent = MyApplication.getInstance().getPackageManager().getLaunchIntentForPackage(MyApplication.getInstance().getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        MyApplication.getInstance().startActivity(intent);
        System.exit(0);
    }

    private void enableNaviBar() {
        try {
            Bundle bundle = new Bundle();
            bundle.putBoolean(DeviceInfoConstrants.ENABLE_NAVI_BAR, true);

            int ret = DeviceHelper.getDeviceService().setProperties(bundle);
            DialogUtils.showAlertDialog(getActivity(), ret == 0 ? "Success" : "Fail");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void disableNaviBar() {
        try {
            Bundle bundle = new Bundle();
            bundle.putBoolean(DeviceInfoConstrants.DISABLE_NAVI_BAR, true);
            int ret = DeviceHelper.getDeviceService().setProperties(bundle);
            DialogUtils.showAlertDialog(getActivity(), ret == 0 ? "Success" : "Fail");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showAuxLcdDialog(Activity activity) {
        AlertDialog dialog = new AlertDialog.Builder(activity)
            .setTitle("Aux Lcd")
            .setView(R.layout.dialog_auxlcd)
            .setCancelable(false)
            .setNegativeButton(activity.getString(R.string.tip_cancel), null)
            .create();
        dialog.show();

        ImageView iv_show = dialog.findViewById(R.id.iv_show);
        dialog.findViewById(R.id.btn_showAuxLcdBitmap).setOnClickListener(v -> {
            Bitmap bitmap = FileUtil.getImageFromAssetsFile(activity, "mf_logo.png");
            showAuxLcdBitmap(bitmap);

            iv_show.setImageBitmap(bitmap);
        });
        dialog.findViewById(R.id.btn_showAuxLcdMsg).setOnClickListener(v -> {
            Bitmap bitmap = createBitmapTxt(Color.BLUE, "Hello World", 40, Color.WHITE, Gravity.CENTER, Gravity.CENTER);
            showAuxLcdBitmap(bitmap);

            iv_show.setImageBitmap(bitmap);
        });
        dialog.findViewById(R.id.btn_clearAuxLcd).setOnClickListener(v -> {
            Bitmap bitmap = createBgBitmap(Color.WHITE);
            showAuxLcdBitmap(bitmap);

            iv_show.setImageBitmap(bitmap);
        });
        dialog.findViewById(R.id.btn_closeAuxLcd).setOnClickListener(v -> {
            closeAuxLcd();
        });
    }

    private Bitmap createBgBitmap(int color){
        int width = 320;
        int height = 172;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(color);
        return bitmap;
    }

    /**
     * bgColor: Background Color
     * msg: message text
     * textSize: Text Size
     * textColor: Text Color
     * horizontalGravity: Horizontal Gravity, Support Gravity.LEFT, Gravity.CENTER, Gravity.RIGHT
     * verticalGravity: Vertical Gravity, Support Gravity.TOP, Gravity.CENTER, Gravity.BOTTOM
     * */
    private Bitmap createBitmapTxt(int bgColor, String msg,int textSize, int textColor, int horizontalGravity,int verticalGravity){
        Bitmap bitmap = Bitmap.createBitmap(320, 172, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);

        Rect rect = new Rect(0,0,320,172);
        Paint rectPaint = new Paint();
        rectPaint.setColor(bgColor);
        rectPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(rect, rectPaint);

        Paint textPaint = new Paint();
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
        textPaint.setStyle(Paint.Style.FILL);

        float textWidth = textPaint.measureText(msg); //计算文本宽度
        Rect bound = new Rect();
        textPaint.getTextBounds(msg,0,msg.length(),bound);
        int textHeight = bound.height();//计算文本高度

        float x = 0;
        float y = 0;
        switch (horizontalGravity){
            case Gravity.LEFT:
                x = 0;
                break;
            case Gravity.CENTER:
                x = canvas.getWidth()/2 - textWidth/2;
                break;
            case Gravity.RIGHT:
                x = canvas.getWidth() - textWidth;
                break;
        }
        switch (verticalGravity){
            case Gravity.TOP:
                y = textHeight;
                break;
            case Gravity.CENTER:
                y = canvas.getHeight()/2 + textHeight/2;
                break;
            case Gravity.BOTTOM:
                y = canvas.getHeight() - textHeight/4;
                break;
        }

        canvas.drawText(msg, x, y, textPaint);
        return bitmap;
    }

    /**
     *  note
     *  To display an image, the following 3 steps must be performed
     *  Image format requirements: bmp
     *  Image size requirements: 320*172
     * */
    private void showAuxLcdBitmap(Bitmap bitmap) {
        //step 1
        showAuxLcdLight();
        //step 2
        showAuxLcd(bitmap);
        //step 3
        showAuxLcdFlush();
    }

    /**
     *  Load Images
     * */
    private void showAuxLcd(Bitmap bitmap) {
        try {
            Bundle bundle = new Bundle();
            bundle.putBoolean(DeviceInfoConstrants.SHOW_AUXLCD, true);
            bundle.putParcelable(DeviceInfoConstrants.AUXLCD_BITMAP, bitmap);
            DeviceHelper.getDeviceService().setProperties(bundle);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     *  Startup screen
     * */
    private void showAuxLcdLight() {
        try {
            Bundle bundle = new Bundle();
            bundle.putBoolean(DeviceInfoConstrants.LIGHT_AUXLCD, true);
            DeviceHelper.getDeviceService().setProperties(bundle);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     *  screen refresh
     * */
    private void showAuxLcdFlush() {
        try {
            Bundle bundle = new Bundle();
            bundle.putBoolean(DeviceInfoConstrants.FLUSH_AUXLCD, true);
            DeviceHelper.getDeviceService().setProperties(bundle);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     *  Off screen
     * */
    private void closeAuxLcd() {
        try {
            Bundle bundle = new Bundle();
            bundle.putBoolean(DeviceInfoConstrants.CLOSE_AUXLCD, true);
            DeviceHelper.getDeviceService().setProperties(bundle);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
