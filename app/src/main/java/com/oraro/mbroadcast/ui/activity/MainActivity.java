package com.oraro.mbroadcast.ui.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.oraro.mbroadcast.Constants;
import com.oraro.mbroadcast.MBroadcastApplication;
import com.oraro.mbroadcast.R;
import com.oraro.mbroadcast.broadcasts.NetWorkReceiver;
import com.oraro.mbroadcast.listener.IAutoPlayStatusListener;
import com.oraro.mbroadcast.model.SimpleEvent;
import com.oraro.mbroadcast.service.IMyAidlInterface;
import com.oraro.mbroadcast.service.OnRefreshUIListener;
import com.oraro.mbroadcast.service.Service1;
import com.oraro.mbroadcast.ui.fragment.AddAndEditFragment;
import com.oraro.mbroadcast.ui.fragment.ChooseFragment;
import com.oraro.mbroadcast.ui.fragment.DelayActivityFragment;
import com.oraro.mbroadcast.ui.fragment.ExcelFragment;
import com.oraro.mbroadcast.ui.fragment.FileSelectFragment;
import com.oraro.mbroadcast.ui.fragment.MainFragment;
import com.oraro.mbroadcast.ui.fragment.SearchFlightFragment;
import com.oraro.mbroadcast.ui.fragment.SlidingMenuFragment;
import com.oraro.mbroadcast.ui.fragment.UrgentBroadcastFragment;

import com.oraro.mbroadcast.utils.CustomFragmentManager;
import com.oraro.mbroadcast.utils.LogUtils;
import com.oraro.mbroadcast.utils.SPUtils;
import com.oraro.mbroadcast.utils.UIUtils;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;


public class MainActivity extends BaseActivity {
    public static final String TAG = "MainActivity";

    private boolean isMainPosition = true;
    private boolean mIsNeedChangeMenu = false;
    private SimpleEvent mSimpleEvent;

    private SlidingMenu menu;
    private MainFragment mMainFragment;
    private DelayActivityFragment mDelayActivityFragment;
    private ChooseFragment mChooseFragment;
    private SearchFlightFragment mSearchFlightFragment;
    private ExcelFragment mExcelFragment;
    private SlidingMenuFragment mSlidingMenuFragment;

    private CustomFragmentManager mCustomFragmentManager;
    private UrgentBroadcastFragment urgentBroadcastFragment;
    private AddAndEditFragment addAndEditFragment;
    private IMyAidlInterface iMyAidlInterface;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData(Bundle paramBundle) {

    }

    @Override
    protected void initView() {
        menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        //设置触摸屏幕的模式
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setShadowDrawable(R.drawable.shadow);

        // 设置滑动菜单视图的宽度
        UIUtils mUiUtils = new UIUtils();
        menu.setBehindOffset(mUiUtils.getDisplayMetrics(this).widthPixels * 4 / 5);
        // 设置渐入渐出效果的值
        menu.setFadeDegree(0.35f);
        /**
         * SLIDING_WINDOW：菜单栏里不包括ActionBar或标题
         * SLIDING_CONTENT：菜单栏里包括ActionBar或标题
         */
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        //为侧滑菜单设置布局
        menu.setMenu(R.layout.left_menu_frame);

        // menu.setOnClosedListener(mOnSlidingMenuClosed);

        mSlidingMenuFragment = new SlidingMenuFragment();
        // 将侧滑栏的mSlidingMenuFragment类填充到侧滑栏的容器的布局文件中
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        transaction.replace(R.id.id_left_menu_frame, mSlidingMenuFragment, "Slidingmenu");
        transaction.commit();

        CustomFragmentManager.setCustomFragmentManagerNull();
        mCustomFragmentManager = CustomFragmentManager.getInstance(this);
        mCustomFragmentManager.setShowViewId(R.id.frame_1);

        mMainFragment = new MainFragment();

        mDelayActivityFragment = new DelayActivityFragment();
        mCustomFragmentManager.addFragment(mDelayActivityFragment);

        urgentBroadcastFragment = new UrgentBroadcastFragment();
        mCustomFragmentManager.addFragment(urgentBroadcastFragment);

        mExcelFragment = new ExcelFragment();
        mCustomFragmentManager.addFragment(mExcelFragment);

        addAndEditFragment = new AddAndEditFragment();
        mCustomFragmentManager.addFragment(addAndEditFragment);

        mSearchFlightFragment = new SearchFlightFragment();
        mCustomFragmentManager.addFragment(mSearchFlightFragment);

        mChooseFragment = new ChooseFragment();
        mCustomFragmentManager.addFragment(mChooseFragment);

        mCustomFragmentManager.setMainFragment(mMainFragment);
        mCustomFragmentManager.startFragment(mMainFragment);
    }

