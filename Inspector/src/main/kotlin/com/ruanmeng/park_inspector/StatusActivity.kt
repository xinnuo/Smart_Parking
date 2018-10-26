package com.ruanmeng.park_inspector

import android.os.Bundle
import android.support.design.widget.TabLayout
import com.lzg.extend.JsonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.addItems
import com.ruanmeng.base.getString
import com.ruanmeng.base.gone
import com.ruanmeng.model.CommonModel
import com.ruanmeng.model.StatusData
import com.ruanmeng.share.BaseHttp
import kotlinx.android.synthetic.main.activity_status.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import org.jetbrains.anko.collections.forEachWithIndex
import org.jetbrains.anko.design.listeners.__TabLayout_OnTabSelectedListener

class StatusActivity : BaseActivity() {

    private val list = ArrayList<StatusData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status)
        init_title("车位状况监控")

        getData()
    }

    override fun init_title() {
        super.init_title()
        empty_hint.text = "暂无相关车位信息"
    }

    private fun initTabLayout() {
        status_tab.apply {
            onTabSelectedListener {
                onTabSelected {
                    OkGo.getInstance().cancelTag(this@StatusActivity)
                    // window.decorView.postDelayed({ runOnUiThread { updateList() } }, 300)
                }
            }

            list.forEachWithIndex { position, data ->
                addTab(this.newTab().setText(data.groupName), position == 0)
            }
        }
    }

    override fun getData() {
        OkGo.post<CommonModel>(BaseHttp.find_parking_info)
                .tag(this@StatusActivity)
                .headers("token", getString("token"))
                .execute(object : JsonDialogCallback<CommonModel>(baseContext, true) {

                    override fun onSuccess(response: Response<CommonModel>) {

                        list.apply {
                            clear()
                            addItems(response.body().`object`)
                        }

                        initTabLayout()
                    }

                })
    }

    private fun updateList() {
        swipe_refresh.isRefreshing = true

        empty_view.gone()
        if (list.isNotEmpty()) {
            list.clear()
            mAdapter.notifyDataSetChanged()
        }

        getData(pageNum)
    }

    private fun TabLayout.onTabSelectedListener(init: __TabLayout_OnTabSelectedListener.() -> Unit) {
        val listener = __TabLayout_OnTabSelectedListener()
        listener.init()
        addOnTabSelectedListener(listener)
    }
}
