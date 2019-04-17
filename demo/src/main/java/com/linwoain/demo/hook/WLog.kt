package com.linwoain.demo.hook

import com.gh0u1l5.wechatmagician.spellbook.interfaces.IXLogHook
import com.linwoain.demo.ui.log

object WLog:IXLogHook {

    override fun onXLogWrite(level: String, tag: String, msg: String) {
        if (level=="error") {
            log("$tag -- $msg")
        }
    }
}