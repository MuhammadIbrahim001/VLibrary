package com.v.demo

import android.content.pm.ActivityInfo
import android.graphics.Color
import com.v.base.VBApplication
import com.v.base.VBConfig
import com.v.base.VBConfigOptions
import com.v.base.net.VBNetOptions

class DemoApplication : VBApplication() {

    /**
     * 开启日志打印(日志TAG为 PRETTY_LOGGER)
     */
    override fun isDebug(): Boolean {
        return true
    }

    override fun initData() {

        val netOptions = VBNetOptions.Builder()
            .setBaseUrl("https://www.wanandroid.com/")
            .build()

        val options = VBConfigOptions.Builder()
            .setNetOptions(netOptions)
            .setStatusBarColor(Color.parseColor("#ffffff"))
            .build()

        VBConfig.init(options)

    }

}