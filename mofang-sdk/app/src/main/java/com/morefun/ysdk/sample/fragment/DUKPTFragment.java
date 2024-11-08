package com.morefun.ysdk.sample.fragment;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.morefun.yapi.device.dukpt.DukptConstants;
import com.morefun.yapi.device.pinpad.DispTextMode;
import com.morefun.yapi.device.pinpad.DukptCalcObj;
import com.morefun.yapi.device.pinpad.DukptLoadObj;
import com.morefun.yapi.device.pinpad.MacAlgorithmType;
import com.morefun.yapi.device.pinpad.OnPinPadInputListener;
import com.morefun.yapi.device.pinpad.PinAlgorithmMode;
import com.morefun.yapi.device.pinpad.PinPadConstrants;
import com.morefun.yapi.device.pinpad.PinPadType;
import com.morefun.ysdk.sample.R;
import com.morefun.ysdk.sample.device.DeviceHelper;
import com.morefun.ysdk.sample.utils.BytesUtil;
import com.morefun.ysdk.sample.utils.DialogUtils;
import com.morefun.ysdk.sample.utils.HexUtil;
import com.morefun.ysdk.sample.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DUKPTFragment extends Fragment {

    @BindView(R.id.et_index)
    EditText et_index;
    private String mEncryptData = null;
    private final String TAG = getClass().getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dukpt, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick({R.id.btn_login, R.id.btn_dukptInit, R.id.btn_currentKsn, R.id.btn_increaseKsn,
            R.id.btn_dukptEncrypt, R.id.btn_dukptDecrypt, R.id.btn_onlinePin, R.id.btn_offlinePin,
            R.id.btn_calcMac, R.id.btn_pin_block})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                login();
                break;
            case R.id.btn_dukptInit:
                dukptInit();
                break;
            case R.id.btn_currentKsn:
                increaseKsn(false);
                break;
            case R.id.btn_increaseKsn:
                increaseKsn(true);
                break;
            case R.id.btn_dukptEncrypt:
                dukptEncrypt();
                break;
            case R.id.btn_dukptDecrypt:
                dukptDecrypt();
                break;
            case R.id.btn_onlinePin:
                onlinePin();
                break;
            case R.id.btn_offlinePin:
                offlinePin();
                break;
            case R.id.btn_calcMac:
                calcMac();
                break;
            case R.id.btn_pin_block:
                pinBlock();
                break;
        }
    }

    @OnClick({R.id.btn_loginAES, R.id.btn_dukptAESInit, R.id.btn_currentKsnAES, R.id.btn_increaseKsnAES,
            R.id.btn_dukptEncryptAES, R.id.btn_dukptDecryptAES})
    public void dukptAES(View view) {
        switch (view.getId()) {
            case R.id.btn_loginAES:
                loginAES();
                break;
            case R.id.btn_dukptAESInit:
                dukptInitAES();
                break;
            case R.id.btn_currentKsnAES:
                getCurrentKsnAES();
                break;
            case R.id.btn_increaseKsnAES:
                increaseKsnAES();
                break;
            case R.id.btn_dukptEncryptAES:
                dukptEncryptAES();
                break;
            case R.id.btn_dukptDecryptAES:
                dukptDecryptAES();
                break;
        }
    }

    private void login() {
        Bundle bundle = new Bundle();
        try {
            String businessId = "09000000";

            int ret = DeviceHelper.getDeviceService().login(bundle, businessId);
            if (ret == 0) {
                ToastUtils.show(getContext(), getString(R.string.tip_login_success));
                return;
            }
            ToastUtils.show(getContext(), getString(R.string.tip_login_error));
            return;
        } catch (RemoteException e) {
            ToastUtils.show(getContext(), e.getMessage());
        } catch (NullPointerException e) {
            ToastUtils.show(getContext(), e.getMessage());
        }
    }

    private void dukptInit() {
        try {
            String bdk = "C1D0F8FB4958670DBA40AB1F3752EF0A";
            String ksn = "21FFFF33110000000000";

            DukptLoadObj dukptLoadObj = new DukptLoadObj(bdk, ksn
                    , DukptLoadObj.DukptKeyTypeEnum.DUKPT_BDK_PLAINTEXT
                    , DukptLoadObj.DukptKeyIndexEnum.values()[getKeyIndex()]);

            int ret = DeviceHelper.getPinpad().dukptLoad(dukptLoadObj);
            if (ret == 0) {
                DialogUtils.showAlertDialog(getActivity(), "DUKPT Load Success");
            } else {
                DialogUtils.showAlertDialog(getActivity(), "DUKPT Load Fail");
            }
        } catch (Exception e) {
        }
    }

    /*
     * increaseKsn needs to be called every time the device is restarted bIncrease true
     */
    private void increaseKsn(boolean bIncrease) {
        try {
            String ksn = DeviceHelper.getPinpad().increaseKSN(getKeyIndex(), bIncrease);
            DialogUtils.showAlertDialog(getActivity(), "KSN:" + ksn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void calcMac() {
        try {
            byte[] data = HexUtil.hexStringToByte("00000000005010016222620910029130840241205100100367FD3414057DB801BE18A309A544C5174CC777525974CBD467BCC56EA16629F3B016488A6C314921485C75F57066D4682FEDC1F910C5C8136A201279B590898B40D7098461D345168810CCFEBC61204B3E6F364A95175EF54C7EBAAEC2A6AEE44D9783747124D313B78A3F754C5ECC611533C4957377DD2067DF927C80461C4E4C20A8A4CC57EF1CCE2BC1AEEA442431256F66A25AB855912BA82FB8AD308F0EDE358CDDDEA63C95401B8335C8689E5735E0FB96733426FD71A7248E140A95CB4B4313AC0DBDA1E70EA8800000000000");
            String ksn = DeviceHelper.getPinpad().increaseKSN(getKeyIndex(), true);

            Bundle bundle = new Bundle();
            bundle.putInt(DukptCalcObj.Param.DUKPT_KEY_INDEX, getKeyIndex());
            byte[] mac = DeviceHelper.getPinpad().getMac(-1, MacAlgorithmType.ISO9797_ALG3, -1, data, bundle);

            DialogUtils.showAlertDialog(getActivity(), String.format("MAC:%s\nKSN:%s", HexUtil.bytesToHexString(mac), ksn));
        } catch (Exception e) {

        }
    }

    private void dukptDecrypt() {
        try {
            String data = mEncryptData;

            DukptCalcObj.DukptAlgEnum alg = DukptCalcObj.DukptAlgEnum.DUKPT_ALG_CBC;
            DukptCalcObj.DukptOperEnum oper = DukptCalcObj.DukptOperEnum.DUKPT_DECRYPT;
            DukptCalcObj.DukptTypeEnum type = DukptCalcObj.DukptTypeEnum.DUKPT_DES_KEY_DATA1;

            DukptCalcObj dukptCalcObj = new DukptCalcObj(DukptCalcObj.DukptKeyIndexEnum.values()[getKeyIndex()], type, oper, alg, data);

            Bundle bundle = DeviceHelper.getPinpad().dukptCalcDes(dukptCalcObj);

            DialogUtils.showAlertDialog(getActivity(), String.format("DECRYPT:%s\nKSN:%s",
                    bundle.getString(DukptCalcObj.DUKPT_DATA),
                    bundle.getString(DukptCalcObj.DUKPT_KSN)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dukptEncrypt() {
        try {
            String data = "12345678ABCDEF45";

            DukptCalcObj.DukptAlgEnum alg = DukptCalcObj.DukptAlgEnum.DUKPT_ALG_CBC;
            DukptCalcObj.DukptOperEnum oper = DukptCalcObj.DukptOperEnum.DUKPT_ENCRYPT;
            DukptCalcObj.DukptTypeEnum type = DukptCalcObj.DukptTypeEnum.DUKPT_DES_KEY_DATA1;

            DukptCalcObj dukptCalcObj = new DukptCalcObj(DukptCalcObj.DukptKeyIndexEnum.values()[getKeyIndex()], type, oper, alg, data);

            Bundle bundle = DeviceHelper.getPinpad().dukptCalcDes(dukptCalcObj);
            mEncryptData = bundle.getString(DukptCalcObj.DUKPT_DATA);
            DialogUtils.showAlertDialog(getActivity(), String.format("ENCRYPT:%s\nKSN:%s",
                    bundle.getString(DukptCalcObj.DUKPT_DATA),
                    bundle.getString(DukptCalcObj.DUKPT_KSN)));

        } catch (Exception e) {

        }
    }

    private void onlinePin() {
        byte[] panBlock = "1234567890123456".getBytes();
        Bundle bundle = new Bundle();

        bundle.putIntArray(PinPadConstrants.COMMON_BG_COLOR, new int[]{-1, -1, Color.GREEN});
        bundle.putString(PinPadConstrants.TITLE_HEAD_CONTENT, "Please input the online pin");
        bundle.putBoolean(PinPadConstrants.COMMON_SUPPORT_BYPASS, true);
        bundle.putBoolean(PinPadConstrants.COMMON_IS_RANDOM, false);
        if (Build.MODEL.equals("MF960")) {
            bundle.putBoolean(PinPadConstrants.COMMON_IS_PHYSICAL_KEYBOARD, true);
        }
        if (Build.MODEL.equals("H9PRO")) {
            bundle.putBoolean(PinPadConstrants.COMMON_IS_PHYSICAL_KEYBOARD, true);
        }
        int minLength = 0;
        int maxLength = 6;
        try {
            DeviceHelper.getPinpad().setTimeOut(60);
            DeviceHelper.getPinpad().setSupportPinLen(new int[]{minLength, maxLength});
            DeviceHelper.getPinpad().inputOnlinePin(bundle, panBlock, getKeyIndex(), PinAlgorithmMode.ISO9564FMT1, new OnPinPadInputListener.Stub() {
                @Override
                public void onInputResult(int ret, byte[] pinBlock, String ksn) throws RemoteException {
                    Log.d(TAG, "pinBlock:" + pinBlock.length);
                    DialogUtils.showAlertDialog(getActivity(), String.format("PIN BLOCK:%s\nKSN:%s",
                            HexUtil.bytesToHexString(pinBlock), ksn));
                }

                @Override
                public void onSendKey(byte keyCode) throws RemoteException {

                }
            });
        } catch (Exception e) {

        }
    }

    private void offlinePin() {
        Bundle bundle = new Bundle();
        bundle.putString(PinPadConstrants.TITLE_HEAD_CONTENT, "Please input the offline pin");
        bundle.putBoolean(PinPadConstrants.COMMON_IS_RANDOM, true);
        bundle.putBoolean(PinPadConstrants.COMMON_IS_PHYSICAL_KEYBOARD, false);

        int minLength = 4;
        int maxLength = 4;
        try {
            DeviceHelper.getPinpad().setTimeOut(60);
            DeviceHelper.getPinpad().setSupportPinLen(new int[]{minLength, maxLength});
            DeviceHelper.getPinpad().inputText(bundle, new OnPinPadInputListener.Stub() {
                @Override
                public void onInputResult(int ret, byte[] pinBlock, String ksn) throws RemoteException {
                    if (pinBlock != null) {
                        DialogUtils.showAlertDialog(getActivity(), new String(pinBlock));
                    }
                }

                @Override
                public void onSendKey(byte keyCode) throws RemoteException {

                }

            }, DispTextMode.PLAINTEXT);
        } catch (RemoteException e) {

        }
    }

    private void pinBlock() {
        try {
            int type = PinPadType.SEC_DUKPT_FIELD;
            int pinFormat = PinPadType.SEC_PIN_FORMAT0;
            int keyId = getKeyIndex();
            byte[] pan = "1234567890123456".getBytes();
            byte[] pin = "123456".getBytes();
            byte[] pinBlock = new byte[8];

            int ret = DeviceHelper.getPinpad().getPinBlock(type, pinFormat, keyId, pan, pin, pinBlock);

            if (ret == 0) {
                DialogUtils.showAlertDialog(getActivity(), "PIN BLOCK: " + BytesUtil.bytes2HexString(pinBlock));
            } else {
                DialogUtils.showAlertDialog(getActivity(), "Get pin block fail!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            DialogUtils.showAlertDialog(getActivity(), e.getMessage());
        }
    }

    private int getKeyIndex() {
        try {
            return Integer.parseInt(et_index.getText().toString());
        } catch (Exception e) {
        }
        return 0;
    }

    private void loginAES() {
        Bundle bundle = new Bundle();
        try {
            String businessId = "08000000";

            int ret = DeviceHelper.getDeviceService().login(bundle, businessId);
            if (ret == 0) {
                ToastUtils.show(getContext(), getString(R.string.tip_login_success));
                return;
            }
            ToastUtils.show(getContext(), getString(R.string.tip_login_error));
        } catch (RemoteException e) {
            ToastUtils.show(getContext(), e.getMessage());
        } catch (NullPointerException e) {
            ToastUtils.show(getContext(), e.getMessage());
        }
    }

    private void dukptInitAES() {
        try {
            int index = getKeyIndex();
            String key = "FEDCBA9876543210F1F1F1F1F1F1F1F1";
            String ksn = "1234567890ABCDEF00000001";
            int mode = DukptConstants.LoadKeyMode.BDK;
            int keyType = DukptConstants.LoadKeyType.AES128;

            int ret = DeviceHelper.getDukpt().dukptLoad(index, key, ksn, mode, keyType);

            DialogUtils.showAlertDialog(getActivity(), ret == 0 ? "Dukpt Init Success" : "Dukpt Init Fail");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void increaseKsnAES() {
        try {
            int index = getKeyIndex();
            int ret = DeviceHelper.getDukpt().increaseKsn(index);
            DialogUtils.showAlertDialog(getActivity(), ret == 0 ? "Success" : "Fail");
        } catch (Exception e) {

        }
    }

    private void getCurrentKsnAES() {
        try {
            int index = getKeyIndex();
            String ksn = DeviceHelper.getDukpt().getCurrentKsn(index);
            DialogUtils.showAlertDialog(getActivity(), "KSN:" + ksn);
        } catch (Exception e) {

        }
    }

    private void dukptEncryptAES() {
        try {
            int index = getKeyIndex();
            int keyType = DukptConstants.KeyUsageType.AES128;
            int keyUsage = DukptConstants.KeyUsageAes.DATA_ENCRYPTION_BOTH;
            int computeMode = DukptConstants.ComputeMode.ENCRYPTION;
            int workMode = DukptConstants.WorkMode.ECB;
            byte[] data = "1111111111111111".getBytes();
            byte[] iv = null;

            byte[] result = DeviceHelper.getDukpt().dukptCompute(index, keyType, keyUsage, computeMode, workMode, data, iv);
            mEncryptData = BytesUtil.bytes2HexString(result);
            DialogUtils.showAlertDialog(getActivity(), "result:" + mEncryptData);
        } catch (Exception e) {

        }
    }

    private void dukptDecryptAES() {
        try {
            int index = getKeyIndex();
            int keyType = DukptConstants.KeyUsageType.AES128;
            int keyUsage = DukptConstants.KeyUsageAes.DATA_ENCRYPTION_BOTH;
            int mode = DukptConstants.ComputeMode.DECRYPTION;
            int fillingMode = DukptConstants.WorkMode.ECB;
            byte[] data = BytesUtil.hexString2Bytes(mEncryptData);
            byte[] iv = null;

            byte[] result = DeviceHelper.getDukpt().dukptCompute(index, keyType, keyUsage, mode, fillingMode, data, iv);
            DialogUtils.showAlertDialog(getActivity(), "result:" + BytesUtil.bytes2HexString(result));
        } catch (Exception e) {

        }
    }

}
