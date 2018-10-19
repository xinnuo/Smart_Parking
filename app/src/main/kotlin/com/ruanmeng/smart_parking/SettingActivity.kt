package com.ruanmeng.smart_parking

import android.os.Bundle
import android.view.View
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.utils.GlideCacheUtil
import kotlinx.android.synthetic.main.activity_setting.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.startActivity

class SettingActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        init_title("设置")
    }

    override fun init_title() {
        super.init_title()
        setting_cache.text = GlideCacheUtil.getInstance().getCacheSize(this@SettingActivity)
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.setting_cache_ll -> { }
            R.id.bt_quit -> {
                alert {
                    title = "退出登录"
                    message = "确定要退出当前账号吗？"
                    negativeButton("取消") {}
                    positiveButton("退出") {
                        startActivity<LoginActivity>("offLine" to true)
                    }
                }.show()
            }
        }
    }
}
