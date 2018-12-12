package com.ruanmeng.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.TranslateAnimation
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.*
import com.amap.api.maps.model.animation.ScaleAnimation
import com.lzg.extend.BaseResponse
import com.lzg.extend.StringDialogCallback
import com.lzg.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseFragment
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.LocationMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.smart_parking.R
import com.ruanmeng.smart_parking.SearchActivity
import com.ruanmeng.utils.*
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_park.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.jetbrains.anko.sdk25.listeners.onClick
import org.jetbrains.anko.support.v4.browse
import org.jetbrains.anko.support.v4.startActivity
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class ParkFragment : BaseFragment() {

    private lateinit var aMap: AMap
    private var locationLatLng: LatLng? = null
    private var isAnimating = false
    private var mLastZoom = 16f

    private val list = ArrayList<CommonData>()
    private var nowCity = "" //当前市
    private var mPoiName = ""
    private var mLat = ""
    private var mLng = ""

    //调用这个方法切换时不会释放掉Fragment
    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        this.view?.visibility = if (menuVisible) View.VISIBLE else View.GONE
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_park, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        park_map.onCreate(savedInstanceState)
        init_title()

        EventBus.getDefault().register(this@ParkFragment)
    }

    override fun init_title() {
        aMap = park_map.map

        aMap.apply {
            uiSettings.apply {
                isScaleControlsEnabled = false    //比例尺
                isZoomControlsEnabled = false     //缩放按钮
                isCompassEnabled = false          //指南针
                isMyLocationButtonEnabled = false //定位按钮
                isTiltGesturesEnabled = true      //倾斜手势
                isRotateGesturesEnabled = false   //旋转手势
                setLogoBottomMargin(-50)          //隐藏logo
            }

            isTrafficEnabled = true        //实时交通状况
            mapType = AMap.MAP_TYPE_NORMAL //矢量地图模式
            isMyLocationEnabled = true     //触发定位并显示定位层
            showIndoorMap(true)            //设置是否显示室内地图
            moveCamera(CameraUpdateFactory.zoomTo(16f)) //缩放级别

            myLocationStyle = MyLocationStyle().apply {
                //连续定位、蓝点不会移动到地图中心点，并且蓝点会跟随设备移动
                myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER)
                interval(5000)                     //设置连续定位模式下的定位间隔
                strokeColor(Color.TRANSPARENT)     //设置定位蓝点精度圆圈的边框颜色
                radiusFillColor(Color.TRANSPARENT) //设置定位蓝点精度圆圈的填充颜色
                showMyLocation(true)               //设置是否显示定位小蓝点
                anchor(0.5f, 0.9f)                 //设置定位蓝点图标的锚点
                //设置定位蓝点的icon图标方法
                myLocationIcon(BitmapDescriptorFactory.fromResource(R.mipmap.index_icon03))
            }

            /**
             * 用户定位信息监听接口
             */
            setOnMyLocationChangeListener { location ->
                val bundle = location.extras
                @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
                nowCity = bundle.getString("City")

                if (locationLatLng == null) {
                    locationLatLng = LatLng(location.latitude, location.longitude)
                    aMap.animateCamera(CameraUpdateFactory.changeLatLng(locationLatLng))

                    /**
                     * 地图状态发生变化的监听
                     */
                    this.setOnCameraChangeListener {
                        onCameraChange {
                            if (!isAnimating) moveToViewBottom()
                        }

                        onCameraChangeFinish { item ->
                            if (item == null) return@onCameraChangeFinish

                            if (mLastZoom == item.zoom) {
                                Flowable.just(item)
                                        .throttleWithTimeout(500, TimeUnit.MILLISECONDS)
                                        .observeOn(Schedulers.newThread())
                                        .subscribe {
                                            OkGo.getInstance().cancelTag(this@ParkFragment)
                                            getAroundData(it.target)
                                        }
                            } else mLastZoom = item.zoom
                        }
                    }
                } else locationLatLng = LatLng(location.latitude, location.longitude)
            }

            /**
             * marker的信息窗口点击事件监听
             */
            setOnMarkerClickListener {
                if (it.title.isNotEmpty()) {
                    moveToViewLocation()
                    getParkData(it.title)
                }
                return@setOnMarkerClickListener true
            }

            park_location.onClick {
                if (locationLatLng != null) aMap.animateCamera(CameraUpdateFactory.changeLatLng(locationLatLng))
            }

            park_search.onClick { startActivity<SearchActivity>("city" to nowCity) }

            park_nav.onClick {
                if (mLat.isNotEmpty() && mLng.isNotEmpty()) startToNavi()
            }
        }
    }

    private fun getAroundData(latLng: LatLng) {
        OkGo.post<BaseResponse<ArrayList<CommonData>>>(BaseHttp.index_data)
                .tag(this@ParkFragment)
                .params("lat", latLng.latitude)
                .params("lng", latLng.longitude)
                .execute(object : JacksonDialogCallback<BaseResponse<ArrayList<CommonData>>>(activity!!) {

                    override fun onSuccess(response: Response<BaseResponse<ArrayList<CommonData>>>) {

                        list.clear()
                        list.addItems(response.body().`object`)

                        aMap.clear(true)
                        list.forEach {
                            //绘制marker
                            val markerOption = MarkerOptions()
                                    .position(LatLng(it.plat.toDouble(), it.plng.toDouble()))
                                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.index_icon01))
                                    .title(it.publicParkingId)
                                    .anchor(0.5f, 0.7f)  //设置Marker的锚点
                                    .draggable(false)    //设置Marker是否可拖动
                            markerOption.isFlat = true   //设置marker平贴地图效果
                            startGrowAnimation(aMap.addMarker(markerOption))
                        }
                    }

                })
    }

    private fun getParkData(id: String) {
        OkGo.post<String>(BaseHttp.index_price_info)
                .tag(this@ParkFragment)
                .params("publicParkingId", id)
                .execute(object : StringDialogCallback(activity!!, false) {

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        val obj = JSONObject(response.body())
                                .optJSONObject("object") ?: JSONObject()

                        park_hint.text = obj.optString("chargeExplain")
                        park_number.text = obj.optString("vacancySum")
                        park_price.text = obj.optString("pcost")

                        val item = list.firstOrNull { it.publicParkingId == id }
                        mPoiName = item?.parkName ?: ""
                        mLat = item?.plat ?: ""
                        mLng = item?.plng ?: ""
                    }

                })
    }

    private fun startToNavi() {
        when {
            activity!!.isAvilible("com.autonavi.minimap") ->
                activity!!.toAMapRoute(
                        resources.getString(R.string.app_name),
                        mLat,
                        mLng,
                        mPoiName)
            activity!!.isAvilible("com.baidu.BaiduMap") ->
                activity!!.toBaiduDirection(
                        "name:$mPoiName|latlng:$mLat,$mLng",
                        "andr.ruanmeng.smart_parking",
                        "driving",
                        "gcj02")
            activity!!.isAvilible("com.tencent.map") ->
                activity!!.toTenCentRoute(
                        "drive",
                        "$mLat,$mLng",
                        mPoiName)
            else -> {
                //高德地图网页版 https://lbs.amap.com/api/uri-api/guide/travel/route
                browse("https://uri.amap.com/navigation?to=$mLng,$mLat,$mPoiName&mode=car&src=com.ruanmeng.smart_parking&callnative=0")

                //百度地图网页版 http://lbsyun.baidu.com/index.php?title=uri/api/web
                /*browse("http://api.map.baidu.com/direction?origin=name:我的位置|latlng:${locationLatLng!!.latitude},${locationLatLng!!.longitude}" +
                        "&destination=name:$mPoiName|latlng:$mLat,$mLng" +
                        "&region=$nowCity" +
                        "&mode=driving" +
                        "&output=html" +
                        "&src=webapp.ruanmeng.smart_parking")*/
            }
        }
    }

    private fun startGrowAnimation(marker: Marker) {
        val animation = ScaleAnimation(0f, 1f, 0f, 1f)
        animation.setInterpolator(LinearInterpolator())
        //整个移动所需要的时间
        animation.setDuration(600)
        //设置动画
        marker.setAnimation(animation)
        //开始动画
        marker.startAnimation()
    }

    private fun moveToViewLocation() {
        if (!park_card.isVisble()) {
            park_card.visible()
            park_bottom.startAnimation(TranslateAnimation(
                    Animation.RELATIVE_TO_SELF,
                    0.0f,
                    Animation.RELATIVE_TO_SELF,
                    0.0f,
                    Animation.RELATIVE_TO_SELF,
                    0.6f,
                    Animation.RELATIVE_TO_SELF, 0.0f).apply {
                duration = 300
            })
        }
    }

    @SuppressLint("CheckResult")
    private fun moveToViewBottom() {
        if (park_card.isVisble()) {
            isAnimating = true

            park_bottom.startAnimation(TranslateAnimation(
                    Animation.RELATIVE_TO_SELF,
                    0.0f,
                    Animation.RELATIVE_TO_SELF,
                    0.0f,
                    Animation.RELATIVE_TO_SELF,
                    0.0f,
                    Animation.RELATIVE_TO_SELF,
                    0.6f).apply {
                duration = 300
                setAnimationListener {
                    onAnimationEnd {
                        park_card.gone()
                        mPoiName = ""
                        mLat = ""
                        mLng = ""
                        isAnimating = false
                    }
                }
            })
        }
    }

    @Subscribe
    fun onMessageEvent(event: LocationMessageEvent) {
        when (event.type) {
            "周边搜索" -> {
                aMap.animateCamera(CameraUpdateFactory.changeLatLng(LatLng(
                        event.lat.toDouble(),
                        event.lng.toDouble())))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        park_map.onResume()
    }

    override fun onPause() {
        super.onPause()
        park_map.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        park_map.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this@ParkFragment)
        park_map?.onDestroy()
    }
}
