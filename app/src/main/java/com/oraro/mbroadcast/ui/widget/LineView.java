package com.oraro.mbroadcast.ui.widget;

import android.content.Context;
import android.graphics.Color;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oraro.mbroadcast.utils.LogUtils;

/**
 * Created by Administrator on 2016/8/29 0029.
 */
public class LineView extends LinearLayout {
    private Context mContext;
    private TextView textView;
    private LineEditText lineEditText;

    public LineView(Context context) {
        super(context);
        mContext = context;
        this.setOrientation(LinearLayout.HORIZONTAL);
        setChildView();
    }

    public LineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        this.setOrientation(LinearLayout.HORIZONTAL);
        setChildView();
    }

    public void setTwoTexts(String tText, String eText) {
        String text = "";
        if ("占位".equals(tText)) {
            text = "";
            eText = "";
        } else {
            text = tText + ": ";
        }
        textView.setText(text);
        if (null != eText) {
            if ("无".equals(eText)) {
                lineEditText.setHint(eText);
            } else {
                lineEditText.setText(eText);
            }
            lineEditText.setSelection(lineEditText.getText().length());
        }
    }

    public TextView getTextView() {
        return textView;
    }

    public LineEditText getEditText() {
        return lineEditText;
    }

    public void setLineEdtEdited(boolean flag) {
        lineEditText.showLine(flag);
    }

    private void setChildView() {
        this.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        textView = new TextView(mContext);

        textView.setTextSize(22);
        textView.setTextColor(Color.parseColor("#383838"));
        this.addView(textView, lp);

        lineEditText = new LineEditText(mContext);
        lineEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText textView = (EditText) v;
                String hint = "";
                String text = textView.getText().toString();
                if (hasFocus) {
                    if (null != textView.getHint()) {
                        hint = textView.getHint().toString();
                        textView.setTag(hint);
                        textView.setHint("");
                    }
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(v, InputMethodManager.SHOW_FORCED);
                } else {
                    if (null != textView.getHint()) {
                        hint = textView.getTag().toString();
                        textView.setHint(hint);
                    }
                    if ("".equals(textView.getText().toString())) {
                        textView.setHint("无");
                    }
                    InputMethodManager mInputKeyBoard = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    mInputKeyBoard.hideSoftInputFromWindow(v.getWindowToken(),0);

                }
            }
        });
        this.addView(lineEditText, lp);
    }

}
