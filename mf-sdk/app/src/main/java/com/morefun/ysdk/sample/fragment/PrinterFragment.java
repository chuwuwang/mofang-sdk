package com.morefun.ysdk.sample.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.zxing.BarcodeFormat;
import com.morefun.yapi.ServiceResult;
import com.morefun.yapi.device.printer.FontFamily;
import com.morefun.yapi.device.printer.MulPrintStrEntity;
import com.morefun.yapi.device.printer.OnPrintListener;
import com.morefun.yapi.device.printer.PrinterConfig;
import com.morefun.ysdk.sample.R;
import com.morefun.ysdk.sample.device.DeviceHelper;
import com.morefun.ysdk.sample.utils.FileUtil;
import com.morefun.ysdk.sample.utils.PrinterUtil;
import com.morefun.ysdk.sample.utils.QRCodeUtil;
import com.morefun.ysdk.sample.utils.ToastUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.morefun.ysdk.sample.utils.PrinterUtil.makeLineText;

public class PrinterFragment extends Fragment {
    @BindView(R.id.rb_light)
    RadioButton rb_light;

    @BindView(R.id.rb_normal)
    RadioButton rb_normal;

    @BindView(R.id.rb_strong)
    RadioButton rb_strong;

    @BindView(R.id.tv_battery)
    TextView tv_battery;
    private static final String TAG = "PrinterFragment";
    private BroadcastReceiver batteryLevelRcvr;

    private IntentFilter batteryLevelFilter;

    private int mBatteryVoltage;

