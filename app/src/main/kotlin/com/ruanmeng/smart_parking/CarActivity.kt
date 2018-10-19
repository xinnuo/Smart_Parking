package com.ruanmeng.smart_parking

import android.os.Bundle
import android.view.View
import com.ruanmeng.base.BaseActivity
import org.jetbrains.anko.include
import org.jetbrains.anko.startActivity

class CarActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        include<View>(R.layout.layout_list)
        init_title("我的车辆管理", "添加车辆")
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.tv_nav_right -> startActivity<CarAddActivity>()
        }
    }
}
