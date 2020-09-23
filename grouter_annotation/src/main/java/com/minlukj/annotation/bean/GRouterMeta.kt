package com.minlukj.annotation.bean

import javax.lang.model.element.Element

/**
 * 封装路由路径Path的实体类对象，用于初始化和传递参数。
 * @param typeEnum 枚举类型：Activity, 暂时只有Activity，使用枚举为了后期的扩展
 * @param clazz 被注解标注的类 如：MainActivity.kt  ...
 * @param path Activity的路由路径 "/app/MainActivity" or "/login/Login_MainActivity"
 * @param group 路由组名称 "app" or "order" or "login"
 */
data class GRouterMeta(
    var typeEnum: TypeEnum?,
    var clazz: Class<*>?,
    var path: String?,
    var group: String?
) {
    lateinit var element: Element

    /**
     * @param element 类节点 可以从这里拿到类的很多信息
     */
    constructor(
        typeEnum: TypeEnum?,
        clazz: Class<*>?,
        path: String?,
        group: String?,
        element: Element?//这里需要注意 是javax.lang.model.element包下的Element
    ) : this(typeEnum, clazz, path, group) {
        if (element != null) {
            this.element = element
        }
    }

    companion object {
        //提供对外暴露的方法，初始化路由框架的实体类
        fun create(typeEnum: TypeEnum, clazz: Class<*>, path: String, group: String): GRouterMeta {
            return GRouterMeta(typeEnum, clazz, path, group)
        }
    }

    class Builder {
        private var typeEnum: TypeEnum? = null
        private var element: Element? = null
        private var clazz: Class<*>? = null
        private var path: String? = null
        private var group: String? = null
        fun addType(typeEnum: TypeEnum): Builder {
            this.typeEnum = typeEnum
            return this
        }

        fun addElement(element: Element): Builder {
            this.element = element
            return this
        }

        fun addClazz(clazz: Class<*>): Builder {
            this.clazz = clazz
            return this
        }

        fun addPath(path: String): Builder {
            this.path = path
            return this
        }

        fun addGroup(group: String): Builder {
            this.group = group
            return this
        }

        fun build(): GRouterMeta {
            if (path?.isEmpty()!!)
                throw RuntimeException("必填项path为空")
            return GRouterMeta(typeEnum, clazz, path, group, element)
        }
    }

    override fun toString(): String {
        return "GRouteBean{path='$path',group='$group'}"
    }
}