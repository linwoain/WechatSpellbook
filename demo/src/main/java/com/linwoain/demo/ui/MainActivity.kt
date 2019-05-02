package com.linwoain.demo.ui

import android.content.Intent
import android.os.Bundle
import android.os.Process
import androidx.appcompat.app.AppCompatActivity
import com.linwoain.demo.R
import com.linwoain.demo.hothook.Status
import com.linwoain.demo.service.RebootService
import com.linwoain.demo.utils.RootCmd
import kotlinx.android.synthetic.main.activity_main.reboot
import kotlinx.android.synthetic.main.activity_main.reboot_current
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.concurrent.timer

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    log("hook status is ${Status.hook()}")
    log("process pid = " + Process.myPid())
    reboot.setOnClickListener { reboot() }
    reboot_current.setOnClickListener {
      rebootCurrentProcess()
    }
  }

  private fun startTimerTask() {

    timer(period = 2000) {
      log("hook status is ${Status.hook()}")

    }
  }

  private fun rebootCurrentProcess() {
    startService(Intent(this, RebootService::class.java))
    System.exit(0)
  }

  private fun reboot() = GlobalScope.launch {
    val myUid = Process.myUid()
    println(myUid)
    val result = RootCmd.execCmd("am force-stop $TARGET")
    Thread.sleep(3000)
    if (result) {
      val intent = packageManager.getLaunchIntentForPackage(TARGET)
      startActivity(intent)
    } else {
      log("打开微信失败")
    }

  }

  companion object {
    const val ALIPAY = "com.eg.android.AlipayGphone"
    const val WECHAT = "com.tencent.mm"
    const val TARGET = WECHAT
  }
}
