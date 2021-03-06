package com.linwoain.demo.hook

import android.content.ContentValues
import com.gh0u1l5.wechatmagician.backend.storage.cache.MessageCache
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.MsgStorageObject
import com.gh0u1l5.wechatmagician.spellbook.base.Operation
import com.gh0u1l5.wechatmagician.spellbook.base.Operation.Companion.nop
import com.gh0u1l5.wechatmagician.spellbook.base.Operation.Companion.replacement
import com.gh0u1l5.wechatmagician.spellbook.interfaces.IDatabaseHook
import com.gh0u1l5.wechatmagician.spellbook.interfaces.IFileSystemHook
import com.gh0u1l5.wechatmagician.spellbook.interfaces.IMessageStorageHook
import com.gh0u1l5.wechatmagician.spellbook.interfaces.IXmlParserHook
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.storage.Methods.MsgInfoStorage_insert
import com.gh0u1l5.wechatmagician.spellbook.util.BasicUtil.tryAsynchronously
import com.gh0u1l5.wechatmagician.spellbook.util.BasicUtil.tryVerbosely
import com.gh0u1l5.wechatmagician.spellbook.util.ReflectionUtil
import com.linwoain.demo.utils.MessageUtil
import de.robv.android.xposed.XposedHelpers
import java.io.File

object AntiRevoke : IDatabaseHook, IFileSystemHook, IMessageStorageHook, IXmlParserHook {

    private const val ROOT_TAG = "sysmsg"
    private const val TYPE_TAG = ".sysmsg.\$type"
    private const val REPLACE_MSG_TAG = ".sysmsg.revokemsg.replacemsg"


    override fun onMessageStorageInserted(msgId: Long, msgObject: Any) {
        tryAsynchronously {
            MessageCache[msgId] = msgObject
        }
    }

    override fun onXmlParsed(xml: String, root: String, result: MutableMap<String, String>) {
        if (root == ROOT_TAG && result[TYPE_TAG] == "revokemsg") {
            val msg = result[REPLACE_MSG_TAG] ?: return
            if (msg.startsWith("\"")) {
                result[REPLACE_MSG_TAG] = MessageUtil.applyEasterEgg(msg, "妄图撤回一条消息，啧啧")
            }
        }
    }

    override fun onDatabaseUpdating(thisObject: Any, table: String, values: ContentValues, whereClause: String?, whereArgs: Array<String>?, conflictAlgorithm: Int): Operation<Int> {
        if (table == "message" && values["type"] == 10000) {
            if (values.getAsString("content").startsWith("\"")) {
                handleMessageRecall(values)
                return replacement(1)
            }
        }
        return nop()
    }

    override fun onDatabaseDeleting(thisObject: Any, table: String, whereClause: String?, whereArgs: Array<String>?): Operation<Int> {
        return when (table) {
            "ImgInfo2", "voiceinfo", "videoinfo2", "WxFileIndex2" -> replacement(1)
            else -> nop()
        }
    }

    override fun onFileDeleting(file: File): Operation<Boolean> {
        val path = file.absolutePath
        return when {
            path.contains("/image2/") -> replacement(true)
            path.contains("/voice2/") -> replacement(true)
            path.contains("/video/") -> replacement(true)
            else -> nop()
        }
    }

    // handleMessageRecall notifies user that someone has recalled the given message.
    private fun handleMessageRecall(values: ContentValues) {
        if (MsgStorageObject == null) {
            return
        }

        tryVerbosely {
            val msgId = values["msgId"] as Long
            val msg = MessageCache[msgId] ?: return@tryVerbosely

            val copy = msg::class.java.newInstance()
            ReflectionUtil.shadowCopy(msg, copy)

            val createTime = XposedHelpers.getLongField(msg, "field_createTime")
            XposedHelpers.setIntField(copy, "field_type", values["type"] as Int)
            XposedHelpers.setObjectField(copy, "field_content", values["content"])
            XposedHelpers.setLongField(copy, "field_createTime", createTime + 1L)

            when (MsgInfoStorage_insert.parameterTypes.size) {
                1 -> MsgInfoStorage_insert.invoke(MsgStorageObject, copy)
                2 -> MsgInfoStorage_insert.invoke(MsgStorageObject, copy, false)
            }
        }
    }
}