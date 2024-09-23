package com.morefun.ysdk.sample.fragment;

import android.os.Bundle;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.morefun.yapi.ServiceResult;
import com.morefun.yapi.device.pinpad.DesLoadObj;
import com.morefun.yapi.emv.EmvAidPara;
import com.morefun.yapi.emv.EmvBlackCard;
import com.morefun.yapi.emv.EmvBlackCardHash;
import com.morefun.yapi.emv.EmvBlackCardMac;
import com.morefun.yapi.emv.EmvCapk;
import com.morefun.yapi.emv.EmvTermDRL;
import com.morefun.ysdk.sample.R;
import com.morefun.ysdk.sample.device.DeviceHelper;
import com.morefun.ysdk.sample.utils.BytesUtil;
import com.morefun.ysdk.sample.utils.DialogUtils;
import com.morefun.ysdk.sample.utils.HexUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EMVParamFragment extends Fragment {
    @BindView(R.id.tv_tip)
    TextView tvTip;

    private final String TAG = EMVParamFragment.class.getName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_emv_param, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick({R.id.btn_downloadAID, R.id.btn_getAID, R.id.btn_clearAID,
            R.id.btn_downloadCAPK, R.id.btn_getCAPK, R.id.btn_clearCAPK,
            R.id.btn_downloadBalckCard, R.id.btn_getBlackCard, R.id.btn_clearBlackCard,
            R.id.btn_downloadBlackCardHash, R.id.btn_getBlackCardHash, R.id.btn_clearBlackCardHash, R.id.btn_deleteBlackCardHash,
            R.id.btn_downloadBlackCardMac, R.id.btn_getBlackCardMac,
            R.id.btn_downloadBlackCert, R.id.btn_clearBlackCert, R.id.btn_downloadDynamicLimit, R.id.btn_clearDynamicLimit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_downloadAID:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        downloadAID();
                    }
                }).start();
                break;
            case R.id.btn_getAID:
                getAIDList();
                break;
            case R.id.btn_clearAID:
                clearAID();
                break;
            case R.id.btn_downloadCAPK:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        downloadCAPK();
                    }
                }).start();
                break;
            case R.id.btn_getCAPK:
                getCAPKList();
                break;
            case R.id.btn_clearCAPK:
                clearCAPK();
                break;
            case R.id.btn_downloadBalckCard:
                downloadBlackCard();
                break;
            case R.id.btn_getBlackCard:
                getBlackCardList();
                break;
            case R.id.btn_clearBlackCard:
                clearBlackCard();
                break;
            case R.id.btn_downloadBlackCardHash:
                downloadBlackCardHash();
                break;
            case R.id.btn_getBlackCardHash:
                getBlackCardHashNum();
                break;
            case R.id.btn_downloadBlackCardMac:
                downloadBlackCardMac();
                break;
            case R.id.btn_getBlackCardMac:
                getBlackCardHashMac();
                break;
            case R.id.btn_clearBlackCardHash:
                clearBlackCardHash();
                break;
            case R.id.btn_deleteBlackCardHash:
                delBlackCardHash();
                break;
            case R.id.btn_downloadBlackCert:
                downloadBlackCertList();
                break;
            case R.id.btn_clearBlackCert:
                clearBlackCertList();
                break;
            case R.id.btn_downloadDynamicLimit:
                downloadDynamicLimit();
                break;
            case R.id.btn_clearDynamicLimit:
                clearDynamicLimit();
                break;
        }
    }

    private void downloadAID() {
        String[] aidList = new String[]{
                "9F0607A0000000031010DF0101009F09020096DF11050000000000DF12050000000000DF130500000000009F1B0400003A98DF1504000000009F1D080000000000000000DF160100DF170100DF14039F3704DF1801319F7B06000000002000DF1906000000000000DF2006000010000000DF2106000000100000",
                "9F0608A000000003101003DF0101009F09020096DF11050000000000DF12050000000000DF130500000000009F1B0400003A98DF1504000000009F1D080000000000000000DF160150DF170110DF14039F3704DF1801319F7B06000000200000DF1906000000000000DF2006000002000000DF2106000000100000",
                "9F0608A000000003101004DF0101009F09020096DF11050000000000DF12050000000000DF130500000000009F1B0400003A98DF1504000000009F1D080000000000000000DF160150DF170120DF14039F3704DF1801319F7B06000000000020DF1906000000000000DF2006000010000000DF2106000000100000",
                "9F0608A000000003101005DF0101009F09020096DF11050000000000DF12050000000000DF130500000000009F1B0400003A98DF1504000000009F1D080000000000000000DF160150DF170120DF14039F3704DF1801319F7B06000000000020DF1906000000000000DF2006000000200000DF2106000000100000",
                "9F0608A000000003101006DF0101009F09020096DF11050000000000DF12050000000000DF130500000000009F1B0400003A98DF1504000000009F1D080000000000000000DF160150DF170120DF14039F3704DF1801319F7B06000000200000DF1906000000000000DF2006000002000000DF2106000000100000",
                "9F0608A000000003101007DF0101019F09020096DF11050000000000DF12050000000000DF130500000000009F1B0400003A98DF1504000000009F1D080000000000000000DF160150DF170120DF14039F3704DF1801319F7B06000000200000DF1906000000000000DF2006000002000000DF2106000000100000",
                "9F0607A0000000041010DF0101009F09020002DF11050000000000DF12050000000000DF130540000000009F1B0400004E20DF1504000000009F1D080000000000000000DF160150DF170120DF14039F3704DF1801319F7B06000000200000DF1906000000000000DF2006000002000000DF2106000000100000",
                "9F0607A0000000651010DF0101009F09020200DF11050000000000DF12050000000000DF130500000080009F1B04000061A8DF1504000000009F1D080000000000000000DF160150DF170120DF14039F3704DF1801319F7B06000000200000DF1906000000000000DF2006000002000000DF2106000000100000",
                "9F0607A0000000999090DF0101009F09020009DF11050000000000DF12050000000000DF130500000000009F1B0400002710DF1504000000009F1D080000000000000000DF160150DF170120DF1801319F7B06000000200000DF1906000000000000DF2006000002000000DF2106000000100000",
                "9F0607A0000003330101DF0101009F09020030DF11050000000000DF12050000000000DF130500000000009F1B0400002710DF1504000000009F1D080000000000000000DF160100DF170100DF14039F3704DF1801319F7B06000000200000DF1906000000000000DF2006000010000000DF2106000000100000",
                "9F0606A00000999901DF0101009F09029999DF11050000000000DF12050000000000DF130500000000009F1B0400002710DF1504000000009F1D080000000000000000DF160101DF170100DF14039F3704DF1801319F7B06000000000020DF1906000000000000DF2006000010000000DF2106000000100000",
                "9F0608A000000025010501DF0101009F09020001DF11050000000000DF12050000000000DF130500200000009F1B0400007530DF1504000000009F1D080000000000000000DF160150DF170120DF14039F3704DF1801319F7B06000000200000DF1906000000000000DF2006000002000000DF2106000000100000",
                "9F0605A122334455DF0101009F09021234DF11050000000000DF12050000000000DF130500000000009F1B0400002710DF1504000000009F1D080000000000000000DF160100DF170100DF14039F3704DF1801319F7B06000000000001DF1906000000000000DF2006000010000000DF2106000000000002",
                "9F0608A000000003101001DF0101009F09020096DF11050000000000DF12050000000000DF130500000000009F1B0400003A98DF1504000000009F1D080000000000000000DF160150DF170120DF14039F0206DF1801319F7B06000000200000DF1906000000000000DF2006000002000000DF2106000000100000",
                "9F0608A000000333010101DF0101009F09020030DF11050000000000DF12050000000000DF130500000000009F1B04000186A0DF1504000000009F1D080000000000000000DF160100DF170100DF14039F3704DF1801319F7B06000000010000DF1906000000000000DF2006000010000000DF2106000000100000",
                "9F0608A000000333010102DF0101009F09020030DF11050000000000DF12050000000000DF130500000000809F1B0400009C40DF1504000000009F1D080000000000000000DF160100DF170100DF14039F3704DF1801319F7B06000000200000DF1906000000000000DF2006000010000000DF2106000000100000",
                "9F0610A0000000031010010203040506070809DF0101009F09020096DF11050000000000DF12050000000000DF130500000000009F1B0400003A98DF1504000000009F1D080000000000000000DF160150DF170120DF14039F3704DF1801319F7B06000000200000DF1906000000000000DF2006000000200000DF2106000000100000",
                "9F0608A000000333010106DF0101009F09020030DF11050000000000DF12050000000000DF130500000000009F1B04000186A0DF1504000000009F1D080000000000000000DF160100DF170100DF14039F3704DF1801319F7B06000000200000DF1906000000000000DF2006000010000000DF2106000000100000",
                "9F0607A0000001523010DF0101009F09020001DF11050000000000DF12050000000000DF130500000000009F1B0400001388DF1504000000009F1D080000000000000000DF160150DF170120DF14039F3704DF1801319F7B06000000200000DF1906000000000000DF2006000002000000DF2106000000100000",
                "9F0606A00000002501DF0101009F09020001DF11050000000000DF12050000000000DF130500000000009F1B0400003A98DF1504000000009F1D080000000000000000DF160100DF170100DF14039F3704DF1801019F7B06000000200000DF1906000000000000DF2006000010000000DF2106000000100000",
                "9F0608A000000524010101DF0101009F09020030DF11050000000000DF12050000000000DF130500000000009F1B04000186A0DF1504000000009F1D080000000000000000DF160100DF170100DF14039F3704DF1801319F7B06000000010000DF1906000000000000DF2006000010000000DF2106000000100000",
                "9F0607A0000003241010DF0101009F09020001DF11050000000000DF12050000000000DF130500000000009F1B0400001388DF1504000000009F1D080000000000000000DF160150DF170120DF14039F3704DF1801319F7B06000000200000DF1906000000000000DF2006000002000000DF2106000000100000",
                "9F0607A0000005241010DF0101009F09020002DF11050000000000DF12050000000000DF130500000000009F1B04000186A0DF1504000000009F1D080000000000000000DF160105DF170100DF1801319F7B06000000010000DF1906000000000500DF2006000010000000DF2106000000100000",
                "9F0607A0000005241011DF0101009F09020002DF11050000000000DF12050000000000DF130500000000009F1B04000186A0DF1504000000009F1D080000000000000000DF160105DF170100DF1801319F7B06000000010000DF1906000000000000DF2006000010000000DF2106000000100000",
                "9F0608A000000524010101DF0101009F09020030DF11050000000000DF12050000000000DF130500000000009F1B04000186A0DF150400000000DF160100DF170100DF14039F3704DF1801319F7B06000000010000DF1906000000000000DF2006000200000000DF2106000000100000",
                "9F0607A0000003241010DF0101009F09020001DF11050000000000DF12050000000000DF130500000000009F1B0400001388DF150400000000DF160150DF170120DF14039F3704DF1801319F7B06000000200000DF1906000000000000DF2006000002000000DF2106000000100000",
                "9F0607A00000052410119F09020002DF11050000000000DF12050000000000DF130500000000009F1B04000186A05F2A0203565F360102DF1801319F7B06000000010000DF1906000000000000DF2006000001000000DF170100DF160105DF1504000001F4DF2106000000100000",
                "9F0607A0000000041010DF0101009F09020002DF11050000000000DF12050000000000DF130540000000009F1B0400004E20DF150400000000DF160150DF1701209f1d086C00000000000000DF2106000000200000DF2006000001000000DF1906000000000000",
                "9F0607A0000000043060DF0101009F09020002DF11050000000000DF12050000000000DF130540000000009F1B0400004E20DF150400000000DF160150DF1701209f1d084C00000000000000DF2106000000200000DF2006000001000000DF1906000000000000",
                "9F0607A0000000041010DF0101009F09020002DF11050000000000DF12050000000000DF130540000000009F1B0400004E20DF1504000000009F1D080000000000000000DF160150DF170120DF14039F3704DF1801319F7B06000000200000DF1906000000000000DF2006000002000000DF2106000000100000",
                "9F0607A00000060210109F08020002DF11050000000000DF12050000000000DF130500000000009F1B04000186A05F2A0203565F360102DF1801319F7B06000000010000DF1906000000010000DF2006000000050000DF170100DF160105DF1504000001F4",
        };
        int ret = -1;
        try {
            for (int i = 0; i < aidList.length; i++) {
                String tip = "DOWNLOAD AID" + String.format("(%d)", i);
                showResult(tip);
                String aid = aidList[i];
                ret = DeviceHelper.getEmvHandler().addAidParam(HexUtil.hexStringToByte(aid));

                if (ret != ServiceResult.Success) {
                    break;
                }
                SystemClock.sleep(50);
            }
            showResult("DOWNLOAD AID " + (ret == ServiceResult.Success ? "success" : "fail"));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void clearAID() {
        try {
            DeviceHelper.getEmvHandler().clearAIDParam();
            showResult("CLEAR AID SUCCESS!");
        } catch (RemoteException e) {

        }
    }

    private void getAIDList() {
        try {
            List<EmvAidPara> aidList = DeviceHelper.getEmvHandler().getAidParaList();
            StringBuilder builder = new StringBuilder();

            builder.append("\n AID NUM:" + aidList.size());

            for (int i = 0; i < aidList.size(); i++) {
                EmvAidPara emvAidPara = aidList.get(i);
                builder.append("\n AID:[" + i + "]" + HexUtil.bytesToHexString(emvAidPara.getAID()).substring(0, 16));
                builder.append("\n TERM APP VER(9F09):" + HexUtil.bytesToHexString(emvAidPara.getTermAppVer()));
                builder.append("\n TFL DOMESTIC(9F1B):" + HexUtil.bytesToHexString(emvAidPara.getTFL_Domestic()));
                builder.append("\n TAC DEFAULT(DF11):" + HexUtil.bytesToHexString(emvAidPara.getTAC_Default()));
                builder.append("\n TAC ONLINE(DF12):" + HexUtil.bytesToHexString(emvAidPara.getTAC_Online()));
                builder.append("\n TAC DENIAL(DF13):" + HexUtil.bytesToHexString(emvAidPara.getTAC_Denial()));
                builder.append("\n RF OFFLINE LIMIT(DF19):" + HexUtil.bytesToHexString(emvAidPara.getRFOfflineLimit()));
                builder.append("\n RF TRANS LIMIT(DF20):" + HexUtil.bytesToHexString(emvAidPara.getRFTransLimit()));
                builder.append("\n RF CVM LIMIT(DF21):" + HexUtil.bytesToHexString(emvAidPara.getRFCVMLimit()));
                builder.append("\n EC TFL(9F7B):" + HexUtil.bytesToHexString(emvAidPara.getEC_TFL()));
            }

            showResult(builder.toString());

        } catch (RemoteException e) {

        } catch (NullPointerException e) {
            showResult(e.getMessage());
        }
    }


    private void downloadCAPK() {
        String[] pukList = new String[]{
                "9F0605A0000003339F220102DF050420211231DF060101DF070101DF028190A3767ABD1B6AA69D7F3FBF28C092DE9ED1E658BA5F0909AF7A1CCD907373B7210FDEB16287BA8E78E1529F443976FD27F991EC67D95E5F4E96B127CAB2396A94D6E45CDA44CA4C4867570D6B07542F8D4BF9FF97975DB9891515E66F525D2B3CBEB6D662BFB6C3F338E93B02142BFC44173A3764C56AADD202075B26DC2F9F7D7AE74BD7D00FD05EE430032663D27A57DF040103DF031403BB335A8549A03B87AB089D006F60852E4B8060",
                "9F0605A0000003339F220103DF050420221231DF060101DF070101DF0281B0B0627DEE87864F9C18C13B9A1F025448BF13C58380C91F4CEBA9F9BCB214FF8414E9B59D6ABA10F941C7331768F47B2127907D857FA39AAF8CE02045DD01619D689EE731C551159BE7EB2D51A372FF56B556E5CB2FDE36E23073A44CA215D6C26CA68847B388E39520E0026E62294B557D6470440CA0AEFC9438C923AEC9B2098D6D3A1AF5E8B1DE36F4B53040109D89B77CAFAF70C26C601ABDF59EEC0FDC8A99089140CD2E817E335175B03B7AA33DDF040103DF031487F0CD7C0E86F38F89A66F8C47071A8B88586F26",
                "9F0605A0000003339F220104DF050420221231DF060101DF070101DF0281F8BC853E6B5365E89E7EE9317C94B02D0ABB0DBD91C05A224A2554AA29ED9FCB9D86EB9CCBB322A57811F86188AAC7351C72BD9EF196C5A01ACEF7A4EB0D2AD63D9E6AC2E7836547CB1595C68BCBAFD0F6728760F3A7CA7B97301B7E0220184EFC4F653008D93CE098C0D93B45201096D1ADFF4CF1F9FC02AF759DA27CD6DFD6D789B099F16F378B6100334E63F3D35F3251A5EC78693731F5233519CDB380F5AB8C0F02728E91D469ABD0EAE0D93B1CC66CE127B29C7D77441A49D09FCA5D6D9762FC74C31BB506C8BAE3C79AD6C2578775B95956B5370D1D0519E37906B384736233251E8F09AD79DFBE2C6ABFADAC8E4D8624318C27DAF1DF040103DF0314F527081CF371DD7E1FD4FA414A665036E0F5E6E5",
                "9F0605A0000000259F2201C9DF0281B0B362DB5733C15B8797B8ECEE55CB1A371F760E0BEDD3715BB270424FD4EA26062C38C3F4AAA3732A83D36EA8E9602F6683EECC6BAFF63DD2D49014BDE4D6D603CD744206B05B4BAD0C64C63AB3976B5C8CAAF8539549F5921C0B700D5B0F83C4E7E946068BAAAB5463544DB18C63801118F2182EFCC8A1E85E53C2A7AE839A5C6A3CABE73762B70D170AB64AFC6CA482944902611FB0061E09A67ACB77E493D998A0CCF93D81A4F6C0DC6B7DF22E62DBDF03148E8DFF443D78CD91DE88821D70C98F0638E51E49DF0403000003DF050420311214DF060101DF070101",
                "9F0605A0000000259F2201CADF0281F8C23ECBD7119F479C2EE546C123A585D697A7D10B55C2D28BEF0D299C01DC65420A03FE5227ECDECB8025FBC86EEBC1935298C1753AB849936749719591758C315FA150400789BB14FADD6EAE2AD617DA38163199D1BAD5D3F8F6A7A20AEF420ADFE2404D30B219359C6A4952565CCCA6F11EC5BE564B49B0EA5BF5B3DC8C5C6401208D0029C3957A8C5922CBDE39D3A564C6DEBB6BD2AEF91FC27BB3D3892BEB9646DCE2E1EF8581EFFA712158AAEC541C0BBB4B3E279D7DA54E45A0ACC3570E712C9F7CDF985CFAFD382AE13A3B214A9E8E1E71AB1EA707895112ABC3A97D0FCB0AE2EE5C85492B6CFD54885CDD6337E895CC70FB3255E3DF040103DF03146BDA32B1AA171444C7E8F88075A74FBFE845765FDF050420311223DF060101DF070101"
        };
        int ret = -1;
        try {
            for (int i = 0; i < pukList.length; i++) {
                String tip = "DOWNLOAD CAPK" + String.format("(%d)", i);
                String capk = pukList[i];
                ret = DeviceHelper.getEmvHandler().addCAPKParam(HexUtil.hexStringToByte(capk));

                if (ret != ServiceResult.Success) {
                    break;
                }
                showResult(tip);
            }
            showResult("DOWNLOAD CAPK " + (ret == ServiceResult.Success ? "SUCCESS" : "FAIL"));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void clearCAPK() {
        try {
            DeviceHelper.getEmvHandler().clearCAPKParam();
            showResult("ClEAR CAPK SUCCESS");
        } catch (RemoteException e) {

        } catch (NullPointerException e) {
            showResult(e.getMessage());
        }
    }

    private void getCAPKList() {
        try {
            List<EmvCapk> capkList = DeviceHelper.getEmvHandler().getCapkList();
            DeviceHelper.getEmvHandler().setCAPKList(capkList);
            StringBuilder builder = new StringBuilder();

            for (int i = 0; i < capkList.size(); i++) {
                builder.append("\nRID:" + HexUtil.bytesToHexString(capkList.get(i).getRID()));
                builder.append("\nCAPK INDEX:" + HexUtil.byteToHex(capkList.get(i).getCA_PKIndex()));
                builder.append("\nCAPK EXP DATE:" + HexUtil.bytesToHexString(capkList.get(i).getCAPKExpDate()));
                builder.append("\nCAPK EXP DATE:" + HexUtil.bytesToHexString(capkList.get(i).getCAPKExponent()));
                builder.append("\nCAPK HASH:" + HexUtil.bytesToHexString(capkList.get(i).getChecksumHash()));
            }

            showResult(builder.toString());

        } catch (RemoteException e) {
            showResult(e.getMessage());
        } catch (NullPointerException e) {
            showResult(e.getMessage());
        }
    }

    private void downloadBlackCard() {
        String[] cardList = new String[]{
                "8174280000000613",
                "8174280000000614",
                "8174280000000615",
                "8174280000000616",
        };

        int ret = -1;
        try {
            for (int i = 0; i < cardList.length; i++) {
                final boolean bCover = true;
                EmvBlackCard blackCard = new EmvBlackCard();

                byte[] card = BytesUtil.hexString2Bytes(cardList[i]);
                blackCard.setCardNo(card);
                blackCard.setCardSn((byte) (0));
                blackCard.setLen((byte) card.length);

                ret = DeviceHelper.getEmvHandler().addBlackCard(blackCard, bCover);

                if (ret != ServiceResult.Success) {
                    break;
                }
                String tip = "Download Black Card" + String.format("(%d)", i);
                showResult(tip);
            }
            showResult("Download Card List " + (ret == ServiceResult.Success ? "success" : "fail"));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void clearBlackCard() {
        try {
            DeviceHelper.getEmvHandler().clearBlackCard();
            showResult("Clear Black Card Success!");
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    private void getBlackCardList() {
        try {
            List<EmvBlackCard> list = DeviceHelper.getEmvHandler().getBlackCard();
            StringBuilder builder = new StringBuilder();

            for (int i = 0; i < list.size(); i++) {
                EmvBlackCard blackCard = list.get(i);

                builder.append(HexUtil.bytesToHexString(blackCard.getCardNo())).append("\n");
            }

            showResult(builder.toString());
        } catch (RemoteException e) {

        } catch (NullPointerException e) {
            showResult(e.getMessage());
        }
    }

    private void clearBlackCardHash() {
        try {
            DeviceHelper.getEmvHandler().initBlackCardHash();
            showResult("Clear Black Card Success!");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void downloadBlackCardHash() {
        String[] cardHashList = new String[]{
                "865A9913D11475B02D1793A3F2ECFAFE58219F4825F3332DF144A9DD8B2F1E62",
                "865A9913D11475B02D1793A3F2ECFAFE58219F4825F3332DF144A9DD8B2F1E63",
                "865A9913D11475B02D1793A3F2ECFAFE58219F4825F3332DF144A9DD8B2F1E64",
                "865A9913D11475B02D1793A3F2ECFAFE58219F4825F3332DF144A9DD8B2F1E65",
        };

        int ret = -1;
        try {
            for (int i = 0; i < cardHashList.length; i++) {
                final boolean bCover = true;
                EmvBlackCardHash blackCard = new EmvBlackCardHash();

                byte[] hash = BytesUtil.hexString2Bytes(cardHashList[i]);
                blackCard.setHash(hash);
                blackCard.setKeyIndex((byte) 0x03);

                ret = DeviceHelper.getEmvHandler().addBlackCardHash(blackCard, bCover);

                if (ret != ServiceResult.Success) {
                    break;
                }
                String tip = "Download Black Card Hash" + String.format("(%d)", i);
                showResult(tip);
            }
            showResult("Download Card List " + (ret == ServiceResult.Success ? "success" : "fail"));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void getBlackCardHashNum() {
        try {
            int num = DeviceHelper.getEmvHandler().getCardBlackCardHashNum();
            showResult("Get Black Card Hash Num:" + num);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private final byte CMAC_KEY_INDEX = 0;
    private void loadCMACKey() {
        try {
            byte[] key = HexUtil.hexStringToByte("0123456789ABCDEFFEDCBA9876543210");
            DesLoadObj desLoadObj = new DesLoadObj(DesLoadObj.KeyTypeEnum.AES_AP_PLAINTEXT, new int[]{CMAC_KEY_INDEX}, key);
            int ret = DeviceHelper.getPinpad().desLoad(desLoadObj);
            DialogUtils.showAlertDialog(getActivity(), ret == 0 ? "Success" : "Fail");
        } catch (RemoteException e) {

        }
    }

    private void downloadBlackCardMac() {
        String[] cardMacList = new String[]{
                "865A9913D11475B0",
                "865A9913D11475B1",
                "865A9913D11475B2",
                "865A9913D11475B3",
        };
        loadCMACKey();

        int ret = -1;
        try {
            for (int i = 0; i < cardMacList.length; i++) {
                final boolean bCover = true;
                EmvBlackCardMac blackCard = new EmvBlackCardMac();

                byte[] mac = BytesUtil.hexString2Bytes(cardMacList[i]);
                blackCard.setMacValue(mac);
                blackCard.setIndex((byte) CMAC_KEY_INDEX);

                ret = DeviceHelper.getEmvHandler().addBlackCardMac(blackCard, bCover);

                if (ret != ServiceResult.Success) {
                    break;
                }
                String tip = "Download Black Card Mac" + String.format("(%d)", i);
                showResult(tip);
            }
            showResult("Download Card List " + (ret == ServiceResult.Success ? "success" : "fail"));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void getBlackCardHashMac() {
        try {
            int num = DeviceHelper.getEmvHandler().getCardBlackCardMacNum();
            showResult("Get Black Card Mac Num:" + num);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void delBlackCardHash() {
        String[] cardHashList = new String[]{
                "865A9913D11475B02D1793A3F2ECFAFE58219F4825F3332DF144A9DD8B2F1E62",
                "865A9913D11475B02D1793A3F2ECFAFE58219F4825F3332DF144A9DD8B2F1E63",
                "865A9913D11475B02D1793A3F2ECFAFE58219F4825F3332DF144A9DD8B2F1E64",
                "865A9913D11475B02D1793A3F2ECFAFE58219F4825F3332DF144A9DD8B2F1E65",
        };

        int ret = -1;
        try {
            for (int i = 0; i < cardHashList.length; i++) {
                EmvBlackCardHash blackCard = new EmvBlackCardHash();

                byte[] hash = BytesUtil.hexString2Bytes(cardHashList[i]);
                blackCard.setHash(hash);
                blackCard.setKeyIndex((byte) 0x03);

                ret = DeviceHelper.getEmvHandler().delBlackCardHash(blackCard);

                if (ret != ServiceResult.Success) {
                    break;
                }
                String tip = "Del Black Card Hash" + String.format("(%d)", i);
                showResult(tip);
            }
            showResult("Del Black Card " + (ret == ServiceResult.Success ? "success" : "fail"));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void downloadBlackCertList(){
        String[] pukList = new String[]{
                "9F0605A0000003339F220102DF08038190A3"
        };
        int ret = -1;
        try {
            for (int i = 0; i < pukList.length; i++) {
                String tip = "Download Cert Revoked List" + String.format("(%d)", i);
                String aid = pukList[i];
                ret = DeviceHelper.getEmvHandler().addCertRevokedList(HexUtil.hexStringToByte(aid));

                if (ret != ServiceResult.Success) {
                    break;
                }
                showResult(tip);
            }
            showResult("Download Black Cert " + (ret == ServiceResult.Success ? "success" : "fail"));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void clearBlackCertList() {
        try {
            DeviceHelper.getEmvHandler().clearCertRevokedList();
            showResult("Clear Black Cert Success!");
        } catch (RemoteException e) {

        } catch (NullPointerException e) {
            showResult(e.getMessage());
        }
    }

    private void downloadDynamicLimit(){
        ArrayList<EmvTermDRL> emvTermDRLS = new ArrayList<>();
        EmvTermDRL termDRL = new EmvTermDRL();
        termDRL.setAppid(new byte[]{0x0C});
        termDRL.setAppidLen((byte) 0x01);
        //0:visa 1:amex
        termDRL.setKernal_type((byte) 0x01);
        //this for visa,
        // default value 0xFE 0xC0 B1bit8:checkAmtZore.B1bit7:checkRCTL.B1bit6:checkStatus.B1bit5:checkFloorLimit.B1bit4:checkCVMLimit.B1bit3:checkOnPIN.B1bit2:checkSig.B1bit1:optionAmtZore.B2b8:DF19.B2b7:9f1b
        termDRL.setDRL_ConfigCheck(new byte[]{(byte) 0xFE, (byte) 0xC0});
        termDRL.setDRL_Contactless_Floor_Limit(HexUtil.hexStringToByte("000000000300"));
        termDRL.setDRL_Contactless_CVM_Limit(HexUtil.hexStringToByte("000000000200"));
        termDRL.setDRL_Contactless_Transation_Limit(HexUtil.hexStringToByte("000000000100"));
        emvTermDRLS.add(termDRL);


        termDRL = new EmvTermDRL();
        termDRL.setAppid(new byte[]{0x40,0x01,0x56,0x01,0x56});
        termDRL.setAppidLen((byte) 0x05);
        //0:visa 1:amex
        termDRL.setKernal_type((byte) 0x00);
        //this for visa,  default value 0xFE 0xC0 B1bit8:checkAmtZore.B1bit7:checkRCTL.B1bit6:checkStatus.B1bit5:checkFloorLimit.B1bit4:checkCVMLimit.B1bit3:checkOnPIN.B1bit2:checkSig.B1bit1:optionAmtZore.B2b8:DF19.B2b7:9f1b
        termDRL.setDRL_ConfigCheck(new byte[]{(byte) 0xFE, (byte) 0xC0});
        termDRL.setDRL_Contactless_Floor_Limit(HexUtil.hexStringToByte("000000000700"));
        termDRL.setDRL_Contactless_CVM_Limit(HexUtil.hexStringToByte("000000000900"));
        termDRL.setDRL_Contactless_Transation_Limit(HexUtil.hexStringToByte("000000001000"));
        emvTermDRLS.add(termDRL);
        int ret = -1;
        try {
            for (int i = 0; i < emvTermDRLS.size(); i++) {
                String tip = "Download Dynamic Limit List" + String.format("(%d)", i);
                EmvTermDRL drl = emvTermDRLS.get(i);
                ret = DeviceHelper.getEmvHandler().addEmvDynamicLimit(drl);
                if (ret != ServiceResult.Success) {
                    break;
                }
                showResult(tip);
            }
            showResult("Download DRL " + (ret == ServiceResult.Success ? "success" : "fail"));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void clearDynamicLimit() {
        try {
            DeviceHelper.getEmvHandler().clearDynamicLimit();
            showResult("Clear DynamicLimit success!");
        } catch (RemoteException e) {

        } catch (NullPointerException e) {
            showResult(e.getMessage());
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
