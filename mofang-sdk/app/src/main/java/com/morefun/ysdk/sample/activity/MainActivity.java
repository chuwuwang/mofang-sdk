package com.morefun.ysdk.sample.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.morefun.yapi.ServiceResult;
import com.morefun.ysdk.sample.MyApplication;
import com.morefun.ysdk.sample.R;
import com.morefun.ysdk.sample.fragment.BeepFragment;
import com.morefun.ysdk.sample.fragment.CpuCardFragment;
import com.morefun.ysdk.sample.fragment.DUKPTFragment;
import com.morefun.ysdk.sample.fragment.DeviceFragment;
import com.morefun.ysdk.sample.fragment.EMVFragment;
import com.morefun.ysdk.sample.fragment.EMVParamFragment;
import com.morefun.ysdk.sample.fragment.EmulateCardFragment;
import com.morefun.ysdk.sample.fragment.FelicaFragment;
import com.morefun.ysdk.sample.fragment.LedFragment;
import com.morefun.ysdk.sample.fragment.LoginFragment;
import com.morefun.ysdk.sample.fragment.M0CardFragment;
import com.morefun.ysdk.sample.fragment.M1CardFragment;
import com.morefun.ysdk.sample.fragment.MKSKFragment;
import com.morefun.ysdk.sample.fragment.MagCardFragment;
import com.morefun.ysdk.sample.fragment.NTagCardFragment;
import com.morefun.ysdk.sample.fragment.OctopusSecureFragment;
import com.morefun.ysdk.sample.fragment.PSAMCardFragment;
import com.morefun.ysdk.sample.fragment.PedFragment;
import com.morefun.ysdk.sample.fragment.PrintTestFragment;
import com.morefun.ysdk.sample.fragment.PrinterFragment;
import com.morefun.ysdk.sample.fragment.RuPayFragment;
import com.morefun.ysdk.sample.fragment.SLE4442CardFragment;
import com.morefun.ysdk.sample.fragment.ScannerFragment;
import com.morefun.ysdk.sample.fragment.SerialPortFragment;
import com.morefun.ysdk.sample.fragment.SignatureFragment;
import com.morefun.ysdk.sample.fragment.TpmFragment;
import com.morefun.ysdk.sample.fragment.TransTestFragment;
import com.morefun.ysdk.sample.fragment.UsbSerialFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tabLayout)
    TabLayout tableLayout;

    @BindView(R.id.menu_pager)
    ViewPager mViewPager;

    @BindView(R.id.tv_version)
    public TextView tvVersion;

    @BindView(R.id.tv_sdk_version)
    public TextView tvSdkVersion;
    private static String[] tabs = {
            getStringValue(R.string.menu_login),
            getStringValue(R.string.menu_device),
            getStringValue(R.string.menu_beep),
            getStringValue(R.string.menu_led),
            getStringValue(R.string.menu_print),
            getStringValue(R.string.menu_scanner),
            getStringValue(R.string.menu_serial_port),
            getStringValue(R.string.menu_mag_card),
            getStringValue(R.string.menu_cpu_card),
            getStringValue(R.string.menu_m1_card),
            getStringValue(R.string.menu_dukpt),
            getStringValue(R.string.menu_mksk),
            getStringValue(R.string.menu_emv),
            getStringValue(R.string.menu_emv_param),
            getStringValue(R.string.menu_m0_card),
            getStringValue(R.string.menu_ntag_card),
            getStringValue(R.string.menu_sle4442_card),
            getStringValue(R.string.menu_psam_card),
            getStringValue(R.string.menu_felica_card),
            getStringValue(R.string.menu_rupay),
            getStringValue(R.string.menu_sign),
            getStringValue(R.string.menu_print_test),
            getStringValue(R.string.menu_trans_test),
            getStringValue(R.string.menu_oct_secure),
            getStringValue(R.string.menu_emulate_card),
            getStringValue(R.string.menu_usb_serial),
            getStringValue(R.string.menu_ped),
            getStringValue(R.string.menu_TPM)
    };

    private static HashMap<String, Fragment> hashMap = new HashMap<>();

    private String[] REQUIRED_PERMISSION_LIST;
    private List<String> mMissPermissions = new ArrayList<>();
    private static final int REQUEST_CODE = 1;

    private final String TAG = "MainActivity";

    static {
        hashMap.put(tabs[0], new LoginFragment());
        hashMap.put(tabs[1], new DeviceFragment());
        hashMap.put(tabs[2], new BeepFragment());
        hashMap.put(tabs[3], new LedFragment());
        hashMap.put(tabs[4], new PrinterFragment());
        hashMap.put(tabs[5], new ScannerFragment());
        hashMap.put(tabs[6], new SerialPortFragment());
        hashMap.put(tabs[7], new MagCardFragment());
        hashMap.put(tabs[8], new CpuCardFragment());
        hashMap.put(tabs[9], new M1CardFragment());
        hashMap.put(tabs[10], new DUKPTFragment());
        hashMap.put(tabs[11], new MKSKFragment());
        hashMap.put(tabs[12], new EMVFragment());
        hashMap.put(tabs[13], new EMVParamFragment());
        hashMap.put(tabs[14], new M0CardFragment());
        hashMap.put(tabs[15], new NTagCardFragment());
        hashMap.put(tabs[16], new SLE4442CardFragment());
        hashMap.put(tabs[17], new PSAMCardFragment());
        hashMap.put(tabs[18], new FelicaFragment());
        hashMap.put(tabs[19], new RuPayFragment());
        hashMap.put(tabs[20], new SignatureFragment());
        hashMap.put(tabs[21], new PrintTestFragment());
        hashMap.put(tabs[22], new TransTestFragment());
        hashMap.put(tabs[23], new OctopusSecureFragment());
        hashMap.put(tabs[24], new EmulateCardFragment());
        hashMap.put(tabs[25], new UsbSerialFragment());
        hashMap.put(tabs[26], new PedFragment());
        hashMap.put(tabs[27], new TpmFragment());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
        checkAndRequestPermissions();
    }

    private void initView() {
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return getTabItem(position);
            }

            @Override
            public int getCount() {
                return tabs.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return tabs[position];
            }
        });

        for (int i = 0; i < tabs.length; i++) {
            tableLayout.addTab(tableLayout.newTab().setText(tabs[i]));
        }

        tableLayout.setupWithViewPager(mViewPager);
        if (Build.MODEL.equals("H9PRO")) {
            RelativeLayout managerLayout = findViewById(R.id.managerLayout);
            managerLayout.setVisibility(View.GONE);
        } else {
            tvSdkVersion.setText("YSDK-" + getSdkVersion(this));
            tvVersion.setText("YDemo-" + getVersionName(this));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public Fragment getTabItem(int position) {
        return hashMap.get(tabs[position]);
    }

    private String getVersionName(Context context) {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "0";
        }
    }

    private String getSdkVersion(Context context) {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo("com.morefun.ysdk", 0);
            return pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "Not Install";
        }
    }

    private static String getStringValue(int resId) {
        return MyApplication.getInstance().getString(resId);
    }

    private void checkAndRequestPermissions() {
        mMissPermissions.clear();
        REQUIRED_PERMISSION_LIST = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,

        };
        for (String permission : REQUIRED_PERMISSION_LIST) {
            int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                mMissPermissions.add(permission);
            }
        }
        // check permissions has granted
        if (!mMissPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    mMissPermissions.toArray(new String[mMissPermissions.size()]),
                    REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            for (int i = grantResults.length - 1; i >= 0; i--) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    mMissPermissions.remove(permissions[i]);
                }
            }
        }
        // Get permissions success or not
        if (!mMissPermissions.isEmpty()) {
            Log.e(TAG, "Some permissions are not allowed exit!");
            //finish();
        }
    }
}