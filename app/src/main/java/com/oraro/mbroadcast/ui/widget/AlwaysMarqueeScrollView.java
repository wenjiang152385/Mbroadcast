package com.oraro.mbroadcast.ui.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/10/24 0024.
 */
public class AlwaysMarqueeScrollView extends TextView {

    // com.duopin.app.AlwaysMaguequeScrollView
    public AlwaysMarqueeScrollView(Context context) {

        super(context);

        // TODO Auto-generated constructor stub
    }

    public AlwaysMarqueeScrollView(Context context, AttributeSet attrs) {

        super(context, attrs);

        // TODO Auto-generated constructor stub
    }

    public AlwaysMarqueeScrollView(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);

        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean isFocused() {

        return true;

    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {

        // TODO Auto-generated method stub
        // fobid call parent constructor
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
    }

}