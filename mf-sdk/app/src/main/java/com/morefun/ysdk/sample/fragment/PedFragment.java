package com.morefun.ysdk.sample.fragment;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.morefun.yapi.ServiceResult;
import com.morefun.yapi.device.ped.KeyType;
import com.morefun.yapi.device.ped.PedCipher;
import com.morefun.yapi.device.pinpad.DesMode;
import com.morefun.yapi.device.pinpad.OnPinPadInputListener;
import com.morefun.yapi.device.pinpad.PinAlgorithmMode;
import com.morefun.yapi.device.pinpad.PinPadConstrants;
import com.morefun.yapi.device.pinpad.PinPadType;
import com.morefun.ysdk.sample.R;
import com.morefun.ysdk.sample.device.DeviceHelper;
import com.morefun.ysdk.sample.utils.BytesUtil;
import com.morefun.ysdk.sample.utils.DialogUtils;
import com.morefun.ysdk.sample.utils.HexUtil;
import com.morefun.ysdk.sample.utils.RSAUtil;
import com.morefun.ysdk.sample.utils.ToastUtils;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PedFragment extends Fragment {
    @BindView(R.id.tv_tip)
    TextView tvTip;

    private final String TAG = PedFragment.class.getName();

    private final int RSA_PUBLIC_KEY_INDEX = 1;
    private final int RSA_PRIVATE_KEY_INDEX = 1;
    private final int TEK_KEY_INDEX = 1;
    private final int MAIN_KEY_INDEX = 1;
    private final int PIN_KEY_INDEX = 1;
    private final int MAC_KEY_INDEX = 0x09;
    private final int TDK_KEY_INDEX = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ped, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick({R.id.btn_login, R.id.btn_input_pin, R.id.btn_format, R.id.btn_loadTEK, R.id.btn_checkKey,
            R.id.btn_loadCipherMainKey, R.id.btn_loadClearMainKey, R.id.btn_loadWorkKey, R.id.btn_loadPlainWorkKey,
            R.id.btn_calcMac, R.id.btn_calcKcv, R.id.btn_des, R.id.btn_loadRSAKey, R.id.btn_readRSAKey, R.id.btn_rsaDecrypt})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                login();
                break;
            case R.id.btn_input_pin:
                inputPin();
                break;
            case R.id.btn_format:
                format();
                break;
            case R.id.btn_loadTEK:
                loadTEK();
                break;
            case R.id.btn_checkKey:
                checkKey();
                break;
            case R.id.btn_loadCipherMainKey:
                loadCipherMainKey();
                break;
            case R.id.btn_loadClearMainKey:
                loadClearMainKey();
                break;
            case R.id.btn_loadWorkKey:
                loadWorkKey();
                break;
            case R.id.btn_loadPlainWorkKey:
                loadPlainWorkKey();
                break;
            case R.id.btn_calcMac:
                calcMac();
                break;
            case R.id.btn_calcKcv:
                calcKcv();
                break;
            case R.id.btn_des:
                des();
                break;
            case R.id.btn_loadRSAKey:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        loadRSAKey();
                    }
                }).start();
                break;
            case R.id.btn_readRSAKey:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        readRSAKey();
                    }
                }).start();
            case R.id.btn_rsaDecrypt:
                rsaDecrypt();
                break;
            default:
                break;
        }
    }

    private void login() {
        Bundle bundle = new Bundle();
        try {
            String businessId = "01000000";

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

    private void inputPin() {
        byte[] panBlock = "1234567890123456".getBytes();
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
            DeviceHelper.getPinpad().inputOnlinePin(bundle, panBlock, PIN_KEY_INDEX, PinAlgorithmMode.ISO9564FMT1, new OnPinPadInputListener.Stub() {
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

    private void format() {
        try {
            boolean ret = DeviceHelper.getDeviceService().getPed().format();
            showResult(ret ? "Format Success" : "Format Fail!");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void loadTEK() {
        try {
            byte[] key = HexUtil.hexStringToByte("111111111111111111111111111111111111111111111111");
            byte[] checkValue = HexUtil.hexStringToByte("82E13665");

            int ret = DeviceHelper.getPed().loadTEK(TEK_KEY_INDEX, key, checkValue);
            showResult("loadKEK:" + ret);
        } catch (RemoteException e) {

        }
    }

    private void checkKey() {
        int keyType = KeyType.PIN_KEY;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    boolean mainKeyExist = DeviceHelper.getPed().isKeyExist(KeyType.MAIN_KEY, MAIN_KEY_INDEX);
                    boolean pinKeyExist = DeviceHelper.getPed().isKeyExist(KeyType.PIN_KEY, PIN_KEY_INDEX);
                    boolean macKeyExist = DeviceHelper.getPed().isKeyExist(KeyType.MAC_KEY, MAC_KEY_INDEX);
                    boolean tdkKeyExist = DeviceHelper.getPed().isKeyExist(KeyType.TDK_KEY, TDK_KEY_INDEX);

                    showResult("mainKeyExist:" + mainKeyExist);
                    showResult("pinKeyExist:" + pinKeyExist);
                    showResult("macKeyExist:" + macKeyExist);
                    showResult("tdkKeyExist:" + tdkKeyExist);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void loadCipherMainKey() {
        try {
            byte[] masterKey = HexUtil.hexStringToByte("F40379AB9E0EC533F40379AB9E0EC533F40379AB9E0EC533");
            byte[] checkValue = HexUtil.hexStringToByte("82E13665");

             int ret = DeviceHelper.getPed().loadEncryptMainKey(TEK_KEY_INDEX, MAIN_KEY_INDEX, masterKey, checkValue);
            showResult("loadEncryptMainKey:" + ret);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void loadClearMainKey() {
        try {
            byte[] key = HexUtil.hexStringToByte("111111111111111111111111111111111111111111111111");
            byte[] checkValue = HexUtil.hexStringToByte("82E13665");

            int ret = DeviceHelper.getPed().loadMainKey(MAIN_KEY_INDEX, key, checkValue);
            showResult("loadClearMainKey:" + ret);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void loadWorkKey() {
        try {
            byte[] workKey = HexUtil.hexStringToByte("9E90DE82745E68529E90DE82745E68529E90DE82745E6852");
            byte[] checkValue = HexUtil.hexStringToByte("6FB23EAD");

            int loadPinKey = DeviceHelper.getPed().loadWorkKey(KeyType.PIN_KEY, MAIN_KEY_INDEX, PIN_KEY_INDEX, workKey, checkValue);
            int loadMacKey = DeviceHelper.getPed().loadWorkKey(KeyType.MAC_KEY, MAIN_KEY_INDEX, MAC_KEY_INDEX, workKey, checkValue);
            int loadTdkKey = DeviceHelper.getPed().loadWorkKey(KeyType.TDK_KEY, MAIN_KEY_INDEX, TDK_KEY_INDEX, workKey, checkValue);

            showResult("loadPinKey:" + loadPinKey);
            showResult("loadMacKey:" + loadMacKey);
            showResult("loadTdkKey:" + loadTdkKey);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void loadPlainWorkKey() {
        try {
            byte[] workKey = HexUtil.hexStringToByte("111111111111111122222222222222223333333333333333");

            int loadPinKey = DeviceHelper.getPed().loadPlainWorkKey(KeyType.PIN_KEY, PIN_KEY_INDEX, workKey, null);
            int loadMacKey = DeviceHelper.getPed().loadPlainWorkKey(KeyType.MAC_KEY, MAC_KEY_INDEX, workKey, null);
            int loadTdkKey = DeviceHelper.getPed().loadPlainWorkKey(KeyType.TDK_KEY, TDK_KEY_INDEX, workKey, null);

            showResult("loadPinKey:" + loadPinKey);
            showResult("loadMacKey:" + loadMacKey);
            showResult("loadTdkKey:" + loadTdkKey);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void calcMac() {
        try {
            int keyId = MAC_KEY_INDEX;
            int format = PedCipher.MacFormat.SEC_MAC_X919_FORMAT;
            int desMode = PedCipher.DesMode.ECB;
            byte[] data = "12345678".getBytes();
            byte[] iv = new byte[8];
            byte[] mac = new byte[8];

            DeviceHelper.getPed().calcMAC(keyId, format, desMode, data, iv, mac);
            showResult("calcMac:" + BytesUtil.bytes2HexString(mac));
        } catch (Exception e) {

        }
    }

    private void calcKcv() {
        try {

            byte[] pinKcv = DeviceHelper.getPed().calcKCV(KeyType.PIN_KEY, PIN_KEY_INDEX);
            byte[] macKcv = DeviceHelper.getPed().calcKCV(KeyType.MAC_KEY, MAC_KEY_INDEX);
            byte[] tdkKcv = DeviceHelper.getPed().calcKCV(KeyType.TDK_KEY, TDK_KEY_INDEX);

            showResult("pinKcv:" + BytesUtil.bytes2HexString(pinKcv));
            showResult("macKcv:" + BytesUtil.bytes2HexString(macKcv));
            showResult("tdkKcv:" + BytesUtil.bytes2HexString(tdkKcv));
        } catch (Exception e) {

        }
    }

    private void des() {
        try {
            int keyType = KeyType.TDK_KEY;
            int keyId = TDK_KEY_INDEX;
            int desType = PedCipher.DesType.ENCRYPT;
            int desMode = PedCipher.DesMode.CBC;
            byte[] data = BytesUtil.hexString2Bytes("1111111111111111");
            byte[] iv = BytesUtil.hexString2Bytes("1111111111111111");
            byte[] out = new byte[data.length];

            int ret = DeviceHelper.getPed().calculateDes(keyType, keyId, desType, desMode, data, iv, out);
            showResult("result:" + BytesUtil.bytes2HexString(out));
        } catch (Exception e) {

        }
    }

    private void loadRSAKey() {
        try {
            KeyPair keyPair = RSAUtil.generateRSAKeyPair(2048);
            PublicKey publicKey = keyPair.getPublic();
            byte[] publicKeyEncoded = publicKey.getEncoded();

            PrivateKey privateKey = keyPair.getPrivate();
            byte[] privateKeyEncode = privateKey.getEncoded();

            if (DeviceHelper.getPed().loadRSAPublicKey(RSA_PUBLIC_KEY_INDEX, publicKeyEncoded)) {
                showResult("Write RSA public key success");
            } else {
                showResult("Write RSA public key fail");
            }

            if (DeviceHelper.getPed().loadRSAPrivateKey(RSA_PRIVATE_KEY_INDEX, privateKeyEncode)) {
                showResult("Write RSA private key success");
            } else {
                showResult("Write RSA private key fail");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void readRSAKey() {
        try {
            byte[] readPublicKey = DeviceHelper.getPed().readRSAPublicKey(RSA_PUBLIC_KEY_INDEX);

            if (readPublicKey != null) {
                showResult("publicKey:" + BytesUtil.bytes2HexString(readPublicKey));
                RSAPublicKey getPublicKey = (RSAPublicKey) RSAUtil.getPublicKey(readPublicKey);
                showResult("Modulus:" + getPublicKey.getModulus().toString(16));
                showResult("Exponent:" + getPublicKey.getPublicExponent().toString(16));
            } else {
                showResult("Read RSA public key fail");
            }

            byte[] readPrivateKey = DeviceHelper.getPed().readRSAPrivateKey(RSA_PRIVATE_KEY_INDEX);

            if (readPrivateKey != null) {
                showResult("privateKey:" + BytesUtil.bytes2HexString(readPrivateKey));
                PrivateKey getPrivateKey = RSAUtil.getPrivateKey(readPrivateKey);
            } else {
                showResult("Read RSA private key fail");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.fillInStackTrace();
        }
    }

    private void rsaDecrypt() {
        try {
            byte[] readPublicKey = DeviceHelper.getPed().readRSAPublicKey(RSA_PUBLIC_KEY_INDEX);
            RSAPublicKey getPublicKey = (RSAPublicKey) RSAUtil.getPublicKey(readPublicKey);

            byte[] readPrivateKey = DeviceHelper.getPed().readRSAPrivateKey(RSA_PRIVATE_KEY_INDEX);
            PrivateKey getPrivateKey = RSAUtil.getPrivateKey(readPrivateKey);

            String data = "12345678";
            byte[] encrypt = RSAUtil.encrypt(data.getBytes(), getPublicKey, 1);
            showResult("Encrypt data:" + data);
            showResult("After Encrypt:" + BytesUtil.bytes2HexString(encrypt));

            byte[] decrypt = RSAUtil.decrypt(encrypt, getPrivateKey, 1);

            showResult("After Decrypt:" + new String(decrypt));

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
