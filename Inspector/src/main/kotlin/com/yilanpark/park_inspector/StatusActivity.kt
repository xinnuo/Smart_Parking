package com.yilanpark.park_inspector

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.widget.GridLayoutManager
import com.lzg.extend.JsonDialogCallback
import com.lzg.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.yilanpark.base.*
import com.yilanpark.model.CommonData
import com.yilanpark.model.CommonModel
import com.yilanpark.model.StatusData
import com.yilanpark.share.BaseHttp
import com.yilanpark.utils.DialogHelper
import kotlinx.android.synthetic.main.activity_status.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter
import org.jetbrains.anko.collections.forEachWithIndex
import org.jetbrains.anko.design.listeners.__TabLayout_OnTabSelectedListener
import org.jetbrains.anko.startActivity
import com.yilanpark.R
import com.yilanpark.model.RefreshMessageEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class StatusActivity : BaseActivity() {

    private val listTabs = ArrayList<StatusData>()
    private val list = ArrayList<CommonData>()
    private val listGroup = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status)
        init_title("车位状况监控")

        EventBus.getDefault().register(this@StatusActivity)

        swipe_refresh.isRefreshing = true
        getData()
    }

    override fun init_title() {
        super.init_title()
        empty_hint.text = "暂无相关车位信息"
        swipe_refresh.refresh { getRefreshData() }
        recycle_list.load_Grid {
            layoutManager = GridLayoutManager(baseContext, 4)
        }

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_status_grid) { data, injector ->
                    injector.text(R.id.item_status_title, data.parkingNo)
                            .text(R.id.item_status_num, data.carNo)
                            .background(R.id.item_status_num,
                                    if (data.handexp == "1") R.drawable.rec_bg_purple_bottom_r5
                                    else {
                                        when (data.parkingStatus) {
                                            "0" -> R.drawable.rec_bg_blue_bottom_r5
                                            "1" -> R.drawable.rec_bg_orange_bottom_r5
                                            "2" -> R.drawable.rec_bg_red_bottom_r5
                                            else -> R.drawable.rec_bg_blue_bottom_r5
                                        }
                                    })
                            .clicked(R.id.item_status) {
                                startActivity<StatusDetailActivity>(
                                        "parkId" to data.parkingId,
                                        "space" to data.parkingNo,
                                        "address" to data.paddress)
                            }

                            .longClicked(R.id.item_status) { v ->
                                if (listGroup.filterNot { it == "未分组" }.isEmpty()) return@longClicked true

                                val groupName = listGroup[status_tab.selectedTabPosition]
                                val items = ArrayList<String>()
                                items.addAll(listGroup.filterNot { it == groupName || it == "未分组" })
                                if (groupName != "未分组") items.add("取消分组")

                                DialogHelper.showItemDialog(
                                        baseContext,
                                        "更换分组",
                                        items) { _, name ->
                                    val nowGroupId = listTabs[status_tab.selectedTabPosition].groupId
                                    val selectIndex = listGroup.indexOf(name)

                                    OkGo.post<String>(BaseHttp.add_parking_group)
                                            .tag(this@StatusActivity)
                                            .headers("token", getString("token"))
                                            .params("parkingId", data.parkingId)
                                            .params("userGroupId", if (name == "取消分组") nowGroupId else listTabs[selectIndex].groupId)
                                            .params("type", if (name == "取消分组") "1" else "")
                                            .execute(object : StringDialogCallback(baseContext) {

                                                override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                                    showToast(msg)
                                                    val itemIndex = list.indexOf(data)

                                                    if (selectIndex < 0) listTabs.first { it.groupName == "未分组" }.ls?.add(data)
                                                    else listTabs[selectIndex].ls?.add(data)
                                                    listTabs.first { it.groupId == nowGroupId }.ls?.remove(data)

                                                    list.remove(data)
                                                    mAdapter.notifyItemRemoved(itemIndex)
                                                }

                                            })

                                }

                                return@longClicked true
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
                listGroup.add(data.groupName)
                addTab(this.newTab().setText(data.groupName), position == 0)
            }
        }
    }

    override fun getData() {
        OkGo.post<CommonModel>(BaseHttp.find_parking_info)
                .tag(this@StatusActivity)
                .headers("token", getString("token"))
                .execute(object : JsonDialogCallback<CommonModel>(baseContext) {

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

                    override fun onFinish() {
                        super.onFinish()
                        swipe_refresh.isRefreshing = false
                    }

                })
    }

    private fun getRefreshData() {
        OkGo.post<CommonModel>(BaseHttp.find_parking_info)
                .tag(this@StatusActivity)
                .headers("token", getString("token"))
                .execute(object : JsonDialogCallback<CommonModel>(baseContext) {

                    override fun onSuccess(response: Response<CommonModel>) {

                        listTabs.apply {
                            clear()
                            addItems(response.body().`object`)
                        }

                        if (listTabs.isNotEmpty()) {
                            list.clear()
                            list.addItems(listTabs[if (listTabs.any { it.groupName.isEmpty() }) 0 else status_tab.selectedTabPosition].ls)
                            mAdapter.updateData(list)

                            empty_view.apply { if (list.isEmpty()) visible() else gone() }
                        }
                    }

                    override fun onFinish() {
                        super.onFinish()
                        swipe_refresh.isRefreshing = false
                    }

                })
    }

    override fun finish() {
        EventBus.getDefault().unregister(this@StatusActivity)
        super.finish()
    }

    @Subscribe
    fun onMessageEvent(event: RefreshMessageEvent) {
        when (event.name) {
            "置空成功" -> {
                swipe_refresh.isRefreshing = true
                getRefreshData()
            }
        }
    }

    private fun TabLayout.onTabSelectedListener(init: __TabLayout_OnTabSelectedListener.() -> Unit) {
        val listener = __TabLayout_OnTabSelectedListener()
        listener.init()
        addOnTabSelectedListener(listener)
    }
}
