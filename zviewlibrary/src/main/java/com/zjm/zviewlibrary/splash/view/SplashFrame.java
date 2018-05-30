package com.zjm.zviewlibrary.splash.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.zjm.zviewlibrary.R;
import com.zjm.zviewlibrary.common.utils.ImgDonwloadUtils;
import com.zjm.zviewlibrary.common.utils.UiUtils;
import com.zjm.zviewlibrary.splash.model.SplashModel;

import java.io.File;

/**
 * When I wrote this, only God and I understood what I was doing
 * Now, God only knows
 * <p>
 * 一个简单好用的闪屏控件
 * 有两个显示模式
 * - 全屏幕模式：整个闪屏图片，右上角有个倒计时按钮
 * - 上下模式：下面是一个 logo 图，上面是个闪屏图，右上角有个倒计时按钮
 * <p>
 * 第一次弹出显示默认图片
 * 如果调用 cacheData 缓存数据后
 * 显示上一次缓存的数据
 * Created by zjm on 2018/3/9.
 */
public class SplashFrame extends FrameLayout {

    private Activity mContext;
    private OnSplashActionListener mActionListener;
    private boolean isActionBarShowing = true;
    private static int COUNT_DOWN_TIME = 3;
    private int mIvBottomRes;
    private static final String SP_NAME = "splash";
    private static final String SP_KEY = "SplashModel";


    public SplashFrame(@NonNull Activity context, int bottomImgRes, OnSplashActionListener listener, BaseSkipTextView skipTextView) {
        super(context);
        mContext = context;
        mIvBottomRes = bottomImgRes;
        mActionListener = listener;
        initView();

        skipTextView.setListener(new BaseSkipTextView.OnCountDownListener() {
            @Override
            public void onComplete() {
                hideSplash();
            }
        });
        skipTextView.startCountDown(COUNT_DOWN_TIME);
        addView(skipTextView);
    }

    /**
     * 全屏幕闪屏模式弹出闪屏页
     * 默认倒计时3秒
     *
     * @param activity
     */
    public static void show(Activity activity) {
        show(activity, null);
    }

    /**
     * 全屏幕闪屏模式弹出闪屏页
     * 默认倒计时3秒
     *
     * @param activity
     * @param listener 闪屏页监听器
     */
    public static void show(Activity activity, OnSplashActionListener listener) {
        show(activity, -1, listener);
    }

    /**
     * 弹出闪屏页
     * 默认倒计时3秒
     *
     * @param activity
     * @param bottomImgRes 下面的 logo 图res 资源；如果传入-1则使用全屏幕闪屏默认
     * @param listener     闪屏页监听器
     */
    public static void show(Activity activity, int bottomImgRes, OnSplashActionListener listener) {
        show(activity, bottomImgRes, listener, COUNT_DOWN_TIME);
    }

    /**
     * 弹出闪屏页
     *
     * @param activity
     * @param bottomImgRes  下面的 logo 图res 资源；如果传入-1则使用全屏幕闪屏默认
     * @param listener      闪屏页监听器
     * @param downCountTime 倒计时时长，单位：秒
     */
    public static void show(Activity activity, int bottomImgRes, OnSplashActionListener listener, int downCountTime) {
        show(activity, bottomImgRes, listener, downCountTime, new CountDownTextViewView(activity));
    }

    /**
     * 弹出闪屏页
     *
     * @param activity
     * @param bottomImgRes  下面的 logo 图res 资源；如果传入-1则使用全屏幕闪屏默认
     * @param listener      闪屏页监听器
     * @param downCountTime 倒计时时长，单位：秒
     * @param skipTextView  自定义倒计时按钮，继承BaseSkipTextView即可
     */
    public static void show(Activity activity, int bottomImgRes, OnSplashActionListener listener, int downCountTime, BaseSkipTextView skipTextView) {

        COUNT_DOWN_TIME = downCountTime;
        ViewGroup contentView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        if (null == contentView || 0 == contentView.getChildCount()) {
            throw new IllegalStateException("请在 Activity 的 setContentView(）方法后调用");
        }
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        final SplashFrame splashFrame = new SplashFrame(activity, bottomImgRes, listener, skipTextView);
        splashFrame.showHideActionBar(false);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        contentView.addView(splashFrame, layoutParams);
    }

