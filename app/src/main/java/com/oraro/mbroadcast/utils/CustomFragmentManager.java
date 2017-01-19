package com.oraro.mbroadcast.utils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.oraro.mbroadcast.R;
import com.oraro.mbroadcast.ui.fragment.DelayActivityFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by Administrator on 2016/11/25 0025.
 *
 * @author jiang wen[佛祖保佑 永无BUG]
 */
public class CustomFragmentManager {

    private static CustomFragmentManager mCustomFragmentManager = null;

    public FragmentActivity mFragmentActivity;

    private Fragment mMainFragment;

    /**
     * 记录菜单对应的fragment
     */
    private List<Fragment> mAllMenuFragmentList = new ArrayList<>();

    /**
     * 管理栈中的fragment
     */
    private Stack<Fragment> mFragmentStack = new Stack<>();

    private int mViewId;

    public static CustomFragmentManager getInstance(FragmentActivity activity) {
        if (null == mCustomFragmentManager) {
            synchronized (CustomFragmentManager.class) {

                if (null == mCustomFragmentManager) {
                    mCustomFragmentManager = new CustomFragmentManager(activity);
                }
            }
        }
        return mCustomFragmentManager;
    }

    public static void setCustomFragmentManagerNull() {
        mCustomFragmentManager = null;
    }

    public void addFragment(Fragment f) {
        mAllMenuFragmentList.add(f);
    }


    private CustomFragmentManager(FragmentActivity activity) {
        mFragmentActivity = activity;
    }

    /**
     * 设置fragment附属的view
     *
     * @param id
     */
    public void setShowViewId(int id) {
        mViewId = id;
    }

    public void setMainFragment(Fragment f) {
        mMainFragment = f;
    }

    /**
     * 启动菜单对应的fragment
     *
     * @param position 菜单对应的位置
     */
    public void startFragment(int position) {
        if(mAllMenuFragmentList.size() == 0){
            return;
        }
        mFragmentStack.clear();
        Bundle bundle = new Bundle();
        bundle.putLong("info", -1);
        pushFragment(mMainFragment);
        if (null == mAllMenuFragmentList.get(position).getArguments()) {
            mAllMenuFragmentList.get(position).setArguments(bundle);
        } else {
            mAllMenuFragmentList.get(position).getArguments().putLong("info", -1);
        }
        //mFragmentActivity.getSupportFragmentManager().beginTransaction().replace(mViewId, mAllMenuFragmentList.get(position), mAllMenuFragmentList.get(position).getTag()).commit();
        replaceFragment(mAllMenuFragmentList.get(position),true);
        pushFragment(mAllMenuFragmentList.get(position));
    }

    public Fragment getFragment(int position) {
        if (position <= mAllMenuFragmentList.size()) {
            return mAllMenuFragmentList.get(position);
        } else {
            throw new IllegalStateException("out of Index");
        }
    }

    /**
     * 启动一个新的fragment
     *
     * @param f
     */
    public void startFragment(Fragment f) {
        //mFragmentActivity.getSupportFragmentManager().beginTransaction().replace(mViewId, f, f.getTag()).commit();
        replaceFragment(f,true);
        pushFragment(f);
    }

    public void replaceFragment(Fragment f,boolean isOpen){
        /**
         * 将动画效果取消,在快速切换过程会出现异常
         */
//        int mAnimIn = -1;
//        int mAnimOut = -1;
//        if(isOpen){
//            mAnimIn = R.anim.anim_scale_fragment_open_in;
//            mAnimOut = R.anim.anim_scale_fragment_open_out;
//            mFragmentActivity.getSupportFragmentManager().beginTransaction()
//                    //.setCustomAnimations(mAnimIn,mAnimOut,mAnimIn,mAnimOut)
//                    .replace(mViewId, f, f.getTag())
//                    .commit();
//        }else{
//            /**
//             *fragment结束时不执行切换动画，由于执行切换动画时fragment会出现重叠的问题，
//             * 需要在每个fragment添加background，但是紧急播报页面有webview，添加背景后
//             * 会导致webview中的输入框背景颜色错误。
//             */
//
//            mAnimIn = R.anim.anim_scale_fragment_close_in;
//            mAnimOut = R.anim.anim_scale_fragment_close_out;
//            mFragmentActivity.getSupportFragmentManager().beginTransaction()
//                    //.setCustomAnimations(mAnimIn,mAnimOut,mAnimIn,mAnimOut)
//                    .replace(mViewId, f, f.getTag())
//                    .commit();
//        }

        mFragmentActivity.getSupportFragmentManager().beginTransaction()
                .replace(mViewId, f, f.getTag())
                .commit();
    }

    /**
     * 关闭当前的fragment
     */
    public int finishFragment() {
        Fragment f = peekFragment();
        //如果关闭的是延误信息页面，则需要判断当前是否正则更新延误信息
        if(f instanceof DelayActivityFragment){
            DelayActivityFragment mDelayActivityFragment = (DelayActivityFragment) f;
            if(mDelayActivityFragment.checkUpdateState(false)){
                return getSize();
            }
        }
        popFragment();
        f = peekFragment();
        //mFragmentActivity.getSupportFragmentManager().beginTransaction().replace(mViewId, f, f.getTag()).commit();
        replaceFragment(f,false);
        return getSize();
    }

    /**
     * 将一个fragment放入到管理栈中
     *
     * @param f
     */
    private void pushFragment(Fragment f) {
        mFragmentStack.push(f);
    }

    /**
     * 去除管理栈中的第一个fragment
     *
     * @return
     */
    private Fragment popFragment() {
        Fragment f = mFragmentStack.pop();
        return f;
    }

    /**
     * 取出管理栈中的第一个fragment
     *
     * @return
     */
    private Fragment peekFragment() {
        return mFragmentStack.peek();
    }

    /**
     * 获取管理栈中fragment的个数
     *
     * @return
     */
    public int getSize() {
        return mFragmentStack.size();
    }

    /**
     * 销毁fragment的管理
     */
    public void destory() {
        mFragmentStack.clear();
        mAllMenuFragmentList.clear();
        mFragmentActivity = null;
        mCustomFragmentManager = null;
        mMainFragment = null;
    }

}
