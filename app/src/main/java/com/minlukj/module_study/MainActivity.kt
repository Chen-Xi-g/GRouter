package com.minlukj.module_study

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.minlukj.annotation.GRouter
import com.minlukj.grouter_api.GRouterManager
import kotlinx.android.synthetic.main.activity_main.*

@GRouter(path = "/app/MainActivity")
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tv_bundle_tag.text = intent.getStringExtra("tag")
        order_btn.setOnClickListener {
            GRouterManager.instance
                .build("/order/Order_MainActivity")
                .withString("tag", "/app/MainActivity")
                .navigation(this)
//            startActivity(Intent(this, Order_MainActivity::class.java))
        }
        login_btn.setOnClickListener {
            GRouterManager.instance
                .build("/login/LoginMainActivity")
                .withString("tag", "app/MainActivity")
                .navigation(this)
//            startActivity(Intent(this, LoginMainActivity::class.java))
        }
    }
}