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
import android.widget.Toast;

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
 * Created by zjm on 2018/3/9.
 */
public class SplashFrame extends FrameLayout {

    private Activity mContext;
    private OnSplashActionListener mActionListener;
    private boolean isActionBarShowing = true;
    private final static int COUNT_DOWN_TIME = 10;
    private int mIvBottomRes;
    private static final String SP_NAME = "splash";
    private static final String SP_KEY = "SplashModel";


    public SplashFrame(@NonNull Activity context, int bottomImgRes, OnSplashActionListener listener) {
        super(context);
        mContext = context;
        mIvBottomRes = bottomImgRes;
        mActionListener = listener;
        initView();
    }

    private void initView() {
        setBackgroundColor(Color.WHITE);

        LinearLayout layoutBg = new LinearLayout(mContext);
        layoutBg.setOrientation(LinearLayout.VERTICAL);
        LayoutParams bgParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(layoutBg, bgParams);

        ImageView ivTop = new ImageView(mContext);
        ivTop.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ivTop.setImageResource(R.drawable.default_image);
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
                Toast.makeText(mContext,"1111111",Toast.LENGTH_SHORT).show();
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

    public static void show(Activity activity) {
        show(activity, null);
    }

    public static void show(Activity activity, OnSplashActionListener listener) {
        show(activity, -1, listener);
    }

    public static void show(Activity activity, int bottomImgRes, OnSplashActionListener listener) {
        ViewGroup contentView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        if (null == contentView || 0 == contentView.getChildCount()) {
            throw new IllegalStateException("请在 Activity 的 setContentView(）方法后调用");
        }
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        final SplashFrame splashFrame = new SplashFrame(activity, bottomImgRes,listener);
        splashFrame.showHideActionBar(false);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        contentView.addView(splashFrame, layoutParams);
    }

    public static void cacheData(final Context context, final SplashModel model) {
        if (model == null) {
            throw new IllegalStateException("你丫传个 null 进来干吗");
        }
        final SharedPreferences splashSP = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        //TODO 判断是否已缓存
        String json = splashSP.getString(SP_KEY, "");
        SplashModel lastModel = SplashModel.fromJson(json);
        if (isDataExist(lastModel, model)) {
            return;
        }

        //TODO 下载图片
        ImgDonwloadUtils.donwloadImg(context, model.imgUrl, "闪屏", new ImgDonwloadUtils.OnDownloadListener() {
            @Override
            public void onSucceed(String path) {
                Toast.makeText(context, "下载成功" + path, Toast.LENGTH_SHORT).show();
                model.imgPath = path;
                splashSP.edit().putString(SP_KEY, new Gson().toJson(model)).commit();
                //TODO 保存事件到 sp
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


    public interface OnSplashActionListener {
        void onImageClick(String event, String target);

        void onHide();
    }

}
