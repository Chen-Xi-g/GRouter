package com.minlukj.groute_api

import com.minlukj.annotation.bean.GRouterBean

/**
 *
 * @ProjectName:    Module_Study
 * @Package:        com.minlukj.groute_api
 * @ClassName:      GRoutePath
 * @Description:    路由框架的组件路径
 * @Author:         忞鹿
 * @CreateDate:     2020/9/8 15:20
 */
interface GRouterPath {
    /**
     * 这里存放着每个组件下所有的Activity路径
     *      列入：login组件下所有路由实体类信息。
     *          --> GRouteBean(login_main_activity)
     *          --> GRouteBean(login_main_activity2)
     *          --> .......
     * 所以需要设计一个Map来存储这些信息
     */
    fun getPathMap(): Map<String, GRouterBean>
}