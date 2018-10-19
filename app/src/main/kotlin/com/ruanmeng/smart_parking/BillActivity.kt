package com.ruanmeng.smart_parking

import android.os.Bundle
import android.support.design.widget.TabLayout
import com.lzy.okgo.OkGo
import com.ruanmeng.base.BaseActivity
import kotlinx.android.synthetic.main.activity_bill.*
import org.jetbrains.anko.design.listeners.__TabLayout_OnTabSelectedListener

class BillActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bill)
        init_title("我的账单")
    }

    override fun init_title() {
        super.init_title()
        // 28.0.0版本以后TabLayout已更新
        bill_tab.apply {
            onTabSelectedListener {
                onTabSelected {
                    OkGo.getInstance().cancelTag(this@BillActivity)
                    window.decorView.postDelayed({ runOnUiThread { } }, 300)
                }
            }

            addTab(this.newTab().setText("未付款"), true)
            addTab(this.newTab().setText("已付款"), false)
            addTab(this.newTab().setText("开发票"), false)
        }
    }

    private fun TabLayout.onTabSelectedListener(init: __TabLayout_OnTabSelectedListener.() -> Unit) {
        val listener = __TabLayout_OnTabSelectedListener()
        listener.init()
        addOnTabSelectedListener(listener)
    }
}
