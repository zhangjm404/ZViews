package com.zjm.zviewlibrary.splash.view;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;

/**
 * When I wrote this, only God and I understood what I was doing
 * Now, God only knows
 * <p>
 * Created by zjm on 2018/3/15.
 */
public abstract class BaseSkipTextView extends AppCompatTextView {

    public OnCountDownListener mListener;

    public BaseSkipTextView(Context context) {
        super(context);
    }

    /**
     * @param time 开始倒计时
     */
    public abstract void startCountDown(int time);

    public void setListener(OnCountDownListener listener) {
        mListener = listener;
    }

    public interface OnCountDownListener {
        void onComplete();
    }
}
