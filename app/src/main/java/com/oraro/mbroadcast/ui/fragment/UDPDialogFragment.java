package com.oraro.mbroadcast.ui.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.oraro.mbroadcast.R;
import com.oraro.mbroadcast.listener.IDialogFragListener;
import com.oraro.mbroadcast.model.DeviceEntity;
import com.oraro.mbroadcast.utils.UIUtils;

/**
 * Created by Administrator on 2016/10/9 0009.
 */
public class UDPDialogFragment extends DialogFragment {


    private String mButtonText = "";
    private String mButton1Text = "";
    private String mIp = "";
    private String mMac = "";
    private IDialogFragListener mIDialogFragListener;
    private TextView mText_Ip;
    private TextView mText_Mac;
    private Button btn_connected;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = setViewParams(inflater, container, savedInstanceState);
        initViews(view);
        return view;
    }

    public void setOnButtonClickListener(IDialogFragListener iDialogFragListener) {
        mIDialogFragListener = iDialogFragListener;
    }

    public void setText(String buttonText) {
        if (null != btn_connected) {
            btn_connected.setText(buttonText);
        }
    }

    public void setStatus(String ip, String mac, String buttonText, String button2Text) {
        mIp = ip;
        mMac = mac;
        mButtonText = buttonText;
        mButton1Text = button2Text;
    }

    public DeviceEntity getDeviceEntity() {
        DeviceEntity deviceEntity = new DeviceEntity();
        deviceEntity.setIp(mText_Ip.getText().toString());
        deviceEntity.setMac(mText_Mac.getText().toString());
        return deviceEntity;
    }

    private void initViews(View view) {
        mText_Ip = (TextView) view.findViewById(R.id.text_ip);
        mText_Mac = (TextView) view.findViewById(R.id.text_mac);
        btn_connected = (Button) view.findViewById(R.id.btn_connected);
        Button btn_add = (Button) view.findViewById(R.id.btn_add);
        btn_connected.setOnClickListener(mOnClickListener);
        btn_add.setOnClickListener(mOnClickListener);
        btn_connected.setText(mButtonText);
        btn_add.setText(mButton1Text);
        mText_Ip.setText(mIp);
        mText_Mac.setText(mMac);
    }

    private View setViewParams(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Window window = getDialog().getWindow();
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.layout_udp_dialog, ((ViewGroup) window.findViewById(android.R.id.content)), false);
//      window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        UIUtils toolUtils = new UIUtils();
        window.setLayout(toolUtils.getDisplayMetrics(getActivity()).widthPixels / 2,
                toolUtils.getDisplayMetrics(getActivity()).heightPixels / 3);

        return view;

    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_connected:
                    mIDialogFragListener.onButton1Click1(mText_Mac.getText().toString() + "");
                    break;
                case R.id.btn_add:
                    mIDialogFragListener.onButton2Click(mText_Mac.getText().toString() + "");
                    break;

                default:

                    break;
            }
        }
    };
}
