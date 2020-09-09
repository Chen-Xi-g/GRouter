package com.minlukj.module_study

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.minlukj.annotation.GRouter

@GRouter(path = "/app/MainActivity2")
class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
    }
}