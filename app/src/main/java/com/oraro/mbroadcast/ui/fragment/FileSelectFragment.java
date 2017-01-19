package com.oraro.mbroadcast.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.oraro.mbroadcast.Constants;
import com.oraro.mbroadcast.MBroadcastApplication;
import com.oraro.mbroadcast.R;
import com.oraro.mbroadcast.dao.DBManager;
import com.oraro.mbroadcast.mina.client.MinaFileClientThread;
import com.oraro.mbroadcast.mina.client.MinaStringClientThread;
import com.oraro.mbroadcast.model.SimpleEvent;
import com.oraro.mbroadcast.mp.PlayAudio;
import com.oraro.mbroadcast.proxy.TTSProXy;
import com.oraro.mbroadcast.service.IMyAidlInterface;
import com.oraro.mbroadcast.service.OnRefreshUIListener;
import com.oraro.mbroadcast.ui.activity.FlightExcelActivity;
import com.oraro.mbroadcast.ui.activity.MainActivity;
import com.oraro.mbroadcast.utils.LogUtils;
import com.oraro.mbroadcast.utils.MD5Util;
import com.oraro.mbroadcast.utils.PlayStateUtils;
import com.oraro.mbroadcast.utils.SPUtils;
import com.oraro.mbroadcast.vo.PlayVO;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by admin on 2016/9/6
 *
 * @author zmy
 */
