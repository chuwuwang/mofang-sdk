package com.morefun.ysdk.sample.fragment;

import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.morefun.yapi.ServiceResult;
import com.morefun.yapi.card.mifare.M0CHandler;
import com.morefun.yapi.card.mifare.M0Ev1Handler;
import com.morefun.yapi.device.reader.icc.ICCSearchResult;
import com.morefun.yapi.device.reader.icc.IccCardReader;
import com.morefun.yapi.device.reader.icc.IccCardType;
import com.morefun.yapi.device.reader.icc.IccReaderSlot;
import com.morefun.yapi.device.reader.icc.OnSearchIccCardListener;
import com.morefun.ysdk.sample.R;
import com.morefun.ysdk.sample.device.DeviceHelper;
import com.morefun.ysdk.sample.utils.BytesUtil;
import com.morefun.ysdk.sample.utils.Cipher;
import com.morefun.ysdk.sample.utils.DialogUtils;
import com.morefun.ysdk.sample.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.morefun.ysdk.sample.utils.BytesUtil.bytes2HexString;
import static com.morefun.ysdk.sample.utils.BytesUtil.insertLast2First;

public class M0CardFragment extends Fragment {
    @BindView(R.id.tv_tip)
    TextView tvTip;

    private final String TAG = M1CardFragment.class.getName();
    private IccCardReader rfReader;
    private M0Ev1Handler m0Ev1Handler;
    private M0CHandler m0CHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_m0_card, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick({R.id.btn_m0CCard, R.id.btn_m0Ev1Card})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_m0CCard:
                DialogUtils.showProgressDialog(getActivity(), getString(R.string.tip_tap_card));
                m0CCard(new String[]{IccCardType.M0CCARD});
                break;
            case R.id.btn_m0Ev1Card:
                DialogUtils.showProgressDialog(getActivity(), getString(R.string.tip_tap_card));
                m0Ev1Card(new String[]{IccCardType.M0Ev1CARD});
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            rfReader.stopSearch();
            m0Ev1Handler.close();
            m0CHandler.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void m0Ev1Card(final String[] cardType) {
        try {
            rfReader = DeviceHelper.getIccCardReader(IccReaderSlot.RFSlOT);
            OnSearchIccCardListener.Stub listener = new OnSearchIccCardListener.Stub() {
                @Override
                public void onSearchResult(int retCode, Bundle bundle) throws RemoteException {
                    rfReader.stopSearch();
                    DialogUtils.setProgressMessage(getActivity(), "Exchange...");
                    if (ServiceResult.Success == retCode) {
                        String cardType = bundle.getString(ICCSearchResult.CARDTYPE);
                        if (IccCardType.M0Ev1CARD.equals(cardType)) {
                            int slot = bundle.getInt(ICCSearchResult.CARDOTHER);
                            m0Ev1Handler = DeviceHelper.getM0Ev1CardHandler(null);
                            boolean openRst = m0Ev1Handler.open();

                            showResult("Open EV1Card Result:" + openRst);

                            byte[] rst = new byte[16];
                            m0Ev1Handler.getVersion(BytesUtil.hexString2Bytes("03000000"), rst);
                            String s = BytesUtil.bytes2HexString(rst);
                            String version = s.substring(Math.max(0, s.length() - 4));

                            showResult("Get Version:" + version);

                            byte[] rst40 = new byte[40];
                            m0Ev1Handler.readSign(BytesUtil.hexString2Bytes("0B000000"), rst40);
                            String s1 = BytesUtil.bytes2HexString(rst40);
                            showResult(s1);

                            byte[] bytes = new byte[4];
                            byte[] readRst = new byte[24];
                            for (int i = 0; i <= ("0E03".equalsIgnoreCase(version) ? 40 : "0B03".equalsIgnoreCase(version) ? 19 : 0); i++) {
                                BytesUtil.int2bytes(i, bytes, 0);
                                m0Ev1Handler.read(BytesUtil.hexString2Bytes("04000000" + BytesUtil.bytes2HexString(bytes)), readRst);
                                String outPUt = "page " + i + " " + " page byte " + BytesUtil.bytes2HexString(bytes) + " " + BytesUtil.bytes2HexString(readRst);
                                showResult(outPUt);
                                Log.i("M0ev1-read", outPUt);
                            }
                            boolean closeRst = m0Ev1Handler.close();
                            showResult("Close EV1Card:" + closeRst);
                        }
                        DialogUtils.dismissProgressDialog(getActivity());
                    } else {
                        DialogUtils.dismissProgressDialog(getActivity());
                        DialogUtils.showAlertDialog(getActivity(), "Search Card Fail!");
                    }
                }
            };
            rfReader.searchCard(listener, 10, cardType);
        } catch (RemoteException e) {
            e.printStackTrace();
            showResult(e.toString());
        }
    }

