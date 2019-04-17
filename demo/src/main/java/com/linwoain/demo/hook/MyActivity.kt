package com.linwoain.demo.hook

import android.app.Activity
import android.os.Bundle
import com.gh0u1l5.wechatmagician.spellbook.interfaces.IActivityHook
import com.linwoain.demo.ui.log

object MyActivity:IActivityHook {
    override fun onActivityCreating(activity: Activity, savedInstanceState: Bundle?) {

        log("${activity.javaClass.simpleName}打开")
    }

}