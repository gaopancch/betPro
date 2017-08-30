package com.bet.gaopan.betpro.utils;

import android.content.Context;
import android.content.Intent;

import com.bet.gaopan.betpro.activity.RoomActivity;
import com.bet.gaopan.betpro.activity.ServerActivity;

/**
 * Created by letv on 2017/8/29.
 */

public class ActivityUtils {

    public static void goToActivity(Context packageContext, Class<?> cls){
        Intent intent = new Intent();
        intent.setClass(packageContext,cls);
        packageContext.startActivity(intent);
    }
}
