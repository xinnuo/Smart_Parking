package com.ruanmeng.smart_parking

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.lzg.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.share.Const
import com.ruanmeng.utils.*
import kotlinx.android.synthetic.main.activity_bind.*
import org.jetbrains.anko.startActivity
import org.json.JSONObject

class BindActivity : BaseActivity() {

    private var timeCount: Int = 180
    private lateinit var thread: Runnable
    private var YZM: String = ""
    private var mTel: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bind)
        init_title("绑定账号")
    }

    override fun init_title() {
        super.init_title()
        bt_bind.setBackgroundResource(R.drawable.rec_bg_ova_d0d0d0)
        bt_bind.isClickable = false

        et_name.addTextChangedListener(this@BindActivity)
        et_yzm.addTextChangedListener(this@BindActivity)
        et_pwd.addTextChangedListener(this@BindActivity)
    }

    @SuppressLint("SetTextI18n")
    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.tv_yzm -> {
                if (et_name.text.isBlank()) {
                    et_name.requestFocus()
                    showToast("请输入手机号")
                    return
                }

                if (!et_name.text.isMobile()) {
                    et_name.requestFocus()
                    et_name.setText("")
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
                val encodeTel = DESUtil.encode(EncryptUtil.getkey(Const.MAKER), et_name.text.toString())

                OkGo.post<String>(BaseHttp.identify_get2)
                        .tag(this@BindActivity)
                        .isMultipart(true)
                        .params("mobile", encodeTel)
                        .params("time", Const.MAKER)
                        .execute(object : StringDialogCallback(baseContext) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                YZM = JSONObject(response.body()).optString("object")
                                mTel = et_name.text.trimString()
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
            R.id.bt_bind -> {
                if (!et_name.text.isMobile()) {
                    et_name.requestFocus()
                    et_name.setText("")
                    showToast("请输入正确的手机号")
                    return
                }

                if (et_name.text.toString() != mTel) {
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
                    showToast("密码长度不少于6位")
                    return
                }

                OkGo.post<String>(BaseHttp.login_sub)
                        .tag(this@BindActivity)
                        .isMultipart(true)
                        .params("mobile", mTel)
                        .params("smscode", et_yzm.text.trimString())
                        .params("password", et_pwd.text.trimString())
                        .params("loginType", "WX")
                        .params("openId", intent.getStringExtra("openId"))
                        .params("nickName", intent.getStringExtra("nickName"))
                        .params("headImgUrl", intent.getStringExtra("headImgUrl"))
                        .execute(object : StringDialogCallback(baseContext) {
                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                val obj = JSONObject(response.body()).getJSONObject("object")

                                putBoolean("isLogin", true)
                                putString("token", obj.optString("token"))
                                putString("mobile", obj.optString("mobile"))
                                putString("carNum", obj.optString("carNum"))
                                putString("loginType", "WX")

                                startActivity<MainActivity>()
                                ActivityStack.screenManager.popActivities(this@BindActivity::class.java, LoginActivity::class.java)
                            }

                        })
            }
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (et_name.text.isNotBlank()
                && et_yzm.text.isNotBlank()
                && et_pwd.text.isNotBlank()) {
            bt_bind.setBackgroundResource(R.drawable.rec_bg_ova_blue)
            bt_bind.isClickable = true
        } else {
            bt_bind.setBackgroundResource(R.drawable.rec_bg_ova_d0d0d0)
            bt_bind.isClickable = false
        }
    }
}
