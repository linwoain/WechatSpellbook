package com.linwoain.demo.hothook

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build.VERSION
import com.linwoain.demo.BuildConfig
import com.linwoain.demo.WechatHook
import com.linwoain.demo.ui.log
import dalvik.system.PathClassLoader
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XposedHelpers.*
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.io.File

/**
 * Created by linwoain on 2018/12/11.
 */
class HookManager : IXposedHookLoadPackage {

    override fun handleLoadPackage(loadpackageParam: XC_LoadPackage.LoadPackageParam) {
      log(loadpackageParam.processName)

        if (VERSION.SDK_INT > 25) {
            WechatHook().dispatch(loadpackageParam)
        } else {
            invokeHandleHookMethod(//com.linwoain.testroot.hook.HotHookManager
                    getContext(), BuildConfig.APPLICATION_ID, WechatHook::class.java.name, "dispatch",
                    loadPackageParam = loadpackageParam
            )
        }

    }

    companion object {

        fun getContext(): Context {
            val aClass = findClass("android.app.ActivityThread", null)
            val `object` = arrayOfNulls<Any>(0)
            val currentActivityThread = callStaticMethod(aClass, "currentActivityThread", *`object`)
            return callMethod(currentActivityThread, "getSystemContext", *`object`) as Context
        }

    }

    /**
     * 安装app以后，系统会在/data/app/下备份了一份.apk文件，通过动态加载这个apk文件，调用相应的方法
     * 这样就可以实现，只需要第一次重启，以后修改hook代码就不用重启了
     * @param context context参数
     * @param modulePackageName 当前模块的packageName
     * @param handleHookClass   指定由哪一个类处理相关的hook逻辑
     * @param loadPackageParam  传入XC_LoadPackage.LoadPackageParam参数
     * @throws Throwable 抛出各种异常,包括具体hook逻辑的异常,寻找apk文件异常,反射加载Class异常等
     */
    @Throws(Throwable::class)
    private fun invokeHandleHookMethod(
            context: Context,
            modulePackageName: String,
            handleHookClass: String,
            handleHookMethod: String,
            loadPackageParam: XC_LoadPackage.LoadPackageParam
    ) {
        //        File apkFile = findApkFileBySDK(modulePackageName);//会受其它Xposed模块hook 当前宿主程序的SDK_INT的影响
        //        File apkFile = findApkFile(modulePackageName);
        //原来的两种方式不是很好,改用这种新的方式
        val apkFile = findApkFile(context, modulePackageName) ?: throw RuntimeException("寻找模块apk失败")
//加载指定的hook逻辑处理类，并调用它的handleHook方法
        val pathClassLoader =
                PathClassLoader(apkFile.absolutePath, ClassLoader.getSystemClassLoader())
        val cls = Class.forName(handleHookClass, true, pathClassLoader)
        val instance = cls.newInstance()
        val method =
                cls.getDeclaredMethod(handleHookMethod, loadPackageParam.javaClass)
        method.invoke(instance, loadPackageParam)
    }

    /**
     * 根据包名构建目标Context,并调用getPackageCodePath()来定位apk
     * @param context context参数
     * @param modulePackageName 当前模块包名
     * @return return apk file
     */
    private fun findApkFile(
            context: Context?,
            modulePackageName: String
    ): File? {
        if (context == null) {
            return null
        }
        try {
            val re = context.createPackageContext(
                    modulePackageName, Context.CONTEXT_INCLUDE_CODE or Context.CONTEXT_IGNORE_SECURITY
            )
            val apkPath = re.packageCodePath
            return File(apkPath)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return null
    }
}