package com.github.guilhermesgb.buttons;

import android.app.Application;
import android.os.Looper;

import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.plugins.RxJavaPlugins;
import retrofit2.HttpException;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class ButtonsApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable
            -> AndroidSchedulers.from(Looper.getMainLooper(), true));
        RxJavaPlugins.setErrorHandler(throwable -> {
            if (!(throwable instanceof Exception && !(throwable instanceof RuntimeException))) {
                if (throwable instanceof HttpException) {
                    return;
                }
                Thread.currentThread().getUncaughtExceptionHandler()
                    .uncaughtException(Thread.currentThread(), throwable);
            }
        });
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
            .setDefaultFontPath("fonts/Lato-Regular.ttf")
            .build());
    }

}
