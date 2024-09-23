package com.morefun.ysdk.sample.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Looper;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.morefun.yapi.ServiceResult;
import com.morefun.yapi.device.printer.FontFamily;
import com.morefun.yapi.device.printer.MulPrintStrEntity;
import com.morefun.yapi.device.printer.OnPrintListener;
import com.morefun.yapi.device.printer.PrinterConfig;
import com.morefun.ysdk.sample.R;
import com.morefun.ysdk.sample.device.DeviceHelper;
import com.morefun.ysdk.sample.utils.FileUtil;
import com.morefun.ysdk.sample.utils.ToastUtils;

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

public class PrintTestFragment extends Fragment {

    private final String TAG = PrintTestFragment.class.getName();

    @BindView(R.id.btn_start)
    Button btnStart;

    @BindView(R.id.tv_currentTimes)
    TextView tvCurrentTime;

    @BindView(R.id.tv_successTime)
    TextView tvSuccessTime;

    @BindView(R.id.tv_failTime)
    TextView tvFailTime;

    @BindView(R.id.et_times)
    EditText etTestTime;

    @BindView(R.id.tv_failList)
    TextView tvFailList;

    @BindView(R.id.et_interval)
    EditText etInterval;

    private int failTimes = 0;
    private int successTimes = 0;
    private int currentTimes = 0;

    private List<String> failList = new ArrayList<>();
    private CountDownLatch startSignal;
    private boolean bStop = false;

    private final String LOG_FILE_NAME = "_print_log.txt";
    private final String LOG_PATH = "/sdcard/test/print/";

