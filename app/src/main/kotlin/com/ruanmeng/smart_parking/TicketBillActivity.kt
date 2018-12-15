package com.ruanmeng.smart_parking

import android.os.Bundle
import android.view.View
import com.lzg.extend.BaseResponse
import com.lzg.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.RefreshMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.toNotDouble
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.include
import java.text.DecimalFormat

class TicketBillActivity : BaseActivity() {

    private val list = ArrayList<Any>()
    private var goodIds = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        include<View>(R.layout.layout_list)
        init_title("发票账单")

        goodIds = intent.getStringExtra("goodIds")

        swipe_refresh.isRefreshing = true
        getData()
    }

    override fun init_title() {
        super.init_title()
        empty_hint.text = "暂无相关账单信息"
        swipe_refresh.refresh { getData() }
        recycle_list.load_Linear(baseContext, swipe_refresh)

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_ticket_list) { data, injector ->

                    val isLast = list.indexOf(data) == list.size - 1

                    @Suppress("DEPRECATION")
                    injector.text(R.id.item_ticket_num, "车牌号：${data.carNo}")
                            .text(R.id.item_ticket_start, "开始时间：${data.startDate}")
                            .text(R.id.item_ticket_end, "结束时间：${data.endDate}")
                            .text(R.id.item_ticket_address, data.daddress)
                            .text(R.id.item_ticket_money, DecimalFormat("0.00").format(data.paySum.toNotDouble()))
                            .text(R.id.item_ticket_fee, DecimalFormat("0.00").format(data.LateFee.toNotDouble()))

                            .visibility(R.id.item_ticket_late, if (data.LateFee.isEmpty()) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_ticket_divider, if (isLast) View.VISIBLE else View.GONE)

                            .clicked(R.id.item_ticket) {
                                EventBus.getDefault().post(RefreshMessageEvent(
                                        "添加发票",
                                        data.goodsOrderId,
                                        if (data.realPaySum.isEmpty()) "0.00" else data.realPaySum))
                                ActivityStack.screenManager.popActivities(this@TicketBillActivity::class.java)
                            }
                }
                .attachTo(recycle_list)
    }

    override fun getData() {
        OkGo.post<BaseResponse<ArrayList<CommonData>>>(BaseHttp.goodsorder_list_data)
                .tag(this@TicketBillActivity)
                .headers("token", getString("token"))
                .params("status", 5)
                .execute(object : JacksonDialogCallback<BaseResponse<ArrayList<CommonData>>>(baseContext) {

                    override fun onSuccess(response: Response<BaseResponse<ArrayList<CommonData>>>) {

                        val items = ArrayList<CommonData>()
                        items.addItems(response.body().`object`)

                        list.apply {
                            clear()
                            addAll(items.filterNot { it.goodsOrderId in goodIds })
                        }

                        mAdapter.updateData(list)
                    }

                    override fun onFinish() {
                        super.onFinish()
                        swipe_refresh.isRefreshing = false

                        empty_view.apply { if (list.isEmpty()) visible() else gone() }
                    }

                })
    }

}
