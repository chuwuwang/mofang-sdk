package com.morefun.ysdk.sample.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.morefun.yapi.ServiceResult;
import com.morefun.yapi.device.pinpad.DispTextMode;
import com.morefun.yapi.device.pinpad.OnPinPadInputListener;
import com.morefun.yapi.device.pinpad.PinAlgorithmMode;
import com.morefun.yapi.device.pinpad.PinPadConstrants;
import com.morefun.yapi.device.reader.icc.ICCSearchResult;
import com.morefun.yapi.device.reader.icc.IccCardReader;
import com.morefun.yapi.device.reader.icc.IccCardType;
import com.morefun.yapi.device.reader.icc.IccReaderSlot;
import com.morefun.yapi.device.reader.icc.OnSearchIccCardListener;
import com.morefun.yapi.device.reader.mag.MagCardInfoEntity;
import com.morefun.yapi.device.reader.mag.MagCardReader;
import com.morefun.yapi.device.reader.mag.OnSearchMagCardListener;
import com.morefun.yapi.emv.EmvErrorCode;
import com.morefun.yapi.emv.EmvErrorConstrants;
import com.morefun.yapi.emv.EmvListenerConstrants;
import com.morefun.yapi.emv.EmvOnlineRequest;
import com.morefun.yapi.emv.EmvOnlineResult;
import com.morefun.yapi.emv.EmvProcessResult;
import com.morefun.yapi.emv.EmvRupayCallback;
import com.morefun.yapi.emv.GoToConstants;
import com.morefun.yapi.emv.OnEmvProcessListener;
import com.morefun.ysdk.sample.R;
import com.morefun.ysdk.sample.device.DeviceHelper;
import com.morefun.ysdk.sample.listener.OnInputPinListener;
import com.morefun.ysdk.sample.utils.CardUtil;
import com.morefun.ysdk.sample.utils.DialogUtils;
import com.morefun.ysdk.sample.utils.EmvUtil;
import com.morefun.ysdk.sample.utils.HexUtil;
import com.morefun.ysdk.sample.utils.TlvDataList;
import com.morefun.ysdk.sample.utils.ToastUtils;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EMVFragment extends Fragment {
    @BindView(R.id.tv_tip)
    TextView tvTip;

    @BindView(R.id.et_amount)
    EditText et_amount;

    @BindView(R.id.btn_emvStart)
    Button btnEmvStart;

    private final String TAG = EMVFragment.class.getName();
    private IccCardReader iccCardReader;
    private IccCardReader rfReader;
    private MagCardReader magCardReader;
    private long startTick;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_emv, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick({R.id.btn_emvStart, R.id.btn_cardExist})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_emvStart:
                try {
                    btnEmvStart.setEnabled(false);
                    startEMV();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_cardExist:
                isCardExist();
                break;
        }
    }

    @Override
    public void onPause() {
        endEMV();
        Log.d(TAG, "onPause>>>>>");
        super.onPause();
    }

    private void isCardExist() {
        try {
            iccCardReader = DeviceHelper.getIccCardReader(IccReaderSlot.ICSlOT1);
            rfReader = DeviceHelper.getIccCardReader(IccReaderSlot.RFSlOT);

            boolean icCardExist = iccCardReader.isCardExists();
            boolean rfCardExist = rfReader.isCardExists();

            DialogUtils.showAlertDialog(getActivity(), String.format("IC CARD EXIST:%b\nRF CARD EXIST:%b", icCardExist, rfCardExist));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void startEMV() throws Exception {
        String amount = et_amount.getText().toString();
        if (TextUtils.isEmpty(amount)) {
            ToastUtils.show(getContext(), "Please Input Amount");
            return;
        }
        clearResult();

        DeviceHelper.getEmvHandler().initTermConfig(EmvUtil.getInitTermConfig());

        iccCardReader = DeviceHelper.getIccCardReader(IccReaderSlot.ICSlOT1);
        rfReader = DeviceHelper.getIccCardReader(IccReaderSlot.RFSlOT);
        magCardReader = DeviceHelper.getMagCardReader();

        DialogUtils.showProgressDialog(getActivity(), getString(R.string.tip_dip_tap_card));
        magCardReader.searchCard(new OnSearchMagCardListener.Stub() {
            @Override
            public void onSearchResult(int retCode, MagCardInfoEntity magCardInfoEntity) throws RemoteException {
                showResult("MAG CARD:" + retCode);
                DialogUtils.dismissProgressDialog(getActivity());
                if (retCode == ServiceResult.Success) {
                    StringBuilder builder = new StringBuilder();

                    builder.append("PAN:" + magCardInfoEntity.getCardNo());
                    builder.append("\nTRACK1:" + magCardInfoEntity.getTk1());
                    builder.append("\nTRACK2:" + magCardInfoEntity.getTk2());
                    builder.append("\nTRACK3:" + magCardInfoEntity.getTk3());
                    builder.append("\nKSN: " + magCardInfoEntity.getKsn());
                    builder.append("\nSERVICE CODE: " + magCardInfoEntity.getServiceCode());

                    showResult(builder.toString());
                }
                stopSearch();
                endEMV();
            }
        }, 60, new Bundle());

        emvTrans(amount);
    }

    private void emvTrans(String amount) throws RemoteException {
        showResult("START EMV TRANS");
        int ret = DeviceHelper.getEmvHandler().emvTrans(EmvUtil.getTransBundle(amount), new OnEmvProcessListener.Stub() {

            @Override
            public void onSelApp(List<String> appNameList, boolean isFirstSelect) throws RemoteException {
                DialogUtils.dismissProgressDialog(getActivity());
                showResult("ON SEL APP");
                selApp(appNameList);
            }

            @Override
            public void onConfirmCardNo(String cardNo) throws RemoteException {
                DialogUtils.dismissProgressDialog(getActivity());

                showResult("CONFIRM CARD:" + cardNo);
                showResult("TIME:" + (System.currentTimeMillis() - startTick) + "ms");

                DialogUtils.showAlertDialog(getActivity(), cardNo, new DialogUtils.OnClickListener() {
                    @Override
                    public void onConfirm() {
                        try {
                            DeviceHelper.getEmvHandler().onSetConfirmCardNoResponse(true);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancel() {
                        try {
                            DeviceHelper.getEmvHandler().onSetConfirmCardNoResponse(false);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            /**
             *
             * @param isOnlinePin
             * @param offlinePinType  3:offline pin normal 2:offline pin again 1:offline pin last
             * @throws RemoteException
             */
            @Override
            public void onCardHolderInputPin(boolean isOnlinePin, int offlinePinType) throws RemoteException {
                showResult("IS ONLINE PIN:" + isOnlinePin);
                showResult("TIME:" + (System.currentTimeMillis() - startTick) + "ms");
                showResult("9F34:" + EmvUtil.getPbocData("9F34", true));
                DialogUtils.dismissProgressDialog(getActivity());
                String cardNo = EmvUtil.readPan();

                if (isOnlinePin) {
                    inputOnlinePin(cardNo, pinBlock -> {
                        try {
                            DeviceHelper.getEmvHandler().onSetCardHolderInputPin(pinBlock);
                        } catch (RemoteException e) {

                        }
                    });
                } else {
                    inputOfflinePin(cardNo, pinBlock -> {
                        try {
                            DeviceHelper.getEmvHandler().onSetCardHolderInputPin(pinBlock);
                        } catch (RemoteException e) {

                        }
                    });
                }
            }

            @Override
            public void onPinPress(byte keyCode) throws RemoteException {
            }

            @Override
            public void onDisplayOfflinePin(int retCode) throws RemoteException {
                showResult("DISPLAY OFFLINE PIN:" + retCode);
            }

            @Override
            public void inputAmount(int type) throws RemoteException {
                showResult("INPUT AMOUNT");
                try {
                    DeviceHelper.getEmvHandler().onSetInputAmountResponse("0.3");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onGetCardResult(int retCode, Bundle bundle) throws RemoteException {
                DialogUtils.dismissProgressDialog(getActivity());

                if (retCode == ServiceResult.Success) {
                    startTick = System.currentTimeMillis();
                    showResult("ON GET CARD RESULT:" + retCode);
                    // 7: TAP card 1:DIP card
                    showResult("CARD TYPE:" + (bundle.getInt(ICCSearchResult.CARDOTHER)));
                } else {
                    showResult("READ CARD FAIL: " + retCode);
                    endEMV();
                }
            }

            @Override
            public void onDisplayMessage() throws RemoteException {
                showResult("ON DISPLAY MESSAGE");
                String aid = EmvUtil.getPbocData("4F", true);

                DialogUtils.showAlertDialog(getActivity(), aid, new DialogUtils.OnClickListener() {
                    @Override
                    public void onConfirm() {
                        try {
                            DeviceHelper.getEmvHandler().onSetConfirmDisplayMessage(0);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancel() {
                        try {
                            DeviceHelper.getEmvHandler().onSetConfirmDisplayMessage(0);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onUpdateServiceAmount(String serviceRelatedData) throws RemoteException {

            }

            @Override
            public void onCheckServiceBlackList(String pan, String amount) throws RemoteException {

            }

            @Override
            public void onGetServiceDirectory(byte[] directory) throws RemoteException {
                DeviceHelper.getEmvHandler().onGetServiceDirectory(0);
            }

            @Override
            public void onRupayCallback(int type, Bundle bundle) throws RemoteException {
                byte[] data = bundle.getByteArray(EmvRupayCallback.RUPAY_DATA_OUT);

                Bundle ret = new Bundle();

                ret.putInt(EmvRupayCallback.KEY_RET_CODE, 0);
                DeviceHelper.getEmvHandler().onSetRupayCallback(type, ret);
            }

            @Override
            public void onOnlineProc(Bundle data) throws RemoteException {
                showResult("GO ONLINE");
                showResult("CVM FLAG:" + data.getInt(EmvOnlineRequest.CVM_FLAG));
                showResult("TIME:" + (System.currentTimeMillis() - startTick) + "ms");

                onlineProc();
            }

            @Override
            public void onContactlessOnlinePlaceCardMode(int mode) throws RemoteException {
                showResult("ON CONTRACT LESS ONLINE PLACE CARD MODE");
                if (mode == EmvListenerConstrants.NEED_CHECK_CONTACTLESS_CARD_AGAIN) {

                    startSearchContractLess(new OnSearchIccCardListener.Stub() {
                        @Override
                        public void onSearchResult(int retCode, Bundle bundle) throws RemoteException {
                            stopSearch();
                            try {
                                DeviceHelper.getEmvHandler().onSetContactlessOnlinePlaceCardModeResponse(ServiceResult.Success == retCode);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    //show Dialog Prompt the user not to remove the card
                    DeviceHelper.getEmvHandler().onSetContactlessOnlinePlaceCardModeResponse(true);
                }
            }

            @Override
            public void onFinish(int retCode, Bundle data) throws RemoteException {
                showResult("ON FINISH");
                showResult("TIME:" + (System.currentTimeMillis() - startTick) + "ms");
                showResult("CVM FLAG:" + data.getInt(EmvOnlineRequest.CVM_FLAG));
                showResult("CVM SIGNATURE:" + data.getBoolean(EmvOnlineRequest.CVM_SIGNATURE));
                emvFinish(retCode, data);
            }

            @Override
            public void onCertVerify(String certName, String certInfo) throws RemoteException {
                showResult("ON CERT VERIFY");
                DeviceHelper.getEmvHandler().onSetCertVerifyResponse(true);
            }

            @Override
            public void onSetAIDParameter(String aid) throws RemoteException {
                showResult("ON SET AID:" + aid);
            }

            @Override
            public void onSetCAPubkey(String rid, int index, int algMode) throws RemoteException {
                showResult("ON SET CAPK");
            }

            @Override
            public void onTRiskManage(String pan, String panSn) throws RemoteException {
                showResult("ON TRISK MANAGE");
            }

            @Override
            public void onSelectLanguage(String language) throws RemoteException {
                showResult("ON SELECT LANGUAGE");
            }

            @Override
            public void onSelectAccountType(List<String> accountTypes) throws RemoteException {
                showResult("ON SELECT ACCOUNT TYPE");
            }

            @Override
            public void onIssuerVoiceReference(String pan) throws RemoteException {
                showResult("ON ISSUER VOICE REFERENCE");
            }

        });

        if (ret != 0) {
            showResult("EMV INIT ERROR: " + ret);
            DialogUtils.dismissProgressDialog(getActivity());
            endEMV();
        }
    }

    private void inputOnlinePin(String pan, OnInputPinListener listener) {
        showResult("INPUT ONLINE PIN:" + pan);

        byte[] panBlock = pan.getBytes();
        Bundle bundle = new Bundle();
        bundle.putBoolean(PinPadConstrants.COMMON_IS_RANDOM, false);
        if (Build.MODEL.equals("MF960")) {
            bundle.putBoolean(PinPadConstrants.COMMON_IS_PHYSICAL_KEYBOARD, true);
        }

        if (Build.MODEL.equals("H9PRO")) {
            bundle.putBoolean(PinPadConstrants.COMMON_IS_PHYSICAL_KEYBOARD, true);
        }
        bundle.putString(PinPadConstrants.TITLE_HEAD_CONTENT, "Please input the online pin \n " +
                "Amount: " + et_amount.getText().toString());

        try {
            DeviceHelper.getPinpad().setTimeOut(10);
            int minLength = 0;
            int maxLength = 6;
            DeviceHelper.getPinpad().setSupportPinLen(new int[]{minLength, maxLength});
            DeviceHelper.getPinpad().inputOnlinePin(bundle, panBlock, 0, PinAlgorithmMode.ISO9564FMT1, new OnPinPadInputListener.Stub() {
                @Override
                public void onInputResult(int ret, byte[] pinBlock, String ksn) throws RemoteException {
                    StringBuilder builder = new StringBuilder();

                    builder.append("ON INPUT RESULT:" + ret);
                    builder.append("\nPIN BLOCK:" + HexUtil.bytesToHexString(pinBlock));
                    builder.append("\nKSN:" + ksn);

                    showResult(builder.toString());

                    listener.onInputPin(pinBlock);
                }

                @Override
                public void onSendKey(byte keyCode) throws RemoteException {
                    if (keyCode == (byte) ServiceResult.PinPad_Input_Cancel) {
                        listener.onInputPin(null);
                    }
                }

            });
        } catch (RemoteException e) {

        }
    }

    private void inputOfflinePin(String pan, OnInputPinListener listener) {
        showResult("INPUT OFFLINE PIN:" + pan);

        Bundle bundle = new Bundle();
        bundle.putBoolean(PinPadConstrants.COMMON_IS_RANDOM, false);
        bundle.putString(PinPadConstrants.TITLE_HEAD_CONTENT, "Please Enter PIN");

        try {
            int minLength = 0;
            int maxLength = 6;
            DeviceHelper.getPinpad().setSupportPinLen(new int[]{minLength, maxLength});
            DeviceHelper.getPinpad().inputText(bundle, new OnPinPadInputListener.Stub() {
                @Override
                public void onInputResult(int ret, byte[] pinBlock, String ksn) throws RemoteException {
                    StringBuilder builder = new StringBuilder();

                    builder.append("INPUT PIN RESULT:" + ret);
                    builder.append("\nPIN BLOCK:" + HexUtil.bytesToHexString(pinBlock));
                    builder.append("\nKSN:" + ksn);

                    showResult(builder.toString());

                    listener.onInputPin(pinBlock);
                }

                @Override
                public void onSendKey(byte keyCode) throws RemoteException {
                    if (keyCode == (byte) ServiceResult.PinPad_Input_Cancel) {
                        listener.onInputPin(null);
                    }
                }
            }, DispTextMode.PASSWORD);
        } catch (RemoteException e) {

        }
    }

    private void selApp(List<String> appList) {
        String[] options = new String[appList.size()];
        for (int i = 0; i < appList.size(); i++) {
            options[i] = appList.get(i);
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
                alertBuilder.setTitle("PLEASE SELECT APP");
                alertBuilder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int index) {
                        try {
                            DeviceHelper.getEmvHandler().onSetSelAppResponse(index);
                        } catch (RemoteException e) {

                        }
                    }
                });
                AlertDialog alertDialog1 = alertBuilder.create();
                alertDialog1.show();
            }
        });

    }


    private void emvFinish(int ret, Bundle bundle) throws RemoteException {
        byte[] errorCode = bundle.getByteArray(EmvErrorConstrants.EMV_ERROR_CODE);
        if (errorCode != null) {
            showResult("ERROR CODE:" + new String(errorCode));
        }

        if (ret == ServiceResult.Success) {//trans accept
            onFinishShow(bundle);
            showResult("EMV FINISH SUCCESS");
        } else if (ret == ServiceResult.Emv_FallBack) {// fallback
            showResult("EMV FINISH FALL BACK");
            magCardReader.searchCard(new OnSearchMagCardListener.Stub() {
                @Override
                public void onSearchResult(int retCode, MagCardInfoEntity magCardInfoEntity) throws RemoteException {
                    showResult("MAG CARD" + retCode);
                    if (retCode == ServiceResult.Success) {
                        StringBuilder builder = new StringBuilder();

                        builder.append("PAN:" + magCardInfoEntity.getCardNo());
                        builder.append("\nTRACK1:" + magCardInfoEntity.getTk1());
                        builder.append("\nTRACK2:" + magCardInfoEntity.getTk2());
                        builder.append("\nTRACK3:" + magCardInfoEntity.getTk3());
                        builder.append("\nKSN:" + magCardInfoEntity.getKsn());
                        builder.append("\nSERVICE CODE:" + magCardInfoEntity.getServiceCode());

                        showResult(builder.toString());
                    }
                    stopSearch();
                }
            }, 60, new Bundle());
            return;
        } else if (ret == ServiceResult.Emv_Terminate) {// trans end
            showResult("TRACK1 TERMINATE");
            if (errorCode != null) {
                showResult("ERROR CODE: " + new String(errorCode).trim());
                //TODO if the amount of connect less transactions is more than 2,0000. The interface prompts you to swipe or insert a card.
                if (DeviceHelper.getEmvHandler().isErrorCode(EmvErrorCode.QPBOC_ERR_PRE_AMTLIMIT)) {
                    showResult("RF Limit Exceed, Pls Try Another Page! ");
                } else if (DeviceHelper.getEmvHandler().isErrorCode(EmvErrorCode.EMV_ERR_INITAPP_GETOP)) {
                    showResult("VISA Read Card Error!");
                } else if (DeviceHelper.getEmvHandler().isErrorCode(EmvErrorCode.EMV_ERR_SELAPP_APPLOCK)) {
                    showResult("Card APP Lock!");
                }
            }
        } else if (ret == ServiceResult.Emv_Declined) {// trans refuse
            //TODO Please noted android time is correct.
            showResult("EMV FINISH SUCCESS DECLINED");
            if (errorCode != null) {
                showResult("ERROR CODE: " + new String(errorCode).trim());
            }

        } else if (ret == ServiceResult.Emv_TryAgain) {
            showResult("EMV FINISH TRY AGAIN");
            //Master need support
            int retCode = bundle.getInt(EmvErrorConstrants.EMV_GOTO_CODE, 0);
            if (retCode == GoToConstants.GOTO_TRY_AGAIN_CARD || retCode == GoToConstants.GOTO_TRY_AGAIN_MOBILE) {
                showResult("PLEASE TAP CARD");
                startSearchContractLess(new OnSearchIccCardListener.Stub() {
                    @Override
                    public void onSearchResult(int retCode, Bundle bundle) throws RemoteException {
                        stopSearch();
                        //TODO do next transaction
                    }
                });
            }
        } else if (ret == ServiceResult.Emv_TryOtherPage) {
            int retCode = bundle.getInt(EmvErrorConstrants.EMV_GOTO_CODE, 0);
            if (retCode == -8) {
                showResult("PLEASE DIP, SWIPE OR TRY ANOTHER CARD");
            } else if (retCode == -9) {
                showResult("PLEASE DIP, SWIPE CARD");
            } else {
                showResult("EMV TRY OTHER PAGE:" + retCode);
            }
        } else if (ret == ServiceResult.Emv_Cancel) {
            showResult("EMV CANCEL");
        } else {
            showResult("Other Error");
        }
        endEMV();
    }

    private void onlineProc() throws RemoteException {
        String arqcTlv = EmvUtil.getTLVDatas(EmvUtil.arqcTLVTags);
        //TODO Here you should send the data to the server and then call onSetOnlineProcResponse

        Bundle online = new Bundle();
        //TODO onlineRespCode is DE 39—RESPONSE CODE, detail see ISO8583
        String onlineRespCode = "00";
        //TODO DE 55.
        byte[] arpcData = EmvUtil.getExampleARPCData();

        online.putString(EmvOnlineResult.REJCODE, onlineRespCode);
        online.putByteArray(EmvOnlineResult.RECVARPC_DATA, arpcData);
        DeviceHelper.getEmvHandler().onSetOnlineProcResponse(ServiceResult.Success, online);
        showResult(arqcTlv);
    }

    private void onFinishShow(Bundle bundle) throws RemoteException {
        ArrayList<String> list = bundle.getStringArrayList(EmvProcessResult.EMVLOG);
        StringBuilder builder = new StringBuilder();
        String tlv = EmvUtil.getTLVDatas(EmvUtil.tags);
        TlvDataList tlvDataList = TlvDataList.fromBinary(tlv);

        builder.append("CARD NO:" + EmvUtil.readPan() + "\n");
        builder.append("CARD ORG:" + CardUtil.getCardTypFromAid(EmvUtil.getPbocData("4F", true)) + "\n");
        builder.append("CARD TRACK 2:" + EmvUtil.readTrack2() + "\n");
        builder.append("CARD SN:" + EmvUtil.getPbocData("5F34", true) + "\n");


        if (list != null) {
            builder.append("CARD LOG:" + list.toString() + "\n");
        }

        for (String tag : EmvUtil.tags) {
            if ("9F4E".equalsIgnoreCase(tag)) {
                builder.append(tag + "=" + tlvDataList.getTLV(tag) + "\n");
            } else if ("5F20".equalsIgnoreCase(tag)) {
                builder.append(tag + "=" + tlvDataList.getTLV(tag) + "\n");
            } else {
                builder.append(tag + "=" + tlvDataList.getTLV(tag) + "\n");
            }
        }
        showResult(builder.toString());
    }

    private void startSearchContractLess(OnSearchIccCardListener.Stub listener) throws RemoteException {
        rfReader = DeviceHelper.getIccCardReader(IccReaderSlot.RFSlOT);
        rfReader.searchCard(listener, 60, new String[]{IccCardType.CPUCARD});
    }

    private void stopSearch() {
        if (rfReader == null || iccCardReader == null) {
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    iccCardReader.stopSearch();
                    rfReader.stopSearch();
                    magCardReader.stopSearch();
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void endEMV() {
        try {
            DeviceHelper.getEmvHandler().endPBOC();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btnEmvStart.setEnabled(true);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showResult(final String text) {
        Log.d(TAG, text);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvTip.append("[");
                tvTip.append(getCurrentTime("MM-dd HH:mm:ss.SSS"));
                tvTip.append("]");
                tvTip.append("#" + text + "\r\n");
            }
        });
    }

    private String getCurrentTime(String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        Date curDate = new Date(System.currentTimeMillis());

        return df.format(curDate);
    }
    private void clearResult() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvTip.setText("");
            }
        });
    }

    private void inputPinDetectCardRemove() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                        if (!DeviceHelper.getIccCardReader(IccReaderSlot.ICSlOT1).isCardExists()) {
                            Log.w(TAG, "Card Have Remove");
                            DeviceHelper.getPinpad().cancelInput();
                            ToastUtils.show(getContext(), "Card Have Remove");
                            return;
                        }

                        if (!DeviceHelper.getPinpad().isInputting()) {
                            Log.w(TAG, "isInputting false");
                            return;
                        }
                    }
                } catch (RemoteException e) {
                }
            }
        }).start();
    }

}
