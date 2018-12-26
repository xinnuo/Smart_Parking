package com.yilanpark.park_inspector

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import com.kernal.plateid.MemoryCameraActivity
import com.lzg.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.yilanpark.base.BaseActivity
import com.yilanpark.base.getString
import com.yilanpark.base.showToast
import com.yilanpark.share.BaseHttp
import com.yilanpark.utils.*
import kotlinx.android.synthetic.main.activity_scan.*
import org.jetbrains.anko.sdk25.listeners.onClick
import org.jetbrains.anko.startActivity
import com.yilanpark.R
import org.jetbrains.anko.startActivityForResult

class ScanActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)
        init_title("异常上传", "异常信息上传")

        val parkingNo = intent.getStringExtra("parkingNo") ?: ""
        scan_place.setText(parkingNo)
    }

    @SuppressLint("SetTextI18n")
    override fun init_title() {
        super.init_title()
        scan_submit.setBackgroundResource(R.drawable.rec_bg_d0d0d0_r5)
        scan_submit.isClickable = false

        scan_num.filters = arrayOf<InputFilter>(NameLengthFilter(10))
        scan_num.addTextChangedListener(this@ScanActivity)
        scan_place.addTextChangedListener(this@ScanActivity)

        scan_img.onClick {
            startActivityForResult<MemoryCameraActivity>(101)
        }

        scan_clear.onClick {
            scan_num.setText("豫A88888")
            scan_num.setSelection(scan_num.text.length)
        }

        tvRight.onClick { startActivity<WrongActivity>() }
        scan_submit.onClick {
            if (!scan_num.text.trimToUpperCase().isCarNumber()) {
                scan_num.setText("")
                showToast("请输入正确的车牌号")
                return@onClick
            }

            DialogHelper.showItemDialog(
                    baseContext,
                    "选择异常类型",
                    listOf("停车不规范", "越野车", "其他")) { _, name ->

                OkGo.post<String>(BaseHttp.add_scaninfo)
                        .tag(this@ScanActivity)
                        .isMultipart(true)
                        .headers("token", getString("token"))
                        .params("carno", scan_num.text.trimToUpperCase())
                        .params("dno", scan_place.text.trimString())
                        .params("exReason", name)
                        .execute(object : StringDialogCallback(baseContext) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                showToast(msg)
                                ActivityStack.screenManager.popActivities(this@ScanActivity::class.java)
                            }

                        })
            }

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null) {
            scan_num.setText(data.getStringExtra("num").replace("null", "空"))
        }

    }

}
