package com.ruanmeng.smart_parking

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import com.cuieney.rxpay_annotation.WX
import com.cuieney.sdk.rxpay.RxPay
import com.lzg.extend.BaseResponse
import com.lzg.extend.StringDialogCallback
import com.lzg.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.lzy.okgo.utils.OkLogger
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.RefreshMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.toNotDouble
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_bill.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.jetbrains.anko.sdk25.listeners.onClick
import org.jetbrains.anko.startActivity
import org.json.JSONObject
import java.text.DecimalFormat

@WX(packageName = "com.ruanmeng.smart_parking")
class BillActivity : BaseActivity() {

    private val list = ArrayList<Any>()
    private var mStatus = ""
    private var mBanlance = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bill)
        init_title("我的账单")

        EventBus.getDefault().register(this@BillActivity)

        getData()
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
                    val paySum = data.paySum.toNotDouble()

                    @Suppress("DEPRECATION")
                    injector.text(R.id.item_bill_num, "车牌号：${data.carNo}")
                            .text(R.id.item_bill_start, "开始时间：${data.startDate}")
                            .text(R.id.item_bill_end, "结束时间：${data.endDate}")
                            .text(R.id.item_bill_address, data.daddress)
                            .text(R.id.item_bill_money, DecimalFormat("0.00").format(data.paySum.toNotDouble()))
                            .text(R.id.item_bill_fee, DecimalFormat("0.00").format(data.LateFee.toNotDouble()))
                            .text(R.id.item_bill_press, when (data.status) {
                                "-1", "0", "1" -> if (paySum > 0.0) "去付款" else "无需支付"
                                "5" -> "开发票"
                                else -> ""
                            })
                            .background(R.id.item_bill_press, when (data.status) {
                                "-1", "0", "1" -> {
                                    if (paySum > 0.0) R.drawable.rec_bg_trans_stroke_orange
                                    else R.drawable.rec_bg_trans_stroke_d0d0d0
                                }
                                else -> R.drawable.rec_bg_trans_stroke_orange
                            })
                            .textColor(R.id.item_bill_press, resources.getColor(if (paySum == 0.0) R.color.orange else R.color.light))
                            .textColor(R.id.item_bill_press, when (data.status) {
                                "-1", "0", "1" -> {
                                    resources.getColor(if (paySum > 0.0) R.color.orange else R.color.light)
                                }
                                else -> resources.getColor(R.color.orange)
                            })

                            .visibility(R.id.item_bill_bar, when (data.status) {
                                "-1", "0", "1", "5" -> View.VISIBLE
                                else -> View.GONE
                            })
                            .visibility(R.id.item_bill_late, if (data.LateFee.isEmpty()) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_bill_divider, if (isLast) View.VISIBLE else View.GONE)

                            .clicked(R.id.item_bill_press) {
                                when (data.status) {
                                    "-1", "0", "1" -> if (paySum > 0.0) showChargeDialog(data.goodsOrderId, data.parkingInfoId)
                                    "5" -> startActivity<TicketActivity>(
                                            "goodsOrderId" to data.goodsOrderId,
                                            "money" to if (data.realPaySum.isEmpty()) "0.00" else data.realPaySum)
                                }
                            }
                }
                .attachTo(recycle_list)
    }

    override fun getData() {
        OkGo.post<String>(BaseHttp.user_balance)
                .tag(this@BillActivity)
                .headers("token", getString("token"))
                .execute(object : StringDialogCallback(baseContext, false) {

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        val obj = JSONObject(response.body())
                        mBanlance = obj.optStringNotEmpty("object", "0").toDouble()
                    }

                })
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

    private fun getPayData(id: String, parkId: String, way: String) {
        OkGo.post<String>(BaseHttp.goodsorder_pay)
                .tag(this@BillActivity)
                .headers("token", getString("token"))
                .params("goodsOrderId", id)
                .params("payType", way)
                .params("parkingInfoId", parkId)
                .execute(object : StringDialogCallback(baseContext) {

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        val obj = JSONObject(response.body()).optString("object")
                        val data = JSONObject(response.body()).optString("object")
                        when (way) {
                            "AliPay" -> RxPay(baseContext)
                                    .requestAlipay(obj)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({
                                        if (it) paySuccessAfter(parkId)
                                        else showToast("支付失败")
                                    }) {
                                        OkLogger.printStackTrace(it)
                                    }
                            "WxPay" -> RxPay(baseContext)
                                    .requestWXpay(data)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({
                                        if (it) paySuccessAfter(parkId)
                                        else showToast("支付失败")
                                    }) {
                                        OkLogger.printStackTrace(it)
                                    }
                        }
                    }

                })
    }

    private fun getBanlanceData(id: String, parkId: String) {
        OkGo.post<String>(BaseHttp.goodsorder_paybalance)
                .tag(this@BillActivity)
                .headers("token", getString("token"))
                .params("goodsOrderId", id)
                .params("parkingInfoId", parkId)
                .execute(object : StringDialogCallback(baseContext, false) {

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        paySuccessAfter(parkId)
                    }

                })
    }

    private fun paySuccessAfter(parkId: String) {
        EventBus.getDefault().post(RefreshMessageEvent("支付成功"))
        startActivity<HelpActivity>(
                "title" to "支付成功",
                "parkId" to parkId)
    }

    private fun updateList() {
        swipe_refresh.isRefreshing = true

        empty_view.gone()
        if (list.isNotEmpty()) {
            list.clear()
            mAdapter.notifyDataSetChanged()
        }

        pageNum = 1
        getData(pageNum)
    }

    @SuppressLint("InflateParams", "SetTextI18n")
    private fun showChargeDialog(id: String, parkId: String) {
        val view = LayoutInflater.from(baseContext).inflate(R.layout.dialog_bill_pay, null) as View
        val payGroup = view.findViewById<RadioGroup>(R.id.pay_group)
        val payCheck3 = view.findViewById<RadioButton>(R.id.pay_check3)
        val btPay = view.findViewById<Button>(R.id.bt_pay)
        val dialog = BottomSheetDialog(baseContext, R.style.BottomSheetDialogStyle)

        payCheck3.text = "余额支付(${DecimalFormat("0.00").format(mBanlance)}元)"
        payGroup.check(R.id.pay_check1)

        btPay.onClick {
            dialog.dismiss()

            when (payGroup.checkedRadioButtonId) {
                R.id.pay_check1 -> getPayData(id, parkId, "WxPay")
                R.id.pay_check2 -> getPayData(id, parkId, "AliPay")
                R.id.pay_check3 -> getBanlanceData(id, parkId)
            }
        }

        dialog.setContentView(view)
        dialog.show()
    }

    override fun finish() {
        EventBus.getDefault().unregister(this@BillActivity)
        super.finish()
    }

    @Subscribe
    fun onMessageEvent(event: RefreshMessageEvent) {
        when (event.type) {
            "支付成功", "开发票" -> updateList()
        }
    }

}
