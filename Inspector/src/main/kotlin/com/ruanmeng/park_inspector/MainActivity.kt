package com.ruanmeng.park_inspector

import android.os.Bundle
import cn.jpush.android.api.JPushInterface
import com.lzg.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.share.Const
import com.ruanmeng.utils.DialogHelper
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity
import android.content.Intent
import com.amap.api.AMapLocationHelper
import com.amap.api.services.core.AMapException
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.geocoder.GeocodeResult
import com.amap.api.services.geocoder.GeocodeSearch
import com.amap.api.services.geocoder.RegeocodeQuery
import com.amap.api.services.geocoder.RegeocodeResult
import com.lzy.okgo.utils.OkLogger
import com.ruanmeng.receiver.LocationForegoundService
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.startService
import org.jetbrains.anko.stopService
import java.util.concurrent.TimeUnit

class MainActivity : BaseActivity() {

    private lateinit var geocoderSearch: GeocodeSearch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init_title("首页", "设置")

        if (!getBoolean("isTS")) {
            JPushInterface.resumePush(applicationContext)
            //设置别名（先初始化）
            JPushInterface.setAlias(
                    applicationContext,
                    Const.JPUSH_SEQUENCE,
                    getString("token"))
        }

        startService<LocationForegoundService>()
        AMapLocationHelper.getInstance(baseContext)
                .setDuration(900000)
                .startLocation(111) { location, isSuccessed, _ ->

                    if (isSuccessed) {
                        Flowable.just(location)
                                .throttleWithTimeout(5000, TimeUnit.MILLISECONDS)
                                .observeOn(Schedulers.newThread())
                                .subscribe {
                                    geocoderSearch.getFromLocationAsyn(RegeocodeQuery(
                                            LatLonPoint(location.latitude, location.longitude),
                                            100f,
                                            GeocodeSearch.AMAP))
                                }
                    }
                }
    }

    override fun init_title() {
        super.init_title()
        ivBack.gone()

        tvRight.setOneClickListener { startActivity<SettingActivity>() }
        main_item1.setOneClickListener { startActivity<ScanActivity>() }
        main_item2.setOneClickListener { startActivity<StatusActivity>() }
        main_item3.setOneClickListener { startActivity<CustomActivity>() }
        main_item4.setOneClickListener { startActivity<PreviewActivity>() }
        main_item5.setOneClickListener { startActivity<WrongActivity>() }
        main_item6.setOneClickListener { getData(0) }
        main_item7.setOneClickListener { getData(1) }

        geocoderSearch = GeocodeSearch(baseContext)
        geocoderSearch.setOnGeocodeSearchListener(object : GeocodeSearch.OnGeocodeSearchListener {

            override fun onRegeocodeSearched(result: RegeocodeResult?, code: Int) {
                if (code == AMapException.CODE_AMAP_SUCCESS) {
                    if (result?.regeocodeAddress != null
                            && result.regeocodeAddress.formatAddress != null) {

                        val point = result.regeocodeQuery.point
                        val district = result.regeocodeAddress.district
                        val township = result.regeocodeAddress.township

                        getLocationData(
                                point.longitude.toString(),
                                point.latitude.toString(),
                                district,
                                township)
                    }
                }
            }

            override fun onGeocodeSearched(result: GeocodeResult, code: Int) {}

        })
    }

    private fun getLocationData(lng: String,
                                lat: String,
                                district: String,
                                township: String) {

        OkGo.post<String>(BaseHttp.add_uposition)
                .tag(this@MainActivity)
                .isMultipart(true)
                .headers("token", getString("token"))
                .params("lng", lng)
                .params("lat", lat)
                .params("district", district)
                .params("township", township)
                .execute(object : StringDialogCallback(baseContext, false) {

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                        OkLogger.i(msg)
                    }

                    override fun onSuccessResponseErrorCode(response: Response<String>?, msg: String?, msgCode: String?) {
                        OkLogger.i(msg)
                    }

                })
    }

    override fun getData(pindex: Int) {
        OkGo.post<String>(BaseHttp.add_punchClock)
                .tag(this@MainActivity)
                .headers("token", getString("token"))
                .params("type", pindex)
                .execute(object : StringDialogCallback(baseContext) {

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                        DialogHelper.showHintDialog(baseContext)
                    }

                    override fun onSuccessResponseErrorCode(response: Response<String>, msg: String, msgCode: String) {
                        if (msgCode == "102") showToast(msg)
                        else startActivity<ClockActivity>()
                    }
                })
    }

    override fun onDestroy() {
        super.onDestroy()

        AMapLocationHelper.getInstance(baseContext).stopLocation()
        stopService<LocationForegoundService>()
    }

    override fun onBackPressed() {
        // 实现Home键效果
        startActivity(Intent(Intent.ACTION_MAIN).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            addCategory(Intent.CATEGORY_HOME)
        })
    }
}
