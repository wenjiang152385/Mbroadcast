package com.oraro.mbroadcast.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Selection;
import android.text.Spannable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by Administrator on 2016/8/29 0029.
 */
public class LineEditText extends EditText {
    private boolean mShowLine = false;
    private Context mContext;
    private int mColor;
    private Boolean flag;

    public LineEditText(Context context) {
        super(context);
        mContext = context;
        setEditText();
        setEditTextStyle(false);
    }


    public LineEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEditText();
        setEditTextStyle(false);
    }

    private void setEditText() {
        this.setMinimumWidth(100);
        this.setTextSize(22);
        this.setBackground(null);
        this.setSingleLine();
        this.setPadding(0, 0, 2, 0);
    }

    public void setLineColor(int color) {
        mColor = color;
        invalidate();
    }


    public void showLine(boolean showLine) {
        mShowLine = showLine;
        setEditTextStyle(showLine);
        invalidate();
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        setEndCursor();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mShowLine) {
            drawLine(canvas);
        }
    }


    private void setEditTextStyle(boolean flag) {
//        this.setCursorVisible(flag);
//        this.setFocusable(flag);
//        this.setFocusableInTouchMode(flag);
//        this.setClickable(flag);
    }

    public void setFalse(boolean flag) {
        this.setCursorVisible(flag);
        this.setFocusable(flag);
        this.setClickable(flag);
    }

    public void setFalse1(boolean flag) {
        this.flag = flag;
        this.setCursorVisible(flag);
        this.setFocusable(flag);
        this.setFocusableInTouchMode(flag);
        this.setClickable(flag);
    }

    public boolean getFlag() {
        if (null == flag) {
            flag = false;
        }
        return flag;
    }


    private void setEndCursor() {
        CharSequence text = this.getText();
        if (text instanceof Spannable) {
            Spannable spanText = (Spannable) text;
            Selection.setSelection(spanText, text.length());
        }
    }

    private void drawLine(Canvas canvas) {
        Paint paint = new Paint();
        if (mColor == -1) {
            mColor = Color.WHITE;
        }
        paint.setColor(mColor);
        paint.setStyle(Paint.Style.STROKE);

        canvas.drawLine(0, this.getHeight() - 2, this.getWidth(), this.getHeight() - 2, paint);
    }
}
