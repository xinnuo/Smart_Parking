package com.ruanmeng.smart_parking

import android.os.Bundle
import android.text.InputFilter
import android.view.View
import com.lzg.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getString
import com.ruanmeng.base.showToast
import com.ruanmeng.model.RefreshMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.*
import kotlinx.android.synthetic.main.activity_car_add.*
import org.greenrobot.eventbus.EventBus

class CarAddActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_car_add)
        init_title("添加车辆")
    }

    override fun init_title() {
        super.init_title()
        bt_save.setBackgroundResource(R.drawable.rec_bg_ova_d0d0d0)
        bt_save.isClickable = false

        et_name.filters = arrayOf<InputFilter>(NameLengthFilter(10))
        et_name.addTextChangedListener(this@CarAddActivity)
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.bt_save -> {
                if (!et_name.text.trimToUpperCase().isCarNumber()) {
                    et_name.setText("")
                    showToast("请输入正确的车牌号")
                    return
                }

                OkGo.post<String>(BaseHttp.add_car)
                        .tag(this@CarAddActivity)
                        .isMultipart(true)
                        .headers("token", getString("token"))
                        .params("carNo", et_name.text.trimToUpperCase())
                        .params("owmycar", "0")
                        .execute(object : StringDialogCallback(baseContext) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                showToast(msg)
                                EventBus.getDefault().post(RefreshMessageEvent("添加车辆"))
                                ActivityStack.screenManager.popActivities(this@CarAddActivity::class.java)
                            }

                        })
            }
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (et_name.text.isNotBlank()) {
            bt_save.setBackgroundResource(R.drawable.rec_bg_ova_blue)
            bt_save.isClickable = true
        } else {
            bt_save.setBackgroundResource(R.drawable.rec_bg_ova_d0d0d0)
            bt_save.isClickable = false
        }
    }
}
