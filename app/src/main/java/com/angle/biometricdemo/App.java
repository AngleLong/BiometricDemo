package com.angle.biometricdemo;

import android.app.Application;

public class App extends Application {
    private static Application mApplication;
    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
    }

    public static Application getAPPContext(){
        return mApplication;
    }
}
