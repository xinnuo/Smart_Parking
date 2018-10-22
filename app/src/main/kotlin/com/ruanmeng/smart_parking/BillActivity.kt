package com.ruanmeng.smart_parking

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.view.View
import com.lzg.extend.BaseResponse
import com.lzg.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.toNotDouble
import kotlinx.android.synthetic.main.activity_bill.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter
import org.jetbrains.anko.design.listeners.__TabLayout_OnTabSelectedListener
import java.text.DecimalFormat

class BillActivity : BaseActivity() {

    private val list = ArrayList<Any>()
    private var mStatus = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bill)
        init_title("我的账单")
    }

    override fun init_title() {
        super.init_title()
        // 28.0.0版本以后TabLayout已更新
        bill_tab.apply {
            onTabSelectedListener {
                onTabSelected {
                    mStatus = when (it!!.position) {
                        0 -> "0"
                        1 -> "2"
                        2 -> "5"
                        else -> ""
                    }

                    OkGo.getInstance().cancelTag(this@BillActivity)
                    window.decorView.postDelayed({ runOnUiThread { updateList() } }, 300)
                }
            }

            addTab(this.newTab().setText("未付款"), true)
            addTab(this.newTab().setText("已付款"), false)
            addTab(this.newTab().setText("开发票"), false)
        }

        empty_hint.text = "暂无相关账单信息"
        swipe_refresh.refresh { getData(1) }
        recycle_list.load_Linear(baseContext, swipe_refresh) {
            if (!isLoadingMore) {
                isLoadingMore = true
                getData(pageNum)
            }
        }

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_bill_list) { data, injector ->

                    val isLast = list.indexOf(data) == list.size - 1

                    injector.text(R.id.item_bill_start, "开始时间：${data.startDate}")
                            .text(R.id.item_bill_end, "结束时间：${data.endDate}")
                            .text(R.id.item_bill_address, data.daddress)
                            .text(R.id.item_bill_money, DecimalFormat("0.00").format(data.paySum.toNotDouble()))
                            .text(R.id.item_bill_fee, DecimalFormat("0.00").format(data.LateFee.toNotDouble()))
                            .text(R.id.item_bill_press, when (data.status) {
                                "0" -> "去付款"
                                "5" -> "开发票"
                                else -> ""
                            })

                            .visibility(R.id.item_bill_bar, when (data.status) {
                                "0", "5" -> View.VISIBLE
                                else -> View.GONE
                            })
                            .visibility(R.id.item_bill_late, if (data.LateFee.isEmpty()) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_bill_divider, if (isLast) View.VISIBLE else View.GONE)

                            .clicked(R.id.item_bill_press) { }
                }
                .attachTo(recycle_list)
    }

    override fun getData(pindex: Int) {
        OkGo.post<BaseResponse<ArrayList<CommonData>>>(BaseHttp.goodsorder_list_data)
                .tag(this@BillActivity)
                .headers("token", getString("token"))
                .params("status", mStatus)
                .params("page", pindex)
                .execute(object : JacksonDialogCallback<BaseResponse<ArrayList<CommonData>>>(baseContext) {

                    override fun onSuccess(response: Response<BaseResponse<ArrayList<CommonData>>>) {

                        list.apply {
                            if (pindex == 1) {
                                clear()
                                pageNum = pindex
                            }
                            addItems(response.body().`object`)
                            if (count(response.body().`object`) > 0) pageNum++
                        }

                        mAdapter.updateData(list)
                    }

                    override fun onFinish() {
                        super.onFinish()
                        swipe_refresh.isRefreshing = false
                        isLoadingMore = false

                        empty_view.apply { if (list.isEmpty()) visible() else gone() }
                    }

                })
    }

    fun updateList() {
        swipe_refresh.isRefreshing = true

        empty_view.visibility = View.GONE
        if (list.isNotEmpty()) {
            list.clear()
            mAdapter.notifyDataSetChanged()
        }

        pageNum = 1
        getData(pageNum)
    }

    private fun TabLayout.onTabSelectedListener(init: __TabLayout_OnTabSelectedListener.() -> Unit) {
        val listener = __TabLayout_OnTabSelectedListener()
        listener.init()
        addOnTabSelectedListener(listener)
    }
}