    /**
     * auth1out-08000000FFFFFFFF1438393933353500
     * 1438393933353500
     * rbEncrypt-1438393933353500
     * rb-467B5D0ECCEEE674
     * rbf2l-7B5D0ECCEEE67446
     * ra+rbf2l-A8AF3B256C75ED407B5D0ECCEEE67446
     * IV-1438393933353500
     * asAuth2InputStr-949878012703E22ECAA9F6925F14E2F5
     * auth2-09000000949878012703E22ECAA9F6925F14E2F5
     * auth2Out-09000000FFFFFFFF1438393933353500
     * auth2Out8-1438393933353500
     * IVCard-CAA9F6925F14E2F5
     * randomAF2L-8CD2AB9C93FA0481
     * randomAoriginal-818CD2AB9C93FA04
     *
     * @param cardType
     */
    private void m0CCard(final String[] cardType) {
        try {
            rfReader = DeviceHelper.getIccCardReader(IccReaderSlot.RFSlOT);
            OnSearchIccCardListener.Stub listener = new OnSearchIccCardListener.Stub() {
                @Override
                public void onSearchResult(int retCode, Bundle bundle) throws RemoteException {
                    rfReader.stopSearch();
                    if (ServiceResult.Success == retCode) {
                        try {
                            DialogUtils.setProgressMessage(getActivity(), "Exchange...");
                            final String CMD_READ = "04000000";

                            String cardType = bundle.getString(ICCSearchResult.CARDTYPE);
                            if (IccCardType.M0CCARD.equals(cardType)) {
                                m0CHandler = DeviceHelper.getM0CCardHandler(null);
                                boolean openRst = m0CHandler.open();
                                showResult("Open M0CCard:" + openRst);

                                for (int i = 0; i < 21; i++) {
                                    byte[] pageIdx = new byte[4];
                                    byte[] rst2 = new byte[24];
                                    BytesUtil.int2bytes(i, pageIdx, 0);
                                    m0CHandler.read(BytesUtil.hexString2Bytes(CMD_READ + bytes2HexString(pageIdx)), rst2);
                                    String outPUt = "page " + i + " " + " page byte " + bytes2HexString(pageIdx) + " " + bytes2HexString(rst2);
                                    showResult(outPUt);
                                }
                                //auth1auth2
                                byte[] KEY_DOUBLE = BytesUtil.hexString2Bytes("49454D4B41455242214E4143554F5946");
                                byte randomA[] = BytesUtil.hexString2Bytes("A8AF3B256C75ED40");
                                String AUTH1_CMD = "08000000";
                                String AUTH2_CMD = "09000000";
                                byte[] auth1Out = new byte[16];
                                byte[] auth1cmd = BytesUtil.hexString2Bytes(AUTH1_CMD);

                                m0CHandler.auth1(auth1cmd, auth1Out);
                                Log.i(TAG, "Auth1:" + bytes2HexString(auth1Out));

                                byte[] randomBEncrypt = new byte[8];
                                System.arraycopy(auth1Out, 8, randomBEncrypt, 0, 8);

                                byte[] IV = new byte[8];
                                System.arraycopy(randomBEncrypt, 0, IV, 0, 8);

                                Log.i(TAG, bytes2HexString(randomBEncrypt));//获取加密后的随机数

                                byte[] randomB = new Cipher().setAlgorithm(Cipher.Algorithm.DES_Triple)
                                        .setMode(Cipher.Mode.CBC)
                                        .setKey(KEY_DOUBLE)
                                        .setPadding(Cipher.Padding.NoPadding)
                                        .setData(randomBEncrypt)
                                        .deCrypt();

                                byte[] randomBFirstB2Last = BytesUtil.insertFirst2Last(randomB);//第一个字节放到最后一个

                                String randomArandomBFirst2Last = bytes2HexString(randomA) + bytes2HexString(randomBFirstB2Last);
                                Log.i(TAG, "rbEncrypt-" + bytes2HexString(randomBEncrypt));
                                Log.i(TAG, "rb-" + bytes2HexString(randomB));
                                Log.i(TAG, "rbf2l-" + bytes2HexString(randomBFirstB2Last));
                                Log.i(TAG, "ra+rbf2l-" + randomArandomBFirst2Last);
                                Log.i(TAG, "IV-" + bytes2HexString(IV));
                                byte[] asAuth2Input = new Cipher()
                                        .setAlgorithm(Cipher.Algorithm.DES_Triple)
                                        .setMode(Cipher.Mode.CBC)
                                        .setKey(KEY_DOUBLE)
                                        .setPadding(Cipher.Padding.NoPadding)
                                        .setData(BytesUtil.hexString2Bytes(randomArandomBFirst2Last))
                                        .setIv(IV)
                                        .encrypt();

                                String asAuth2InputStr = bytes2HexString(asAuth2Input);
                                byte[] IVCard = new byte[8];
                                System.arraycopy(asAuth2Input, 8, IVCard, 0, 8);

                                Log.i(TAG, "asAuth2InputStr-" + asAuth2InputStr);
                                byte[] auth2 = BytesUtil.hexString2Bytes(AUTH2_CMD + asAuth2InputStr);
                                byte[] auth2Out = new byte[16];
                                m0CHandler.auth2(auth2, auth2Out);

                                byte[] auth2Out8 = new byte[8];
                                System.arraycopy(auth2Out, 8, auth2Out8, 0, 8);

                                byte[] randomAF2L = new Cipher().setAlgorithm(Cipher.Algorithm.DES_Triple)
                                        .setMode(Cipher.Mode.CBC)
                                        .setKey(KEY_DOUBLE)
                                        .setData(auth2Out8)
                                        .setIv(IVCard)
                                        .setPadding(Cipher.Padding.NoPadding)
                                        .deCrypt();

                                byte[] randomAOriginal = insertLast2First(randomAF2L);

                                Log.i(TAG, "Auth2:" + bytes2HexString(auth2));
                                Log.i(TAG, "Auth2 Out:" + bytes2HexString(auth2Out));
                                Log.i(TAG, "Auth2 Out8:" + bytes2HexString(auth2Out8));
                                Log.i(TAG, "IVCard:" + bytes2HexString(IVCard));
                                Log.i(TAG, "RandomAF2L:" + bytes2HexString(randomAF2L));
                                Log.i(TAG, "RandomA Original:" + bytes2HexString(randomAOriginal));

                                if (bytes2HexString(randomA).equals(bytes2HexString(randomAOriginal))) {
                                    showResult("Auth1 Auth2 Success");
                                } else {
                                    showResult("Auth1 Auth2 Fail");
                                }
                                boolean closeRst = m0CHandler.close();
                                showResult("M0CCard Close:" + closeRst);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            DialogUtils.dismissProgressDialog(getActivity());
                        }
                    } else {
                        DialogUtils.dismissProgressDialog(getActivity());
                        DialogUtils.showAlertDialog(getActivity(), "Search Card Fail!");
                    }
                }
            };
            rfReader.searchCard(listener, 10, cardType);
        } catch (RemoteException e) {
            e.printStackTrace();
            showResult(e.toString());
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
