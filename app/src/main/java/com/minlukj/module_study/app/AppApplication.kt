package com.minlukj.module_study.app

import android.app.Application

/**
 *
 * @ProjectName:    Module_Study
 * @Package:        com.minlukj.module_study.app
 * @ClassName:      AppApplication
 * @Description:    Application
 * @Author:         忞鹿
 * @CreateDate:     2020/9/4 16:14
 */
class AppApplication : Application() {
    override fun onCreate() {
        super.onCreate()

//        RecordPathManager.joinGroup("app","MainActivity",MainActivity::class.java)
//        RecordPathManager.joinGroup("order","Order_MainActivity", Order_MainActivity::class.java)
//        RecordPathManager.joinGroup("login","LoginMainActivity", LoginMainActivity::class.java)
    }

}