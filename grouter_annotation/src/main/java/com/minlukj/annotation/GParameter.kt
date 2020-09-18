package com.minlukj.annotation

/**
 *
 * @ProjectName:    Module_Study
 * @Package:        com.minlukj.annotation
 * @ClassName:      GParameter
 * @Description:    获取 Intent 传递参数的注解
 * @Author:         忞鹿
 * @CreateDate:     2020/9/18 10:19
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.SOURCE)
annotation class GParameter constructor(val name: String = "")