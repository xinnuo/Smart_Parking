package com.ruanmeng.smart_parking

import android.os.Bundle
import android.view.View
import com.ruanmeng.base.BaseActivity
import org.jetbrains.anko.startActivity

class HelpActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
        init_title("帮助与反馈", "反馈")
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.tv_nav_right -> startActivity<FeedbackActivity>()
        }
    }
}
