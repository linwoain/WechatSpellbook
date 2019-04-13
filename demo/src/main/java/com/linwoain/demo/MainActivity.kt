package com.linwoain.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        reboot()
        reboot.setOnClickListener { reboot() }
    }

    fun reboot() {
        check()

    }
    private fun check() {
        GlobalScope.launch {
            val result = RootCmd.execCmd("am force-stop $TARGET")
            if (result) {

                val intent = packageManager.getLaunchIntentForPackage(TARGET)
                startActivity(intent)
            } else {
                log("打开支付宝失败")
            }

        }

    }
    companion object {
        const val ALIPAY = "com.eg.android.AlipayGphone"
        const val WECHAT = "com.tencent.mm"
        const val TARGET = WECHAT
    }
}
