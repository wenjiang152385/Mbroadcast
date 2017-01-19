package com.oraro.mbroadcast.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.oraro.mbroadcast.Constants;
import com.oraro.mbroadcast.MBroadcastApplication;
import com.oraro.mbroadcast.R;
import com.oraro.mbroadcast.listener.IDialogFragment;
import com.oraro.mbroadcast.model.SimpleEvent;
import com.oraro.mbroadcast.service.DoubleService;
import com.oraro.mbroadcast.udpthread.UDPReceiveThread;
import com.oraro.mbroadcast.udpthread.UDPSendThread;
import com.oraro.mbroadcast.utils.SPUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Administrator on 2016/12/14 0014.
 */
public class DeviceFragment extends Fragment {

    private ImageView mIv_pad;
    private ImageView mIv_radio;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device, null, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        ((TextView)view.findViewById(R.id.header_text)).setText("设备选择");
        view.findViewById(R.id.header_left_btn).setOnClickListener(mOnClickListener);
        view.findViewById(R.id.header_left_img).setVisibility(View.GONE);
        view.findViewById(R.id.header_left_btn).setVisibility(View.VISIBLE);
        mIv_pad = (ImageView) view.findViewById(R.id.iv_pad);
        mIv_pad.setOnClickListener(mOnClickListener);
        mIv_radio = (ImageView) view.findViewById(R.id.iv_radio);
        mIv_radio.setOnClickListener(mOnClickListener);
        if (SPUtils.getPrefInt(getActivity(), "deviceType", -1) == 0) {
            mIv_radio.setImageResource(R.mipmap.radio_pressed);
        } else if (SPUtils.getPrefInt(getActivity(), "deviceType", -1) == 1) {
            mIv_pad.setImageResource(R.mipmap.pad_pressed);
        }

    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.header_left_btn:
                    EventBus.getDefault().post(new SimpleEvent(Constants.BACK_MAIN_CLOSE));
                    break;

                case R.id.iv_radio:
                    mIv_pad.setFocusable(false);
                    mIv_pad.setEnabled(false);
                    if (SPUtils.getPrefInt(getActivity(), "deviceType", -1) == 0) {
                        setSTYLE(0);
                        return;
                    }
                    showDialog("提示", "确认当前设备为音箱？", "确认", 0);
                    UDPReceiveThread udpReceiveThread = UDPReceiveThread.newInstance();
                    if (null != udpReceiveThread) {
                        udpReceiveThread.setInterrupted(true);
                    }
                    break;

                case R.id.iv_pad:
                    mIv_radio.setFocusable(false);
                    mIv_radio.setEnabled(false);
                    if (SPUtils.getPrefInt(getActivity(), "deviceType", -1) == 1) {
                        setSTYLE(1);
                        return;
                    }
                    showDialog("提示", "确认当前设备为平板？", "确认", 1);
                    UDPSendThread udpSendThread = UDPSendThread.newInstance();
                    if (null != udpSendThread) {
                        udpSendThread.setInterrupted(true);
                    }
                    break;

                default:
                    break;
            }
        }
    };


    private void setSTYLE(int flag) {
        String text = "";
        if (flag == 0) {
            text = "当前已经是音箱设备";
        } else if (flag == 1) {
            text = "当前已经是平板设备";
        }
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
        mIv_pad.setFocusable(true);
        mIv_pad.setEnabled(true);
        mIv_radio.setFocusable(true);
        mIv_radio.setEnabled(true);

    }


    private void showDialog(String title, String content, String submit, final int flag) {
        final SimpleDialogFragment simpleDialogFragment = new SimpleDialogFragment();
        simpleDialogFragment.show(getActivity().getFragmentManager(), "simpleDialogFragment");
        simpleDialogFragment.setTitle(title);
        simpleDialogFragment.setContent(content);
        simpleDialogFragment.setSubmit(submit);

        simpleDialogFragment.setDismissCallback(new SimpleDialogFragment.DismissCallback() {
            @Override
            public void dismissCallback() {
                mIv_pad.setFocusable(true);
                mIv_pad.setEnabled(true);
                mIv_radio.setFocusable(true);
                mIv_radio.setEnabled(true);
            }
        });

        simpleDialogFragment.setOnButtonClickListener(new IDialogFragment() {
            @Override
            public void onDialogFragmentButtonClickListener() {
                if (flag == 0) {
                    mIv_radio.setImageResource(R.mipmap.radio_pressed);
                    mIv_pad.setImageResource(R.mipmap.pad_unpressed);
                    SPUtils.setPrefInt(getActivity(),"STATUS",0);
                } else if (flag == 1) {
                    mIv_radio.setImageResource(R.mipmap.radio_unpressed);
                    mIv_pad.setImageResource(R.mipmap.pad_pressed);
                }
                SPUtils.setPrefInt(getActivity(), "deviceType", flag);
                DoubleService.getInstance().StartServices(MBroadcastApplication.getMyContext(), flag);
                simpleDialogFragment.dismiss();
                EventBus.getDefault().post(new SimpleEvent(Constants.BACK_MAIN_CLOSE));
            }
        });
    }
}
