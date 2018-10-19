package com.ruanmeng.park_inspector

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.showToast
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.isMobile
import kotlinx.android.synthetic.main.activity_forget.*
import org.jetbrains.anko.startActivity

class ForgetActivity : BaseActivity() {

    private var timeCount: Int = 180
    private lateinit var thread: Runnable
    private var YZM: String = ""
    private var mTel: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget)
        init_title("忘记密码")
    }

    override fun init_title() {
        super.init_title()
        bt_submit.setBackgroundResource(R.drawable.rec_bg_ova_d0d0d0)
        bt_submit.isClickable = false

        et_tel.addTextChangedListener(this@ForgetActivity)
        et_yzm.addTextChangedListener(this@ForgetActivity)
        et_pwd.addTextChangedListener(this@ForgetActivity)
    }

    @SuppressLint("SetTextI18n")
    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.tv_sign -> {
                startActivity<RegisterActivity>()
                ActivityStack.screenManager.popActivities(this@ForgetActivity::class.java)
            }
            R.id.tv_login -> ActivityStack.screenManager.popActivities(this@ForgetActivity::class.java)
            R.id.tv_yzm -> {
                if (et_tel.text.isBlank()) {
                    et_tel.requestFocus()
                    showToast("请输入手机号")
                    return
                }

                if (!et_tel.text.toString().isMobile()) {
                    et_tel.requestFocus()
                    et_tel.setText("")
                    showToast("手机号码格式错误，请重新输入")
                    return
                }

                thread = Runnable {
                    tv_yzm.text = "${timeCount}秒后重发"
                    if (timeCount > 0) {
                        tv_yzm.postDelayed(thread, 1000)
                        timeCount--
                    } else {
                        tv_yzm.text = "获取验证码"
                        tv_yzm.isClickable = true
                        timeCount = 180
                    }
                }
            }
            R.id.bt_sign -> { }
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (et_tel.text.isNotBlank()
                && et_yzm.text.isNotBlank()
                && et_pwd.text.isNotBlank()) {
            bt_submit.setBackgroundResource(R.drawable.rec_bg_ova_blue)
            bt_submit.isClickable = true
        } else {
            bt_submit.setBackgroundResource(R.drawable.rec_bg_ova_d0d0d0)
            bt_submit.isClickable = false
        }
    }
}