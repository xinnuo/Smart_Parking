package com.ruanmeng.park_inspector

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.widget.GridLayoutManager
import android.widget.ImageView
import com.lzg.extend.JsonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.CommonModel
import com.ruanmeng.model.StatusData
import com.ruanmeng.share.BaseHttp
import kotlinx.android.synthetic.main.activity_status.*
import kotlinx.android.synthetic.main.layout_empty.*
import net.idik.lib.slimadapter.SlimAdapter
import org.jetbrains.anko.collections.forEachWithIndex
import org.jetbrains.anko.design.listeners.__TabLayout_OnTabSelectedListener
import org.jetbrains.anko.startActivity

class StatusActivity : BaseActivity() {

    private val listTabs = ArrayList<StatusData>()
    private val list = ArrayList<CommonData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status)
        init_title("车位状况监控")

        getData()
    }

    override fun init_title() {
        super.init_title()
        empty_hint.text = "暂无相关车位信息"
        recycle_list.load_Grid {
            layoutManager = GridLayoutManager(baseContext, 4)
        }

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_status_grid) { data, injector ->
                    injector.text(R.id.item_status_title, data.deviceId)
                            .with<ImageView>(R.id.item_status_img) {
                                when (data.parkingStatus) {
                                    "0" -> it.setImageResource(R.mipmap.index_icon08)
                                    "1" -> it.setImageResource(R.mipmap.index_icon09)
                                    "2" -> it.setImageResource(R.mipmap.index_icon10)
                                }
                            }
                            .clicked(R.id.item_status) {
                                startActivity<StatusDetailActivity>(
                                        "parkId" to data.parkingId,
                                        "space" to data.deviceId,
                                        "address" to data.paddress)
                            }
                }
                .attachTo(recycle_list)
    }

    private fun initTabLayout() {
        status_tab.apply {
            onTabSelectedListener {
                onTabSelected {
                    list.clear()
                    list.addItems(listTabs[it!!.position].ls)
                    mAdapter.updateData(list)

                    empty_view.apply { if (list.isEmpty()) visible() else gone() }
                }
            }

            listTabs.forEachWithIndex { position, data ->
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

                        listTabs.apply {
                            clear()
                            addItems(response.body().`object`)
                        }

                        if (listTabs.isNotEmpty()) {
                            if (listTabs.any { it.groupName.isEmpty() }) {
                                status_container.gone()

                                list.clear()
                                list.addItems(listTabs[0].ls)
                                mAdapter.updateData(list)

                                empty_view.apply { if (list.isEmpty()) visible() else gone() }
                            } else {
                                status_container.visible()
                                initTabLayout()
                            }
                        }
                    }

                })
    }

    private fun TabLayout.onTabSelectedListener(init: __TabLayout_OnTabSelectedListener.() -> Unit) {
        val listener = __TabLayout_OnTabSelectedListener()
        listener.init()
        addOnTabSelectedListener(listener)
    }
}
