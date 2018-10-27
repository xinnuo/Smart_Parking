package com.ruanmeng.park_inspector

import android.os.Bundle
import com.ruanmeng.base.BaseActivity

class StatusDetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status_detail)
        init_title("车位详情")
    }
}
