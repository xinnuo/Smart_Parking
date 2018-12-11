package com.yilanpark.smart_parking

import android.os.Bundle
import android.view.View
import com.lzg.extend.BaseResponse
import com.lzg.extend.StringDialogCallback
import com.lzg.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.yilanpark.base.*
import com.yilanpark.model.CommonData
import com.yilanpark.model.RefreshMessageEvent
import com.yilanpark.share.BaseHttp
import com.yilanpark.utils.DialogHelper
import kotlinx.android.synthetic.main.activity_car.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.jetbrains.anko.startActivity

class CarActivity : BaseActivity() {

    private val list = ArrayList<Any>()
    private var owmycar = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_car)
        init_title("我的车辆管理")

        EventBus.getDefault().register(this@CarActivity)
    }

    override fun init_title() {
        super.init_title()
        car_tab.apply {
            onTabSelectedListener {
                onTabSelected {
                    owmycar = (1 - it!!.position).toString()
                    OkGo.getInstance().cancelTag(this@CarActivity)
                    window.decorView.postDelayed({ runOnUiThread { updateList() } }, 300)
                }
            }

            addTab(this.newTab().setText("绑定车辆"), true)
            addTab(this.newTab().setText("添加车辆"), false)
        }

        empty_hint.text = "暂无相关车辆信息！"
        swipe_refresh.refresh { getData() }
        recycle_list.load_Linear(baseContext, swipe_refresh)

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_car_list) { data, injector ->

                    val isLast = list.indexOf(data) == list.size - 1

                    injector.text(R.id.item_car_title, data.carNo)

                            .visibility(R.id.item_car_divider1, if (isLast) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_car_divider2, if (!isLast) View.GONE else View.VISIBLE)

                            .clicked(R.id.item_car) {
                                startActivity<CarBillActivity>("carNo" to data.carNo)
                            }

                            .clicked(R.id.item_car_del) {

                                DialogHelper.showDelDialog(
                                        baseContext,
                                        data.carNo) {

                                    OkGo.post<String>(BaseHttp.delete_car)
                                            .tag(this@CarActivity)
                                            .headers("token", getString("token"))
                                            .params("mycarId", data.mycarId)
                                            .execute(object : StringDialogCallback(baseContext) {

                                                override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                                    showToast(msg)
                                                    EventBus.getDefault().post(RefreshMessageEvent("删除车辆"))
                                                    val index = list.indexOf(data)
                                                    list.removeAt(index)
                                                    mAdapter.notifyItemRemoved(index)

                                                    empty_view.apply { if (list.isEmpty()) visible() else gone() }
                                                }

                                            })
                                }
                            }
                }
                .attachTo(recycle_list)
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.car_add -> {
                if (owmycar == "0") startActivity<CarAddActivity>()
                else startActivity<CarBindActivity>()
            }
        }
    }

    override fun getData() {
        OkGo.post<BaseResponse<ArrayList<CommonData>>>(BaseHttp.car_list_data)
                .tag(this@CarActivity)
                .headers("token", getString("token"))
                .params("owmycar", owmycar)
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

    private fun updateList() {
        swipe_refresh.isRefreshing = true

        empty_view.gone()
        if (list.isNotEmpty()) {
            list.clear()
            mAdapter.notifyDataSetChanged()
        }

        getData()
    }

    override fun finish() {
        EventBus.getDefault().unregister(this@CarActivity)
        super.finish()
    }

    @Subscribe
    fun onMessageEvent(event: RefreshMessageEvent) {
        when (event.type) {
            "添加车辆" -> updateList()
        }
    }
}
