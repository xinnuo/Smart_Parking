package com.ruanmeng.smart_parking

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.lzg.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.showToast
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.share.Const
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.DESUtil
import com.ruanmeng.utils.EncryptUtil
import com.ruanmeng.utils.isMobile
import kotlinx.android.synthetic.main.activity_register.*
import org.jetbrains.anko.startActivity
import org.json.JSONObject

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

                if (!et_tel.text.isMobile()) {
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

                EncryptUtil.DESIV = EncryptUtil.getiv(Const.MAKER)
                val encodeTel = DESUtil.encode(EncryptUtil.getkey(Const.MAKER), et_tel.text.toString())

                OkGo.post<String>(BaseHttp.identify_get)
                        .tag(this@RegisterActivity)
                        .isMultipart(true)
                        .params("mobile", encodeTel)
                        .params("time", Const.MAKER)
                        .execute(object : StringDialogCallback(baseContext) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                YZM = JSONObject(response.body()).optString("object")
                                mTel = et_tel.text.toString()
                                if (BuildConfig.LOG_DEBUG) {
                                    et_yzm.setText(YZM)
                                    et_yzm.setSelection(et_yzm.text.length)
                                }

                                tv_yzm.isClickable = false
                                timeCount = 180
                                tv_yzm.post(thread)
                            }

                        })
            }
            R.id.bt_sign -> {
                if (!et_tel.text.isMobile()) {
                    et_tel.requestFocus()
                    et_tel.setText("")
                    showToast("请输入正确的手机号")
                    return
                }

                if (et_tel.text.toString() != mTel) {
                    showToast("手机号码不匹配，请重新获取验证码")
                    return
                }

                if (et_yzm.text.trim().toString() != YZM) {
                    et_yzm.requestFocus()
                    et_yzm.setText("")
                    showToast("验证码错误，请重新输入")
                    return
                }

                if (et_pwd.text.length < 6) {
                    showToast("新密码长度不少于6位")
                    return
                }

                OkGo.post<String>(BaseHttp.register_sub)
                        .tag(this@RegisterActivity)
                        .params("mobile", mTel)
                        .params("loginType", "mobile")
                        .params("smscode", et_yzm.text.toString())
                        .params("password", et_pwd.text.toString())
                        .execute(object : StringDialogCallback(baseContext) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                showToast(msg)
                                startActivity<LoginActivity>()
                                ActivityStack.screenManager.popActivities(this@RegisterActivity::class.java)
                            }

                        })
            }
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
