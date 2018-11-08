package com.ruanmeng.park_inspector

import android.os.Bundle
import cn.jpush.android.api.JPushInterface
import com.lzg.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.share.Const
import com.ruanmeng.utils.DialogHelper
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

        tvRight.setOneClickListener { startActivity<SettingActivity>() }
        main_item1.setOneClickListener { startActivity<ScanActivity>() }
        main_item2.setOneClickListener { startActivity<StatusActivity>() }
        main_item3.setOneClickListener { startActivity<CustomActivity>() }
        main_item4.setOneClickListener { startActivity<PreviewActivity>() }
        main_item5.setOneClickListener { startActivity<WrongActivity>() }
        main_item6.setOneClickListener { getData() }
    }

    override fun getData() {
        OkGo.post<String>(BaseHttp.add_punchClock)
                .tag(this@MainActivity)
                .headers("token", getString("token"))
                .execute(object : StringDialogCallback(baseContext) {

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                        DialogHelper.showHintDialog(baseContext)
                    }

                    override fun onSuccessResponseErrorCode(response: Response<String>, msg: String, msgCode: String) {
                        startActivity<ClockActivity>()
                    }
                })
    }

    private var exitTime: Long = 0
    override fun onBackPressed() {
        if (System.currentTimeMillis() - exitTime > 2000) {
            showToast("再按一次退出程序")
            exitTime = System.currentTimeMillis()
        } else super.onBackPressed()
    }
}
