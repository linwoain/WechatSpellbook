package com.linwoain.demo.hook

import android.content.ContentValues
import com.gh0u1l5.wechatmagician.spellbook.base.Operation
import com.gh0u1l5.wechatmagician.spellbook.interfaces.IDatabaseHook
import com.linwoain.demo.ui.log

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
}