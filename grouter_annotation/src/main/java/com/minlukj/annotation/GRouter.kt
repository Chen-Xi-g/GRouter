package com.minlukj.annotation

/**
 *
 * @ProjectName:    Module_Study
 * @Package:        com.minlukj.annotation
 * @ClassName:      GRoute
 * @Description:    自定义路由注解
 * @Author:         忞鹿
 * @CreateDate:     2020/9/7 10:12
 */
// 哪里可以使用该注解
@Target(AnnotationTarget.CLASS)
// 注解在编译时使用
@Retention(AnnotationRetention.SOURCE)
annotation class GRouter constructor(val group: String = "", val path: String)