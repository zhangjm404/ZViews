package com.zjm.zviewlibrary.splash.view;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.zjm.zviewlibrary.R;
import com.zjm.zviewlibrary.common.utils.UiUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;

/**
 * When I wrote this, only God and I understood what I was doing
 * Now, God only knows
 * <p>
 * Created by zjm on 2018/3/12.
 */
public class CountDownTextViewView extends BaseSkipTextView {

    public CountDownTextViewView(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.TOP | Gravity.RIGHT;
        layoutParams.rightMargin = UiUtils.dp2px(getContext(), 16);
        layoutParams.topMargin = UiUtils.dp2px(getContext(), 16);
        setText("跳过");
        setTextColor(Color.parseColor("#ccffffff"));
        setLayoutParams(layoutParams);
        setBackgroundResource(R.drawable.splash_skip_bg);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onComplete();
                }
            }
        });
    }

    @Override
    public void startCountDown(final int time) {
        Observable
                .interval(0, 1, TimeUnit.SECONDS)
                .take(time + 1)
                .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long aLong) throws Exception {
                        return time - aLong;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Long downTime) {
                        setText(String.format("%d | 跳过", downTime));
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        setText("跳过");
                        if (mListener != null) {
                            mListener.onComplete();
                        }
                    }
                });
    }

}
