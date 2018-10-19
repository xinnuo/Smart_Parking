package com.ruanmeng.smart_parking

import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import com.ruanmeng.base.BaseActivity
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.listeners.onClick

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
                onClick {  }
            }.lparams(width = matchParent, height = dip(40)) {
                topMargin = dip(40)
                horizontalMargin = dip(40)
            }
        }
        init_title("我的钱包")
    }
}
