package com.oraro.mbroadcast.ui.fragment;

import android.app.DialogFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.oraro.mbroadcast.R;
import com.oraro.mbroadcast.listener.DelayDialogListener;
import com.oraro.mbroadcast.utils.UIUtils;

/**
 * Created by Administrator on 2016/10/19 0019.
 */
public class DelayFragment extends DialogFragment {
    private DelayDialogListener mDelayDialogListener;
    private TextView mTitle;
    private TextView mContent;
    private Button mSubmit;
    private Button mCancle;
    private String title;
    private String content;
    private String submit;
    private String cancle;
    private SpannableStringBuilder ssb;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = setViewParams(inflater, container, savedInstanceState);
        initViews(view);
        mTitle.setText(title);
        //mContent.setText(content);
        mSubmit.setText(submit);
        mCancle.setText(cancle);
        mSubmit.setOnClickListener(mOnClickListener);
        mCancle.setOnClickListener(mOnClickListener);
        ssb.setSpan(new ForegroundColorSpan(Color.RED), 8, 13,
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        mContent.setText(ssb);
        return view;
    }

    public void setOnButtonClickListener(DelayDialogListener iDialogFragment) {
        mDelayDialogListener = iDialogFragment;
    }

    public void setTitle(String text) {
        title = text;
    }

    public void setContent(SpannableStringBuilder text) {
        ssb = new SpannableStringBuilder(text);
    }

    public void setSubmit(String text) {
        submit = text;
    }
    public  void setmCancle(String text){cancle=text;}

    private void initViews(View view) {
        mTitle = (TextView) view.findViewById(R.id.dialog_title);
        mSubmit = (Button) view.findViewById(R.id.submit);
        mCancle=(Button)view.findViewById(R.id.cancle);


    }

    private View setViewParams(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Window window = getDialog().getWindow();
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.layout__delay_dialog, ((ViewGroup) window.findViewById(android.R.id.content)), false);
//        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mContent = (TextView) view.findViewById(R.id.content);
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
                    mDelayDialogListener.onDelaySumbitListener();
//                    SimpleDialogFragment.this.dismiss();
                    break;
                case R.id.cancle:
                    mDelayDialogListener.onDelayCancleListener();

                    break;
                default:
                    break;
            }
        }
    };


}
