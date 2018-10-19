package com.ruanmeng.smart_parking

import android.os.Bundle
import com.ruanmeng.base.BaseActivity

class FeedbackActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)
        init_title("反馈")
    }
}