    private void initView() {
        setBackgroundColor(Color.WHITE);

        LinearLayout layoutBg = new LinearLayout(mContext);
        layoutBg.setOrientation(LinearLayout.VERTICAL);
        LayoutParams bgParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(layoutBg, bgParams);

        ImageView ivTop = new ImageView(mContext);
        ivTop.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ivTop.setImageResource(R.drawable.bg_splash_default_image);
        LinearLayout.LayoutParams ivTopParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        ivTopParams.weight = 1;
        layoutBg.addView(ivTop, ivTopParams);
        setIvTopData(ivTop);

        if (mIvBottomRes != -1) {
            ImageView ivBottom = new ImageView(mContext);
            ivBottom.setScaleType(ImageView.ScaleType.CENTER);
            ivBottom.setImageResource(mIvBottomRes);
            LinearLayout.LayoutParams ivBottomParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UiUtils.dp2px(mContext, 130));
            layoutBg.addView(ivBottom, ivBottomParams);
        }
    }

    private void setIvTopData(ImageView ivTop) {
        SharedPreferences splashSP = mContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        String json = splashSP.getString(SP_KEY, "");
        final SplashModel model = SplashModel.fromJson(json);
        if (model == null) {
            return;
        }
        if (isFileExist(model.imgPath)) {
            ivTop.setImageURI(Uri.fromFile(new File(model.imgPath)));
        }
        ivTop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mActionListener != null) {
                    mActionListener.onImageClick(model.event, model.target);
                }
            }
        });
    }

    public static boolean isFileExist(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        } else {
            File file = new File(filePath);
            return file.exists() && file.isFile();
        }
    }

    public static void cacheData(final Context context, final SplashModel model) {
        if (model == null) {
            throw new IllegalStateException("你丫传个 null 进来干吗");
        }
        final SharedPreferences splashSP = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        String json = splashSP.getString(SP_KEY, "");
        SplashModel lastModel = SplashModel.fromJson(json);
        if (isDataExist(lastModel, model)) {
            return;
        }

        ImgDonwloadUtils.donwloadImg(context, model.imgUrl, "闪屏", new ImgDonwloadUtils.OnDownloadListener() {
            @Override
            public void onSucceed(String path) {
                model.imgPath = path;
                splashSP.edit().putString(SP_KEY, new Gson().toJson(model)).commit();
            }
        });
    }

    public static boolean isDataExist(SplashModel lastModel, SplashModel newModel) {
        if (lastModel == null) {
            return false;
        } else {
            lastModel.imgPath = lastModel.imgPath == null ? "" : lastModel.imgPath;
            lastModel.imgUrl = lastModel.imgUrl == null ? "" : lastModel.imgUrl;
            lastModel.target = lastModel.target == null ? "" : lastModel.target;
            lastModel.event = lastModel.event == null ? "" : lastModel.event;

            return (lastModel.imgUrl.equals(newModel.imgUrl) && lastModel.event.equals(newModel.event) && lastModel.target.equals(newModel.target) && lastModel.imgPath.length() > 0);
        }
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
                    showHideActionBar(true);
                    parent.removeView(SplashFrame.this);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    showHideActionBar(true);
                    parent.removeView(SplashFrame.this);
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
    }

    @SuppressLint("RestrictedApi")
    private void showHideActionBar(boolean isShow) {
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


    /**
     * 事件监听
     */
    public interface OnSplashActionListener {
        /**
         * 广告被点击了
         *
         * @param event
         * @param target
         */
        void onImageClick(String event, String target);

        /**
         * 闪屏页关闭
         */
        void onHide();
    }

}