    public void setCallBack(IAutoPlayStatusListener iAutoPlayStatusListener) {
        mMainFragment.setCallBack(iAutoPlayStatusListener);
    }

    public void resetMenu(int size) {
        if (size == 1) {
            //mSlidingMenuFragment.setClickPosition(-1);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(SimpleEvent event) {
        mSimpleEvent = event;
        switch (event.getMsg()) {
            case Constants.File_Trans_Deuplicate:
                String msg = "onEventMainThread收到了消息：" + event.getMsg();
                Log.e(TAG, msg);
                Toast.makeText(MBroadcastApplication.getMyContext(), "当前有文件正在传输，请耐心等待，谢谢！", Toast.LENGTH_LONG).show();
                break;

            case Constants.BACK_MAIN_CLOSE:
                isMainPosition = true;
                mIsNeedChangeMenu = true;
                int i = mCustomFragmentManager.finishFragment();
                resetMenu(i);
                break;

            case Constants.CALL_TO_START:

                Bundle bundle = new Bundle();
                bundle.putLong("info", event.getmMsgId());
                addAndEditFragment.setArguments(bundle);
                mCustomFragmentManager.startFragment(addAndEditFragment);
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().removeAllStickyEvents();
        EventBus.getDefault().unregister(this);
        mCustomFragmentManager.destory();
        if (conn != null) {
            MBroadcastApplication.getMyContext().unbindService(conn);
        }
        MBroadcastApplication.setFrushHandler(null);
        try {
            if (null != iMyAidlInterface)
            iMyAidlInterface.unRegisterOnRefreshUIListener();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        hideInput(this, this.getCurrentFocus());
    }

    /**
     * 强制隐藏输入法键盘
     */
    private void hideInput(Context context,View view){
        if(view != null){
            InputMethodManager inputMethodManager =
                    (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FileSelectFragment.FILE_PICKER_REQUEST_CODES && resultCode == RESULT_OK) {
//            Uri uri = data.getData();
//            String path = FileUtils.getPath(this, uri);
            String path = data.getStringExtra(FlightExcelActivity.REQUEST_FILE_PATH);

            onClickFileListener.onClickFileListener(path);
        }
    }

    public OnClickFileListener onClickFileListener;

    public interface OnClickFileListener {
        void onClickFileListener(String path);
    }

    public void setOnClickFileListener(OnClickFileListener onClickFileListener) {
        this.onClickFileListener = onClickFileListener;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (menu.isMenuShowing()) {
                menu.toggle();
                return  true;
            }
            if (mCustomFragmentManager.getSize() > 1) {
                int i = mCustomFragmentManager.finishFragment();
                resetMenu(i);
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            iMyAidlInterface = IMyAidlInterface.Stub.asInterface(iBinder);
            MBroadcastApplication.setIMyAidlInterface(iMyAidlInterface);
            EventBus.getDefault().postSticky(new SimpleEvent(Constants.SERVICE1_CONNECT_SUCESSFUL));
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(MBroadcastApplication.getMyContext(), Service1.class);
        MBroadcastApplication.getMyContext().bindService(intent, conn, Context.BIND_AUTO_CREATE);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }



    public void showSlidingMenu() {
        menu.showMenu();
    }

    public void hideSlidingMenu() {
        menu.toggle(false);
    }

}
