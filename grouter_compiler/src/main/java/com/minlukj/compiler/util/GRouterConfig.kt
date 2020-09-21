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
        const val ACTIVITY_PACKAGE = "android.app.Activity"

        //GRouter api 包名
        const val GROUTER_API_PACKAGE = "com.minlukj.grouter_api"

        //GRouter api 的 GRouterGroup 文件
        const val GROUTER_API_GROUP = "${GROUTER_API_PACKAGE}.GRouterGroup"

        //GRouter api 的 GRouterPath 文件
        const val GROUTER_API_PATH = "${GROUTER_API_PACKAGE}.GRouterPath"

        const val GROUTER_API_PARAMETER_GET = "${GROUTER_API_PACKAGE}.GParameterGet"

        //路由组中的 Path 方法名
        const val PATH_METHOD_NAME = "getPathMap"

        //路由组中的 Group 方法名
        const val GROUP_METHOD_NAME = "getGroupMap"

        //获取参数的方法名
        const val PARAMETER_METHOD_NAME = "getParameter"

        // String 的路径
        const val STRING = "java.lang.String"

        //路由组中的 Path 变量名
        const val PATH_VAR = "pathMap"

        //路由组中的 Group 变量名
        const val GROUP_VAR = "groupMap"

        //获取参数的变量名
        const val PARAMETER_VAR = "targetParameter"

        //路由组 Path 生成的名字规则
        const val PATH_FILE_NAME = "GRouter$\$Path$\$"

        //路由组 Group 生成的名字规则
        const val GROUP_FILE_NAME = "GRouter$\$Group$\$"

        //Parameter 注解的包名
        const val PARAMETER_PAKAGE = "com.minlukj.annotation.GParameter"

        //自动生成类名的后缀
        const val PARAMETER_SUFFIX_NAME = "$\$Parameter"
    }
}