public class FileSelectFragment extends DialogFragment implements View.OnClickListener {
    private static final String TAG = FileSelectFragment.class.getSimpleName();
    public static final int FILE_PICKER_REQUEST_CODES = 2;
    private TextView tv_path;
    private TextView tv_state;
    private Button btn_search;
    private Button btn_confirm;
    private Button btn_cancel;
    private MainActivity mainActivity;
    private String filePath;
    private PlayVO playVO;
    private DBManager dbManager;
    private String usePath;
    private String fromActivityFilePath;
    private String[] audioFlagArray;
    private int flag = -1;
    private Button btn_instant_play;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file_select, null);
        mainActivity = (MainActivity) getActivity();
        getPlayVO();
        initView(view);
        return view;
    }

    private void initView(View view) {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        tv_path = (TextView) view.findViewById(R.id.tv_path);
        tv_state = (TextView) view.findViewById(R.id.tv_state);
        btn_search = (Button) view.findViewById(R.id.btn_search);
        btn_confirm = (Button) view.findViewById(R.id.btn_confirm);
        btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        btn_instant_play = (Button) view.findViewById(R.id.btn_instant_play);

        btn_search.setOnClickListener(this);
        btn_instant_play.setOnClickListener(this);
        btn_confirm.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        audioFlagArray = new String[]{"mp3", "3gpp", "M4A", "WAV", "AMR", "AWB", "WMA", "OGG", "MID", "XMF", "RTTTL", "SMF", "IMY", "flac", "ape", "aac", "VQF", "eAAC+",};
        mainActivity.setOnClickFileListener(new MainActivity.OnClickFileListener() {
            @Override
            public void onClickFileListener(String path) {
                setConfirmBtn(true);
//                fromActivityFilePath = path;
                String text = (String) tv_path.getText();
//                String path1 = path.substring(path.indexOf(".") + 1);
//                for (String audio : audioFlagArray) {
//                    boolean isAudioFile = path1.equalsIgnoreCase(audio);
//                    if (isAudioFile) {
//                        flag = 1;
//                    }
//                }
//                if (flag == 1) {
//                    tv_path.setText(path);
//                    flag = -1;
//                } else {
//                    tv_path.setText(R.string.FileSelectFragment_path_wrong);
//                }
//                boolean isPlaying = (PlayAudio.getInstance().isPlaying() || TTSProXy.getInstance(getActivity(), getActivity().getPackageName()).isSpeeking()) && !MBroadcastApplication.isincout;

                if (path == null) {
                    tv_path.setText(R.string.FileSelectFragment_path_isnull);
                } else {
                    tv_path.setText(path);
                }
                if (text != null && !text.equals(path)) {
                    tv_state.setText(getString(R.string.FileSelectFragment_unused));
                    setInstantPlayBtn(false);
                } else {// 已使用
                    setConfirmBtn(false);
                    if (PlayStateUtils.isPlaying(getContext())) {
                        setInstantPlayBtn(false);
                    } else {
                        setInstantPlayBtn(true);
                    }
                }
            }
        });

        dbManager = DBManager.getInstance(getActivity());
        setUseState();
        tv_path.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        tv_path.requestFocus();
        tv_path.requestFocusFromTouch();
        tv_path.setFocusable(true);
        tv_path.setFocusableInTouchMode(true);
        if (playVO.getEntity().getId().equals(MBroadcastApplication.getPlayID())) {
            setCancelBtn(false);
        }
    }

    public void getPlayVO() {
        Bundle bundle = getArguments();
        playVO = bundle.getParcelable("playVO");
        filePath = playVO.getEntity().getFileParentPath();
    }

    @Subscribe(sticky = true)
    public void onEvent(SimpleEvent event) {/* Do something */
        if (event.getMsg() == 9823442) {
            setPlayCompleteBtn();// 播放完成改变button状态
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().removeStickyEvent(this);
    }

    private void setPlayCompleteBtn() {
        if (!TextUtils.isEmpty(usePath)) {
            setInstantPlayBtn(true);
        }
        if (!TextUtils.isEmpty(filePath)) {
            setInstantPlayBtn(true);
        }
        setCancelBtn(true);
//        if (!TextUtils.isEmpty(fromActivityFilePath) && !fromActivityFilePath.equals(filePath)) {
//            return;
//        }
    }

    private void setUseState() {
//        boolean isPlaying = (PlayAudio.getInstance().isPlaying() || TTSProXy.getInstance(getActivity(), getActivity().getPackageName()).isSpeeking()) && !MBroadcastApplication.isincout;
        setConfirmBtn(false);
        if (filePath != null) {
            tv_state.setText(getString(R.string.FileSelectFragment_used));
            tv_path.setText(filePath);
            setInstantPlayBtn(true);
        }
        if (filePath != null && PlayStateUtils.isPlaying(getContext())) {
            setInstantPlayBtn(false);
        }
        if (filePath == null) {
            setInstantPlayBtn(false);
        }
    }

    /**
     * 设置确认按钮是否可用
     *
     * @param isEnable
     */
    private void setConfirmBtn(boolean isEnable) {
        if (isEnable) {
            btn_confirm.setBackgroundResource(R.mipmap.record_dialog);
        } else {
            btn_confirm.setBackgroundResource(R.mipmap.ttsdialog_btn);
        }
        btn_confirm.setEnabled(isEnable);
    }

    /**
     * 设置立即播放按钮是否可用
     *
     * @param isEnable
     */
    private void setInstantPlayBtn(boolean isEnable) {
        if (isEnable) {
            btn_instant_play.setBackgroundResource(R.mipmap.record_dialog);
        } else {
            btn_instant_play.setBackgroundResource(R.mipmap.ttsdialog_btn);
        }
        btn_instant_play.setEnabled(isEnable);
    }

    /**
     * 设置取消按钮是否可用
     *
     * @param isEnable
     */
    private void setCancelBtn(boolean isEnable) {
        if (isEnable) {
            btn_cancel.setBackgroundResource(R.mipmap.delete_dialog);
        } else {
            btn_cancel.setBackgroundResource(R.mipmap.ttsdialog_btn);
        }
        btn_cancel.setEnabled(isEnable);
    }

    IMyAidlInterface iMyAidlInterface = MBroadcastApplication.getIMyAidlInterface();

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_search:
                Intent intent = new Intent(getActivity(), FlightExcelActivity.class);
                intent.putExtra("type", 2);
                getActivity().startActivityForResult(intent, FILE_PICKER_REQUEST_CODES);
                break;
            case R.id.btn_confirm:
                Set set = SPUtils.getPrefStringSet(getContext(), "set", null);
                if (SPUtils.getPrefInt(getActivity(), "STATUS", -1) == 0 ||
                        SPUtils.getPrefInt(getActivity(), "STATUS", -1) == -1 ||
                        null == set || set.size() == 0) {
                    Toast.makeText(getActivity(),"未连接任何设备无法传输",Toast.LENGTH_SHORT).show();
                    return;
                }

                usePath = (String) tv_path.getText();
                if (usePath.contains(".")) {
                    playVO.getEntity().setFileParentPath(usePath);
                    //在Media解析完成后，将通知音响接收该Media文件
                    playVO.getEntity().setFileName(usePath.substring(usePath.lastIndexOf(File.separator) + 1));
                    dbManager.update(playVO.getEntity(), dbManager.getPlayEntryDao(DBManager.WRITE_ONLY));
                    tv_state.setText(getString(R.string.FileSelectFragment_used));
                    EventBus.getDefault().postSticky(new SimpleEvent(Constants.UPDATE_PLAYVO_ONE));

//                    ((DialogFragment) this.getParentFragment()).dismiss();

                        setInstantPlayBtn(!PlayStateUtils.isPlaying(getContext()));
                    setConfirmBtn(false);
                    try {
                        iMyAidlInterface.refresh();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    File file = new File(playVO.getEntity().getFileParentPath());
                    String MD5sum = MD5Util.getFileMD5String(file);
                    if (null != set) {
                        Iterator iterator = set.iterator();
                        while (iterator.hasNext()) {
                            String ip = (String) iterator.next();
                            Log.e("wjq","ip = " + ip);
                            MinaFileClientThread minaFileClientThread = new MinaFileClientThread();
                            minaFileClientThread.setType(Constants.MD_FILE_UPDATE);
                            minaFileClientThread.setPlayVO(playVO);
                            minaFileClientThread.setIp(ip);
                            minaFileClientThread.setMd5sum(MD5sum);
                            MinaStringClientThread.getThreadPoolExecutor().execute(minaFileClientThread);
                            Toast.makeText(getContext(), "开始向ip地址为" + minaFileClientThread.getIp() + "音响发送录音文件", Toast.LENGTH_LONG).show();
                            LogUtils.e(TAG, "send media file message ip = " + ip);
                            LogUtils.e(TAG, "send media file message id = " + playVO.getEntity().getId());
                            LogUtils.e(TAG, "send media file message playVO.getEntity()= " + playVO.getEntity().toString());
                        }
                    EventBus.getDefault().postSticky(new SimpleEvent(Constants.UPDATE_PLAYVO_ONE));
                    }
                }
                break;
            case R.id.btn_instant_play:
                // TODO: 2016/10/20 执行播放逻辑
                usePath = (String) tv_path.getText();

                try {
                    if (iMyAidlInterface != null) {

                        iMyAidlInterface.startMediaPlay(playVO.getEntity().getId(), usePath, 1, new OnRefreshUIListener.Stub() {
                            /**
                             * 播放完成的回调
                             * @param id
                             * @param error
                             * @throws RemoteException
                             */
                            @Override
                            public void completed(long id, String error) throws RemoteException {
                                LogUtils.e("huanghui", "completed = " + id);
                                MBroadcastApplication.setPlayID((long) -1);

                                setInstantPlayBtn(true);
                                setCancelBtn(true);
                                EventBus.getDefault().post(new SimpleEvent(9823442));// 播放完成发送通知改变button状态
                            }

                            /**
                             * 开始播放的回调
                             *
                             * @param id
                             * @throws RemoteException
                             */
                            @Override
                            public void frushPlaying(long id) throws RemoteException {
                                LogUtils.e("huanghui", "frushPlaying = " + id);
                                MBroadcastApplication.setPlayID(playVO.getEntity().getId());
                                setInstantPlayBtn(false);
                                setCancelBtn(false);
                                EventBus.getDefault().post(new SimpleEvent(Constants.A_PLAY));
                            }
                        });
                    }
                } catch (RemoteException e) {
                    MBroadcastApplication.setPlayID((long) -1);
                    setInstantPlayBtn(true);
                    EventBus.getDefault().post(new SimpleEvent(Constants.A_PLAYED));
                    e.printStackTrace();
                }
                break;
            case R.id.btn_cancel:
                // TODO: 2016/9/7
//                usePath = (String) tv_path.getText();
//                if (usePath.contains(".")) {
//                    playVO.getEntity().setFileParentPath(null);
//                    dbManager.update(playVO.getEntity(), dbManager.getPlayEntryDao(DBManager.WRITE_ONLY));
//                    tv_state.setText(getString(R.string.FileSelectFragment_unused));
//                    tv_path.setText(getString(R.string.FileSelectFragment_search));
////                    ((DialogFragment) this.getParentFragment()).dismiss();
//                }
                playVO.getEntity().setFileParentPath(null);
                dbManager.update(playVO.getEntity(), dbManager.getPlayEntryDao(DBManager.WRITE_ONLY));
                EventBus.getDefault().postSticky(new SimpleEvent(Constants.Analytic_Cmpletion_Notice));
                ((DialogFragment) this.getParentFragment()).dismiss();
                break;
        }
    }

}
