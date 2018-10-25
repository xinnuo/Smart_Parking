package com.ruanmeng.smart_parking

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.GravityCompat
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.CompoundButton
import cn.jpush.android.api.JPushInterface
import com.lzg.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.fragment.ParkFragment
import com.ruanmeng.fragment.CareFragment
import com.ruanmeng.fragment.MallFragment
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.share.Const
import com.ruanmeng.utils.Tools
import com.ruanmeng.utils.phoneReplaceWithStar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.sdk25.listeners.onTouch
import org.jetbrains.anko.startActivity
import org.json.JSONObject

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setToolbarVisibility(false)
        init_title()

        if (!getBoolean("isTS")) {
            JPushInterface.resumePush(applicationContext)
            //设置别名（先初始化）
            JPushInterface.setAlias(
                    applicationContext,
                    Const.JPUSH_SEQUENCE,
                    getString("token"))
        }

        main_check3.performClick()
    }

    override fun onStart() {
        super.onStart()
        getInfoData()
    }

    override fun init_title() {
        nav_view.setNavigationItemSelectedListener(this)

        main_check1.setOnCheckedChangeListener(this)
        main_check2.setOnCheckedChangeListener(this)
        main_check3.setOnCheckedChangeListener(this)

        main_check4.onTouch { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                drawer_layout.openDrawer(GravityCompat.START)
                return@onTouch false
            }
            return@onTouch false
        }
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.nav_img -> {
                drawer_layout.closeDrawer(GravityCompat.START)
                v.postDelayed({ startActivity<InfoActivity>() }, 300)
            }
            R.id.park_add -> startActivity<CarAddActivity>()
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        // instantiateItem从FragmentManager中查找Fragment，找不到就getItem新建一个，
        // setPrimaryItem设置隐藏和显示，最后finishUpdate提交事务。
        if (isChecked) {
            val fragment = mFragmentPagerAdapter
                    .instantiateItem(main_container, buttonView.id) as Fragment
            mFragmentPagerAdapter.setPrimaryItem(main_container, 0, fragment)
            mFragmentPagerAdapter.finishUpdate(main_container)
        }
    }

    private val mFragmentPagerAdapter = object : FragmentPagerAdapter(supportFragmentManager) {

        override fun getItem(position: Int): Fragment = when (position) {
            R.id.main_check1 -> MallFragment()
            R.id.main_check2 -> CareFragment()
            R.id.main_check3 -> ParkFragment()
            else -> ParkFragment()
        }

        override fun getCount(): Int = 3
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        window.decorView.postDelayed({
            when (item.itemId) {
                R.id.nav_bill -> startActivity<BillActivity>()
                R.id.nav_car -> startActivity<CarActivity>()
                R.id.nav_wallet -> startActivity<WalletActivity>()
                R.id.nav_contact -> getData()
                R.id.nav_feedback -> startActivity<HelpActivity>()
                R.id.nav_setting -> startActivity<SettingActivity>()
            }
        }, 300)

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun getData() {
        OkGo.post<String>(BaseHttp.help_center)
                .tag(this@MainActivity)
                .params("htmlKey", "lxwm")
                .execute(object : StringDialogCallback(baseContext) {

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        val mTel = JSONObject(response.body()).optString("help")
                        alert {
                            title = "联系我们"
                            message = "联系电话：$mTel"
                            negativeButton("取消") {}
                            positiveButton("拨打") {
                                if (mTel.isNotEmpty()) makeCall(mTel)
                            }
                        }.show()
                    }

                })
    }

    private fun getInfoData() {
        OkGo.post<String>(BaseHttp.user_msg_data)
                .tag(this@MainActivity)
                .headers("token", getString("token"))
                .execute(object : StringDialogCallback(baseContext, false) {

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        val obj = JSONObject(response.body()).optJSONObject("userMsg") ?: JSONObject()
                        putString("nickName", Tools.decodeUnicode(obj.optString("nickName")))
                        putString("userHead", obj.optString("userhead"))
                        putString("sex", obj.optString("sex", "1"))

                        nav_title.text = getString("nickName")
                        nav_desc.text = getString("mobile").phoneReplaceWithStar()

                        if (nav_img.getTag(R.id.nav_img) == null) {
                            nav_img.loadImage(BaseHttp.baseImg + getString("userHead"))
                            nav_img.setTag(R.id.nav_img, getString("userHead"))
                        } else {
                            if (nav_img.getTag(R.id.nav_img) != getString("userHead")) {
                                nav_img.loadImage(BaseHttp.baseImg + getString("userHead"))
                                nav_img.setTag(R.id.nav_img, getString("userHead"))
                            }
                        }
                    }

                })
    }

    private var exitTime: Long = 0
    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            if (System.currentTimeMillis() - exitTime > 2000) {
                showToast("再按一次退出程序")
                exitTime = System.currentTimeMillis()
            } else super.onBackPressed()
        }
    }
}
