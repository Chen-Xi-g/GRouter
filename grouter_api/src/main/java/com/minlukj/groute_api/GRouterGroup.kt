package com.minlukj.groute_api

/**
 *
 * @ProjectName:    Module_Study
 * @Package:        com.minlukj.groute_api
 * @ClassName:      GRouteGroup
 * @Description:    路由框架的分组接口
 * @Author:         忞鹿
 * @CreateDate:     2020/9/8 15:06
 */
interface GRouterGroup {
    /**
     * 每个组件都是一个组，每个组下都会有该组件的Activity。
     *      列如：login组件下有 LoginMainActivity 这个组下有很多通过注解设置的path ："login/login_main_activity"
     * 所以我们需要设计一个Map来存储这些信息
     */
    fun getGroupMap(): Map<String, Class<out GRouterPath>>
}