package com.morefun.ysdk.sample.fragment;

import android.os.Bundle;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.morefun.ysdk.sample.R;
import com.morefun.ysdk.sample.device.DeviceHelper;
import com.morefun.ysdk.sample.utils.DialogUtils;
import com.morefun.ysdk.sample.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginFragment extends Fragment {

    @BindView(R.id.et_businessId)
    EditText et_businessId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick({R.id.btn_login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                login();
                break;
        }
    }

    private void login() {
        Bundle bundle = new Bundle();
        try {
            String businessId = et_businessId.getText().toString();
            if (businessId.length() != 8) {
                ToastUtils.show(getContext(), getString(R.string.tip_login_business_id_error));
                return;
            }

            int ret = DeviceHelper.getDeviceService().login(bundle, businessId);
            if (ret == 0) {
                DialogUtils.showAlertDialog(getActivity(), getString(R.string.tip_login_success));
                return;
            }
            DialogUtils.showAlertDialog(getActivity(), getString(R.string.tip_login_error));
            return;
        } catch (RemoteException e) {
            ToastUtils.show(getContext(), e.getMessage());
        } catch (NullPointerException e) {
            ToastUtils.show(getContext(), e.getMessage());
        }
    }

}
