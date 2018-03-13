package com.zjm.zviewlibrary.splash.model;

import android.support.annotation.Nullable;

import com.google.gson.Gson;

/**
 * When I wrote this, only God and I understood what I was doing
 * Now, God only knows
 * <p>
 * Created by zjm on 2018/3/12.
 */
public class SplashModel {
    public String imgUrl = "";
    public String event = "";
    public String target = "";
    public String imgPath = "";

    public static SplashModel fromJson(@Nullable String json) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        return new Gson().fromJson(json, SplashModel.class);
    }
}
