package com.zjm.zviewlibrary.splash.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.zjm.zviewlibrary.R;
import com.zjm.zviewlibrary.common.utils.UiUtils;

/**
 * When I wrote this, only God and I understood what I was doing
 * Now, God only knows
 * <p>
 * Created by zjm on 2018/3/9.
 */
public class SplashFrame extends FrameLayout {

    private Activity mContext;
    private OnSplashActionListener mActionListener;
    private boolean isActionBarShowing = true;
    private final static int COUNT_DOWN_TIME = 10;
    private int mIvBottomRes;


    public SplashFrame(@NonNull Activity context, int bottomImgRes) {
        super(context);
        mContext = context;
        mIvBottomRes = bottomImgRes;
        initView();
    }

    private void initView() {
        setBackgroundColor(Color.WHITE);

        LinearLayout layoutBg = new LinearLayout(mContext);
        layoutBg.setOrientation(LinearLayout.VERTICAL);
        LayoutParams bgParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(layoutBg,bgParams);

        ImageView ivTop = new ImageView(mContext);
        ivTop.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ivTop.setImageResource(R.drawable.default_image);
        LinearLayout.LayoutParams ivTopParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        ivTopParams.weight = 1;
        layoutBg.addView(ivTop,ivTopParams);

        if (mIvBottomRes != -1) {
            ImageView ivBottom = new ImageView(mContext);
            ivBottom.setScaleType(ImageView.ScaleType.CENTER);
            ivBottom.setImageResource(mIvBottomRes);
            LinearLayout.LayoutParams ivBottomParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UiUtils.dp2px(mContext,130));
            layoutBg.addView(ivBottom,ivBottomParams);
        }

        CountDownTextView countDownTextView = new CountDownTextView(mContext);
        countDownTextView.setListener(new CountDownTextView.OnCountDownListener() {
            @Override
            public void onComplete() {
                hideSplash();
            }
        });
        countDownTextView.startCountDown(COUNT_DOWN_TIME);
        addView(countDownTextView);
    }

    public static void show(Activity activity){
        show(activity,null);
    }

    public static void show(Activity activity,OnSplashActionListener listener) {
        show(activity,-1,listener);
    }

    public static void show(Activity activity,int bottomImgRes,OnSplashActionListener listener){
        ViewGroup contentView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        if (null == contentView || 0 == contentView.getChildCount()) {
            throw new IllegalStateException("请在 Activity 的 setContentView(）方法后调用");
        }
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        final SplashFrame splashFrame = new SplashFrame(activity,bottomImgRes);
        splashFrame.setActionListener(listener);
        splashFrame.showHideAction(false);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        contentView.addView(splashFrame, layoutParams);
    }

    public void setActionListener(OnSplashActionListener actionListener) {
        mActionListener = actionListener;
    }


    private void hideSplash() {
        if (mActionListener != null) {
            mActionListener.onHide();
        }

        final ViewGroup parent = (ViewGroup) this.getParent();
        if (null != parent) {
            @SuppressLint("ObjectAnimatorBinding") ObjectAnimator animator = ObjectAnimator.ofFloat(SplashFrame.this, "scale", 0.0f, 0.5f).setDuration(600);
            animator.start();
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float cVal = (Float) animation.getAnimatedValue();
                    SplashFrame.this.setAlpha(1.0f - 2.0f * cVal);
                    SplashFrame.this.setScaleX(1.0f + cVal);
                    SplashFrame.this.setScaleY(1.0f + cVal);
                }
            });
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    showHideAction(true);
                    parent.removeView(SplashFrame.this);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    showHideAction(true);
                    parent.removeView(SplashFrame.this);
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
    }

    @SuppressLint("RestrictedApi")
    private void showHideAction(boolean isShow) {
        mContext.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (mContext instanceof AppCompatActivity) {
            ActionBar supportActionBar = ((AppCompatActivity) mContext).getSupportActionBar();
            if (null != supportActionBar) {
                if (isActionBarShowing) {
                    if (isShow) {
                        supportActionBar.show();
                    } else {
                        supportActionBar.setShowHideAnimationEnabled(false);
                        isActionBarShowing = supportActionBar.isShowing();
                        supportActionBar.hide();
                    }
                }
            }
        } else if (mContext instanceof Activity) {
            android.app.ActionBar actionBar = mContext.getActionBar();
            if (null != actionBar) {
                if (isActionBarShowing) {
                    actionBar.show();
                } else {
                    isActionBarShowing = actionBar.isShowing();
                    actionBar.hide();
                }
            }
        }
    }


    public interface OnSplashActionListener {
        void onImageClick();
        void onHide();
    }

}
