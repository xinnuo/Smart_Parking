package com.ruanmeng.park_inspector

import android.os.Bundle
import com.ruanmeng.base.BaseActivity

class StatusActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status)
        init_title("车位状况监控")
    }
}