    private int mBatteryLevel;
    private String mFontPath = Environment.getExternalStorageDirectory() + File.separator + "times.ttf";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_printer, null);
        ButterKnife.bind(this, view);
        monitorBatteryState();
        copyFont();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(batteryLevelRcvr);
    }
    @OnClick({R.id.btn_printer, R.id.btn_printerList, R.id.btn_printerCustomFont,
            R.id.btn_printerQrCode, R.id.btn_printTable, R.id.btn_printHTML,
            R.id.btn_setPrintTag, R.id.btn_setPrintTicket, R.id.btn_tagLocate, R.id.btn_printTag1, R.id.btn_printTag2,
            R.id.btn_printBlack})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_printer:
                try {
                    printer();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_printerList:
                try {
                    printerList();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_printerCustomFont:
                try {
                    printerCustomFont();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_printerQrCode:
                try {
                    printerQrCode();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_printTable:
                try {
                    printerTable();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_printHTML:
                try {
                    printerHTML();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_setPrintTag:
                try {
                    setPrinterTag();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_setPrintTicket:
                try {
                    setPrinterTicket();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_tagLocate:
                try {
                    tagPaperLocate();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_printTag1:
                try {
                    printerTag1();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_printTag2:
                try {
                    printerTag2();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_printBlack:
                try {
                    printBlack();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void copyFont() {
        try {
            FileUtil.copy(getActivity().getAssets().open("times.ttf"),
                    Environment.getExternalStorageDirectory() + File.separator + "times.ttf", false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printer() throws RemoteException {
        int fontSize = FontFamily.MIDDLE;

        Bundle config = new Bundle();
        config.putInt(PrinterConfig.COMMON_GRAYLEVEL, getGray());

        List<MulPrintStrEntity> list = new ArrayList<>();

        MulPrintStrEntity entity = new MulPrintStrEntity(String.format("battery level:%d%%, voltage:%dV", mBatteryLevel, mBatteryVoltage), fontSize);
        Bitmap imageFromAssetsFile = FileUtil.getImageFromAssetsFile(getContext(), "printer.bmp");

        entity.setBitmap(imageFromAssetsFile);
        entity.setMarginX(50);
        entity.setGravity(Gravity.CENTER);
        entity.setUnderline(true);
        entity.setYspace(30);
        list.add(entity);

        list.add(new MulPrintStrEntity("MERCHANT NAME：Demo shop name", fontSize));
        list.add(new MulPrintStrEntity("MERCHANT NO.：20321545656687", fontSize));
        list.add(new MulPrintStrEntity("TERMINAL NO.：25689753", fontSize));
        list.add(new MulPrintStrEntity("CARD NUMBER", fontSize));
        list.add(new MulPrintStrEntity("62179390*****3426", fontSize).setGravity(Gravity.END));

        list.add(new MulPrintStrEntity(makeLineText(new PrinterUtil.TextItem("TRANS TYPE").setFont(fontSize),
                new PrinterUtil.TextItem("SALE").setFont(fontSize).setPaddingAlign(Gravity.RIGHT)), fontSize));

        list.add(new MulPrintStrEntity(makeLineText(new PrinterUtil.TextItem("EXP DATE").setFont(fontSize),
                new PrinterUtil.TextItem("2029").setFont(fontSize).setPaddingAlign(Gravity.RIGHT)), fontSize));

        list.add(new MulPrintStrEntity(makeLineText(new PrinterUtil.TextItem("BATCH NO").setFont(fontSize),
                new PrinterUtil.TextItem("000012").setFont(fontSize).setPaddingAlign(Gravity.RIGHT)), fontSize));

        list.add(new MulPrintStrEntity(makeLineText(new PrinterUtil.TextItem("VOUCHER NO").setFont(fontSize),
                new PrinterUtil.TextItem("000001").setFont(fontSize).setPaddingAlign(Gravity.RIGHT)), fontSize));

        list.add(new MulPrintStrEntity("DATE/TIME：2016-05-23 16:50:32", fontSize));
        list.add(new MulPrintStrEntity("==========================", fontSize));
        list.add(new MulPrintStrEntity("\n", fontSize));
        list.add(new MulPrintStrEntity("CARD HOLDER SIGNATURE", fontSize));
        list.add(new MulPrintStrEntity("\n", fontSize));
        list.add(new MulPrintStrEntity("--------------------------------------", fontSize));
        list.add(new MulPrintStrEntity(" I ACKNOWLEDGE	SATISFACTORY RECEIPT OF RELATIVE GOODS/SERVICES", fontSize));
        list.add(new MulPrintStrEntity(" MERCHANT COPY ", fontSize));
        list.add(new MulPrintStrEntity("---X---X---X---X---X--X--X--X--X--X--\n", fontSize));
        list.add(new MulPrintStrEntity("\n", fontSize));

        DeviceHelper.getPrinter().printStr(list, new OnPrintListener.Stub() {
            @Override
            public void onPrintResult(int result) throws RemoteException {
                if (result == ServiceResult.Success) {
                    ToastUtils.show(getContext(), "Printer Success");
                    return;
                }
                ToastUtils.show(getContext(), "Printer Fail " + result);
                return;
            }
        }, config);
    }

    private void printerList() throws RemoteException {
        Bundle config = new Bundle();
        config.putInt(PrinterConfig.COMMON_GRAYLEVEL, getGray());
        List<MulPrintStrEntity> list = new ArrayList<>();

        list.add(new MulPrintStrEntity(makeLineText(new PrinterUtil.TextItem("1").setFont(FontFamily.MIDDLE).setPxSize(20).setPaddingAlign(Gravity.FILL_HORIZONTAL),
                new PrinterUtil.TextItem("Chicken Achari").setFont(FontFamily.MIDDLE).setPxSize(180).setPaddingAlign(Gravity.FILL_HORIZONTAL),
                new PrinterUtil.TextItem("1.00").setFont(FontFamily.MIDDLE).setPxSize(80).setPaddingAlign(Gravity.FILL_HORIZONTAL),
                new PrinterUtil.TextItem("35.71").setFont(FontFamily.MIDDLE).setPxSize(80).setPaddingAlign(Gravity.FILL_HORIZONTAL)), FontFamily.MIDDLE));

        list.add(new MulPrintStrEntity(makeLineText(new PrinterUtil.TextItem("2").setFont(FontFamily.MIDDLE).setPxSize(20).setPaddingAlign(Gravity.FILL_HORIZONTAL),
                new PrinterUtil.TextItem("Alu Puff").setFont(FontFamily.MIDDLE).setPxSize(180).setPaddingAlign(Gravity.FILL_HORIZONTAL),
                new PrinterUtil.TextItem("1.00").setFont(FontFamily.MIDDLE).setPxSize(80).setPaddingAlign(Gravity.FILL_HORIZONTAL),
                new PrinterUtil.TextItem("50.00").setFont(FontFamily.MIDDLE).setPxSize(80).setPaddingAlign(Gravity.FILL_HORIZONTAL)), FontFamily.MIDDLE));

        list.add(new MulPrintStrEntity(makeLineText(new PrinterUtil.TextItem("3").setFont(FontFamily.MIDDLE).setPxSize(20).setPaddingAlign(Gravity.FILL_HORIZONTAL),
                new PrinterUtil.TextItem("Pizza Burger").setFont(FontFamily.MIDDLE).setPxSize(180).setPaddingAlign(Gravity.FILL_HORIZONTAL),
                new PrinterUtil.TextItem("1.00").setFont(FontFamily.MIDDLE).setPxSize(80).setPaddingAlign(Gravity.FILL_HORIZONTAL),
                new PrinterUtil.TextItem("100.00").setFont(FontFamily.MIDDLE).setPxSize(80).setPaddingAlign(Gravity.FILL_HORIZONTAL)), FontFamily.MIDDLE));

        list.add(new MulPrintStrEntity(makeLineText(new PrinterUtil.TextItem("4").setFont(FontFamily.MIDDLE).setPxSize(20).setPaddingAlign(Gravity.FILL_HORIZONTAL),
                new PrinterUtil.TextItem("Chicken Cheese").setFont(FontFamily.MIDDLE).setPxSize(180).setPaddingAlign(Gravity.FILL_HORIZONTAL),
                new PrinterUtil.TextItem("3.00").setFont(FontFamily.MIDDLE).setPxSize(80).setPaddingAlign(Gravity.FILL_HORIZONTAL),
                new PrinterUtil.TextItem("90.00").setFont(FontFamily.MIDDLE).setPxSize(80).setPaddingAlign(Gravity.FILL_HORIZONTAL)), FontFamily.MIDDLE));

        list.add(new MulPrintStrEntity(makeLineText(new PrinterUtil.TextItem("5").setFont(FontFamily.MIDDLE).setPxSize(20).setPaddingAlign(Gravity.FILL_HORIZONTAL),
                new PrinterUtil.TextItem("Chicken Burger").setFont(FontFamily.MIDDLE).setPxSize(180).setPaddingAlign(Gravity.FILL_HORIZONTAL),
                new PrinterUtil.TextItem("2.00").setFont(FontFamily.MIDDLE).setPxSize(80).setPaddingAlign(Gravity.FILL_HORIZONTAL),
                new PrinterUtil.TextItem("76.19").setFont(FontFamily.MIDDLE).setPxSize(80).setPaddingAlign(Gravity.FILL_HORIZONTAL)), FontFamily.MIDDLE));

        DeviceHelper.getPrinter().printStr(list, new OnPrintListener.Stub() {
            @Override
            public void onPrintResult(int result) throws RemoteException {
                if (result == ServiceResult.Success) {
                    ToastUtils.show(getContext(), "Printer Success");
                    return;
                }
                ToastUtils.show(getContext(), "Printer Fail " + result);
                return;
            }
        }, config);
    }

    private void printerCustomFont() throws RemoteException, IOException {
        Bundle config = new Bundle();
        config.putString(PrinterConfig.COMMON_TYPEFACE_PATH, mFontPath);
        config.putInt(PrinterConfig.COMMON_GRAYLEVEL, getGray());
        List<MulPrintStrEntity> list = new ArrayList<>();

        list.add(new MulPrintStrEntity("Font Size 10", 10).setGravity(Gravity.CENTER));
        list.add(new MulPrintStrEntity("Font Size 15", 15).setGravity(Gravity.CENTER));
        list.add(new MulPrintStrEntity("Font Size 20", 20).setGravity(Gravity.CENTER));
        list.add(new MulPrintStrEntity("Font Size 25", 25).setGravity(Gravity.CENTER));
        list.add(new MulPrintStrEntity("Font Size 30", 30).setGravity(Gravity.CENTER));
        list.add(new MulPrintStrEntity("Font Size 40", 40).setGravity(Gravity.CENTER));
        list.add(new MulPrintStrEntity("Font Size 50", 50).setGravity(Gravity.CENTER));
        list.add(new MulPrintStrEntity("Font Size 60", 60).setGravity(Gravity.CENTER));

        DeviceHelper.getPrinter().printStr(list, new OnPrintListener.Stub() {
            @Override
            public void onPrintResult(int result) throws RemoteException {
                if (result == ServiceResult.Success) {
                    ToastUtils.show(getContext(), "Printer Success");
                    return;
                }
                ToastUtils.show(getContext(), "Printer Fail " + result);
                return;
            }
        }, config);
    }

    private void printerQrCode() throws RemoteException {
        Bundle config = new Bundle();
        config.putInt(PrinterConfig.COMMON_GRAYLEVEL, getGray());

        String qrStr = "http://www.morefun-et.com/";
        int qrSize = 300;

        Bitmap qrCode = DeviceHelper.getPrinter().generateQrBitmap(qrStr, qrSize, null);

        DeviceHelper.getPrinter().printImage(qrCode, new OnPrintListener.Stub() {
            @Override
            public void onPrintResult(int result) throws RemoteException {
                if (result == ServiceResult.Success) {
                    ToastUtils.show(getContext(), "Printer Success");
                    return;
                }
                ToastUtils.show(getContext(), "Printer Fail " + result);
                return;
            }
        }, config);
    }

    private void printerTable() throws RemoteException {
        Bundle config = new Bundle();
        config.putString(PrinterConfig.COMMON_TYPEFACE_PATH, "Default");
        config.putInt(PrinterConfig.COMMON_GRAYLEVEL, getGray());
        List<MulPrintStrEntity> list = new ArrayList<>();

        MulPrintStrEntity entity = new MulPrintStrEntity();
        entity.setBitmap(convertViewToBitmap(getTableData()));

        list.add(entity);

        DeviceHelper.getPrinter().printStr(list, new OnPrintListener.Stub() {
            @Override
            public void onPrintResult(int result) throws RemoteException {
                if (result == ServiceResult.Success) {
                    ToastUtils.show(getContext(), "Printer Success");
                    return;
                }
                ToastUtils.show(getContext(), "Printer Fail " + result);
                return;
            }
        }, config);
    }

    private TableLayout getTableData() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_table, null);
        TableLayout tableLayout = view.findViewById(R.id.tableLayout);

        tableLayout.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

        for (int i = 0; i < 5; i++) {
            TableRow tableRow = new TableRow(getContext());

            tableRow.setOrientation(LinearLayout.HORIZONTAL);

            TextView textView1 = new TextView(getContext());
            TextView textView2 = new TextView(getContext());
            TextView textView3 = new TextView(getContext());
            TextView textView4 = new TextView(getContext());

            textView1.setLayoutParams(new TableRow.LayoutParams(100, LinearLayout.LayoutParams.MATCH_PARENT, 0));
            textView2.setLayoutParams(new TableRow.LayoutParams(100, LinearLayout.LayoutParams.MATCH_PARENT, 0));
            textView3.setLayoutParams(new TableRow.LayoutParams(100, LinearLayout.LayoutParams.MATCH_PARENT, 0));
            textView4.setLayoutParams(new TableRow.LayoutParams(100, LinearLayout.LayoutParams.MATCH_PARENT, 0));

            textView1.setText("A" + (i));
            textView2.setText("BB" + (i));
            textView3.setText("CCC" + (i));
            textView4.setText("DDDD" + (i));

            tableRow.addView(textView1);
            tableRow.addView(textView2);
            tableRow.addView(textView3);
            tableRow.addView(textView4);

            tableLayout.addView(tableRow);
        }
        return tableLayout;
    }

    private Bitmap convertViewToBitmap(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        if (bitmap == null) {
            Bitmap bmp = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bmp);
            view.draw(canvas);
            canvas.setBitmap(null);
            return bmp;
        }
        return bitmap;
    }

    private void printerHTML() throws RemoteException {
        Bundle config = new Bundle();
        config.putString(PrinterConfig.COMMON_TYPEFACE_PATH, "Default");
        config.putInt(PrinterConfig.COMMON_GRAYLEVEL, getGray());
        List<MulPrintStrEntity> list = new ArrayList<>();

        MulPrintStrEntity entity = new MulPrintStrEntity();

        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_print_html, null);
        LinearLayout layout = view.findViewById(R.id.container);

        TextView textView1 = new TextView(getContext());
        TextView textView2 = new TextView(getContext());
        TextView textView3 = new TextView(getContext());

        textView1.setLayoutParams(new ViewGroup.LayoutParams(384, ViewGroup.LayoutParams.WRAP_CONTENT));
        textView2.setLayoutParams(new ViewGroup.LayoutParams(384, ViewGroup.LayoutParams.WRAP_CONTENT));
        textView3.setLayoutParams(new ViewGroup.LayoutParams(384, ViewGroup.LayoutParams.WRAP_CONTENT));

        String html1 = "<font><small>Font size small</small></font>";
        String html2 = "<font><big>Font size big</big></font>";
        String html3 = "<font><small>Font size small</small></font><font><big>Font size big</big></font>";

        textView1.setText(Html.fromHtml(html1));
        textView1.setTypeface(Typeface.MONOSPACE);

        textView2.setText(Html.fromHtml(html2));
        textView2.setTypeface(Typeface.SERIF);

        textView3.setText(Html.fromHtml(html3));
        textView3.setTypeface(Typeface.DEFAULT_BOLD);

        layout.addView(textView1);
        layout.addView(textView2);
        layout.addView(textView3);

        entity.setBitmap(convertViewToBitmap(layout));

        list.add(entity);

        DeviceHelper.getPrinter().printStr(list, new OnPrintListener.Stub() {
            @Override
            public void onPrintResult(int result) throws RemoteException {
                if (result == ServiceResult.Success) {
                    ToastUtils.show(getContext(), "Printer Success");
                    return;
                }
                ToastUtils.show(getContext(), "Printer Fail " + result);
                return;
            }
        }, config);
    }

    private void setPrinterTag() throws RemoteException {
        DeviceHelper.getPrinter().setTagPaper(1);
    }

    private void setPrinterTicket() throws RemoteException {
        DeviceHelper.getPrinter().setTagPaper(0);
    }

    private void tagPaperLocate() throws RemoteException {
        DeviceHelper.getPrinter().tagLocate();
    }

    private void printerTag1() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        Bundle config = new Bundle();
        config.putString(PrinterConfig.COMMON_TYPEFACE_PATH, mFontPath);
        config.putInt(PrinterConfig.COMMON_GRAYLEVEL, getGray());
        List<MulPrintStrEntity> list = new ArrayList<>();
        int fontSize = 22;

        list.add(new MulPrintStrEntity("Item:Smart POS Terminal", fontSize).setGravity(Gravity.LEFT).setIsBold(1));
        list.add(new MulPrintStrEntity("Model:MF919       Made-in-China", fontSize).setGravity(Gravity.LEFT).setIsBold(1));
        list.add(new MulPrintStrEntity("Input:5V---2000mA", fontSize).setGravity(Gravity.LEFT).setIsBold(1));
        list.add(new MulPrintStrEntity("Spec:919L /4G/W/BT", fontSize).setGravity(Gravity.LEFT).setIsBold(1));
        list.add(new MulPrintStrEntity("HW:MF_ADHW_2.01 FW:MF_ADFW_2.01", fontSize).setGravity(Gravity.LEFT).setIsBold(1));

        Bitmap qrCode = QRCodeUtil.createBarcode(getContext(), "9823080803670020", BarcodeFormat.CODE_128, 300, 60, false);
        MulPrintStrEntity entity = new MulPrintStrEntity();
        entity.setBitmap(qrCode);

        list.add(entity);
        list.add(new MulPrintStrEntity("SN:9823080803670020", fontSize).setGravity(Gravity.CENTER).setIsBold(1));
        list.add(new MulPrintStrEntity("Fujian Morefun Electronic Technology", fontSize).setGravity(Gravity.LEFT).setIsBold(1));

        DeviceHelper.getPrinter().printStr(list, new OnPrintListener.Stub() {
            @Override
            public void onPrintResult(int result) throws RemoteException {
                countDownLatch.countDown();
                if (result == ServiceResult.Success) {
                    ToastUtils.show(getContext(), "Printer Success");
                    return;
                }
                ToastUtils.show(getContext(), "Printer Fail " + result);
                return;
            }
        }, config);

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
            countDownLatch.countDown();
        }
    }

    private void printerTag2() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        Bundle config = new Bundle();
        config.putString(PrinterConfig.COMMON_TYPEFACE_PATH, mFontPath);
        config.putInt(PrinterConfig.COMMON_GRAYLEVEL, getGray());
        List<MulPrintStrEntity> list = new ArrayList<>();
        int fontSize = 24;

        list.add(new MulPrintStrEntity("物料名称：NFC橙汁", fontSize).setGravity(Gravity.LEFT).setIsBold(1));
        list.add(new MulPrintStrEntity("存储条件：冷冻", fontSize).setGravity(Gravity.LEFT).setIsBold(1));
        list.add(new MulPrintStrEntity("解冻完成时间：2023-09-21 15:25", fontSize).setGravity(Gravity.LEFT).setIsBold(1));
        list.add(new MulPrintStrEntity("到期时间：2023-10-18 15:25", fontSize).setGravity(Gravity.LEFT).setIsBold(1));
        list.add(new MulPrintStrEntity("制作人：Alan", fontSize).setGravity(Gravity.LEFT).setIsBold(1));
        list.add(new MulPrintStrEntity("打印时间：2023-09-20 15:25", fontSize).setGravity(Gravity.LEFT).setIsBold(1));

        DeviceHelper.getPrinter().printStr(list, new OnPrintListener.Stub() {
            @Override
            public void onPrintResult(int result) throws RemoteException {
                countDownLatch.countDown();
                if (result == ServiceResult.Success) {
                    ToastUtils.show(getContext(), "Printer Success");
                    return;
                }
                ToastUtils.show(getContext(), "Printer Fail " + result);
                return;
            }
        }, config);

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
            countDownLatch.countDown();
        }
    }

    private void printBlack() throws RemoteException {
        Bundle config = new Bundle();
        config.putInt(PrinterConfig.COMMON_GRAYLEVEL, getGray());

        Bitmap bitmap = Bitmap.createBitmap(574, 900, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.BLACK);

        List<MulPrintStrEntity> list = new ArrayList<>();
        MulPrintStrEntity entity = new MulPrintStrEntity();
        entity.setBitmap(bitmap);
        list.add(entity);
        list.add(new MulPrintStrEntity("\n", 24));
        list.add(new MulPrintStrEntity("\n", 24));

        DeviceHelper.getPrinter().printStr(list, new OnPrintListener.Stub() {
            @Override
            public void onPrintResult(int result) throws RemoteException {
                if (result == ServiceResult.Success) {
                    ToastUtils.show(getContext(), "Printer Success");
                    return;
                }
                ToastUtils.show(getContext(), "Printer Fail " + result);
                return;
            }
        }, config);
    }

    private int getGray() {
        if (rb_light.isChecked()) {
            return PrinterConfig.PRINT_DENSITY_LIGHT;
        } else if (rb_normal.isChecked()) {
            return PrinterConfig.PRINT_DENSITY_NORMAL;
        } else {
            return PrinterConfig.PRINT_DENSITY_DENSE;
        }
    }

    private void monitorBatteryState() {
        batteryLevelRcvr = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                // TODO Auto-generated method stub
                String action = intent.getAction();
                if (action.equals(Intent.ACTION_POWER_CONNECTED)) {
                } else if (action.equals(Intent.ACTION_POWER_DISCONNECTED)) {
                } else if (action.equals(Intent.ACTION_POWER_USAGE_SUMMARY)) {
                } else if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                    int status = intent.getIntExtra("status", 0);
                    int plugged = intent.getIntExtra("plugged", 0);
                    mBatteryVoltage = intent.getIntExtra("voltage", 0);
                    int capacity = intent.getIntExtra("capacity", 0);
                    mBatteryLevel = intent.getIntExtra("level", 0);

                    Log.d(TAG, "battery voltage: " + mBatteryVoltage);
                    Log.d(TAG, "battery level: " + mBatteryLevel);

                    tv_battery.setText(String.format("Battery Level:%d%%, Battery Voltage:%dV", mBatteryLevel, mBatteryVoltage));
                }
            }

        };
        batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        getActivity().registerReceiver(batteryLevelRcvr, batteryLevelFilter);
    }
}
