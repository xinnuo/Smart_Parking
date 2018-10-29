package com.ruanmeng.park_inspector

import android.os.Bundle
import com.lzg.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getString
import com.ruanmeng.share.BaseHttp
import kotlinx.android.synthetic.main.activity_status_detail.*
import org.json.JSONObject

class StatusDetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status_detail)
        init_title("车位详情")

        getData()
    }

    override fun init_title() {
        super.init_title()
        status_num.setRightString(intent.getStringExtra("space"))
        status_addr.text = intent.getStringExtra("address")
    }

    override fun getData() {
        OkGo.post<String>(BaseHttp.get_parking_details)
                .tag(this@StatusDetailActivity)
                .headers("token", getString("token"))
                .params("parkingId", intent.getStringExtra("parkId"))
                .execute(object : StringDialogCallback(baseContext) {

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        val obj = JSONObject(response.body())
                                .optJSONObject("object") ?: JSONObject()

                        status_car.setRightString(obj.optString("carNo"))
                        status_start.setRightString(obj.optString("startDate"))
                        status_long.setRightString(obj.optString("parkingTime"))
                    }

                })
    }
}
