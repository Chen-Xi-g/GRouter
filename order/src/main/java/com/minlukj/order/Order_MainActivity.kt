package com.minlukj.order

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.minlukj.annotation.GRouter

@GRouter(path = "/order/Order_MainActivity")
class Order_MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.order_activity_main)
//        home_btn.setOnClickListener {
//            startActivity(Intent(this, RecordPathManager.getTargetClass("app","MainActivity")))
//        }
//        login_btn.setOnClickListener {
//            startActivity(
//                Intent(this,
//                    RecordPathManager.getTargetClass("login","LoginMainActivity"))
//            )
//        }
    }
}