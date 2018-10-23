package com.ruanmeng.smart_parking

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import com.amap.api.services.core.AMapException
import com.amap.api.services.core.PoiItem
import com.amap.api.services.poisearch.PoiSearch
import com.ruanmeng.base.*
import com.ruanmeng.model.LocationMessageEvent
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.KeyboardHelper
import com.ruanmeng.utils.setOnPoiSearchListener
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import kotlinx.android.synthetic.main.layout_title_search.*
import net.idik.lib.slimadapter.SlimAdapter
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.listeners.onClick

class SearchActivity : BaseActivity() {

    private var list = ArrayList<Any>()
    private lateinit var query: PoiSearch.Query
    private lateinit var poiSearch: PoiSearch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        verticalLayout {
            include<View>(R.layout.layout_title_search)

            view { backgroundColorResource = R.color.divider }.lparams(height = dip(0.5f))

            include<View>(R.layout.layout_list)
        }
        setToolbarVisibility(false)
        init_title()

        search_edit.requestFocus()
    }

    override fun init_title() {
        empty_hint.text = "没有搜索到相关结果！"
        swipe_refresh.refresh { getData(1) }
        recycle_list.load_Linear(baseContext, swipe_refresh) {
            if (!isLoadingMore) {
                isLoadingMore = true
                getData(pageNum)
            }
        }

        mAdapter = SlimAdapter.create()
                .register<PoiItem>(R.layout.item_search_list) { data, injector ->
                    injector.text(R.id.item_search_name, data.title)
                    injector.text(R.id.item_search_hint, data.snippet)

                            .clicked(R.id.item_search) {
                                EventBus.getDefault().post(LocationMessageEvent(
                                        "周边搜索",
                                        data.latLonPoint.latitude.toString(),
                                        data.latLonPoint.longitude.toString()))
                                ActivityStack.screenManager.popActivities(this@SearchActivity::class.java)
                            }
                }
                .attachTo(recycle_list)

        query = PoiSearch.Query(
                "",
                "",
                intent.getStringExtra("city"))

        poiSearch = PoiSearch(baseContext, query)
        poiSearch.setOnPoiSearchListener {
            onPoiSearched { result, code ->
                swipe_refresh.isRefreshing = false
                isLoadingMore = false

                if (code != AMapException.CODE_AMAP_SUCCESS) return@onPoiSearched

                if (result != null && result.query != null) {
                    if (pageNum == 1) {
                        if (list.isNotEmpty()) recycle_list.scrollToPosition(0)
                        list.clear()
                    }
                    if (result.pois.isNotEmpty()) pageNum++
                    list.addItems(result.pois)
                    mAdapter.updateData(list)

                    empty_view.apply { if (list.isEmpty()) visible() else gone() }
                }
            }
        }

        search_edit.addTextChangedListener(this@SearchActivity)
        search_edit.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                KeyboardHelper.hideSoftInput(baseContext) //隐藏软键盘

                if (search_edit.text.isBlank()) showToast("请输入关键字")
                else {
                    empty_view.gone()
                    if (list.isNotEmpty()) {
                        list.clear()
                        mAdapter.notifyDataSetChanged()
                    }

                    query = PoiSearch.Query(
                            search_edit.text.toString(),
                            "",
                            intent.getStringExtra("city"))
                    poiSearch.query = query

                    swipe_refresh.isRefreshing = true
                    pageNum = 1
                    getData(pageNum)
                }
            }
            return@setOnEditorActionListener false
        }

        search_cancel.onClick {
            if (search_edit.text.isNotBlank()) search_edit.setText("")
            else ActivityStack.screenManager.popActivities(this@SearchActivity::class.java)
        }
    }

    override fun getData(pindex: Int) {
        query.pageSize = 20
        query.pageNum = pindex
        poiSearch.searchPOIAsyn()
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (s.isEmpty() && list.isNotEmpty()) {
            list.clear()
            mAdapter.notifyDataSetChanged()
        }
    }
}
