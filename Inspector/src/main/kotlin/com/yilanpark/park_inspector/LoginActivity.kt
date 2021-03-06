package com.yilanpark.park_inspector

import android.os.Bundle
import android.view.View
import cn.jpush.android.api.JPushInterface
import com.lzg.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.yilanpark.base.*
import com.yilanpark.share.BaseHttp
import com.yilanpark.utils.ActivityStack
import com.yilanpark.utils.isMobile
import com.yilanpark.utils.trimString
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.startActivity
import org.json.JSONObject
import com.yilanpark.R
import com.yilanpark.share.Const

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
                if (!et_tel.text.isMobile()) {
                    et_tel.requestFocus()
                    et_tel.setText("")
                    showToast("请输入正确的手机号")
                    return
                }

                if (et_pwd.text.length < 6) {
                    et_pwd.requestFocus()
                    showToast("密码长度不少于6位")
                    return
                }

                OkGo.post<String>(BaseHttp.login_sub)
                        .tag(this@LoginActivity)
                        .params("accountName", et_tel.text.toString())
                        .params("password", et_pwd.text.trimString())
                        .params("loginType", "mobile")
                        .params("type", "1")
                        .execute(object : StringDialogCallback(baseContext) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                val obj = JSONObject(response.body())
                                        .optJSONObject("object") ?: JSONObject()

                                putBoolean("isLogin", true)
                                putString("token", obj.optString("token"))
                                putString("mobile", obj.optString("mobile"))

                                startActivity<MainActivity>()
                                ActivityStack.screenManager.popActivities(this@LoginActivity::class.java)
                            }

                        })
            }
            R.id.login_wx -> { }
        }
    }

    private fun clearData() {
        putBoolean("isLogin", false)
        putString("token", "")

        putBoolean("isTS", false)

        JPushInterface.stopPush(applicationContext)
        JPushInterface.deleteAlias(baseContext, Const.JPUSH_SEQUENCE)
        JPushInterface.clearAllNotifications(applicationContext)
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
