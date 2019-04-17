package com.linwoain.demo.hook

import com.gh0u1l5.wechatmagician.spellbook.interfaces.IImageStorageHook
import com.linwoain.demo.ui.log

object ImageHook : IImageStorageHook {
    override fun onImageStorageLoaded(imageId: String?, prefix: String?, suffix: String?) {

//        log("imageId = [${imageId}], prefix = [${prefix}], suffix = [${suffix}]")
    }

    override fun onImageStorageLoading(imageId: String?, prefix: String?, suffix: String?): Boolean {
//        log("imageId = [${imageId}], prefix = [${prefix}], suffix = [${suffix}]")
        return false
    }

}