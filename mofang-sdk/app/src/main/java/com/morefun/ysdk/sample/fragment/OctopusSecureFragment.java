package com.morefun.ysdk.sample.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.morefun.ysdk.sample.R;
import com.morefun.ysdk.sample.device.DeviceHelper;
import com.morefun.ysdk.sample.utils.BytesUtil;
import com.morefun.ysdk.sample.utils.DialogUtils;
import com.morefun.ysdk.sample.utils.ToastUtils;

import Decoder.BASE64Decoder;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OctopusSecureFragment extends Fragment {

    @BindView(R.id.et_position)
    EditText et_position;

    @BindView(R.id.et_key)
    EditText et_key;

    private final String KEY_PAIR_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2hoWY6d46j5xpV09k0AOgEbzf6DsoUXrZu8eSMRySqMSdNCDFygD0SXD2BV/lXCo1r2TeS0zLuDd3/VoH5cU06zBpOdvu208ggjCebqgJDLtWA4RLhOdyyo0bFo4w4h9nJnX/vzA/dJd0ZiTxyb6YT1XKsRnPBHStzyeeX2DNXdY8odh/vB/Ql70xs4XGT3n3fEkVPVMob6N917XT3GoltDiuu+CHku2x02NkAZ6tMxlA5d/CagWiRonwlGUTe6wveb9W9r+UjM1SZGKnZQWhuO7xJZJ5IPJXgZgHk0thyvb8RsY1CTRURNUKzBNbuyqAVHkxtUf7+B+89MPKidKrwIDAQAB";
    private final String KEY_PAIR_PRIVATE_KEY = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQDaGhZjp3jqPnGlXT2TQA6ARvN/oOyhRetm7x5IxHJKoxJ00IMXKAPRJcPYFX+VcKjWvZN5LTMu4N3f9WgflxTTrMGk52+7bTyCCMJ5uqAkMu1YDhEuE53LKjRsWjjDiH2cmdf+/MD90l3RmJPHJvphPVcqxGc8EdK3PJ55fYM1d1jyh2H+8H9CXvTGzhcZPefd8SRU9Uyhvo33XtdPcaiW0OK674IeS7bHTY2QBnq0zGUDl38JqBaJGifCUZRN7rC95v1b2v5SMzVJkYqdlBaG47vElknkg8leBmAeTS2HK9vxGxjUJNFRE1QrME1u7KoBUeTG1R/v4H7z0w8qJ0qvAgMBAAECggEAOXkiaURakq+91uvJLCJ3L8qCpCFN+fmo3MkqW9aYn551YJ8JzHJY/yZKg4i2xDSZ6WyvjgEhObs7/KZ4t8piTJ17se7i/Kr+hO22AcnyJVfeP5QFZvT4Ye9PPTZROwWexEOuLZ2BZeGno3Hnj1VwKWu2qqhy49t2gZ7RzAYNK2s5KSs+YnIvlYh/q/qSLGHc1ZnUosqEMMEf4O0X80PLW3GxwIQbUiBfGyGqZwqfX8hO0z1sAieojGc1b0cFV6ncJsI3Le5CkbUbLptJ56og9QcAXLUV2rfrzvkopEEGJkGpg3FYsdJOW436TfDnY2R8EfkdgQuNl9xRp1b0B2hf4QKBgQD5MgVT2HCsY/QpdiIGfnQJCSV+aZ3Pnkyb3LzMN7f+ZMSTdt/DLsGKXOOU61LnqphPptL3AAqFlOWCMWQ5XGNHpM5dy1QLnjYPd3yqzVktYXcFrXKfWr3Vq1RCBRSphuO6ksFq4UclcwCuSxAfi8dCFIIvj/Y/jGNMqez2S9rF5wKBgQDgDrXTPy/G5UtDBZK+hzic4AWTDgLr0HZ46D+pjKftRI/OF3mOwZd/DHOsNBZ4St4odivckRsy6dU3VQiuLnsEw556vbeXTXIIJgEuZJUHGhmmaVupHjfRQ81tj4QyqTZBufnMvAEn6WSYAIMAfmDqPtsoxm8wB1GIrzj4Igkr+QKBgAVVr82Cta7LGQAGOmY/MMa8vmO9aRZ3whYYHKaWjsbQa3Dou5OXHaeTo3+dphG1kjsqTBvjkVYYwO0Pl6CuvKVc/tn9L1hsU6XzHeUeLkmNa5ngg7Kro6K+XzhmmXGjpJ5q498g4YAIvTv9+WNjf6mHfN39y/zCSaHrhVokd5ODAoGAEqSGsn4Gbqkzeu8ix6Ger0bgj9zRl2dwBB1m9qeSbWaQBJjlb2BiR5r0oPZwjC/Gjl1Oxvp35eOY7xKvNzb822efbx0MApXaEB2BaafK0p10VulbTBYMlOfsVtpjKIf10MA1rbhS7Ew6J3+bZtYdIEB8ocbpB3kEargRqGvEkZkCgYAZxyfrz8VqhTYgiC2d069JceSg/B77CooAMfzIrHpl86TDxNdWYIdB5F6P6kF3tUIuTAmuyYwZW+IM8UKSZcqUzpiWf3+C9rNTr9/XbJfMWsOB9FPlgd4xFTK4lP1wyU/q7BSt8Alq+D6ayURfcTnvVjjJaV+CE6BDMkpFRl3/IA==";
    private final String encryptStr = "C1D0F8FB4958670DBA40AB1F3752EF0DC1D0F8FB4958670DBA40AB1F3752EF0D";
    private final int KT_DES = 1;
    private final int KT_T_DES = 2;
    private final int ENCRYPT = 1;
    private final int DECRYPT = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_octopus_secure, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick({R.id.btn_read})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_read:
                read();
                break;
        }
    }

    private void read() {
        try {
            int position = Integer.parseInt(et_position.getText().toString());
            String key = et_key.getText().toString();

            //表明身份
            byte[] rdmEn = DeviceHelper.getHsm().encryptPubKey(new BASE64Decoder().decodeBuffer(KEY_PAIR_PUBLIC_KEY),
                    DeviceHelper.getSecureArea().OclsecureRandom());
            //更新随机数，注入密钥
            boolean injectRst = DeviceHelper.getSecureArea().loadOclEncryptWkey(position, rdmEn,
                    BytesUtil.hexString2Bytes(key), KT_T_DES, 0);
            //更新随机数，加密数据
            rdmEn = DeviceHelper.getHsm().encryptPubKey(new BASE64Decoder().decodeBuffer(KEY_PAIR_PUBLIC_KEY),
                    DeviceHelper.getSecureArea().OclsecureRandom());
            byte[] dataEn = DeviceHelper.getSecureArea().OclEncByWkey(position, rdmEn,
                    BytesUtil.hexString2Bytes(encryptStr), KT_T_DES, ENCRYPT);
            //更新随机数，解密数据
            rdmEn = DeviceHelper.getHsm().encryptPubKey(new BASE64Decoder().decodeBuffer(KEY_PAIR_PUBLIC_KEY),
                    DeviceHelper.getSecureArea().OclsecureRandom());

            byte[] dataDec = DeviceHelper.getSecureArea().OclEncByWkey(position, rdmEn, dataEn, KT_T_DES, DECRYPT);
            String decData = BytesUtil.bytes2HexString(dataDec);

            if (encryptStr.equals(decData)) {
                DialogUtils.showAlertDialog(getActivity(), "Success");
            } else {
                DialogUtils.showAlertDialog(getActivity(), "Fail");
            }
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.show(getContext(), e.getMessage());
        }
    }

}
