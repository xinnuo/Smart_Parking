package com.ruanmeng.smart_parking

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import com.lzg.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getString
import com.ruanmeng.base.optStringNotEmpty
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.startIncreaseAnimator
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.listeners.onClick
import org.json.JSONObject

class WalletActivity : BaseActivity() {

    private lateinit var balanceTV: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        verticalLayout {
            backgroundColorResource = R.color.white
            gravity = Gravity.CENTER_HORIZONTAL

            balanceTV = themedTextView("0.00", R.style.Font15_black) {
                textSize = sp(18).toFloat()
            }.lparams {
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

    private fun getPayData(way: String) { }

    @SuppressLint("InflateParams")
    private fun showChargeDialog() {
        val view = LayoutInflater.from(baseContext).inflate(R.layout.dialog_wallet_pay, null) as View
        val payGroup = view.findViewById<RadioGroup>(R.id.pay_group)
        val btPay = view.findViewById<Button>(R.id.bt_pay)
        val dialog = BottomSheetDialog(baseContext, R.style.BottomSheetDialogStyle)

        payGroup.check(R.id.pay_check1)

        btPay.onClick {
            dialog.dismiss()

            when (payGroup.checkedRadioButtonId) {
                R.id.pay_check1 -> it!!.postDelayed({ getPayData("WxPay") }, 300)
                R.id.pay_check2 -> it!!.postDelayed({ getPayData("AliPay") }, 300)
            }
        }

        dialog.setContentView(view)
        dialog.show()
    }
}
