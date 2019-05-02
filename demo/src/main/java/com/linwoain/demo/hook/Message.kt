package com.linwoain.demo.hook

import android.content.ContentValues
import com.gh0u1l5.wechatmagician.spellbook.base.Operation
import com.gh0u1l5.wechatmagician.spellbook.base.Operation.Companion.nop
import com.gh0u1l5.wechatmagician.spellbook.interfaces.IDatabaseHook
import com.linwoain.demo.ui.log
import com.linwoain.demo.utils.MessageUtil

object Message:IDatabaseHook {

    override fun onDatabaseInserted(thisObject: Any, table: String, nullColumnHack: String?, initialValues: ContentValues?, conflictAlgorithm: Int, result: Long?): Operation<Long> {
        if (table == "message") {
            log("New Message: $initialValues")
        }
        return super.onDatabaseInserted(thisObject, table, nullColumnHack, initialValues, conflictAlgorithm, result)
    }

    override fun onDatabaseOpened(path: String, factory: Any?, flags: Int, errorHandler: Any?, result: Any?): Operation<Any> {
        log("db open")
        return super.onDatabaseOpened(path, factory, flags, errorHandler, result)
    }



    override fun onDatabaseUpdating(thisObject: Any, table: String, values: ContentValues, whereClause: String?, whereArgs: Array<String>?, conflictAlgorithm: Int): Operation<Int> {
        when (table) {
            "SnsInfo" -> { // delete moment
                if (values["type"] !in listOf(1, 2, 3, 15)) {
                    return nop()
                }
                if (values["sourceType"] != 0) {
                    return nop()
                }

                val content = values["content"] as ByteArray?
                handleMomentDelete(content, values)
            }
            "SnsComment" -> { // delete moment comment
                if (values["type"] == 1) {
                    return nop()
                }
                if (values["commentflag"] != 1) {
                    return nop()
                }

                val curActionBuf = values["curActionBuf"] as ByteArray?
                handleCommentDelete(curActionBuf, values)
            }
        }
        return nop()
    }
    // handleMomentDelete notifies user that someone has deleted the given moment.
    private fun handleMomentDelete(content: ByteArray?, values: ContentValues) {
        val label = "[已删除]"

        MessageUtil.notifyInfoDelete(label, content)?.let { msg ->
            values.remove("sourceType")
            values.put("content", msg)
        }
    }

    // handleCommentDelete notifies user that someone has deleted the given comment in moments.
    private fun handleCommentDelete(curActionBuf: ByteArray?, values: ContentValues) {
        val label = "[已删除]"
        MessageUtil.notifyCommentDelete(label, curActionBuf)?.let { msg ->
            values.remove("commentflag")
            values.put("curActionBuf", msg)
        }
    }

}