package com.morefun.ysdk.sample.fragment;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.morefun.yapi.ServiceResult;
import com.morefun.yapi.device.pinpad.CheckKeyEnum;
import com.morefun.yapi.device.pinpad.CheckKeyObj;
import com.morefun.yapi.device.pinpad.DesAlgorithmType;
import com.morefun.yapi.device.pinpad.DesCalcObj;
import com.morefun.yapi.device.pinpad.DesLoadObj;
import com.morefun.yapi.device.pinpad.MacAlgorithmType;
import com.morefun.yapi.device.pinpad.OnPinPadInputListener;
import com.morefun.yapi.device.pinpad.PinAlgorithmMode;
import com.morefun.yapi.device.pinpad.PinPadConstrants;
import com.morefun.yapi.device.pinpad.PinPadType;
import com.morefun.yapi.device.pinpad.TDesKeyObj;
import com.morefun.yapi.device.pinpad.WorkKeyType;
import com.morefun.ysdk.sample.R;
import com.morefun.ysdk.sample.device.DeviceHelper;
import com.morefun.ysdk.sample.utils.BytesUtil;
import com.morefun.ysdk.sample.utils.DialogUtils;
import com.morefun.ysdk.sample.utils.HexUtil;
import com.morefun.ysdk.sample.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.morefun.yapi.device.pinpad.TDesKeyObj.KeyTypeEnum.DES_WK_MAC;
import static com.morefun.yapi.device.pinpad.TDesKeyObj.OperEnum.DELETE_KEY;

public class MKSKFragment extends Fragment {
    @BindView(R.id.tv_tip)
    TextView tvTip;

