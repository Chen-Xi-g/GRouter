package com.minlukj.order

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.minlukj.annotation.GParameter
import com.minlukj.annotation.GRouter
import com.minlukj.grouter_api.GRouterManager
import kotlinx.android.synthetic.main.order_activity_main.*

@GRouter(path = "/order/Order_MainActivity")
class Order_MainActivity : AppCompatActivity() {
    @GParameter
    lateinit var tag: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.order_activity_main)
        tv_bundle_tag.text = intent.getStringExtra("tag")
        home_btn.setOnClickListener {
            GRouterManager.instance
                .build("/app/MainActivity")
                .withString("tag", "/order/Order_MainActivity")
                .navigation(this)
        }
        login_btn.setOnClickListener {
            GRouterManager.instance
                .build("/login/LoginMainActivity")
                .withString("tag", "/order/Order_MainActivity")
                .navigation(this)
        }
    }
}