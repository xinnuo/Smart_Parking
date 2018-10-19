package com.ruanmeng.park_inspector

import android.os.Bundle
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.showToast

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    private var exitTime: Long = 0
    override fun onBackPressed() {
        if (System.currentTimeMillis() - exitTime > 2000) {
            showToast("再按一次退出程序")
            exitTime = System.currentTimeMillis()
        } else super.onBackPressed()
    }
}
