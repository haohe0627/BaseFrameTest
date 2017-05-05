package base;

import android.app.Application;

/**
 * Created by haohe on 2017/5/5 0005.
 * util中的初始化基类，由app中MyApplicaition继承，
 */

public abstract class BaseApplication extends Application {

    private static BaseApplication context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        init();
    }

    private void init(){ // 初始化各种工具类

    }

    public static BaseApplication getContext(){return context;}
    public abstract void showLoading();
    public abstract void hideLoading();
}
