package com.morefun.ysdk.sample.fragment;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.morefun.yapi.ServiceResult;
import com.morefun.yapi.device.printer.MulPrintStrEntity;
import com.morefun.yapi.device.printer.OnPrintListener;
import com.morefun.yapi.device.reader.icc.ICCSearchResult;
import com.morefun.yapi.device.reader.icc.IccCardReader;
import com.morefun.yapi.device.reader.icc.IccCardType;
import com.morefun.yapi.device.reader.icc.IccReaderSlot;
import com.morefun.yapi.device.reader.icc.OnSearchIccCardListener;
import com.morefun.yapi.emv.EmvChannelType;
import com.morefun.yapi.emv.EmvErrorCode;
import com.morefun.yapi.emv.EmvErrorConstrants;
import com.morefun.yapi.emv.EmvListenerConstrants;
import com.morefun.yapi.emv.EmvOnlineResult;
import com.morefun.yapi.emv.EmvTransDataConstrants;
import com.morefun.yapi.emv.OnEmvProcessListener;
import com.morefun.ysdk.sample.R;
import com.morefun.ysdk.sample.device.DeviceHelper;
import com.morefun.ysdk.sample.utils.BytesUtil;
import com.morefun.ysdk.sample.utils.CardUtil;
import com.morefun.ysdk.sample.utils.EmvUtil;
import com.morefun.ysdk.sample.utils.FileUtil;
import com.morefun.ysdk.sample.utils.HexUtil;
import com.morefun.ysdk.sample.utils.TcpService;
import com.morefun.ysdk.sample.utils.TlvData;
import com.morefun.ysdk.sample.utils.TlvDataList;
import com.morefun.ysdk.sample.utils.ToastUtils;
import com.morefun.ysdk.sample.utils.WakeLockUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TransTestFragment extends Fragment {

    private final String TAG = TransTestFragment.class.getName();

    @BindView(R.id.tv_tip)
    TextView textView;

    @BindView(R.id.tv_currentTimes)
    TextView tvCurrentTime;

    @BindView(R.id.tv_successTime)
    TextView tvSuccessTime;

    @BindView(R.id.tv_failTime)
    TextView tvFailTime;

    @BindView(R.id.et_testTimes)
    EditText etTestTime;

    @BindView(R.id.tv_failList)
    TextView tvFailList;

    @BindView(R.id.et_ip)
    EditText etIp;

    @BindView(R.id.et_port)
    EditText etPort;

    @BindView(R.id.et_testInterval)
    EditText etInterval;

    @BindView(R.id.et_printNum)
    EditText etPrintNum;

    @BindView(R.id.btn_start)
    Button btnStart;

    private IccCardReader iccCardReader;
    private IccCardReader rfReader;

    private String amount = "000000000001";

    private int failTimes = 0;
    private int successTimes = 0;
    private int currentTimes = 0;
    private int mPrintNum = 1;

    private CountDownLatch startSignal;

    private List<String> failList = new ArrayList<>();

    private final String LOG_FILE_NAME = "_trans_log.txt";
    private final String LOG_PATH = "/sdcard/test/trans/";
    private FileOutputStream fileOutputStream = null;
    private int mTestTimes = 0;
    private boolean bStop = false;
    private KeepAliveReceiver mKeepAliveReceiver;
    private CountDownLatch printSignal;
    private int mPrintResult = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_trans, null);
        ButterKnife.bind(this, view);

        createFile();
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        return view;
    }

    @OnClick({R.id.btn_start, R.id.btn_stop})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                initTest();

                StringBuilder builder = new StringBuilder();
                builder.append("###" + "Trans Test Total Times [" + mTestTimes + "],Test Interval " + etInterval.getText().toString() + "MS###");

                showResult(textView, builder.toString());

                startTest();
                break;
            case R.id.btn_stop:
                stopTest();
                break;
        }
    }

    private synchronized void startTest() {
        try {
            if (TextUtils.isEmpty(etTestTime.getText().toString())) {
                ToastUtils.show(getContext(), "Please Input Test Times");
                return;
            }

            if (TextUtils.isEmpty(etInterval.getText().toString())) {
                ToastUtils.show(getContext(), "Please Input Test Interval");
                return;
            }

            if (TextUtils.isEmpty(etPrintNum.getText().toString())) {
                ToastUtils.show(getContext(), "Please Input Print num");
                return;
            }

            mPrintNum = Integer.parseInt(etPrintNum.getText().toString());
            mTestTimes = Integer.parseInt(etTestTime.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.show(getContext(), e.getMessage());
            return;
        }

        btnStart.setEnabled(false);
        //setAlarm(ACTION);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Looper.prepare();
                    WakeLockUtil.acquireWakeLock(getActivity());
                    Log.d(TAG, "currentTime:" + currentTimes);

                    while (currentTimes < mTestTimes) {
                        if (bStop) {
                            break;
                        }
                        try {
                            showCurrentTime();
                            searchCard();
                        } catch (RemoteException e) {
                            showResult(textView, "searchCard:" + e.getMessage());
                            startSignal.countDown();
                        }
                        currentTimes++;

                        Thread.sleep(Integer.parseInt(etInterval.getText().toString()));
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btnStart.setEnabled(true);
                        }
                    });
                    WakeLockUtil.release();
                } catch (Exception e) {

                }
            }
        }).start();
    }


    protected void showResult(final TextView textView, final String text) {
        Log.d(TAG, text);
        StringBuilder builder = new StringBuilder();

        builder.append("[");
        builder.append(getCurrentTime("yyyy-MM-dd HH:mm:ss.SSS"));
        builder.append("]");
        builder.append(text);
        builder.append("\r\n");

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.append(builder.toString());
            }
        });

        try {
            fileOutputStream.write(builder.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy>>>");
        stopTest();
    }

    private void stopTest() {
        try {
            bStop = true;
            btnStart.setEnabled(true);
            stopSearch();
            endPBOC();
            getActivity().unregisterReceiver(mKeepAliveReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void endPBOC() {
        try {
            DeviceHelper.getEmvHandler().endPBOC();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void searchCard() throws RemoteException {
        showResult(textView, "(" + currentTimes + ")" + getString(R.string.tip_dip_tap_card));

        startSignal = new CountDownLatch(1);
        iccCardReader = DeviceHelper.getIccCardReader(IccReaderSlot.ICSlOT1);
        rfReader = DeviceHelper.getIccCardReader(IccReaderSlot.RFSlOT);

        OnSearchIccCardListener.Stub listener = new OnSearchIccCardListener.Stub() {
            @Override
            public void onSearchResult(int retCode, Bundle bundle) throws RemoteException {
                showResult(textView, "Search card ret: " + retCode);
                stopSearch();
                if (retCode == ServiceResult.Success) {
                    int channel = bundle.getInt(ICCSearchResult.CARDOTHER) == IccReaderSlot.ICSlOT1 ? EmvChannelType.FROM_ICC : EmvChannelType.FROM_PICC;
                    emvProcess(channel, amount);
                } else {
                    showResult(textView, "Search card Fail×");
                    showFailTime();
                    startSignal.countDown();
                }
            }
        };
        iccCardReader.searchCard(listener, 10, new String[]{IccCardType.CPUCARD, IccCardType.AT24CXX, IccCardType.AT88SC102});
        rfReader.searchCard(listener, 10, new String[]{IccCardType.CPUCARD, IccCardType.AT24CXX, IccCardType.AT88SC102});
        try {
            startSignal.await();
        } catch (InterruptedException e) {
            startSignal.countDown();
        }
    }

    private void emvProcess(int channel, String amount) throws RemoteException {
        DeviceHelper.getEmvHandler().initTermConfig(EmvUtil.getInitTermConfig());

        Bundle bundle = EmvUtil.getTransBundle(amount);
        bundle.putInt(EmvTransDataConstrants.CHANNELTYPE, channel);

        DeviceHelper.getEmvHandler().emvProcess(bundle, new OnEmvProcessListener.Stub() {

            @Override
            public void onSelApp(List<String> appNameList, boolean isFirstSelect) throws RemoteException {
                showResult(textView, "Sel App");
                selApp(appNameList);
            }

            @Override
            public void onConfirmCardNo(String cardNo) throws RemoteException {
                showResult(textView, "Confirm card No: " + cardNo);

                DeviceHelper.getEmvHandler().onSetConfirmCardNoResponse(true);
            }

            @Override
            public void onCardHolderInputPin(boolean isOnlinePin, int offlinePinType) throws RemoteException {
                showResult(textView, "Card Holder Input Pin: " + isOnlinePin);
                String cardNo = EmvUtil.readPan();
                DeviceHelper.getEmvHandler().onSetCardHolderInputPin(new byte[]{0x01, 0x02, 0x03});
            }

            @Override
            public void onPinPress(byte keyCode) throws RemoteException {
                showResult(textView, "onPinPress");
            }

            @Override
            public void onDisplayOfflinePin(int i) throws RemoteException {
                showResult(textView, "offline pin left times: " + i);
            }

            @Override
            public void inputAmount(int type) throws RemoteException {
                DeviceHelper.getEmvHandler().onSetInputAmountResponse("0.3");
            }

            @Override
            public void onGetCardResult(int i, Bundle bundle) throws RemoteException {
                showResult(textView, "onGetCardResult:" + i);
            }

            @Override
            public void onDisplayMessage() throws RemoteException {

            }

            @Override
            public void onUpdateServiceAmount(String s) throws RemoteException {

            }

            @Override
            public void onCheckServiceBlackList(String s, String s1) throws RemoteException {

            }

            @Override
            public void onGetServiceDirectory(byte[] bytes) throws RemoteException {
                DeviceHelper.getEmvHandler().onGetServiceDirectory(0);
            }

            @Override
            public void onRupayCallback(int type, Bundle bundle) throws RemoteException {

            }

            @Override
            public void onCertVerify(String certName, String certInfo) throws RemoteException {
                showResult(textView, "Cert Verify");
                DeviceHelper.getEmvHandler().onSetCertVerifyResponse(true);
            }

            @Override
            public void onOnlineProc(Bundle data) throws RemoteException {
                showResult(textView, "onOnlineProc");
                onlineProc();
            }

            @Override
            public void onContactlessOnlinePlaceCardMode(int mode) throws RemoteException {
                showResult(textView, "Callback:onContactlessOnlinePlaceCardMode");
                if (mode == EmvListenerConstrants.NEED_CHECK_CONTACTLESS_CARD_AGAIN) {
                    rfReader = DeviceHelper.getIccCardReader(IccReaderSlot.RFSlOT);
                    OnSearchIccCardListener.Stub listener = new OnSearchIccCardListener.Stub() {
                        @Override
                        public void onSearchResult(int retCode, Bundle bundle) throws RemoteException {
                            stopSearch();
                            try {
                                DeviceHelper.getEmvHandler().onSetContactlessOnlinePlaceCardModeResponse(ServiceResult.Success == retCode);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    rfReader.searchCard(listener, 10, new String[]{IccCardType.CPUCARD, IccCardType.AT24CXX, IccCardType.AT88SC102});
                } else {
                    //show Dialog Prompt the user not to remove the card
                    DeviceHelper.getEmvHandler().onSetContactlessOnlinePlaceCardModeResponse(true);
                }
            }

            @Override
            public void onFinish(int retCode, Bundle data) throws RemoteException {
                showResult(textView, "onFinish retCode:" + retCode);
                emvFinish(retCode, data);
            }

            @Override
            public void onSetAIDParameter(String aid) throws RemoteException {
                showResult(textView, "onSetAIDParameter");
            }

            @Override
            public void onSetCAPubkey(String rid, int index, int algMode) throws RemoteException {
                showResult(textView, "onSetCAPubkey");
            }

            @Override
            public void onTRiskManage(String pan, String panSn) throws RemoteException {
                showResult(textView, "onTRiskManage");
            }

            @Override
            public void onSelectLanguage(String language) throws RemoteException {
                showResult(textView, "onSelectLanguage");
            }

            @Override
            public void onSelectAccountType(List<String> accountTypes) throws RemoteException {
                showResult(textView, "Callback:onSelectAccountType");
            }

            @Override
            public void onIssuerVoiceReference(String pan) throws RemoteException {
                showResult(textView, "Callback:onIssuerVoiceReference");
            }

        });
    }

    private void selApp(List<String> appList) {
        String[] options = new String[appList.size()];
        for (int i = 0; i < appList.size(); i++) {
            options[i] = appList.get(i);
        }

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
        alertBuilder.setTitle("Please select app");
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

    private void emvFinish(int ret, Bundle bundle) throws RemoteException {
        byte[] errorCode = bundle.getByteArray(EmvErrorConstrants.EMV_ERROR_CODE);

        if (ret == ServiceResult.Success) {//trans accept
            onFinishShow();
            doTrans();
            return;
        } else if (ret == ServiceResult.Emv_FallBack) {// fallback

        } else if (ret == ServiceResult.Emv_Terminate) {// trans end
            if (errorCode != null) {
                showResult(textView, "terminate, Error Code: " + new String(errorCode).trim());
                //TODO if the amount of connect less transactions is more than 2,0000. The interface prompts you to swipe or insert a card.
                if (DeviceHelper.getEmvHandler().isErrorCode(EmvErrorCode.QPBOC_ERR_PRE_AMTLIMIT)) {
                    showResult(textView, "RF Limit Exceed, Pls try another page! ");
                }
            } else {
                showResult(textView, "trans terminate×");
            }

        } else if (ret == ServiceResult.Emv_Declined) {// trans refuse
            //TODO Please noted android time is correct ?
            if (errorCode != null) {
                showResult(textView, "trans refuse, Error Code: " + new String(errorCode).trim());
            } else {
                showResult(textView, "trans refuse");
            }

            onFinishShow();
            doTrans();
            return;

        } else if (ret == ServiceResult.Emv_Cancel) {// trans cancel
            showResult(textView, "Emv cancel×");
        } else {
            showResult(textView, "Other Error×");
        }
        showFailTime();
        startSignal.countDown();
    }

    private void onlineProc() throws RemoteException {
        StringBuilder builder = new StringBuilder();
        String arqcTlv = EmvUtil.getTLVDatas(EmvUtil.arqcTLVTags);

        Bundle online = new Bundle();
        //TODO onlineRespCode is DE 39—RESPONSE CODE, detail see ISO8583
        String onlineRespCode = "00";
        //TODO DE 55.
        byte[] arpcData = EmvUtil.getExampleARPCData();

        if (arpcData == null) {
            return;
        }
        online.putString(EmvOnlineResult.REJCODE, onlineRespCode);
        online.putByteArray(EmvOnlineResult.RECVARPC_DATA, arpcData);

        DeviceHelper.getEmvHandler().onSetOnlineProcResponse(ServiceResult.Success, online);
    }

    void onFinishShow() throws RemoteException {
        StringBuilder builder = new StringBuilder();

        builder.append("Card No:" + EmvUtil.readPan());

        showResult(textView, builder.toString());
    }

    private void stopSearch() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    iccCardReader.stopSearch();
                    rfReader.stopSearch();
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showCurrentTime() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvCurrentTime.setText(getString(R.string.title_test_current_count) + ":" + (currentTimes + 1));
            }
        });
    }

    private void showSuccessTime() {
        successTimes++;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvSuccessTime.setText(getString(R.string.title_test_success_count) + ":" + successTimes);
            }
        });
    }

    private void showFailTime() {
        failTimes++;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvFailTime.setText(getString(R.string.title_test_fail_count) + ":" + failTimes);
            }
        });
        if (failTimes != 0) {
            failList.add(currentTimes + "");
        }

        showFailList();
    }

    private void showFailList() {
        if (failList.size() > 0) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvFailList.setText("Fail List" + ":" + failList.toString());
                }
            });

            try {
                fileOutputStream.write(("Fail List:" + failList.toString() + "\r\n").getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void initTest() {
        try {
            failTimes = 0;
            successTimes = 0;
            currentTimes = 0;

            tvCurrentTime.setText(getString(R.string.title_test_current_count) + ":" + currentTimes);
            tvSuccessTime.setText(getString(R.string.title_test_success_count) + ":" + successTimes);
            tvFailTime.setText(getString(R.string.title_test_fail_count) + ":" + failTimes);

            textView.setText("");
            tvFailList.setText("");

            failList.clear();
            bStop = false;
            //startKeepAlive();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void doTrans() {
        String ip = etIp.getText().toString();

        if (ip.isEmpty() || etPort.getText().toString().isEmpty()) {
            if (!Build.MODEL.startsWith("MF360")) {
                for (int i = 0; i < mPrintNum; i++) {
                    print();
                }
                if (mPrintNum > 0) {
                    if (mPrintResult == 0) {
                        showResult(textView, "Print Success√");
                        showSuccessTime();
                    } else {
                        showFailTime();
                        showResult(textView, "Print Fail×");
                    }
                } else {
                    showSuccessTime();
                }
                startSignal.countDown();
            } else {
                showSuccessTime();
                startSignal.countDown();
            }
            return;
        }

        int port = Integer.parseInt(etPort.getText().toString());
        byte[] packet = BytesUtil.hexString2Bytes("017F600004000061210031100002007024068020C08AB116625965127000759900000000000000000100001122060720000100376259651270007599D220626C55A6BAC2A84D003031363132333736393030343931353535343130303130313536061000000000000001329F2608094711AB4B3711709F2701809F101307020103A00000010A010000000000B164707D9F370440440D1A9F36021BA0950500000000009A032004179C01009F02060000000000015F2A02015682027C009F1A0201569F03060000000000009F3303E0E1C89F3501228408A0000003330101029F090200209F1E086D6639305F30310000874132303832303330303550333237383034303032303430353032333030303033313034393031333031303638383939303038303630303630303735393930373030383737353744314534303830303857494E395632303100387061643930333030303030353835313362643030303030303030303030303030303030303030001422000001000601CB2693C0583D1300");

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (TcpService.getInstance().connect(ip, port)) {
                    showResult(textView, "Connect Success");
                    if (TcpService.getInstance().send(packet)) {
                        if (!Build.MODEL.startsWith("MF360")) {
                            for (int i = 0; i < mPrintNum; i++) {
                                print();
                            }
                            if (mPrintNum > 0) {
                                if (mPrintResult == 0) {
                                    showResult(textView, "Print Success√");
                                    showSuccessTime();
                                } else {
                                    showFailTime();
                                    showResult(textView, "Print Fail×");
                                }
                            } else {
                                showSuccessTime();
                            }
                            startSignal.countDown();
                        } else {
                            showSuccessTime();
                            startSignal.countDown();
                        }
                        /**
                        if (TcpService.getInstance().receive() != null) {
                            print();
                        } else {
                            showResult(textView, "Receive Fail!");
                            showFailTime();
                            startSignal.countDown();
                        }
                         **/
                    } else {
                        showResult(textView, "Send Fail×");
                        showFailTime();
                        startSignal.countDown();
                    }
                } else {
                    showResult(textView, "Connect Server Fail×");
                    showFailTime();
                    startSignal.countDown();
                }
            }
        }).start();
    }

    private void print() {
        try {
            printSignal = new CountDownLatch(1);
            Bundle localBundle = new Bundle();
            ArrayList localArrayList = new ArrayList();
            MulPrintStrEntity localMulPrintStrEntity = new MulPrintStrEntity("", 1);
            localMulPrintStrEntity.setBitmap(FileUtil.getImageFromAssetsFile(getContext(), "network.bmp"));
            localMulPrintStrEntity.setMarginX(50);
            localMulPrintStrEntity.setGravity(30);
            localMulPrintStrEntity.setUnderline(true);
            localMulPrintStrEntity.setYspace(30);
            localArrayList.add(localMulPrintStrEntity);

            DeviceHelper.getPrinter().printStr(localArrayList, new OnPrintListener.Stub() {
                public void onPrintResult(int retCode) throws RemoteException {
                    mPrintResult = retCode;
                    printSignal.countDown();
                }
            }, localBundle);

            try {
                printSignal.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void createFile() {
        try {
            String fileName = getCurrentTime("yyMMddHHmmss") + LOG_FILE_NAME;
            File file = new File(LOG_PATH, fileName);

            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            fileOutputStream = new FileOutputStream(file);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private String getCurrentTime(String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间

        return df.format(curDate);
    }

    private void startKeepAlive() {
        try {
            if (mKeepAliveReceiver != null) {
                try {
                    getContext().unregisterReceiver(mKeepAliveReceiver);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            mKeepAliveReceiver = new KeepAliveReceiver();
            getContext().registerReceiver(mKeepAliveReceiver, new IntentFilter(KeepAliveReceiver.KEEP_ALIVE_ACTION));

            Intent intent = new Intent(KeepAliveReceiver.KEEP_ALIVE_ACTION);
            PendingIntent keepAlivePendingIntent = PendingIntent.getBroadcast(getContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
            AlarmManager alarmManager = ((AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE));
            scheduleAlarm(alarmManager, AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + Integer.parseInt(etInterval.getText().toString()), keepAlivePendingIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void scheduleAlarm(AlarmManager alarmManager, int type, long triggerAtMillis, PendingIntent operation) {
        if (Build.VERSION.SDK_INT >= 23) {
            alarmManager.setExactAndAllowWhileIdle(type, triggerAtMillis, operation);
        } else if (Build.VERSION.SDK_INT >= 19) {
            alarmManager.setExact(type, triggerAtMillis, operation);
        } else {
            alarmManager.set(type, triggerAtMillis, operation);
        }
    }

    public class KeepAliveReceiver extends BroadcastReceiver {
        public final static String KEEP_ALIVE_ACTION = "KEEP_ALIVE_ACTION";
        private final String TAG = "KeepAliveReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(KEEP_ALIVE_ACTION)) {
                Log.d(TAG, "[KeepAlive] Refresh registers");
                if (currentTimes == mTestTimes) {
                    StringBuilder builder = new StringBuilder();
                    builder.append("###Total Times:" + mTestTimes).append(", ");
                    builder.append("Success Times:" + successTimes).append(", ");
                    builder.append("Fail Times:" + failTimes).append(", ");
                    if (failTimes > 0) {
                        builder.append("Fail List:" + failList.toString());
                    }
                    builder.append("###");
                    showResult(textView, builder.toString());
                    return;
                }
                startTest();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    //make sure the application will at least wakes up every 10 mn
                    try {
                        Intent newIntent = new Intent("KEEP_ALIVE_ACTION");

                        PendingIntent keepAlivePendingIntent = PendingIntent.getBroadcast(context, 0, newIntent, PendingIntent.FLAG_ONE_SHOT);

                        AlarmManager alarmManager = ((AlarmManager) context.getSystemService(Context.ALARM_SERVICE));
                        scheduleAlarm(alarmManager, AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + Integer.parseInt(etInterval.getText().toString()), keepAlivePendingIntent);
                    } catch (Exception e) {

                    }
                }
            }
        }
    }

}
