package com.linwoain.demo.hook

import android.widget.BaseAdapter
import com.gh0u1l5.wechatmagician.spellbook.interfaces.IAdapterHook
import com.linwoain.demo.ui.log
import de.robv.android.xposed.XposedHelpers
import java.lang.reflect.Field

object AdapterHook : IAdapterHook {

    override fun onAddressAdapterCreated(adapter: BaseAdapter) {
        log("通讯录中个数"+adapter.count)
        repeat(adapter.count) {
            val item = adapter.getItem(it)
            val nickName = XposedHelpers.findField(item.javaClass, "field_nickname")
            val sigature = XposedHelpers.findField(item.javaClass, "field_signature")
            val userName = XposedHelpers.findField(item.javaClass, "field_username")
            item.apply {
                log("${value(nickName)}--${value(userName)}--${value(sigature)}")
            }
        }

    }

    override fun onConversationAdapterCreated(adapter: BaseAdapter) {
        log("交谈对象个数"+adapter.count)
        repeat(adapter.count) {
            val item = adapter.getItem(it)
            val fieldContent = XposedHelpers.findField(item.javaClass, "field_content")
            val fieldDigest = XposedHelpers.findField(item.javaClass, "field_digest")
            log("第${it}条--${fieldDigest.get(item)}--${fieldContent.get(item)}")
        }
    }

    fun Any.value(field:Field)=
     field.get(this)

}