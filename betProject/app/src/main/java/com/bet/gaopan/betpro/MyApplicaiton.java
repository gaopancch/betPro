package com.bet.gaopan.betpro;

import android.app.Application;

import com.pgyersdk.crash.PgyCrashManager;
import com.pgyersdk.update.PgyUpdateManager;

/**
 * Created by letv on 2017/9/3.
 */

public class MyApplicaiton extends Application {
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();

        PgyCrashManager.register(this);
    }

    /**当终止应用程序对象时调用，不保证一定被调用，当程序是被内核终止以便为其他应用程序释放资源，那
     么将不会提醒，并且不调用应用程序的对象的onTerminate方法而直接终止进程*/
    @Override
    public void onTerminate() {
        super.onTerminate();
        PgyCrashManager.unregister();
        PgyUpdateManager.unregister();
    }
}
