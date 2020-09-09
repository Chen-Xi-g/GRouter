package com.minlukj.common

import androidx.appcompat.app.AppCompatActivity

/**
 *
 * @ProjectName:    Module_Study
 * @Package:        com.minlukj.common
 * @ClassName:      RecordPathManager
 * @Description:    全局路径记录器
 * @Author:         忞鹿
 * @CreateDate:     2020/9/4 15:02
 */
class RecordPathManager {

    companion object {
        private val maps = HashMap<String, ArrayList<PathBean>>()

        /**
         * 加入组件class
         * @param group 组名称，一般为组件名称
         * @param pathName class 路径
         * @param clazz 要跳转的class
         */
        fun joinGroup(group: String, pathName: String, clazz: Class<out AppCompatActivity>) {
            var path = maps[group]
            if (path.isNullOrEmpty()) {
                path = arrayListOf()
                maps[group] = path
            } else {
                path.forEach {
                    if (it.path == pathName) return
                }
            }
            path.add(PathBean(pathName, clazz))
        }

        /**
         * 获取Class对象
         * @param group 组件名称
         * @param path 路径
         */
        fun getTargetClass(group: String, pathName: String): Class<out AppCompatActivity>? {
            val path = maps[group]
            if (path.isNullOrEmpty()) {
                return null
            }
            path.forEach {
                if (it.path == pathName) return it.clazz
            }
            return null
        }
    }
}