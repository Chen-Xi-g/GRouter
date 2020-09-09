package com.minlukj.compiler.util

/**
 *
 * @ProjectName:    Module_Study
 * @Package:        com.minlukj.compiler.util
 * @ClassName:      GRouteConfig
 * @Description:
 * @Author:         忞鹿
 * @CreateDate:     2020/9/8 16:17
 */
class GRouterConfig {
    companion object {
        //@GRouter 注解的包名+类名
        const val GROUTER_PACKAGE = "com.minlukj.annotation.GRoute"

        //接收每个module的Tag标记
        const val OPTIONS = "moduleName"//Module的名称
        const val APT_PACKAGE = "packageNameForAPT"//APT 存放的包名

        //Activity的类名
        val ACTIVITY_PACKAGE = "android.app.Activity"

        //GRouter api 包名
        val GROUTER_API_PACKAGE = "com.minlukj.groute_api"

        //GRouter api 的 GRouterGroup 文件
        val GROUTER_API_GROUP = "${GROUTER_API_PACKAGE}.GRouterGroup"

        //GRouter api 的 GRouterPath 文件
        val GROUTER_API_PATH = "${GROUTER_API_PACKAGE}.GRouterPath"

        //路由组中的 Path 方法名
        val PATH_METHOD_NAME = "getPathMap"

        //路由组中的 Group 方法名
        val GROUP_METHOD_NAME = "getGroupMap"

        //路由组中的 Path 变量名
        val PATH_VAR = "pathMap"

        //路由组中的 Group 变量名
        val GROUP_VAR = "groupMap"

        //路由组 Path 生成的名字规则
        val PATH_FILE_NAME = "GRouter$\$Path$\$"

        //路由组 Group 生成的名字规则
        val GROUP_FILE_NAME = "GRouter$\$Group$\$"
    }
}