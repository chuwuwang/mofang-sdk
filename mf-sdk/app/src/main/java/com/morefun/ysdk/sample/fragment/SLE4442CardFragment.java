package com.morefun.ysdk.sample.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.morefun.yapi.card.industry.SL4442Card;
import com.morefun.ysdk.sample.R;
import com.morefun.ysdk.sample.device.DeviceHelper;
import com.morefun.ysdk.sample.utils.BytesUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.morefun.ysdk.sample.utils.BytesUtil.bytes2HexString;
import static com.morefun.ysdk.sample.utils.BytesUtil.hexString2Bytes;
import static com.morefun.ysdk.sample.utils.BytesUtil.int2bytes;

public class SLE4442CardFragment extends Fragment {

    @BindView(R.id.et_address)
    EditText et_address;

    @BindView(R.id.et_data)
    EditText et_data;

    @BindView(R.id.et_key)
    EditText et_key;

    @BindView(R.id.tv_tip)
    TextView tvTip;

    @BindView(R.id.et_readLen)
    EditText et_readLen;

    private final String TAG = SLE4442CardFragment.class.getName();

    private final String CMD_OPEN = "0A000000";
    private final String CMD_POWER_ON = "01000000";
    private final String CMD_POWER_OFF = "02000000";
    private final String CMD_CLOSE = "0B000000";
    private final String CMD_READ_MM = "03000000";
    private final String CMD_READ_PM = "05000000";
    private final String CMD_READ_SM = "07000000";
    private final String CMD_WRITE_MM = "04000000";
    private final String CMD_WRITE_PM = "06000000";
    private final String CMD_WRITE_SM = "08000000";
    private final String CMD_AUTH = "09000000";
    private final String CMD_CARD_PRESENT = "0C000000";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sle4442_card, null);
        ButterKnife.bind(this, view);

        return view;
    }

    @OnClick({R.id.btn_powerOn, R.id.btn_powerOff, R.id.btn_verify, R.id.btn_present,
            R.id.btn_pmRead, R.id.btn_pmWrite, R.id.btn_mmWrite, R.id.btn_mmRead,
            R.id.btn_smRead, R.id.btn_smWrite})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_powerOn:
                powerOn();
                break;
            case R.id.btn_powerOff:
                powerOff();
                break;
            case R.id.btn_verify:
                verify();
                break;
            case R.id.btn_present:
                isCardPresent();
                break;
            case R.id.btn_pmRead:
                pmRead();
                break;
            case R.id.btn_pmWrite:
                pmWrite();
                break;
            case R.id.btn_mmWrite:
                mmWrite();
                break;
            case R.id.btn_mmRead:
                mmRead();
                break;
            case R.id.btn_smRead:
                smRead();
                break;
            case R.id.btn_smWrite:
                smWrite();
                break;
        }
    }

    private SL4442Card getSLE4442Card() {
        try {
            return DeviceHelper.getSL4442Card(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private byte[] makeReadPacket(String cmd) {
        byte[] addressBuf = new byte[4];
        byte[] lenBuf = new byte[4];
        try {
            int address = Integer.parseInt(et_address.getText().toString());
            int2bytes(address, addressBuf, 0);

            int len = Integer.parseInt(et_readLen.getText().toString());
            int2bytes(len, lenBuf, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String read = cmd + bytes2HexString(addressBuf) + bytes2HexString(lenBuf);
        return hexString2Bytes(read);
    }

    private byte[] makeWritePacket(String cmd, byte[] data) {
        byte[] addressBuf = new byte[4];
        byte[] lenBuf = new byte[4];
        try {
            int address = Integer.parseInt(et_address.getText().toString());
            int2bytes(address, addressBuf, 0);

            int len = Integer.parseInt(et_readLen.getText().toString());
            int2bytes(len, lenBuf, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String write = cmd + bytes2HexString(addressBuf) + bytes2HexString(lenBuf) + bytes2HexString(data);
        return hexString2Bytes(write);
    }

    private void powerOn() {
        try {
            byte[] rst = new byte[256];
            int rstLen = getSLE4442Card().open(hexString2Bytes(CMD_OPEN), rst);
            showResult("OPEN:" + BytesUtil.bytes2HexString(BytesUtil.subBytes(rst, 0, rstLen)));

            rst = new byte[256];
            rstLen = getSLE4442Card().powerOn(hexString2Bytes(CMD_POWER_ON), rst);
            showResult("POWER ON:" + BytesUtil.bytes2HexString(BytesUtil.subBytes(rst, 0, rstLen)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void powerOff() {
        try {
            byte[] rst = new byte[2048];
            int rstLen = getSLE4442Card().powerOff(hexString2Bytes(CMD_POWER_OFF), rst);
            showResult("POWER OFF:" + BytesUtil.bytes2HexString(BytesUtil.subBytes(rst, 0, rstLen)));

            rst = new byte[2048];
            rstLen = getSLE4442Card().close(hexString2Bytes(CMD_CLOSE), rst);
            showResult("CLOSE:" + BytesUtil.bytes2HexString(BytesUtil.subBytes(rst, 0, rstLen)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void pmRead() {
        try {
            byte[] rst = new byte[256];
            byte[] cmd = makeReadPacket(CMD_READ_PM);
            int rstLen = getSLE4442Card().readPM(cmd, rst);
            showResult("Read PM:" + BytesUtil.bytes2HexString(BytesUtil.subBytes(rst, 0, rstLen)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void pmWrite() {
        try {
            byte[] data = BytesUtil.hexString2Bytes(et_data.getText().toString());
            byte[] rst = new byte[256];
            byte[] cmd = makeWritePacket(CMD_WRITE_PM, data);

            int rstLen = getSLE4442Card().writePM(cmd, rst);
            showResult("Write PM:" + BytesUtil.bytes2HexString(BytesUtil.subBytes(rst, 0, rstLen)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mmRead() {
        try {
            byte[] rst = new byte[256];
            byte[] cmd = makeReadPacket(CMD_READ_MM);
            int rstLen = getSLE4442Card().readMM(cmd, rst);
            showResult("Read MM:" + BytesUtil.bytes2HexString(BytesUtil.subBytes(rst, 0, rstLen)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mmWrite() {
        try {
            byte[] data = BytesUtil.hexString2Bytes(et_data.getText().toString());
            byte[] rst = new byte[256];
            byte[] cmd = makeWritePacket(CMD_WRITE_MM, data);

            int rstLen = getSLE4442Card().writeMM(cmd, rst);
            showResult("Write MM:" + BytesUtil.bytes2HexString(BytesUtil.subBytes(rst, 0, rstLen)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void smRead() {
        try {
            byte[] rst = new byte[256];
            byte[] cmd = makeReadPacket(CMD_READ_SM);
            int rstLen = getSLE4442Card().readSM(cmd, rst);
            showResult("Read SM:" + BytesUtil.bytes2HexString(BytesUtil.subBytes(rst, 0, rstLen)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void smWrite() {
        try {
            byte[] data = BytesUtil.hexString2Bytes(et_data.getText().toString());
            byte[] rst = new byte[256];
            byte[] cmd = makeWritePacket(CMD_WRITE_SM, data);

            int rstLen = getSLE4442Card().writeSM(cmd, rst);
            showResult("Write SM:" + BytesUtil.bytes2HexString(BytesUtil.subBytes(rst, 0, rstLen)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void verify() {
        try {
            try {
                byte[] data = BytesUtil.hexString2Bytes(et_key.getText().toString());
                byte[] rst = new byte[256];
                byte[] cmd = makeWritePacket(CMD_AUTH, data);

                int rstLen = getSLE4442Card().auth(cmd, rst);
                showResult("Auth:" + BytesUtil.bytes2HexString(BytesUtil.subBytes(rst, 0, rstLen)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {

        }
    }

    private void isCardPresent() {
        try {
            byte[] rst = new byte[256];
            byte[] cmd = makeReadPacket(CMD_CARD_PRESENT);
            int rstLen = getSLE4442Card().isCardPresent(cmd, rst);
            showResult("IS CARD PRESENT:" + BytesUtil.bytes2HexString(BytesUtil.subBytes(rst, 0, rstLen)));
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
