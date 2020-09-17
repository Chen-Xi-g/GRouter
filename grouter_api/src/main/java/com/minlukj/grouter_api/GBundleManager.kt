package com.minlukj.groute_api

import android.content.Context
import android.os.Bundle
import com.minlukj.grouter_api.GRouterManager

/**
 *
 * @ProjectName:    Module_Study
 * @Package:        com.minlukj.groute_api
 * @ClassName:      GBundleManager
 * @Description:    跳转时 Intent 传递参数
 * @Author:         忞鹿
 * @CreateDate:     2020/9/17 15:37
 */
class GBundleManager {
    var bundle = Bundle()

    //提供带参数的方法
    fun withString(key: String, value: String): GBundleManager {
        bundle.putString(key, value)
        return this
    }

    fun withBoolean(key: String, value: Boolean): GBundleManager {
        bundle.putBoolean(key, value)
        return this
    }

    fun withInt(key: String, value: Int): GBundleManager {
        bundle.putInt(key, value)
        return this
    }

    fun withBundle(bundle: Bundle): GBundleManager {
        this.bundle = bundle
        return this
    }

    //页面跳转
    fun navigation(context: Context): Any? {
        return GRouterManager.instance.navigation(context, this)
    }
}