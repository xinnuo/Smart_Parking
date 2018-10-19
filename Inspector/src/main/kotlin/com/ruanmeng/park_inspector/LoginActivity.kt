package com.ruanmeng.park_inspector

import android.os.Bundle
import android.view.View
import com.ruanmeng.base.*
import com.ruanmeng.utils.ActivityStack
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.startActivity

class LoginActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        init_title("登录")
    }

    override fun init_title() {
        super.init_title()
        ivBack.gone()
        bt_login.setBackgroundResource(R.drawable.rec_bg_ova_d0d0d0)
        bt_login.isClickable = false

        et_tel.addTextChangedListener(this@LoginActivity)
        et_pwd.addTextChangedListener(this@LoginActivity)

        if (getString("mobile").isNotEmpty()) {
            et_tel.setText(getString("mobile"))
            et_tel.setSelection(et_tel.text.length)
        }

        if (intent.getBooleanExtra("offLine", false)) {
            clearData()
            ActivityStack.screenManager.popAllActivityExcept(this@LoginActivity::class.java)
        }
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.tv_forget -> startActivity<ForgetActivity>()
            R.id.tv_register -> startActivity<RegisterActivity>()
            R.id.bt_login -> {
                putBoolean("isLogin", true)
                startActivity<MainActivity>()
                ActivityStack.screenManager.popActivities(this@LoginActivity::class.java)
            }
            R.id.login_wx -> { }
        }
    }

    private fun clearData() {
        putBoolean("isLogin", false)
        putString("token", "")
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (et_tel.text.isNotBlank()
            && et_pwd.text.isNotBlank()) {
            bt_login.setBackgroundResource(R.drawable.rec_bg_ova_blue)
            bt_login.isClickable = true
        } else {
            bt_login.setBackgroundResource(R.drawable.rec_bg_ova_d0d0d0)
            bt_login.isClickable = false
        }
    }
}