    private static final String TAG = "MKSKFragment";
    private static final int KEK_KEY_INDEX = 0;
    private static final int MASTER_KEY_INDEX = 0;
    private static final int WORK_KEY_INDEX = 0;
    private byte[] mEncryptResult = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mksk, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick({R.id.btn_login, R.id.btn_loadKEK, R.id.btn_loadMasterKey, R.id.btn_loadClearMasterKey,
            R.id.btn_loadWorkKey, R.id.btn_loadClearWorkKey, R.id.btn_calcMac, R.id.btn_deleteKey, R.id.btn_encrypt,
            R.id.btn_decrypt, R.id.btn_checkKey, R.id.btn_onlinePin, R.id.btn_pin_block,
            R.id.btn_loadAesKey, R.id.btn_aesEncrypt, R.id.btn_aesDecrypt, R.id.btn_aesCMAC, R.id.btn_HMAC,R.id.btn_sm4Encrypt,R.id.btn_sm4Decrypt})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                login();
                break;
            case R.id.btn_loadKEK:
                loadKEK();
                break;
            case R.id.btn_loadMasterKey:
                loadMasterKey();
                break;
            case R.id.btn_loadClearMasterKey:
                loadClearMasterKey();
                break;
            case R.id.btn_loadWorkKey:
                loadWorkKey();
                break;
            case R.id.btn_loadClearWorkKey:
                loadClearWorkKey();
                break;
            case R.id.btn_calcMac:
                calcMac();
                break;
            case R.id.btn_deleteKey:
                deleteKey();
                break;
            case R.id.btn_encrypt:
                encrypt();
                break;
            case R.id.btn_decrypt:
                decrypt();
                break;
            case R.id.btn_checkKey:
                checkKey();
                break;
            case R.id.btn_onlinePin:
                inputPin();
                break;
            case R.id.btn_pin_block:
                pinBlock();
                break;
            case R.id.btn_loadAesKey:
                loadAesKey();
                break;
            case R.id.btn_aesEncrypt:
                aesEncrypt();
                break;
            case R.id.btn_aesDecrypt:
                aesDecrypt();
                break;
            case R.id.btn_aesCMAC:
                aesCMAC();
                break;
            case R.id.btn_HMAC:
                HMAC();
                break;
            case R.id.btn_sm4Encrypt:
                sm4Encrypt();
                break;
            case R.id.btn_sm4Decrypt:
                sm4Decrypt();
                break;
        }
    }

    private void sm4Encrypt(){
        try {
            byte[] data = BytesUtil.hexString2Bytes("00000000000000000000000000000000");
            byte[] key = BytesUtil.hexString2Bytes("18C44D369D6331B23E80817399CFF164");
            DesCalcObj desCalcObj = new DesCalcObj(DesCalcObj.DesModeEnum.ENCRYPT_SM4, data, key);
            byte[] result = DeviceHelper.getPinpad().desCalcByKey(desCalcObj);
            DialogUtils.showAlertDialog(getActivity(), "SM4 ENCRYPT:" + BytesUtil.bytes2HexString(result));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void sm4Decrypt(){
        try {
            byte[] data = BytesUtil.hexString2Bytes("DD65D6EC8C89BAFC8F69B9DBBF39C339");
            byte[] key = BytesUtil.hexString2Bytes("3db310f51c360a1d7ed33c7af90f4f61");
            DesCalcObj desCalcObj = new DesCalcObj(DesCalcObj.DesModeEnum.DECRYPT_SM4, data, key);
            byte[] result = DeviceHelper.getPinpad().desCalcByKey(desCalcObj);
            DialogUtils.showAlertDialog(getActivity(), "SM4 DECRYPT:" + BytesUtil.bytes2HexString(result));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void login() {
        Bundle bundle = new Bundle();
        try {
            String businessId = "00000000";

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

    private void loadKEK() {
        try {
            byte[] key = HexUtil.hexStringToByte("11111111111111111111111111111111");
            //Use key encrypt 0000000000000000
            byte[] checkValue = HexUtil.hexStringToByte("82E13665");

            boolean ret = DeviceHelper.getPinpad().loadKEK(KEK_KEY_INDEX, key, checkValue);
            if (ret) {
                DialogUtils.showAlertDialog(getActivity(), "KEK Load Success");
            } else {
                DialogUtils.showAlertDialog(getActivity(), "KEK Load Fail");
            }
        } catch (RemoteException e) {

        }
    }

    private void loadMasterKey() {
        try {
            byte[] masterKey = HexUtil.hexStringToByte("4B24C397E2D59A29A176FC37909A54E6");

            // Use KEK decrypt masterKey then encrypt 0000000000000000
            byte[] checkValue = HexUtil.hexStringToByte("64C4E1C6");

            boolean ret = DeviceHelper.getPinpad().loadCipherMKey(MASTER_KEY_INDEX, masterKey, checkValue, KEK_KEY_INDEX);
            if (ret) {
                DialogUtils.showAlertDialog(getActivity(), "Master Key Load Success");
            } else {
                DialogUtils.showAlertDialog(getActivity(), "Master Key Load Fail");
            }
        } catch (RemoteException e) {

        }
    }

    private void loadClearMasterKey() {
        try {
            byte[] masterKey = HexUtil.hexStringToByte("18C44D369D6331B23E80817399CFF164");
            int keyLen = masterKey.length;
            int retCode = DeviceHelper.getPinpad().loadPlainMKey(MASTER_KEY_INDEX, masterKey, keyLen, true);
            if (retCode == 0) {
                DialogUtils.showAlertDialog(getActivity(), "Master Key Load Success");
            } else {
                DialogUtils.showAlertDialog(getActivity(), "Master Key Load Fail");
            }
        } catch (RemoteException e) {

        }

    }

    private void loadWorkKey() {
        int pinKeyRet = -1;
        int macKeyRet = -1;

        //Pin
        try {
            byte[] pinKey = HexUtil.hexStringToByte("6FD766B7047D8F6070DDEF2A6B4067F11D7B6C57");
            pinKeyRet = DeviceHelper.getPinpad().loadWKey(WORK_KEY_INDEX, WorkKeyType.PINKEY, pinKey, pinKey.length);

            byte[] macKey = HexUtil.hexStringToByte("6FD766B7047D8F6070DDEF2A6B4067F11D7B6C57");
            macKeyRet = DeviceHelper.getPinpad().loadWKey(WORK_KEY_INDEX, WorkKeyType.MACKEY, macKey, macKey.length);

            if (pinKeyRet == 0 && macKeyRet == 0) {
                DialogUtils.showAlertDialog(getActivity(), "Work Key Load Success");
            } else {
                DialogUtils.showAlertDialog(getActivity(), "Work Key Load Fail");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadClearWorkKey() {
        int pinKeyRet = -1;
        int macKeyRet = -1;

        try {
            byte[] pinKey = HexUtil.hexStringToByte("33333333333333333333333333333333");
            pinKeyRet = DeviceHelper.getPinpad().loadPlainWKey(WORK_KEY_INDEX, WorkKeyType.PINKEY, pinKey, pinKey.length);

            byte[] macKey = HexUtil.hexStringToByte("070F74F70469F0D9070F74F70469F0D9");
            macKeyRet = DeviceHelper.getPinpad().loadPlainWKey(WORK_KEY_INDEX, WorkKeyType.MACKEY, macKey, macKey.length);

            if (pinKeyRet == 0 && macKeyRet == 0) {
                DialogUtils.showAlertDialog(getActivity(), "Work Key Load Success");
            } else {
                DialogUtils.showAlertDialog(getActivity(), "Work Key Load Fail");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void calcMac() {
        try {
            byte[] data = "12345678".getBytes();

            byte[] mac = DeviceHelper.getPinpad().getMac(WORK_KEY_INDEX, MacAlgorithmType.SM4_ECB, DesAlgorithmType.TDES, data, new Bundle());
            DialogUtils.showAlertDialog(getActivity(), "MAC:" + HexUtil.bytesToHexString(mac));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteKey() {
        try {
            TDesKeyObj tDesKeyObj = new TDesKeyObj(DES_WK_MAC, DELETE_KEY, WORK_KEY_INDEX);
            boolean retCode = DeviceHelper.getPinpad().checkKey(tDesKeyObj);
            if (retCode) {
                DialogUtils.showAlertDialog(getActivity(), "Delete Key Success");
            } else {
                DialogUtils.showAlertDialog(getActivity(), "Delete Key Fail");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void checkKey() {
        try {
            int keyIndex = 0;
            TDesKeyObj tDesKeyObj = new TDesKeyObj(TDesKeyObj.KeyTypeEnum.DES_MAIN_KEY, TDesKeyObj.OperEnum.EXITS_KEY, keyIndex);
            boolean ret = DeviceHelper.getPinpad().checkKey(tDesKeyObj);
            if (ret) {
                DialogUtils.showAlertDialog(getActivity(), "Key exist!");
            } else {
                DialogUtils.showAlertDialog(getActivity(), "The key does not exist!");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void encrypt() {
        try {
            byte[] data = HexUtil.hexStringToByte("11111111111111111111111111111111");
            DesCalcObj desCalcObj = new DesCalcObj(DesCalcObj.KeyTypeEnum.DES_WK_TD
                    , DesCalcObj.DesModeEnum.ENCRYPT, new int[]{1}, data, data.length);
            mEncryptResult = DeviceHelper.getPinpad().desCalcByKey(desCalcObj);

            DialogUtils.showAlertDialog(getActivity(), "ENCRYPT:" + BytesUtil.bytes2HexString(mEncryptResult));
        } catch (RemoteException e) {

        }
    }

    private void decrypt() {
        try {
            DesCalcObj desCalcObj = new DesCalcObj(DesCalcObj.KeyTypeEnum.DES_WK_TD
                    , DesCalcObj.DesModeEnum.DECRYPT, new int[]{1}, mEncryptResult, mEncryptResult.length);
            byte[] result = DeviceHelper.getPinpad().desCalcByKey(desCalcObj);
            DialogUtils.showAlertDialog(getActivity(), "DECRYPT:" + BytesUtil.bytes2HexString(result));
        } catch (Exception e) {
            e.printStackTrace();
            DialogUtils.showAlertDialog(getActivity(), e.getMessage());
        }
    }


    private void inputPin() {
        byte[] panBlock = "6214831082518233".getBytes();
        Bundle bundle = new Bundle();
        bundle.putBoolean(PinPadConstrants.COMMON_NEW_LAYOUT, false);
        bundle.putBoolean(PinPadConstrants.COMMON_SUPPORT_KEYVOICE, true);
        bundle.putBoolean(PinPadConstrants.COMMON_SUPPORT_BYPASS, false);
        bundle.putBoolean(PinPadConstrants.COMMON_IS_RANDOM, false);
        if (Build.MODEL.equals("MF960")) {
            bundle.putBoolean(PinPadConstrants.COMMON_IS_PHYSICAL_KEYBOARD, true);
        }

        if (Build.MODEL.equals("H9PRO")) {
            bundle.putBoolean(PinPadConstrants.COMMON_IS_PHYSICAL_KEYBOARD, true);
        }

        bundle.putIntArray(PinPadConstrants.NUMBER_TEXT_COLOR, new int[] {Color.BLACK, Color.BLACK, Color.BLACK,
                Color.BLACK, Color.BLACK, Color.BLACK,
                Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK});

        bundle.putString(PinPadConstrants.TITLE_HEAD_CONTENT, "Please input the online pin");

        try {
            int minLength = 4;
            int maxLength = 6;
            DeviceHelper.getPinpad().setTimeOut(10);
            DeviceHelper.getPinpad().setSupportPinLen(new int[]{minLength, maxLength});
            DeviceHelper.getPinpad().inputOnlinePin(bundle, panBlock, WORK_KEY_INDEX, PinAlgorithmMode.ISO9564FMT1, new OnPinPadInputListener.Stub() {
                @Override
                public void onInputResult(int ret, byte[] pinBlock, String ksn) throws RemoteException {
                    if (ret == ServiceResult.TimeOut) {
                        DialogUtils.showAlertDialog(getActivity(), "Timeout");
                    } else {
                        DialogUtils.showAlertDialog(getActivity(),
                                String.format("RESULT:%d\nPIN BLOCK:%s\n", ret, HexUtil.bytesToHexString(pinBlock)));
                    }
                }

                @Override
                public void onSendKey(byte keyCode) throws RemoteException {
                }

            });
        } catch (RemoteException e) {

        }

    }

    private void pinBlock() {
        try {
            int type = PinPadType.SEC_MKSK_FIELD;
            int pinFormat = PinPadType.SEC_PIN_FORMAT0;
            int keyId = WORK_KEY_INDEX;
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

    private void loadAesKey() {
        try {
            byte[] data = HexUtil.hexStringToByte("1111111111111111111111111111111111111111111111111111111111111111");
            DesLoadObj desLoadObj = new DesLoadObj(DesLoadObj.KeyTypeEnum.AES_AP_PLAINTEXT, new int[]{1}, data);
            int ret = DeviceHelper.getPinpad().desLoad(desLoadObj);
            DialogUtils.showAlertDialog(getActivity(), ret == 0 ? "Success" : "Fail");
        } catch (RemoteException e) {

        }
    }
    private void aesEncrypt() {
        try {
            byte[] data = BytesUtil.hexString2Bytes("11111111111111111111111111111111");
            DesCalcObj desCalcObj = new DesCalcObj(DesCalcObj.KeyTypeEnum.AES_AP_KEY,
                    DesCalcObj.DesModeEnum.AES_ENCRYPT, new int[]{1}, data, data.length);

            Bundle bundle = new Bundle();
            bundle.putString(DesCalcObj.KEY_TRANSFORMATION, "CBC");
            bundle.putByteArray(DesCalcObj.KEY_IV_PARAMETER_SPEC, new byte[16]);

            desCalcObj.setBundle(bundle);

            mEncryptResult = DeviceHelper.getPinpad().desCalcByKey(desCalcObj);

            DialogUtils.showAlertDialog(getActivity(), "ENCRYPT:" + BytesUtil.bytes2HexString(mEncryptResult));
        } catch (RemoteException e) {

        }
    }

    private void aesDecrypt() {
        try {
            DesCalcObj desCalcObj = new DesCalcObj(DesCalcObj.KeyTypeEnum.AES_AP_KEY,
                    DesCalcObj.DesModeEnum.AES_DECRYPT, new int[]{1}, mEncryptResult, mEncryptResult.length);

            Bundle bundle = new Bundle();
            bundle.putString(DesCalcObj.KEY_TRANSFORMATION, "CBC");
            bundle.putByteArray(DesCalcObj.KEY_IV_PARAMETER_SPEC, new byte[16]);

            desCalcObj.setBundle(bundle);

            byte[] result = DeviceHelper.getPinpad().desCalcByKey(desCalcObj);
            DialogUtils.showAlertDialog(getActivity(), "DECRYPT:" + BytesUtil.bytes2HexString(result));
        } catch (Exception e) {
            e.printStackTrace();
            DialogUtils.showAlertDialog(getActivity(), e.getMessage());
        }
    }

    private void aesCMAC() {
        try {
            byte[] key = HexUtil.hexStringToByte("0123456789ABCDEFFEDCBA9876543210");
            DesLoadObj desLoadObj = new DesLoadObj(DesLoadObj.KeyTypeEnum.AES_AP_PLAINTEXT, new int[]{2}, key);
            int ret = DeviceHelper.getPinpad().desLoad(desLoadObj);

            if (ret == 0) {
                byte[] data = BytesUtil.hexString2Bytes("4073870000477232");
                DesCalcObj desCalcObj = new DesCalcObj(DesCalcObj.KeyTypeEnum.AES_AP_KEY,
                        DesCalcObj.DesModeEnum.AES_CMAC, new int[]{2}, data, data.length);

                Bundle bundle = new Bundle();
                desCalcObj.setBundle(bundle);

                byte[] mac = DeviceHelper.getPinpad().desCalcByKey(desCalcObj);
                DialogUtils.showAlertDialog(getActivity(), "CMAC:" + BytesUtil.bytes2HexString(mac));
            }
        } catch (RemoteException e) {

        }
    }

    private void HMAC() {
        try {
            byte[] key = HexUtil.hexStringToByte("0123456789ABCDEFFEDCBA9876543210");
            DesLoadObj desLoadObj = new DesLoadObj(DesLoadObj.KeyTypeEnum.AES_AP_PLAINTEXT, new int[]{3}, key);
            int ret = DeviceHelper.getPinpad().desLoad(desLoadObj);

            if (ret == 0) {
                byte[] data = BytesUtil.hexString2Bytes("4073870000477232");
                DesCalcObj desCalcObj = new DesCalcObj(DesCalcObj.KeyTypeEnum.AES_AP_KEY,
                        DesCalcObj.DesModeEnum.AES_HMAC, new int[]{3}, data, data.length);

                Bundle bundle = new Bundle();
                desCalcObj.setBundle(bundle);

                byte[] mac = DeviceHelper.getPinpad().desCalcByKey(desCalcObj);
                DialogUtils.showAlertDialog(getActivity(), "HMAC:" + BytesUtil.bytes2HexString(mac));
            }
        } catch (RemoteException e) {

        }
    }


}
