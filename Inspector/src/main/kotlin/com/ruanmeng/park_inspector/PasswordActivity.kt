package com.ruanmeng.park_inspector

import android.os.Bundle
import android.view.View
import com.lzg.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getString
import com.ruanmeng.base.showToast
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import kotlinx.android.synthetic.main.activity_password.*

class PasswordActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password)
        init_title("修改密码")
    }

    override fun init_title() {
        super.init_title()
        bt_ok.setBackgroundResource(R.drawable.rec_bg_d0d0d0_r5)
        bt_ok.isClickable = false

        et_old.addTextChangedListener(this)
        et_new.addTextChangedListener(this)
        et_confirm.addTextChangedListener(this)
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.bt_ok -> {
                if (et_old.text.length < 6
                        || et_new.text.length < 6
                        || et_confirm.text.length < 6) {
                    showToast("密码长度不少于6位")
                    return
                }

                if (et_new.text.toString() != et_confirm.text.toString()) {
                    showToast("密码输入不一致，请重新输入")
                    return
                }

                OkGo.post<String>(BaseHttp.password_change_sub)
                        .tag(this@PasswordActivity)
                        .headers("token", getString("token"))
                        .params("oldPwd", et_old.text.toString())
                        .params("newPwd", et_new.text.toString())
                        .params("confirmPwd", et_confirm.text.toString())
                        .execute(object : StringDialogCallback(baseContext) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                showToast(msg)
                                ActivityStack.screenManager.popActivities(this@PasswordActivity::class.java)
                            }

                        })
            }
        }
    }
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (et_old.text.isNotBlank()
                && et_new.text.isNotBlank()
                && et_confirm.text.isNotBlank()) {
            bt_ok.setBackgroundResource(R.drawable.rec_bg_blue_r5)
            bt_ok.isClickable = true
        } else {
            bt_ok.setBackgroundResource(R.drawable.rec_bg_d0d0d0_r5)
            bt_ok.isClickable = false
        }
    }
}
