package com.zjm.zviewlibrary.common.utils;

import android.content.Context;

/**
 * When I wrote this, only God and I understood what I was doing
 * Now, God only knows
 * <p>
 * Created by zjm on 2018/3/13.
 */
public class UiUtils {

    public static int dp2px(Context context, double dpValue) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * density + 0.5);
    }

}
