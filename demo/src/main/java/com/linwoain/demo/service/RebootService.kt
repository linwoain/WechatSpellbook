package com.linwoain.demo.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.linwoain.demo.ui.MainActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class RebootService : Service() {

  override fun onBind(intent: Intent): IBinder {
    TODO("Return the communication channel to the service.")
  }

  override fun onStartCommand(
    intent: Intent?,
    flags: Int,
    startId: Int
  ): Int = runBlocking {
    delay(2000)
    startActivity(Intent(this@RebootService,MainActivity::class.java).apply {
      addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    })
    super.onStartCommand(intent, flags, startId)
  }
}
