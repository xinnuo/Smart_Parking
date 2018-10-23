package com.ruanmeng.park_inspector

import android.os.Bundle
import cn.jpush.android.api.JPushInterface
import com.ruanmeng.base.*
import com.ruanmeng.share.Const
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.sdk25.listeners.onClick
import org.jetbrains.anko.startActivity

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init_title("首页", "设置")

        if (!getBoolean("isTS")) {
            JPushInterface.resumePush(applicationContext)
            //设置别名（先初始化）
            JPushInterface.setAlias(
                    applicationContext,
                    Const.JPUSH_SEQUENCE,
                    getString("token"))
        }
    }

    override fun init_title() {
        super.init_title()
        ivBack.gone()

        tvRight.onClick { startActivity<SettingActivity>() }
        main_item1.onClick {  }
        main_item2.onClick {  }
        main_item3.onClick {  }
        main_item4.onClick {  }
        main_item5.onClick {  }
        main_item6.onClick {  }
    }

    private var exitTime: Long = 0
    override fun onBackPressed() {
        if (System.currentTimeMillis() - exitTime > 2000) {
            showToast("再按一次退出程序")
            exitTime = System.currentTimeMillis()
        } else super.onBackPressed()
    }
}
