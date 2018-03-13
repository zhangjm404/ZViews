package com.zjm.zviews;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.zjm.zviewlibrary.splash.model.SplashModel;
import com.zjm.zviewlibrary.splash.view.SplashFrame;

/**
 * When I wrote this, only God and I understood what I was doing
 * Now, God only knows
 *
 * @author ${USER}$
 * @date 2017/12/6
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "zviewlibrary";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void goSplash1(View view) {
        SplashFrame.show(this);
    }

    public void goSplash2(View view) {
        SplashFrame.show(this, R.mipmap.ic_launcher, new SplashFrame.OnSplashActionListener() {
            @Override
            public void onImageClick(String event, String target) {
                Log.e(TAG, event + "   -----   " + target);
            }

            @Override
            public void onHide() {

            }
        });
    }

    public void cacheSplash(View view) {
        SplashModel splashModel = new SplashModel("http://5b0988e595225.cdn.sohucs.com/images/20180312/7239efc4c9cf46e68a144748f8010af6.jpeg","1111","2322");
        SplashFrame.cacheData(this, splashModel);
    }
}
