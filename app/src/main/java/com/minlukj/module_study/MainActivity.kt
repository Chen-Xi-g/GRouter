package com.minlukj.module_study

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.minlukj.annotation.GRouter
import com.minlukj.login.LoginMainActivity
import com.minlukj.order.Order_MainActivity
import kotlinx.android.synthetic.main.activity_main.*

@GRouter(path = "/app/MainActivity")
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        order_btn.setOnClickListener {
            startActivity(Intent(this, Order_MainActivity::class.java))
        }
        login_btn.setOnClickListener {
            startActivity(Intent(this, LoginMainActivity::class.java))
        }
    }
}