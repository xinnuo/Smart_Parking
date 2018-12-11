package com.yilanpark.fragment

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.lzg.extend.BaseResponse
import com.lzg.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.yilanpark.base.*
import com.yilanpark.model.CommonData
import com.yilanpark.share.BaseHttp
import com.yilanpark.smart_parking.R
import com.yilanpark.utils.TopDecoration
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.support.v4.browse

class MallFragment : BaseFragment() {

    private val list = ArrayList<Any>()

    //调用这个方法切换时不会释放掉Fragment
    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        this.view?.visibility = if (menuVisible) View.VISIBLE else View.GONE
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return UI {
            verticalLayout {
                textView("车主服务") {
                    backgroundColorResource = R.color.white
                    textSize = 18f
                    textColorResource = R.color.black
                    gravity = Gravity.CENTER
                }.lparams(width = matchParent, height = dip(48))

                view { backgroundColorResource = R.color.divider }.lparams(height = dip(0.5f))

                include<View>(R.layout.layout_list)
            }
        }.view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init_title()

        swipe_refresh.isRefreshing = true
        getData()
    }

    override fun init_title() {
        empty_hint.text = "暂无相关服务信息！"
        empty_img.setImageResource(R.mipmap.icon_wait)

        swipe_refresh.refresh { getData() }
        recycle_list.load_Grid(swipe_refresh, null) {
            layoutManager = GridLayoutManager(activity, 3)
            addItemDecoration(TopDecoration(20))
        }

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_mall_grid) { data, injector ->

                    injector.text(R.id.item_mall_name, data.carshoppName)
                            .with<ImageView>(R.id.item_mall_img) {
                                it.setImageURL(BaseHttp.baseImg + data.carshoppIcon, R.mipmap.icon_wait)
                            }

                            .clicked(R.id.item_mall) {
                                if (data.carshoppUrl.isNotEmpty()) browse(data.carshoppUrl)
                            }
                }
                .attachTo(recycle_list)
    }

    override fun getData() {
        OkGo.post<BaseResponse<ArrayList<CommonData>>>(BaseHttp.find_carshopp_data)
                .tag(this@MallFragment)
                .params("page", "1")
                .execute(object : JacksonDialogCallback<BaseResponse<ArrayList<CommonData>>>(activity) {

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
