package com.linwoain.demo

import com.gh0u1l5.wechatmagician.spellbook.SpellBook
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal
import com.linwoain.demo.hook.AntiRevoke
import com.linwoain.demo.hothook.Status
import com.linwoain.demo.ui.log
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class WechatHook {
  fun dispatch(lpparam: XC_LoadPackage.LoadPackageParam) {
    try {
      if (lpparam.processName == BuildConfig.APPLICATION_ID) {
        log("检测到当前app进程")
        XposedHelpers.findAndHookMethod(
            Status::class.java, "hook",
            object : XC_MethodHook() {
              override fun beforeHookedMethod(param: MethodHookParam?) {
                param?.result = true
              }

            })
      }

      if (SpellBook.isImportantWechatProcess(lpparam)) {
        val strategy = PrettyFormatStrategy.newBuilder()
            .methodCount(1)
            .showThreadInfo(false)
            .methodOffset(1)
            .tag("WechatSpellBook")
            .build()
        Logger.addLogAdapter(AndroidLogAdapter(strategy))
        SpellBook.startup(
            lpparam, listOf(
//                        MyActivity,
//                        WLog,
//                        FileHook,
//                        AdapterHook,
//                        ImageHook,
            AntiRevoke
        )
        )
        log(lpparam.processName + "--" + lpparam.isFirstApplication)
        log("微信版本号：${WechatGlobal.wxVersion.toString()},插件版本号：${BuildConfig.VERSION_NAME}")

      }
    } catch (e: Exception) {
      Logger.e("微信报错", e)

    }

  }
}