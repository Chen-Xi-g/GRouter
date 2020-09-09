package com.minlukj.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.minlukj.annotation.GRouter

@GRouter(path = "/login/LoginMainActivity")
class LoginMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity_main)
//        home_btn.setOnClickListener {
//            startActivity(Intent(this,RecordPathManager.getTargetClass("app","MainActivity")))
//        }
//        order_btn.setOnClickListener {
//            startActivity(Intent(this,RecordPathManager.getTargetClass("order","Order_MainActivity")))
//        }
    }
}