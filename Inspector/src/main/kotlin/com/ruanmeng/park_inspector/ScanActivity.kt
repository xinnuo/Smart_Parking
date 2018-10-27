package com.ruanmeng.park_inspector

import android.os.Bundle
import android.text.InputFilter
import com.lzg.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getString
import com.ruanmeng.base.showToast
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.*
import kotlinx.android.synthetic.main.activity_scan.*
import org.jetbrains.anko.sdk25.listeners.onClick

class ScanActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)
        init_title("车牌扫描上传")
    }

    override fun init_title() {
        super.init_title()
        scan_submit.setBackgroundResource(R.drawable.rec_bg_d0d0d0_r5)
        scan_submit.isClickable = false

        scan_num.filters = arrayOf<InputFilter>(NameLengthFilter(10))
        scan_num.addTextChangedListener(this@ScanActivity)
        scan_place.addTextChangedListener(this@ScanActivity)

        scan_submit.onClick {
            if (!scan_num.text.trimToUpperCase().isCarNumber()) {
                scan_num.setText("")
                showToast("请输入正确的车牌号")
                return@onClick
            }

            OkGo.post<String>(BaseHttp.add_scaninfo)
                    .tag(this@ScanActivity)
                    .isMultipart(true)
                    .headers("token", getString("token"))
                    .params("carno", scan_num.text.trimString())
                    .params("dno", scan_place.text.trimString())
                    .execute(object : StringDialogCallback(baseContext) {

                        override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                            showToast(msg)
                            ActivityStack.screenManager.popActivities(this@ScanActivity::class.java)
                        }

                    })
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (scan_num.text.isNotBlank()
                && scan_place.text.isNotBlank()) {
            scan_submit.setBackgroundResource(R.drawable.rec_bg_blue_r5)
            scan_submit.isClickable = true
        } else {
            scan_submit.setBackgroundResource(R.drawable.rec_bg_d0d0d0_r5)
            scan_submit.isClickable = false
        }
    }
}
