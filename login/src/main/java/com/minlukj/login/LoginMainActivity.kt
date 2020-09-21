package com.minlukj.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.minlukj.annotation.GParameter
import com.minlukj.annotation.GRouter
import com.minlukj.grouter_api.GParameterManager
import com.minlukj.grouter_api.GRouterManager
import kotlinx.android.synthetic.main.login_activity_main.*

@GRouter(path = "/login/LoginMainActivity")
class LoginMainActivity : AppCompatActivity() {
    @GParameter(name = "tag")
    var tag: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity_main)
        GParameterManager.instance.loadParameter(this)
        tv_bundle_tag.text = tag
        tv_bundle_tag.setOnClickListener {
            setResult(987)
            finish()
        }
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