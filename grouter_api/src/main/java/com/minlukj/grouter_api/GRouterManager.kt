package com.minlukj.grouter_api

import android.content.Context
import android.content.Intent
import android.util.Log
import android.util.LruCache
import com.minlukj.annotation.bean.TypeEnum
import com.minlukj.groute_api.GBundleManager
import com.minlukj.groute_api.GRouterGroup
import com.minlukj.groute_api.GRouterPath

/**
 *
 * @ProjectName:    Module_Study
 * @Package:        com.minlukj.grouter_api
 * @ClassName:      GRouterManager
 * @Description:    路由管理器，辅助完成交互通信
 * @Author:         忞鹿
 * @CreateDate:     2020/9/17 15:58
 *
 *  详细流程：
 *      1.拼接 找 GRouter$$Group$$login
 *      2.进入 GRouter$$Group$$login 调用函数返回groupMap
 *      3.执行 groupMap.get(group)  group == login
 *      4.查找  GRouter$$Path$$login.class
 *      5.进入  GRouter$$Path$$login.class 执行函数
 *      6.执行 pathMap.get(path) path = "/login/Login_MainActivity"
 *      7.拿到 RouterBean（login_MainActivity.class）
 *      8.startActivity（new Intent(this, Login_MainActivity.class)）
 */
class GRouterManager private constructor() {

    /**
     * 单例
     * 双重校验锁模式
     */
    companion object {
        val instance: GRouterManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            GRouterManager()
        }
    }

    /**
     * 这两个成员参数的意义：
     *      group : 根据组名可以拿到 KAPT 自动生成的 `GRouter$$Path$$login`
     *      path : 根据路径可以拿到对应的 Class 类 Login_MainActivity.class
     */
    private var group = ""
    private var path = ""

    /**
     * 为了保证性能，使用 LruCache 缓存
     */
    private val groupLruCache = LruCache<String, GRouterGroup>(100)
    private val pathLruCache = LruCache<String, GRouterPath>(100)

    // 方便拼接 FILE_GROUP_NAME + group
    private val FILE_GROUP_NAME = "GRouter$\$Group$\$"

    /**
     * 要跳转到哪个Activity，传入要跳转的Activity路由path校验
     */
    fun build(path: String): GBundleManager {
        // 判断Path是否符合规则 否则提示异常
        // 是否为空 || 是否以 `/` 开头 || 最后一个 `/` 出现的位置 ||
        if (path.isEmpty() || !path.startsWith("/") || path.lastIndexOf("/") == 0)
            throw IllegalAccessException("检查Path是否符合规范，正确Path为：/xxx/Xxx_YyyActivity")


        //截取组名拼接
        val finalGroup = path.substring(1, path.indexOf("/", 1))
        if (finalGroup.isEmpty())
            throw IllegalAccessException("检查Path是否符合规范，正确Path为：/xxx/Xxx_YyyActivity")

        //符合规则为 group 和 path 赋值
        group = finalGroup
        this.path = path

        // 判断
        return GBundleManager()
    }

    /**
     * 在这里完成跳转的逻辑
     * @param context 传入context完成跳转
     * @param bundle 传入bundle完成参数传递
     */
    fun navigation(context: Context, bundle: GBundleManager): Any? {
        //拼接 Group 绝对路径 用来查找 KAPT 在指定 Module 生成的文件
        val groupClassName = "${context.packageName}.$FILE_GROUP_NAME$group"
        Log.d("GRouter>>>>", "navigation：groupClassName = $groupClassName")

        /* TODO 读取 Group 缓存 */
        //从Group缓存中读取 GRouterGroup
        var loadGroup = groupLruCache.get(group)
        if (loadGroup == null) {
            // 加载KAPT路由组Group类文件
            val aClass = Class.forName(groupClassName)
            //初始化 loadGroup
            loadGroup = aClass.newInstance() as GRouterGroup
            groupLruCache.put(group, loadGroup)
        }

        //如果
        if (loadGroup.getGroupMap().isNullOrEmpty())
            throw RuntimeException("路由表Group发生异常，请检查是否符合规范。")


        /* TODO 读取 Path 缓存 */
        //从Path缓存中读取 GRouterPath
        var loadPath = pathLruCache.get(path)
        if (loadPath == null) {
            // LruCache 中没有 Path 从路由表中初始化
            val clazz = loadGroup.getGroupMap()[group]
            loadPath = clazz?.newInstance()
            //存储到缓存
            pathLruCache.put(path, loadPath)
        }

        if (loadPath.getPathMap().isEmpty())
            throw RuntimeException("路由表Path发生异常，请检查是否通过注解设置Path或是否符合规范。")

        // 执行跳转的逻辑, 通过path拿到需要跳转的类
        val gRouterBean = loadPath.getPathMap()[path]
        //判断枚举类型是否为Activity
        when (gRouterBean?.typeEnum) {
            //可以通过自己扩展的类型进行判断
            TypeEnum.ACTIVITY -> {
                val intent = Intent(context, gRouterBean.clazz)
                //携带参数
                intent.putExtras(bundle.bundle)
                context.startActivity(intent, bundle.bundle)
            }
        }

        return null
    }

}