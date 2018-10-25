package com.ruanmeng.park_inspector

import android.os.Bundle
import com.ruanmeng.base.BaseActivity

class ScanActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)
        init_title("车牌扫描上传")
    }
}
