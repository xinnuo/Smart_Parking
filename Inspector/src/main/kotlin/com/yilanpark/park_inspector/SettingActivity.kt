package com.yilanpark.park_inspector

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import cn.jpush.android.api.JPushInterface
import com.yilanpark.base.BaseActivity
import com.yilanpark.base.getBoolean
import com.yilanpark.base.getString
import com.yilanpark.base.putBoolean
import com.yilanpark.share.Const
import com.yilanpark.utils.GlideCacheUtil
import kotlinx.android.synthetic.main.activity_setting.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.sdk25.listeners.onClick
import org.jetbrains.anko.startActivity
import com.yilanpark.R
class SettingActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        init_title("设置")
    }

    override fun init_title() {
        super.init_title()
        setting_cache.text = GlideCacheUtil.getInstance().getCacheSize(this@SettingActivity)
        setting_switch.isChecked = !getBoolean("isTS")

        setting_switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                JPushInterface.resumePush(applicationContext)
                JPushInterface.setAlias(
                        applicationContext,
                        Const.JPUSH_SEQUENCE,
                        getString("token"))
                putBoolean("isTS", false)
            } else {
                JPushInterface.stopPush(applicationContext)
                putBoolean("isTS", true)
            }
        }

        setting_password.onClick { startActivity<PasswordActivity>() }
        setting_message.onClick { startActivity<MessageActivity>() }
    }

    @SuppressLint("SetTextI18n")
    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.setting_cache_ll -> {
                alert {
                    title = "清空缓存"
                    message = "确定要清空缓存吗？"
                    negativeButton("取消") {}
                    positiveButton("清空") {
                        GlideCacheUtil.getInstance().clearImageAllCache(baseContext)
                        setting_cache.text = "0B"
                    }
                }.show()
            }
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
