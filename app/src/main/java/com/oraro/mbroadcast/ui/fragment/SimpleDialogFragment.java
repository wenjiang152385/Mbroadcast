package com.oraro.mbroadcast.ui.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.oraro.mbroadcast.R;
import com.oraro.mbroadcast.listener.IDialogFragment;
import com.oraro.mbroadcast.utils.UIUtils;


/**
 * Created by Administrator on 2016/9/23 0023.
 */
public class SimpleDialogFragment extends DialogFragment {

    private IDialogFragment mIDialogFragment;
    private DismissCallback mDismissCallback;

    private TextView mTitle;
    private TextView mContent;
    private Button mSubmit;
    private String title;
    private String content;
    private String submit;

    @Override
    public void dismiss() {
        super.dismiss();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = setViewParams(inflater, container, savedInstanceState);
        initViews(view);
        mTitle.setText(title);
        mContent.setText(content);
        mSubmit.setText(submit);
        mSubmit.setOnClickListener(mOnClickListener);
        return view;
    }

    public void setDismissCallback(DismissCallback dismissCallback) {
        mDismissCallback = dismissCallback;
    }

    public void setOnButtonClickListener(IDialogFragment iDialogFragment) {
        mIDialogFragment = iDialogFragment;
    }

    public void setTitle(String text) {
        title = text;
    }

    public void setContent(String text) {
        content = text;
    }

    public void setSubmit(String text) {
        submit = text;
    }

    private void initViews(View view) {
        mTitle = (TextView) view.findViewById(R.id.dialog_title);
        mContent = (TextView) view.findViewById(R.id.content);
        mSubmit = (Button) view.findViewById(R.id.submit);
    }

    private View setViewParams(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Window window = getDialog().getWindow();
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.layout_simple_dialog, ((ViewGroup) window.findViewById(android.R.id.content)), false);
//        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        UIUtils toolUtils = new UIUtils();
        window.setLayout(toolUtils.getDisplayMetrics(getActivity()).widthPixels / 3,
                toolUtils.getDisplayMetrics(getActivity()).heightPixels / 3);

        return view;

    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.submit:
                    mIDialogFragment.onDialogFragmentButtonClickListener();
//                    SimpleDialogFragment.this.dismiss();
                    break;
                default:
                    break;
            }
        }
    };


    public interface DismissCallback {
        void dismissCallback();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mDismissCallback) {
            mDismissCallback.dismissCallback();
        }
    }
}
