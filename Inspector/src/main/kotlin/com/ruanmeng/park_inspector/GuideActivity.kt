package com.ruanmeng.park_inspector

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import android.widget.ImageView
import com.ruanmeng.base.getBoolean
import com.ruanmeng.base.showToast
import com.ruanmeng.utils.ActivityStack
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.PermissionListener
import org.jetbrains.anko.*

/**
 * 不继承BaseActivity，解决打开显示空白的问题
 */
class GuideActivity : AppCompatActivity() {

    private var isReady: Boolean = false

    @SuppressLint("HandlerLeak")
    private var handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (isReady) quitGuide()
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
                        Manifest.permission.READ_PHONE_STATE
                )
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

    private fun quitGuide() {
        if (getBoolean("isLogin")) startActivity<MainActivity>()
        else startActivity<LoginActivity>()
        ActivityStack.screenManager.popActivities(this@GuideActivity::class.java)
    }
}
