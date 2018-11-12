package com.ruanmeng.smart_parking

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import com.jude.rollviewpager.RollPagerView
import com.lzg.extend.JsonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.cache.CacheMode
import com.lzy.okgo.model.Response
import com.ruanmeng.adapter.GuideAdapter
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonModel
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.CountDownTimer
import com.ruanmeng.view.rollPagerView
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.PermissionListener
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.listeners.onClick

/**
 * 不继承BaseActivity，解决打开显示空白的问题
 */
class GuideActivity : AppCompatActivity() {

    private var isReady: Boolean = false
    private lateinit var mBanner: RollPagerView
    private lateinit var tvGuide: TextView
    private val timer: CountDownTimer by lazy {
        object : CountDownTimer(4000, 1000) {

            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                tvGuide.text = "跳过 ${Math.ceil(millisUntilFinished * 1.0 / 1000).toInt()}S"
            }

            override fun onFinish() = quitGuide()
        }
    }

    @SuppressLint("HandlerLeak")
    private var handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (isReady) getGuideData()
            else isReady = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //隐藏状态栏（全屏）
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        frameLayout {
            imageView {
                backgroundResource = R.drawable.guide
                scaleType = ImageView.ScaleType.FIT_XY
            }.lparams(width = matchParent, height = matchParent)

            mBanner = rollPagerView().lparams(width = matchParent, height = matchParent)

            tvGuide = textView("跳过") {
                textSize = 15f
                textColorResource = R.color.white
                gravity = Gravity.CENTER
                backgroundResource = R.drawable.rec_bg_ova_trans
                visibility = View.INVISIBLE
                horizontalPadding = dip(15)
                verticalPadding = dip(5)

                onClick {
                    timer.cancel()
                    quitGuide()
                }
            }.lparams {
                gravity = Gravity.END
                topMargin = dip(25)
                rightMargin = dip(10)
            }
        }

        ActivityStack.screenManager.pushActivity(this@GuideActivity)

        window.decorView.postDelayed({ handler.sendEmptyMessage(0) }, 2000)

        AndPermission.with(this@GuideActivity)
                .permission(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE)
                .callback(object : PermissionListener {
                    override fun onSucceed(requestCode: Int, grantPermissions: MutableList<String>) {
                        handler.sendEmptyMessage(0)
                    }

                    override fun onFailed(requestCode: Int, deniedPermissions: MutableList<String>) {
                        showToast("请求权限被拒绝")
                        onBackPressed()
                    }
                }).start()
    }

    private fun getGuideData() {
        OkGo.post<CommonModel>(BaseHttp.guide_info)
                .tag(this@GuideActivity)
                .cacheMode(CacheMode.REQUEST_FAILED_READ_CACHE)
                .cacheKey(BaseHttp.guide_info)
                .execute(object : JsonDialogCallback<CommonModel>(this@GuideActivity) {

                    override fun onSuccess(response: Response<CommonModel>) {
                        val flag = getString("guideFlag")
                        val flagGet = response.body().changge
                        val msgCode = response.body().msgcode

                        if (msgCode == "100") {
                            if (flag == flagGet) quitGuide()
                            else {
                                putString("guideFlag", flagGet)

                                val list = ArrayList<String>()
                                list.addItems(response.body().imgs)
                                startGuide(list)
                            }
                        } else quitGuide()
                    }

                    override fun onError(response: Response<CommonModel>) {
                        super.onError(response)
                        quitGuide()
                    }

                })
    }

    private fun startGuide(imgs: ArrayList<String>) {
        val mLoopAdapter = GuideAdapter(this@GuideActivity)
        mBanner.apply {
            setAdapter(mLoopAdapter)
            setHintViewVisibility(false)
            setOnItemSelectListener { position ->

                timer.cancel()
                if (position == imgs.size - 1) timer.start()
                else tvGuide.text = "跳过"
            }
        }
        mLoopAdapter.setImgs(imgs)
        tvGuide.visibility = View.VISIBLE
        if (imgs.size == 1) timer.start()
    }

    private fun quitGuide() {
        if (getBoolean("isLogin")) startActivity<MainActivity>()
        else startActivity<LoginActivity>()
        ActivityStack.screenManager.popActivities(this@GuideActivity::class.java)
    }

    override fun onDestroy() {
        OkGo.getInstance().cancelTag(this@GuideActivity)
        super.onDestroy()
    }
}
