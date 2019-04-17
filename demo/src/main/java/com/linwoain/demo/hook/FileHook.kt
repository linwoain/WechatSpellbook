package com.linwoain.demo.hook

import com.gh0u1l5.wechatmagician.spellbook.interfaces.IFileSystemHook
import com.linwoain.demo.ui.log
import java.io.File

object FileHook : IFileSystemHook {
    override fun onFileWriting(file: File, append: Boolean) {

    }
}