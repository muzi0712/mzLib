package com.muzi.lib.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * ListView 中跑马灯TextView
 * Created by Administrator on 2015/8/13.
 */
public class FocusedTextView extends TextView {

    public FocusedTextView(Context context) {
        super(context);
    }

    public FocusedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean isFocused() {
        //判断焦点一直返回true
        return true;
    }

}
