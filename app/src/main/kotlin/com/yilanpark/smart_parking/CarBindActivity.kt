package com.yilanpark.smart_parking

import android.os.Bundle
import android.text.InputFilter
import android.view.View
import com.lzg.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.yilanpark.base.BaseActivity
import com.yilanpark.base.getString
import com.yilanpark.base.showToast
import com.yilanpark.share.BaseHttp
import com.yilanpark.utils.*
import kotlinx.android.synthetic.main.activity_car_bind.*

class CarBindActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_car_bind)
        init_title("绑定车辆")
    }

    override fun init_title() {
        super.init_title()
        bt_submit.setBackgroundResource(R.drawable.rec_bg_ova_d0d0d0)
        bt_submit.isClickable = false

        et_car.filters = arrayOf<InputFilter>(NameLengthFilter(10))
        et_car.addTextChangedListener(this@CarBindActivity)
        et_name.addTextChangedListener(this@CarBindActivity)
        et_card.addTextChangedListener(this@CarBindActivity)
        et_motor.addTextChangedListener(this@CarBindActivity)
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.bt_submit -> {
                if (!et_car.text.trimToUpperCase().isCarNumber()) {
                    et_car.setText("")
                    showToast("请输入正确的车牌号")
                    return
                }

                if (!CommonUtil.IDCardValidate(et_card.text.toString())) {
                    et_card.setText("")
                    showToast("请输入正确的身份证号")
                    return
                }

                OkGo.post<String>(BaseHttp.add_car)
                        .tag(this@CarBindActivity)
                        .isMultipart(true)
                        .headers("token", getString("token"))
                        .params("carNo", et_car.text.trimToUpperCase())
                        .params("owmycar", "1")
                        .params("iem1", et_motor.text.trimToUpperCase())
                        .params("iem2", et_name.text.trimString())
                        .params("iem3", et_card.text.toString())
                        .execute(object : StringDialogCallback(baseContext) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                showToast(msg)
                                ActivityStack.screenManager.popActivities(this@CarBindActivity::class.java)
                            }

                        })
            }
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (et_car.text.isNotBlank()
                && et_name.text.isNotBlank()
                && et_card.text.isNotBlank()
                && et_motor.text.isNotBlank()) {
            bt_submit.setBackgroundResource(R.drawable.rec_bg_ova_blue)
            bt_submit.isClickable = true
        } else {
            bt_submit.setBackgroundResource(R.drawable.rec_bg_ova_d0d0d0)
            bt_submit.isClickable = false
        }
    }
}
