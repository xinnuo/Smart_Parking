package com.ruanmeng.park_inspector

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.RadioGroup
import com.cuieney.rxpay_annotation.WX
import com.cuieney.sdk.rxpay.RxPay
import com.lzg.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.lzy.okgo.utils.OkLogger
import com.ruanmeng.base.*
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.toNotDouble
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_status_detail.*
import org.jetbrains.anko.sdk25.listeners.onClick
import org.jetbrains.anko.startActivity
import org.json.JSONObject
import java.text.DecimalFormat

@WX(packageName = "com.ruanmeng.park_inspector")
class StatusDetailActivity : BaseActivity() {

    private var parkingId = ""
    private var parkingInfoId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status_detail)
        init_title("车位详情", "异常上传")

        getData()
    }

    override fun init_title() {
        super.init_title()
        parkingId = intent.getStringExtra("parkId")
        val parkingNo = intent.getStringExtra("space")
        status_num.setRightString(parkingNo)
        status_addr.text = intent.getStringExtra("address")

        status_pay.setOneClickListener { showChargeDialog() }

        tvRight.setOneClickListener {
            startActivity<ScanActivity>("parkingNo" to parkingNo)
        }
    }

    override fun getData() {
        OkGo.post<String>(BaseHttp.get_parking_details)
                .tag(this@StatusDetailActivity)
                .headers("token", getString("token"))
                .params("parkingId", parkingId)
                .execute(object : StringDialogCallback(baseContext) {

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        val obj = JSONObject(response.body())
                                .optJSONObject("object") ?: JSONObject()

                        status_car.setRightString(obj.optString("carNo"))
                        status_start.setRightString(obj.optString("startDate"))
                        status_long.setRightString(obj.optString("parkingTime"))

                        parkingInfoId = obj.optString("parkingInfoId")
                        val price = obj.optString("price").toNotDouble()
                        status_price.setRightString("￥${DecimalFormat("0.00").format(price)}")
                        if (parkingInfoId.isNotEmpty() && price > 0.0) status_pay.visible()
                    }

                })
    }

    private fun getPayData(way: String) {
        OkGo.post<String>(BaseHttp.goodsorder_ppay)
                .tag(this@StatusDetailActivity)
                .headers("token", getString("token"))
                .params("goodsOrderId", "")
                .params("payType", way)
                .params("parkingInfoId", parkingInfoId)
                .execute(object : StringDialogCallback(baseContext) {

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        val obj = JSONObject(response.body()).optString("object")
                        val data = JSONObject(response.body()).optString("object")
                        when (way) {
                            "AliPay" -> RxPay(baseContext)
                                    .requestAlipay(obj)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({
                                        if (it) payAfter(parkingInfoId)
                                        else showToast("支付失败")
                                    }) { OkLogger.printStackTrace(it) }
                            "WxPay" -> RxPay(baseContext)
                                    .requestWXpay(data)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({
                                        if (it) payAfter(parkingInfoId)
                                        else showToast("支付失败")
                                    }) { OkLogger.printStackTrace(it) }
                        }
                    }

                    override fun onSuccessResponseErrorCode(response: Response<String>?, msg: String?, msgCode: String?) {
                        if (msgCode == "102") payAfter(parkingInfoId)
                        else super.onSuccessResponseErrorCode(response, msg, msgCode)
                    }

                })
    }

    private fun payAfter(parkId: String) {
        getData()
        startActivity<WebActivity>("parkId" to parkId)
    }

    @SuppressLint("InflateParams")
    private fun showChargeDialog() {
        val view = LayoutInflater.from(baseContext).inflate(R.layout.dialog_status_pay, null) as View
        val payGroup = view.findViewById<RadioGroup>(R.id.pay_group)
        val btPay = view.findViewById<Button>(R.id.bt_pay)
        val dialog = BottomSheetDialog(baseContext, R.style.BottomSheetDialogStyle)

        payGroup.check(R.id.pay_check1)

        btPay.onClick {
            dialog.dismiss()

            when (payGroup.checkedRadioButtonId) {
                R.id.pay_check1 -> getPayData("WxPay")
                R.id.pay_check2 -> getPayData("AliPay")
            }
        }

        dialog.setContentView(view)
        dialog.show()
    }

}
