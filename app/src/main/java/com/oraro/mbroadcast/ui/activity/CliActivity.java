package com.oraro.mbroadcast.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.oraro.mbroadcast.Constants;
import com.oraro.mbroadcast.MBroadcastApplication;
import com.oraro.mbroadcast.R;
import com.oraro.mbroadcast.dao.DBManager;
import com.oraro.mbroadcast.listener.IDialogFragListener;
import com.oraro.mbroadcast.listener.IReceiveMsg;
import com.oraro.mbroadcast.mina.client.MinaStringClientHandler;
import com.oraro.mbroadcast.mina.client.MinaStringClientThread;
import com.oraro.mbroadcast.model.DeviceEntity;
import com.oraro.mbroadcast.model.PlayEntry;
import com.oraro.mbroadcast.service.IMyAidlInterface;
import com.oraro.mbroadcast.test.ConstantUtil;
import com.oraro.mbroadcast.udpthread.UDPReceiveThread;
import com.oraro.mbroadcast.ui.fragment.UDPDialogFragment;
import com.oraro.mbroadcast.utils.DeviceListControl;
import com.oraro.mbroadcast.utils.SPUtils;
import com.oraro.mbroadcast.vo.PlayVO;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class CliActivity extends FragmentActivity {

    private ToggleButton mSwitchButton;

    private UDPReceiveThread mUDPReceiveThread;

    private final static int CSPORT = 4436;

    private DeviceListControl mDeviceListControl;

    private Handler mHeartHandler;

    private Runnable mHeartRunnable;

    //tuoji list
    private LinearLayout mConnectedLinearLayout;

    private List<RelativeLayout> mConnectedList = new ArrayList<>();

    private UDPDialogFragment mConnecteddialog;

    //lianjie list
    private LinearLayout mConnectingLinearLayout;

    private List<RelativeLayout> mConnectingList = new ArrayList<>();

    private UDPDialogFragment mConnectingdialog;

    //black list
    private LinearLayout mBlackLinearLayout;

    private List<RelativeLayout> mBlackList = new ArrayList<>();

    private UDPDialogFragment mBlackdialog;


    private boolean flag = true;

    private final static String KEY = "STATUS";


    private final int MSG_ADDDEVICE = 0;
    private final int MSG_RMVDEVICE = 1;
    private final int MSG_MINACONNE = 2;
    private final int MSG_RMVCONNED = 3;
    private final int MSG_ADDBLACK = 4;
    private final int MSG_RMVBLACK = 5;
    private final int MSG_SENBLACK = 6;
    private final int MSG_DISCONNE = 7;
    private final int MSG_ERROTCON = 8;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews(savedInstanceState);
        mUDPReceiveThread =  UDPReceiveThread.newInstance();
        mDeviceListControl = new DeviceListControl();

        if (SPUtils.getPrefInt(CliActivity.this, KEY, 0) == 0) {
            mSwitchButton.setChecked(false);
            closeAllService();
        } else if (SPUtils.getPrefInt(CliActivity.this, KEY, 0) == 1) {
            mSwitchButton.setChecked(true);
            startAllService();
            addWhiteViews();
            //checkoutIsConnected();
        }

        addBlackViews();
    }

    private void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_client);
        mSwitchButton = (ToggleButton) findViewById(R.id.switch_button);
        mSwitchButton.setOnCheckedChangeListener(mOnCheckedChangeListener);

        mConnectedLinearLayout = (LinearLayout) findViewById(R.id.connected_linear);
        mConnectingLinearLayout = (LinearLayout) findViewById(R.id.connecting_linear);
        mBlackLinearLayout = (LinearLayout) findViewById(R.id.black_linear);

        TextView tv_back = (TextView) findViewById(R.id.header_left_btn);
        findViewById(R.id.click).setVisibility(View.GONE);
        findViewById(R.id.click1).setVisibility(View.GONE);
        findViewById(R.id.header_left_img).setVisibility(View.GONE);
        tv_back.setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.header_text)).setText("设备连接");
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private IDialogFragListener mIDialogFragListener = new IDialogFragListener() {
        @Override
        public void onButton1Click1(String mac) {
            DeviceEntity deviceEntity = DBManager.getInstance(CliActivity.this).queryDeviceEntityByMac(mac);
            if (deviceEntity.getStatus() == 0 && !deviceEntity.getIsblack()) {
                if (flag) {
                    flag = false;
                    mConnectingdialog.setText("正在连接");
                    ConstantUtil.WEB_MATCH_PATH = mConnectingdialog.getDeviceEntity().getIp();
                    MinaStringClientThread minaThread = new MinaStringClientThread();
                    minaThread.setCallback(mMinaConnectSuccess);
                    minaThread.setType(Constants.MINA_TEST);
                    minaThread.setIp(mConnectingdialog.getDeviceEntity().getIp());
                    PlayEntry playEntry = new PlayEntry();
                    playEntry.setTextDesc("欢迎使用地面广播系统");
                    minaThread.setPlayVO(new PlayVO(playEntry));
                    MinaStringClientThread.getThreadPoolExecutor().execute(minaThread);
                }

            } else if (deviceEntity.getStatus() == 1 && !deviceEntity.getIsblack()) {
                disconnectedInSP(deviceEntity.getIp());
                deviceEntity.setStatus(-1);
                DBManager.getInstance(CliActivity.this).update(deviceEntity, DBManager.getInstance(CliActivity.this).getDeviceEntityDao(DBManager.WRITE_ONLY));
                Message message = new Message();
                message.arg1 = MSG_RMVCONNED;
                message.obj = DBManager.getInstance(CliActivity.this).queryDeviceEntityByMac(deviceEntity.getMac());
                mHandler.sendMessage(message);
                mConnecteddialog.dismiss();
            } else if (deviceEntity.getIsblack()) {
                deviceEntity.setStatus(-1);
                deviceEntity.setIsblack(false);
                DBManager.getInstance(CliActivity.this).update(deviceEntity, DBManager.getInstance(CliActivity.this).getDeviceEntityDao(DBManager.WRITE_ONLY));
                Message message = new Message();
                message.arg1 = MSG_RMVBLACK;
                message.obj = deviceEntity;
                mHandler.sendMessage(message);
                mBlackdialog.dismiss();
            }
        }


        @Override
        public void onButton2Click(String mac) {
            DeviceEntity deviceEntity = DBManager.getInstance(CliActivity.this).queryDeviceEntityByMac(mac);
            if (deviceEntity.getStatus() == 0 && !deviceEntity.getIsblack()) {
                deviceEntity.setStatus(-1);
                deviceEntity.setIsblack(true);
                DBManager.getInstance(CliActivity.this).update(deviceEntity, DBManager.getInstance(CliActivity.this).getDeviceEntityDao(DBManager.WRITE_ONLY));
                Message message = new Message();
                message.arg1 = MSG_ADDBLACK;
                message.obj = deviceEntity;
                mHandler.sendMessage(message);
                mConnectingdialog.dismiss();
            } else if (deviceEntity.getStatus() == 1 && !deviceEntity.getIsblack()) {
                disconnectedInSP(deviceEntity.getIp());
                deviceEntity.setStatus(-1);
                deviceEntity.setIsblack(true);
                DBManager.getInstance(CliActivity.this).update(deviceEntity, DBManager.getInstance(CliActivity.this).getDeviceEntityDao(DBManager.WRITE_ONLY));
                Message message = new Message();
                message.arg1 = MSG_SENBLACK;
                message.obj = deviceEntity;
                mHandler.sendMessage(message);
                mConnecteddialog.dismiss();
            } else if (deviceEntity.getStatus() == -1 && deviceEntity.getIsblack()) {
                mBlackdialog.dismiss();
            }

        }
    };

    private void disconnectedInSP(String ip) {
        Set set = SPUtils.getPrefStringSet(CliActivity.this,"set",null);
        if (null != set) {
            set.remove(ip);
        }
        SPUtils.setPrefStringSet(CliActivity.this,"set",set);
    }

    private MinaStringClientHandler.MinaConnectSuccess mMinaConnectSuccess = new MinaStringClientHandler.MinaConnectSuccess() {

        @Override
        public void connectSuccessCallback(String ip) {
            DeviceEntity deviceEntity = DBManager.getInstance(CliActivity.this).queryDeviceEntityByIP(ip);
            if (deviceEntity.getStatus() == 0) {
                deviceEntity.setStatus(1);
                DBManager.getInstance(CliActivity.this).update(deviceEntity, DBManager.getInstance(CliActivity.this).getDeviceEntityDao(DBManager.WRITE_ONLY));
                Message message = new Message();
                message.obj = DBManager.getInstance(CliActivity.this).queryDeviceEntityByMac(deviceEntity.getMac());
                message.arg1 = MSG_MINACONNE;
                mHandler.sendMessage(message);
                mConnectingdialog.dismiss();
                flag = true;

            } else if (deviceEntity.getStatus() == 1) {
//                Log.e("zzx", "zzx = " + deviceEntity.toString());
            }
        }

        @Override
        public void connectFailCallback(String ip) {
            disconnectedInSP(ip);
            DeviceEntity deviceEntity = DBManager.getInstance(CliActivity.this).queryDeviceEntityByIP(ip);
            if (deviceEntity.getStatus() == 0) {
                Message message = new Message();
                message.arg1 = MSG_ERROTCON;
                mHandler.sendMessage(message);
            } else if (deviceEntity.getStatus() == 1) {
                deviceEntity.setStatus(-1);
                DBManager.getInstance(CliActivity.this).update(deviceEntity, DBManager.getInstance(CliActivity.this).getDeviceEntityDao(DBManager.WRITE_ONLY));
                Message message = new Message();
                message.obj = deviceEntity;
                message.arg1 = MSG_DISCONNE;
                mHandler.sendMessage(message);
            }
        }
    };

    private void startAllService() {
        SPUtils.setPrefInt(CliActivity.this, KEY, 1);
        if (null == mUDPReceiveThread) {
            mUDPReceiveThread = UDPReceiveThread.newInstance();
        }
        mUDPReceiveThread.startUDPReceiveThread(CSPORT, receiveMsg());
        if (null == mHeartHandler) {
            mHeartHandler = new Handler();
        }
        mHeartHandler.post(getHeartRunnable());
    }

    private void closeAllService() {
        SPUtils.setPrefInt(CliActivity.this, KEY, 0);
        if (null != mHeartHandler) {
            mHeartHandler.removeCallbacks(mHeartRunnable);
        }
        mUDPReceiveThread.interrupt();
        mUDPReceiveThread = null;
        mConnectingLinearLayout.removeAllViews();
        closeAllConnection();
    }

    private CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (!buttonView.isPressed()) {
                return;
            }
            if (isChecked) {
                startAllService();

            } else {
                closeAllService();
            }
        }
    };

    private Runnable getHeartRunnable() {
        if (null == mHeartRunnable) {
            mHeartRunnable = new Runnable() {
                @Override
                public void run() {
                    List<DeviceEntity> list = DBManager.getInstance(CliActivity.this).queryDeviceEntityByStatus(0);
                    for (int i = 0; i < list.size(); i++) {
                        DeviceEntity temp = list.get(i);
                        if (System.currentTimeMillis() - temp.getValues() >= 1000 * 4) {
                            temp.setStatus(-1);
                            DBManager.getInstance(CliActivity.this).update(temp, DBManager.getInstance(CliActivity.this).getDeviceEntityDao(DBManager.WRITE_ONLY));
                            Message message = new Message();
                            message.obj = temp;
                            message.arg1 = MSG_RMVDEVICE;
                            mHandler.sendMessage(message);
                        }
                    }
                    mHeartHandler.postDelayed(mHeartRunnable, 1000 * 1);
                }
            };
        }
        return mHeartRunnable;
    }

    private void closeAllConnection() {

        List<DeviceEntity> list = DBManager.getInstance(CliActivity.this).queryDeviceEntityByStatus(1);
        for (int i = 0; i < list.size(); i++) {
            DeviceEntity temp = list.get(i);
            temp.setStatus(-1);
            DBManager.getInstance(CliActivity.this).update(temp, DBManager.getInstance(CliActivity.this).getDeviceEntityDao(DBManager.WRITE_ONLY));
            disconnectedInSP(temp.getIp());
        }
        mConnectedList = null;
        mConnectingList = null;
        mConnectedList = new ArrayList<>();
        mConnectingList = new ArrayList<>();

        mConnectedLinearLayout.removeAllViews();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.arg1 == MSG_ADDDEVICE) {
                if (checkItemIsExit(mConnectingList, msg.obj) == -1) {
                    inflateView(mConnectingLinearLayout, msg.obj, 1);
                }
            } else if (msg.arg1 == MSG_RMVDEVICE) {
                if (checkItemIsExit(mConnectingList, msg.obj) != -1) {
                    removeView(mConnectingLinearLayout, msg.obj, 1);
                    removeFromList(mConnectingList, msg.obj);
                }
                if (null != mConnectingdialog) {
                    mConnectingdialog.dismiss();
                }

            } else if (msg.arg1 == MSG_MINACONNE) {
                try {
                    IMyAidlInterface iMyAidlInterface = MBroadcastApplication.getIMyAidlInterface();
                    if (null != iMyAidlInterface) {
                        MBroadcastApplication.getIMyAidlInterface().refresh();
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                if (checkItemIsExit(mConnectingList, msg.obj) != -1) {
                    removeView(mConnectingLinearLayout, msg.obj, 1);
                    removeFromList(mConnectingList, msg.obj);
                }

                inflateView(mConnectedLinearLayout, msg.obj, 0);
                Set set = new HashSet();
                for (int i = 0; i < mConnectedList.size(); i++) {
                    RelativeLayout temp = mConnectedList.get(i);
                    TextView tv_ip = (TextView) temp.findViewById(R.id.text_ip);
                    set.add(tv_ip.getText().toString());
                }
                SPUtils.setPrefStringSet(CliActivity.this,"set",set);



            } else if (msg.arg1 == MSG_RMVCONNED) {
                if (checkItemIsExit(mConnectedList, msg.obj) != -1) {
                    removeView(mConnectedLinearLayout, msg.obj, 0);
                    removeFromList(mConnectedList, msg.obj);
                }
            } else if (msg.arg1 == MSG_ADDBLACK) {
                if (checkItemIsExit(mConnectingList, msg.obj) != -1) {
                    removeView(mConnectingLinearLayout, msg.obj, 1);
                    removeFromList(mConnectingList, msg.obj);
                }
                addBlackViews();
            } else if (msg.arg1 == MSG_RMVBLACK) {
                if (checkItemIsExit(mBlackList, msg.obj) != -1) {
                    removeView(mBlackLinearLayout, msg.obj, 1);
                    removeFromList(mBlackList, msg.obj);
                }
            } else if (msg.arg1 == MSG_SENBLACK) {
                if (checkItemIsExit(mConnectedList, msg.obj) != -1) {
                    removeView(mConnectedLinearLayout, msg.obj, 1);
                    removeFromList(mConnectedList, msg.obj);
                }
                addBlackViews();
            } else if (msg.arg1 == MSG_DISCONNE) {
                if (checkItemIsExit(mConnectedList, msg.obj) != -1) {
                    removeView(mConnectedLinearLayout, msg.obj, 1);
                    removeFromList(mConnectedList, msg.obj);
                }
            } else if (msg.arg1 == MSG_ERROTCON) {
                flag = true;
                Toast.makeText(CliActivity.this, "连接异常", Toast.LENGTH_LONG).show();
                mConnectingdialog.dismiss();
            }
        }
    };

    private void addBlackViews() {
        List<DeviceEntity> list = DBManager.getInstance(CliActivity.this).queryDeviceEntityByBlack(true);
        mBlackLinearLayout.removeAllViews();
        for (int i = 0; i < list.size(); i++) {
            inflateView(mBlackLinearLayout, list.get(i), 2);
        }
    }

    private void checkoutIsConnected() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                List<DeviceEntity> list = DBManager.getInstance(CliActivity.this).queryDeviceEntityByStatus(1);
                for (int i = 0; i < list.size(); i++) {
                    DeviceEntity deviceEntity = list.get(i);
                    MinaStringClientThread minaThread = new MinaStringClientThread();
                    minaThread.setCallback(mMinaConnectSuccess);
                    minaThread.setType(Constants.MINA_TEST_CONNECT_int);
                    minaThread.setIp(deviceEntity.getIp());
                    PlayEntry playEntry = new PlayEntry();
                    playEntry.setTextDesc("test");
                    minaThread.setPlayVO(new PlayVO(playEntry));
                    MinaStringClientThread.getThreadPoolExecutor().execute(minaThread);
                }
            }
        }.start();
    }

    private void addWhiteViews() {
        List<DeviceEntity> list = DBManager.getInstance(CliActivity.this).queryDeviceEntityByStatus(1);
        mConnectedLinearLayout.removeAllViews();
        for (int i = 0; i < list.size(); i++) {
            inflateView(mConnectedLinearLayout, list.get(i), 0);
        }


    }

    private int checkItemIsExit(List<RelativeLayout> list, Object obj) {
        DeviceEntity deviceEntity = (DeviceEntity) obj;
        for (int i = 0; i < list.size(); i++) {
            RelativeLayout tempLayout = list.get(i);
            TextView macTxt = (TextView) tempLayout.findViewById(R.id.text_mac);
            if (deviceEntity.getMac().equals(macTxt.getText().toString())) {
                return i;
            }
        }
        return -1;
    }


    private void removeFromList(List<RelativeLayout> list, Object obj) {
        DeviceEntity deviceEntity = (DeviceEntity) obj;
        int i = checkItemIsExit(list, deviceEntity);
        if (i >= 0) {
            list.remove(i);
        }
    }

    private void removeView(ViewGroup parentView, Object obj, int flag) {
        DeviceEntity deviceEntity = (DeviceEntity) obj;
        RelativeLayout temp = (RelativeLayout) parentView.findViewWithTag(deviceEntity.getMac());
        parentView.removeView(temp);
    }

    private void inflateView(ViewGroup parentView, Object obj, int flag) {
        DeviceEntity deviceEntity = (DeviceEntity) obj;

        LayoutInflater inflater = LayoutInflater.from(parentView.getContext());
        RelativeLayout device_item = (RelativeLayout) inflater.inflate(R.layout.udp_list_item, null, false);
        device_item.setTag(deviceEntity.getMac());
        if (flag == 1) {
            mConnectingList.add(device_item);
        } else if (flag == 0) {
            mConnectedList.add(device_item);
        } else if (flag == 2) {
            mBlackList.add(device_item);
        }
        parentView.addView(device_item);
        ImageView connectedImg = (ImageView) device_item.findViewById(R.id.img_connected);

        TextView ipTxt = (TextView) device_item.findViewById(R.id.text_ip);

        TextView macTxt = (TextView) device_item.findViewById(R.id.text_mac);

        ImageView statusImg = (ImageView) device_item.findViewById(R.id.img_status);

        device_item.setOnClickListener(mOnClickListener);

        ipTxt.setText(deviceEntity.getIp());

        macTxt.setText(deviceEntity.getMac());

        if (parentView.getId() == R.id.connected_linear) {
        } else if (parentView.getId() == R.id.connecting_linear) {
            connectedImg.setVisibility(View.INVISIBLE);
            statusImg.setVisibility(View.INVISIBLE);
        } else if (parentView.getId() == R.id.black_linear) {
            connectedImg.setVisibility(View.INVISIBLE);
            statusImg.setVisibility(View.INVISIBLE);
        }

    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DeviceEntity deviceEntity = DBManager.getInstance(CliActivity.this).queryDeviceEntityByMac(v.getTag().toString());
            if (null != deviceEntity) {
                if (deviceEntity.getStatus() == 0 && !deviceEntity.getIsblack()) {
                    mConnectingdialog = new UDPDialogFragment();
                    mConnectingdialog.setStatus(deviceEntity.getIp(), deviceEntity.getMac(), "建立连接", "屏蔽连接");
                    mConnectingdialog.setOnButtonClickListener(mIDialogFragListener);
                    mConnectingdialog.show(getFragmentManager(), "mConnectingdialog");
                } else if (deviceEntity.getStatus() == 1 && !deviceEntity.getIsblack()) {
                    mConnecteddialog = new UDPDialogFragment();
                    mConnecteddialog.setStatus(deviceEntity.getIp(), deviceEntity.getMac(), "断开连接", "屏蔽连接");
                    mConnecteddialog.setOnButtonClickListener(mIDialogFragListener);
                    mConnecteddialog.show(getFragmentManager(), "mConnecteddialog");
                } else if (deviceEntity.getIsblack()) {
                    mBlackdialog = new UDPDialogFragment();
                    mBlackdialog.setStatus(deviceEntity.getIp(), deviceEntity.getMac(), "恢复连接", "保持屏蔽");
                    mBlackdialog.setOnButtonClickListener(mIDialogFragListener);
                    mBlackdialog.show(getFragmentManager(), "mBlackdialog");
                }
            }
        }
    };

    private boolean checkIsInBlack(DeviceEntity deviceEntity) {
        List<DeviceEntity> blackList = DBManager.getInstance(CliActivity.this).queryDeviceEntityByBlack(true);
        for (int i = 0; i < blackList.size(); i++) {
            if (deviceEntity.getMac().equals(blackList.get(i).getMac())) {
                return true;
            }
        }
        return false;
    }


    private IReceiveMsg receiveMsg() {
        IReceiveMsg iReceiveMsg = new IReceiveMsg() {
            @Override
            public void receiveMsg(String msg) {
                if (SPUtils.getPrefInt(CliActivity.this, KEY, -1) == 1) {
                    DeviceEntity deviceEntity = mDeviceListControl.getDeviceEntity(msg);
                    if (!checkIsInBlack(deviceEntity)) {
                        DeviceEntity temp = DBManager.getInstance(CliActivity.this).queryDeviceEntityByMac(deviceEntity.getMac());
                        if (temp == null) {
                            DBManager.getInstance(CliActivity.this).insertOrUpdate(deviceEntity, System.currentTimeMillis(), 0, false);
                            Message message = new Message();
                            message.obj = DBManager.getInstance(CliActivity.this).queryDeviceEntityByMac(deviceEntity.getMac());
                            message.arg1 = MSG_ADDDEVICE;
                            mHandler.sendMessage(message);
                        } else if (temp.getStatus() == -1) {
                            temp.setStatus(0);
                            temp.setValues(System.currentTimeMillis());
                            DBManager.getInstance(CliActivity.this).update(temp, DBManager.getInstance(CliActivity.this).getDeviceEntityDao(DBManager.WRITE_ONLY));
                            Message message = new Message();
                            message.obj = DBManager.getInstance(CliActivity.this).queryDeviceEntityByMac(deviceEntity.getMac());
                            message.arg1 = MSG_ADDDEVICE;
                            mHandler.sendMessage(message);
                        } else if (temp.getStatus() == 0) {
                            temp.setValues(System.currentTimeMillis());
                            DBManager.getInstance(CliActivity.this).update(temp, DBManager.getInstance(CliActivity.this).getDeviceEntityDao(DBManager.WRITE_ONLY));
                            Message message = new Message();
                            message.obj = DBManager.getInstance(CliActivity.this).queryDeviceEntityByMac(deviceEntity.getMac());
                            message.arg1 = MSG_ADDDEVICE;
                            mHandler.sendMessage(message);
                        }
                    }
                }
            }
        };
        return iReceiveMsg;
    }

}