    private FileOutputStream fileOutputStream = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_print, null);
        ButterKnife.bind(this, view);
        createFile();
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        return view;
    }

    @OnClick({R.id.btn_start})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                start();
                break;
        }
    }

    @Override
    public void onPause() {
        bStop = true;
        super.onPause();
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
    private void start() {
        bStop = false;
        initTest();

        try {
            int testTime = Integer.parseInt(etTestTime.getText().toString());

            StringBuilder builder = new StringBuilder();
            builder.append("###" + "Print Test Total Times [" + testTime + "],Test Interval " + etInterval.getText().toString() + "MS###");

            writeFile(builder.toString());

            new Thread(() -> {
                Looper.prepare();
                for (currentTimes = 0; currentTimes < testTime; currentTimes++) {
                    if (bStop) {
                        break;
                    }
                    showCurrentTime(currentTimes + 1);
                    printTest();
                }

                StringBuilder resultBuilder = new StringBuilder();
                resultBuilder.append("###Total Times:" + testTime).append(", ");
                resultBuilder.append("Success Times:" + successTimes).append(", ");
                resultBuilder.append("Fail Times:" + failTimes).append(", ");
                if (failTimes > 0) {
                    resultBuilder.append("Fail List:" + failList.toString());
                }
                resultBuilder.append("###");
                writeFile(resultBuilder.toString());
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.show(getContext(), e.getMessage());
        }
    }

    private void printTest() {
        startSignal = new CountDownLatch(1);
        int fontSize = FontFamily.MIDDLE;
        Bundle config = new Bundle();
        config.putInt(PrinterConfig.COMMON_GRAYLEVEL, 30);

        List<MulPrintStrEntity> list = new ArrayList<>();
        MulPrintStrEntity entity = new MulPrintStrEntity("POS purchase order", fontSize);
        Bitmap imageFromAssetsFile = FileUtil.getImageFromAssetsFile(getContext(), "printer.bmp");
        entity.setBitmap(imageFromAssetsFile);
        entity.setMarginX(50);
        entity.setGravity(Gravity.CENTER);
        entity.setUnderline(true);
        entity.setYspace(30);
        list.add(entity);
        MulPrintStrEntity mulPrintStrEntity = new MulPrintStrEntity("=====================", fontSize);

        list.add(mulPrintStrEntity);

        list.add(new MulPrintStrEntity("MERCHANT NAME：Demo shop name", fontSize));
        list.add(new MulPrintStrEntity("MERCHANT NO.：20321545656687", fontSize));
        list.add(new MulPrintStrEntity("TERMINAL NO.：25689753", fontSize));
        list.add(new MulPrintStrEntity("CARD NUMBER", fontSize));
        list.add(new MulPrintStrEntity("62179390*****3426", fontSize));
        list.add(new MulPrintStrEntity("TRANS TYPE", fontSize));
        list.add(new MulPrintStrEntity("SALE", fontSize));
        list.add(new MulPrintStrEntity("EXP DATE：2029", fontSize));
        list.add(new MulPrintStrEntity("BATCH NO：000012", fontSize));
        list.add(new MulPrintStrEntity("VOUCHER NO：000001", fontSize));
        list.add(new MulPrintStrEntity("DATE/TIME：2016-05-23 16:50:32", fontSize));
        list.add(new MulPrintStrEntity("AMOUNT", fontSize));
        list.add(new MulPrintStrEntity("==========================", fontSize));
        //feed pager one line
        list.add(new MulPrintStrEntity("\n", fontSize));
        entity = new MulPrintStrEntity("CARD HOLDER SIGNATURE", fontSize);
        list.add(entity);
        list.add(new MulPrintStrEntity("\n", fontSize));
        list.add(new MulPrintStrEntity("--------------------------------------", fontSize));
        list.add(new MulPrintStrEntity(" I ACKNOWLEDGE	SATISFACTORY RECEIPT OF RELATIVE GOODS/SERVICES", fontSize));
        list.add(new MulPrintStrEntity(" MERCHANT COPY ", fontSize));
        list.add(new MulPrintStrEntity("---X---X---X---X---X--X--X--X--X--X--\n", fontSize));
        list.add(new MulPrintStrEntity("\n", fontSize));
        setEnable(false);

        try {
            DeviceHelper.getPrinter().printStr(list, new OnPrintListener.Stub() {
                @Override
                public void onPrintResult(int result) throws RemoteException {
                    if (result == ServiceResult.Success) {
                        writeFile("> Current Times [" + currentTimes + "] Print Success√");
                        successTimes++;
                        showSuccessTime(successTimes);
                    } else {
                        writeFile("> Current Times [" + currentTimes + "] Print Fail×");
                        failTimes++;
                        showFailTime(failTimes);
                        showFailList();

                        writeFile("> Print Fail List ×××" + failList.toString());
                    }
                    setEnable(true);

                    try {
                        int interval = Integer.parseInt(etInterval.getText().toString());
                        SystemClock.sleep(interval);
                    } catch (Exception e) {

                    }
                    startSignal.countDown();
                }
            }, config);
            try {
                startSignal.await();
            } catch (InterruptedException e) {
                startSignal.countDown();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showCurrentTime(int i) {
        getActivity().runOnUiThread(() -> tvCurrentTime.setText(getString(R.string.title_test_current_count) + ":" + i));
    }

    private void setEnable(boolean enable) {
        getActivity().runOnUiThread(() -> btnStart.setEnabled(enable));
    }

    private void showSuccessTime(int i) {
        getActivity().runOnUiThread(() -> tvSuccessTime.setText(getString(R.string.title_test_success_count) + ":" + i));
    }

    private void writeFile(String text) {
        try {
            StringBuilder builder = new StringBuilder();

            builder.append("[");
            builder.append(getCurrentTime("yyyy-MM-dd HH:mm:ss.SSS"));
            builder.append("]");
            builder.append(text);
            builder.append("\r\n");

            fileOutputStream.write(builder.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showFailTime(int i) {
        getActivity().runOnUiThread(() -> tvFailTime.setText(getString(R.string.title_test_fail_count) + ":" + i));
        if (i != 0) {
            failList.add(currentTimes + "");
        }
    }

    private void showFailList() {
        if (failList.size() > 0) {
            getActivity().runOnUiThread(() -> tvFailList.setText("Fail List" + ":" + failList.toString()));
        }
    }

    private void initTest() {
        showCurrentTime(0);
        showFailTime(0);
        showSuccessTime(0);
        failTimes = 0;
        successTimes = 0;
        currentTimes = 0;
    }

}
