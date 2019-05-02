package com.linwoain.demo.ui

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.forEach
import com.orhanobut.logger.Logger
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodHook.MethodHookParam
import de.robv.android.xposed.XposedHelpers

/**
 * Created by linwoain on 2018/12/11.
 */
fun log(msg: Any?) {
  Logger.i(msg?.toString() ?: "null")
}

fun loge(msg: String?) {
  Log.e("WechatSpellBook", msg)
}

fun Any?.applyLog() {
  Logger.i(this.toString())
}


fun Any?.logJson() {

  Logger.json(this as String)
}

/**
 *
 * @param className 要hook的类名
 * @param methodName 要hook的方法名
 * @param blockAfter 方法执行后
 * @param blockBefore 方法执行前
 * @param args 方法参数
 */
fun ClassLoader.hook(
  className: String,
  methodName: String,
  args: Array<Any?>? = null,
  blockBefore: MethodHookParam.() -> Unit = {},
  blockAfter: MethodHookParam.() -> Unit = {}
) {

  if (XposedHelpers.findClassIfExists(className,this) == null) {
    Logger.e("$className 未找到")
    return
  }
  if (args == null || args.isEmpty()) {
    XposedHelpers.findAndHookMethod(
        className, this@hook, methodName, object : XC_MethodHook() {
      override fun beforeHookedMethod(param: MethodHookParam?) {
        param?.apply(blockBefore)
      }

      override fun afterHookedMethod(param: MethodHookParam?) {
        param?.apply(blockAfter)
      }
    })
  } else {
    XposedHelpers.findAndHookMethod(
        className, this@hook, methodName, *args, object : XC_MethodHook() {
      override fun beforeHookedMethod(param: MethodHookParam?) {
        param?.apply(blockBefore)
      }

      override fun afterHookedMethod(param: MethodHookParam?) {
        param?.apply(blockAfter)
      }
    })
  }

}

/**
 *
 * @param clazz 要hook的字节码
 * @param methodName 要hook的方法名
 * @param blockAfter 方法执行后
 * @param blockBefore 方法执行前
 * @param args 方法参数
 */
fun hookClass(
  clazz: Class<*>,
  methodName: String,
  args: Array<Any?>? = null,
  blockBefore: MethodHookParam.() -> Unit = {},
  blockAfter: MethodHookParam.() -> Unit = {}
) {
  if (args == null || args.isEmpty()) {
    XposedHelpers.findAndHookMethod(
        clazz, methodName, object : XC_MethodHook() {
      override fun beforeHookedMethod(param: MethodHookParam?) {
        param?.apply(blockBefore)
      }

      override fun afterHookedMethod(param: MethodHookParam?) {
        param?.apply(blockAfter)
      }
    })
  } else {
    XposedHelpers.findAndHookMethod(
        clazz, methodName, *args, object : XC_MethodHook() {
      override fun beforeHookedMethod(param: MethodHookParam?) {
        param?.apply(blockBefore)
      }

      override fun afterHookedMethod(param: MethodHookParam?) {
        param?.apply(blockAfter)
      }
    })
  }

}

/**
 * <b>仅在hook Activity 成员方法中的方法时生效</b>
 * 在Activity hook代码中弹出toast
 * @param str 要弹出的toast内容
 */
fun MethodHookParam.toast(str: String) {

  this.thisObject?.takeIf { it is Activity }
      ?.apply {
        Toast.makeText(thisObject as Activity, str, Toast.LENGTH_SHORT)
            .show()
      }
}

val MethodHookParam.activity
    : Activity?
  get() = if (thisObject is Activity) {
    thisObject as Activity
  } else {
    null
  }

fun Context.toast(msg: String) {
  Toast.makeText(this, msg, Toast.LENGTH_SHORT)
      .show()
}

fun ViewGroup.allView(
  block: (v: View) -> Unit
) {
  this.forEach {
    if (it is ViewGroup) {
      it.allView( block)
    } else {
      block.invoke(it)
    }
  }

}

fun findFieldCurrentOrSuper(
  obj: Any?,
  fieldName: String
): Any? {
  if (obj == null) {
    Logger.d("获取到对象为null")
    return null
  }
  var clazz = obj.javaClass
  var field = XposedHelpers.findFieldIfExists(clazz, fieldName)
  while (field == null) {
    Logger.d(clazz.name + "中查找" + fieldName)
    val clazz1 = clazz.superclass as Class<Any>
    if (clazz1 == Object::class.java) {
      return null
    }
    clazz = clazz1
    field = XposedHelpers.findFieldIfExists(clazz, fieldName)
  }
   return field.get(obj)
}