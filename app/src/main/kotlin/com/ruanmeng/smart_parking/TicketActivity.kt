package com.ruanmeng.smart_parking

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.lzg.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getString
import com.ruanmeng.base.showToast
import com.ruanmeng.model.RefreshMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.isMobile
import com.ruanmeng.utils.trimString
import kotlinx.android.synthetic.main.activity_ticket.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.sdk25.listeners.onCheckedChange

class TicketActivity : BaseActivity() {

    private var invoiceType = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ticket)
        init_title("电子发票")

        ticket_check1.performClick()
    }

    @SuppressLint("SetTextI18n")
    override fun init_title() {
        super.init_title()
        ticket_group.onCheckedChange { _, checkedId ->
            when(checkedId) {
                R.id.ticket_check1 -> invoiceType = "1"
                R.id.ticket_check2 -> invoiceType = "0"
            }
        }
        ticket_money.text = "￥${intent.getStringExtra("money")}"
        bt_submit.setBackgroundResource(R.drawable.rec_bg_ova_d0d0d0)
        bt_submit.isClickable = false

        et_title.addTextChangedListener(this@TicketActivity)
        et_num.addTextChangedListener(this@TicketActivity)
        et_content.addTextChangedListener(this@TicketActivity)
        et_name.addTextChangedListener(this@TicketActivity)
        et_tel.addTextChangedListener(this@TicketActivity)
        et_addr.addTextChangedListener(this@TicketActivity)
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.bt_submit -> {
                if (!et_tel.text.isMobile()) {
                    showToast("请输入正确的联系电话")
                    return
                }

                OkGo.post<String>(BaseHttp.add_invoice)
                        .tag(this@TicketActivity)
                        .headers("token", getString("token"))
                        .params("goodsOrderId", intent.getStringExtra("goodsOrderId"))
                        .params("invoiceType", invoiceType)
                        .params("invoiceTitle", et_title.text.trimString())
                        .params("rnumber", et_num.text.trimString())
                        .params("invoiceConet", et_content.text.trimString())
                        .params("invoiceAmount", intent.getStringExtra("money"))
                        .params("contacts", et_name.text.trimString())
                        .params("tel", et_tel.text.toString())
                        .params("address", et_addr.text.trimString())
                        .execute(object : StringDialogCallback(baseContext) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                showToast(msg)
                                EventBus.getDefault().post(RefreshMessageEvent("开发票"))
                                ActivityStack.screenManager.popActivities(this@TicketActivity::class.java)
                            }

                        })
            }
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (et_title.text.isNotBlank()
                && et_num.text.isNotBlank()
                && et_content.text.isNotBlank()
                && et_name.text.isNotBlank()
                && et_tel.text.isNotBlank()
                && et_addr.text.isNotBlank()) {
            bt_submit.setBackgroundResource(R.drawable.rec_bg_ova_blue)
            bt_submit.isClickable = true
        } else {
            bt_submit.setBackgroundResource(R.drawable.rec_bg_ova_d0d0d0)
            bt_submit.isClickable = false
        }
    }
}
