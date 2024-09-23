package com.morefun.ysdk.sample.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.morefun.yapi.device.printer.MultipleAppPrinter;
import com.morefun.yapi.device.printer.OnPrintListener;
import com.morefun.ysdk.sample.R;
import com.morefun.ysdk.sample.device.DeviceHelper;
import com.morefun.ysdk.sample.view.PaintView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignatureFragment extends Fragment {

    @BindView(R.id.tv_tip)
    TextView textView;

    @BindView(R.id.canvas)
    PaintView mPaintView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signature, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick({R.id.btn_print, R.id.btn_clean})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_clean:
                mPaintView.clearCanvas();
                break;
            case R.id.btn_print:
                print();
                break;
        }
    }

    private void print() {
        try {
            Bitmap signBitmap = mPaintView.getCanvasBitmap();
            MultipleAppPrinter printer = DeviceHelper.getPrinter();

            printer.printImage(signBitmap, new OnPrintListener.Stub() {
                @Override
                public void onPrintResult(int retCode) throws RemoteException {
                }

            }, new Bundle());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
