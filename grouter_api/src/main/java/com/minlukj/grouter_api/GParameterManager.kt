package com.minlukj.grouter_api

import android.app.Activity
import android.util.LruCache

/**
 *
 * @ProjectName:    Module_Study
 * @Package:        com.minlukj.grouter_api
 * @ClassName:      GParameterManager
 * @Description:    用于接收参数的辅助类
 * @Author:         忞鹿
 * @CreateDate:     2020/9/18 10:28
 *
 * 详细流程：
 *      1. 查找KAPT自动生成的类 Order_MainActivity$$Parameter
 *      2. 使用 Order_MainActivity$$Parameter
 */
class GParameterManager {
    companion object {
        val instance: GParameterManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            GParameterManager()
        }
    }

    // 使用LruCache缓存
    private val parameterCache = LruCache<String, GParameterGet>(100)

    val FILE_SUFFIX_NAME = "$\$Parameter"

    //使用的时候调用这个方法
    fun loadParameter(activity: Activity) {
        //获取当前class 名称， 在缓存中查找
        val className = activity.javaClass.name
        var gParameterGet = parameterCache[className]
        if (gParameterGet == null) {
            val aClass = Class.forName("$className$FILE_SUFFIX_NAME")
            gParameterGet = aClass.newInstance() as GParameterGet?
            parameterCache.put(className, gParameterGet)
        }
        gParameterGet.getParameter(activity)
    }
}