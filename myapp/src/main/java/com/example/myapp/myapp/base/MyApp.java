package com.example.myapp.myapp.base;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.myapp.myapp.common.InitializeConfig;
import com.example.myapp.myapp.utils.LogUtil;

import java.security.Security;


/**
 * Created by yexing on 2018/3/28.
 */

public class MyApp extends Application {
    public static Context mContext;

    public static Context getContext() {
        return mContext;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        InitializeConfig.init(this);
        //添加代码
        //捕捉异常

//        Thread.setDefaultUncaughtExceptionHandler(handler);

    }


    private Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread thread, Throwable throwable) {
            throwable.printStackTrace();
            //杀死当前进程
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    };

}
