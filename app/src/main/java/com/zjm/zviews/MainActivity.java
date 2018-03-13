package com.zjm.zviews;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.zjm.zviewlibrary.splash.view.SplashFrame;

/**
 *
 * When I wrote this, only God and I understood what I was doing
 * Now, God only knows
 *
 * @author ${USER}$
 * @date 2017/12/6
 */
public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void goSplash1(View view) {
        SplashFrame.show(this);
    }

    public void goSplash2(View view) {
        SplashFrame.show(this, R.mipmap.ic_launcher, null);
    }
}
