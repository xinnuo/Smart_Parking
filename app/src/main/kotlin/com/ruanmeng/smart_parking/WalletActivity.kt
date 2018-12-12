package com.ruanmeng.smart_parking

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.text.InputFilter
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import com.cuieney.rxpay_annotation.WX
import com.cuieney.sdk.rxpay.RxPay
import com.lzg.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.lzy.okgo.utils.OkLogger
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getString
import com.ruanmeng.base.optStringNotEmpty
import com.ruanmeng.base.showToast
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.DecimalNumberFilter
import com.ruanmeng.utils.startIncreaseAnimator
import com.ruanmeng.utils.toTextDouble
import io.reactivex.android.schedulers.AndroidSchedulers
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.listeners.onClick
import org.json.JSONObject

@WX(packageName = "com.ruanmeng.smart_parking")
class WalletActivity : BaseActivity() {

    private lateinit var balanceTV: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        verticalLayout {
            backgroundColorResource = R.color.white
            gravity = Gravity.CENTER_HORIZONTAL

            balanceTV = themedTextView("0.00", R.style.Font15_black) {
                textSize = sp(16).toFloat()
                gravity = Gravity.CENTER
            }.lparams(width = matchParent) {
                topMargin = dip(80)
            }

            themedTextView("账户余额", R.style.Font13_black)
                    .lparams {
                        topMargin = dip(10)
                    }

            themedTextView("充值", R.style.Font15_white) {
                backgroundResource = R.drawable.rec_bg_ova_blue
                gravity = Gravity.CENTER
                onClick { showChargeDialog() }
            }.lparams(width = matchParent, height = dip(40)) {
                topMargin = dip(40)
                horizontalMargin = dip(40)
            }
        }
        init_title("我的钱包")

        getData()
    }

    override fun getData() {
        OkGo.post<String>(BaseHttp.user_balance)
                .tag(this@WalletActivity)
                .headers("token", getString("token"))
                .execute(object : StringDialogCallback(baseContext) {

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        val obj = JSONObject(response.body())
                        val balance = obj.optStringNotEmpty("object", "0").toFloat()
                        balanceTV.startIncreaseAnimator(balance)
                    }

                })
    }

    private fun getPayData(count: String, way: String) {
        OkGo.post<String>(BaseHttp.recharge_request)
                .tag(this@WalletActivity)
                .headers("token", getString("token"))
                .params("rechargeSum", count)
                .params("payType", way)
                .execute(object : StringDialogCallback(baseContext) {

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        val obj = JSONObject(response.body()).optString("object")
                        val data = JSONObject(response.body()).optString("object")
                        when (way) {
                            "AliPay" -> RxPay(baseContext)
                                    .requestAlipay(obj)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({
                                        if (it) {
                                            showToast("支付成功")
                                            window.decorView.postDelayed({ getData() }, 300)
                                        } else showToast("支付失败")
                                    }) { OkLogger.printStackTrace(it) }
                            "WxPay" -> RxPay(baseContext)
                                    .requestWXpay(data)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({
                                        if (it) {
                                            showToast("支付成功")
                                            window.decorView.postDelayed({ getData() }, 300)
                                        } else showToast("支付失败")
                                    }) { OkLogger.printStackTrace(it) }
                        }
                    }

                })
    }

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    @SuppressLint("InflateParams")
    private fun showChargeDialog() {
        val view = LayoutInflater.from(baseContext).inflate(R.layout.dialog_wallet_pay, null) as View
        val payCount = view.findViewById<EditText>(R.id.pay_count)
        val payGroup = view.findViewById<RadioGroup>(R.id.pay_group)
        val btPay = view.findViewById<Button>(R.id.bt_pay)
        val dialog = BottomSheetDialog(baseContext, R.style.BottomSheetDialogStyle)

        payGroup.check(R.id.pay_check1)

        payCount.filters = arrayOf<InputFilter>(DecimalNumberFilter())

        btPay.onClick {
            if (payCount.text.isEmpty()) {
                showToast("请输入充值金额")
                return@onClick
            }

            if (payCount.text.toTextDouble() == 0.0) {
                showToast("输入金额为0元，请重新输入")
                return@onClick
            }

            dialog.dismiss()

            when (payGroup.checkedRadioButtonId) {
                R.id.pay_check1 -> getPayData(payCount.text.toString(), "WxPay")
                R.id.pay_check2 -> getPayData(payCount.text.toString(), "AliPay")
            }
        }

        dialog.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        dialog.setContentView(view)
        dialog.show()
    }
}
