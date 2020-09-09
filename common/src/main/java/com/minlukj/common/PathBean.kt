package com.minlukj.common


import androidx.appcompat.app.AppCompatActivity

data class PathBean(
    val path: String,
    val clazz: Class<out AppCompatActivity>
)