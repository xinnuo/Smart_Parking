package com.ruanmeng.park_inspector

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.showToast
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.isMobile
import kotlinx.android.synthetic.main.activity_register.*
import org.jetbrains.anko.startActivity

class RegisterActivity : BaseActivity() {

    private var timeCount: Int = 180
    private lateinit var thread: Runnable
    private var YZM: String = ""
    private var mTel: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        init_title("注册")
    }

    override fun init_title() {
        super.init_title()
        bt_sign.setBackgroundResource(R.drawable.rec_bg_ova_d0d0d0)
        bt_sign.isClickable = false

        et_tel.addTextChangedListener(this@RegisterActivity)
        et_yzm.addTextChangedListener(this@RegisterActivity)
        et_pwd.addTextChangedListener(this@RegisterActivity)
    }

    @SuppressLint("SetTextI18n")
    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.tv_forget -> {
                startActivity<ForgetActivity>()
                ActivityStack.screenManager.popActivities(this@RegisterActivity::class.java)
            }
            R.id.tv_login -> ActivityStack.screenManager.popActivities(this@RegisterActivity::class.java)
            R.id.tv_yzm -> {
                if (et_tel.text.isBlank()) {
                    et_tel.requestFocus()
                    showToast("请输入手机号")
                    return
                }

                if (!et_tel.text.toString().isMobile()) {
                    et_tel.requestFocus()
                    et_tel.setText("")
                    showToast("请输入正确的手机号")
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
            bt_sign.setBackgroundResource(R.drawable.rec_bg_ova_blue)
            bt_sign.isClickable = true
        } else {
            bt_sign.setBackgroundResource(R.drawable.rec_bg_ova_d0d0d0)
            bt_sign.isClickable = false
        }
    }
}