package com.linwoain.demo

import android.app.Application
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        val strategy = PrettyFormatStrategy.newBuilder()
                .methodCount(1)
                .showThreadInfo(false)
                .methodOffset(1)
                .tag("XPOSED_HOT_TAG")
                .build()
        Logger.addLogAdapter(AndroidLogAdapter(strategy))
    }
}