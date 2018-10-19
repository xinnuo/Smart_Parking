package com.ruanmeng.smart_parking

import android.os.Bundle
import com.ruanmeng.base.BaseActivity

class TicketActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ticket)
        init_title("电子发票")
    }
}
