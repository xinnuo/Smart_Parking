package com.ruanmeng.park_inspector

import android.os.Bundle
import android.view.View
import com.lzg.extend.BaseResponse
import com.lzg.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.share.BaseHttp
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter

class ClockActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clock)
        init_title("打卡记录")

        swipe_refresh.isRefreshing = true
        getData()
    }

    override fun init_title() {
        super.init_title()
        empty_hint.text = "暂无相关打卡记录信息！"
        swipe_refresh.refresh { getData() }
        recycle_list.load_Linear(baseContext, swipe_refresh)

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_clock_list) { data, injector ->

                    val isLast = list.indexOf(data) == list.size - 1

                    injector.text(
                            R.id.item_clock_title,
                            "${data.createDate}   ${if (data.stype == "0") "上班" else "下班"}已打卡")

                            .visibility(R.id.item_clock_divider1, if (isLast) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_clock_divider2, if (!isLast) View.GONE else View.VISIBLE)
                }
                .attachTo(recycle_list)
    }

    override fun getData() {
        OkGo.post<BaseResponse<ArrayList<CommonData>>>(BaseHttp.punchClock_data)
                .tag(this@ClockActivity)
                .headers("token", getString("token"))
                .execute(object : JacksonDialogCallback<BaseResponse<ArrayList<CommonData>>>(baseContext) {

                    override fun onSuccess(response: Response<BaseResponse<ArrayList<CommonData>>>) {

                        list.apply {
                            clear()
                            addItems(response.body().`object`)
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
