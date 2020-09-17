package com.minlukj.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.minlukj.annotation.GRouter
import com.minlukj.grouter_api.GRouterManager
import kotlinx.android.synthetic.main.login_activity_main.*

@GRouter(path = "/login/LoginMainActivity")
class LoginMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity_main)
        tv_bundle_tag.text = intent.getStringExtra("tag")
        home_btn.setOnClickListener {
            GRouterManager.instance
                .build("/app/MainActivity")
                .withString("tag", "/login/LoginMainActivity")
                .navigation(this)
        }
        order_btn.setOnClickListener {
            GRouterManager.instance
                .build("/order/Order_MainActivity")
                .withString("tag", "/login/LoginMainActivity")
                .navigation(this)
        }
    }
}