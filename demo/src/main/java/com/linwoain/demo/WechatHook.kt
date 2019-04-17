package com.linwoain.demo

import com.gh0u1l5.wechatmagician.spellbook.SpellBook
import com.linwoain.demo.hook.*
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import de.robv.android.xposed.callbacks.XC_LoadPackage

class WechatHook {
    fun dispatch(lpparam: XC_LoadPackage.LoadPackageParam) {
        try {
            if (SpellBook.isImportantWechatProcess(lpparam)) {
                val strategy = PrettyFormatStrategy.newBuilder()
                        .methodCount(1)
                        .showThreadInfo(false)
                        .methodOffset(1)
                        .tag("XPOSED_HOT_TAG")
                        .build()
                Logger.addLogAdapter(AndroidLogAdapter(strategy))

                SpellBook.startup(lpparam, listOf(
//                        MyActivity,
//                        WLog,
//                        FileHook,
//                        AdapterHook,
//                        ImageHook,
                        Message
                ))
            }
        } catch (e: Exception) {
            Logger.e("微信报错", e)

        }


    }
}