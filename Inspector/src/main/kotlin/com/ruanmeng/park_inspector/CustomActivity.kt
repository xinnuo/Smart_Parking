package com.ruanmeng.park_inspector

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import com.lzg.extend.BaseResponse
import com.lzg.extend.StringDialogCallback
import com.lzg.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.DialogHelper
import kotlinx.android.synthetic.main.activity_custom.*
import net.idik.lib.slimadapter.SlimAdapter
import net.idik.lib.slimadapter.viewinjector.DefaultViewInjector

class CustomActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    @SuppressLint("HandlerLeak")
    private var handler = object : Handler() {
        @Suppress("DEPRECATION")
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                1 -> getData()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom)
        init_title("自定义车位分组")

        getData()
    }

    override fun init_title() {
        super.init_title()
        custom_list.load_Linear(baseContext)

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_custom_list) { data, injector ->
                    injector.text(R.id.item_custom_title, data.groupName)

                    (injector as DefaultViewInjector).oneClicked(R.id.item_custom_edit) {
                        DialogHelper.showAddDialog(
                                baseContext,
                                "修改组名称",
                                data.groupName,
                                "请输入组名称") { name ->
                            if (name == data.groupName) return@showAddDialog

                            getEditData(data.userGroupId, name)
                        }
                    }.oneClicked(R.id.item_custom_del) {
                        DialogHelper.showDelDialog(baseContext) { _ ->
                            getDelData(data.userGroupId, list.indexOf(data))
                        }
                    }
                }
                .attachTo(custom_list)

        bt_new.setOneClickListener { _ ->
            DialogHelper.showAddDialog(
                    baseContext,
                    "新建组名称",
                    "",
                    "请输入新建组名称") {
                getAddData(it)
            }
        }
    }

    override fun getData() {
        OkGo.post<BaseResponse<ArrayList<CommonData>>>(BaseHttp.find_usergroup_data)
                .tag(this@CustomActivity)
                .headers("token", getString("token"))
                .execute(object : JacksonDialogCallback<BaseResponse<ArrayList<CommonData>>>(baseContext, true) {

                    override fun onSuccess(response: Response<BaseResponse<ArrayList<CommonData>>>) {

                        list.apply {
                            clear()
                            addItems(response.body().`object`)
                        }

                        mAdapter.updateData(list)
                    }

                })
    }

    private fun getAddData(name: String) {
        OkGo.post<String>(BaseHttp.add_usergroup)
                .tag(this@CustomActivity)
                .isMultipart(true)
                .headers("token", getString("token"))
                .params("groupName", name)
                .execute(object : StringDialogCallback(baseContext) {

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        showToast(msg)
                        handler.sendEmptyMessage(1)
                    }

                })
    }

    private fun getEditData(groupId: String, name: String) {
        OkGo.post<String>(BaseHttp.update_usergroup)
                .tag(this@CustomActivity)
                .isMultipart(true)
                .headers("token", getString("token"))
                .params("userGroupId", groupId)
                .params("groupName", name)
                .execute(object : StringDialogCallback(baseContext) {

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        showToast(msg)
                        handler.sendEmptyMessage(1)
                    }

                })
    }

    private fun getDelData(groupId: String, position: Int) {
        OkGo.post<String>(BaseHttp.delete_usergroup)
                .tag(this@CustomActivity)
                .headers("token", getString("token"))
                .params("userGroupId", groupId)
                .execute(object : StringDialogCallback(baseContext) {

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        showToast(msg)
                        list.removeAt(position)
                        mAdapter.notifyItemRemoved(position)
                    }

                })
    }

}
