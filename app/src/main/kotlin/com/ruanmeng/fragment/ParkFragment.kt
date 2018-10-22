package com.ruanmeng.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.*
import com.amap.api.maps.model.animation.ScaleAnimation
import com.lzg.extend.BaseResponse
import com.lzg.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseFragment
import com.ruanmeng.base.addItems
import com.ruanmeng.base.gone
import com.ruanmeng.base.visible
import com.ruanmeng.model.CommonData
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.smart_parking.R
import com.ruanmeng.utils.setOnCameraChangeListener
import kotlinx.android.synthetic.main.fragment_park.*
import org.jetbrains.anko.sdk25.listeners.onClick

class ParkFragment : BaseFragment() {

    private lateinit var aMap: AMap
    private var locationLatLng: LatLng? = null

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
             * 地图状态发生变化的监听
             */
            setOnCameraChangeListener {
                onCameraChange {
                    park_card.gone()
                }
            }

            /**
             * 用户定位信息监听接口
             */
            setOnMyLocationChangeListener {
                if (locationLatLng == null) {
                    locationLatLng = LatLng(it.latitude, it.longitude)
                    aMap.animateCamera(CameraUpdateFactory.changeLatLng(locationLatLng))
                } else locationLatLng = LatLng(it.latitude, it.longitude)
            }

            /**
             * marker的信息窗口点击事件监听
             */
            setOnMarkerClickListener {
                park_card.visible()
                return@setOnMarkerClickListener true
            }

            park_location.onClick {
                if (locationLatLng != null) {
                    aMap.animateCamera(CameraUpdateFactory.changeLatLng(locationLatLng))
                    getAroundData(locationLatLng!!)
                }
            }
        }
    }

    private fun getAroundData(latLng: LatLng) {
        OkGo.post<BaseResponse<ArrayList<CommonData>>>(BaseHttp.index_data)
                .tag(this@ParkFragment)
                .params("lat", latLng.latitude)
                .params("lng", latLng.longitude)
                .execute(object : JacksonDialogCallback<BaseResponse<ArrayList<CommonData>>>(activity) {

                    override fun onSuccess(response: Response<BaseResponse<ArrayList<CommonData>>>) {

                        val list = ArrayList<CommonData>()
                        list.addItems(response.body().`object`)

                        aMap.clear(true)
                        list.forEach {
                            //绘制marker
                            val markerOption = MarkerOptions()
                                    .position(LatLng(it.plat.toDouble(), it.plng.toDouble()))
                                    .icon(BitmapDescriptorFactory.fromResource(when (it.ptype) {
                                        "1" -> R.mipmap.index_icon01
                                        else -> R.mipmap.index_icon02
                                    }))
                                    .anchor(0.5f, 0.7f)  //设置Marker的锚点
                                    .draggable(false)    //设置Marker是否可拖动
                            markerOption.isFlat = true   //设置marker平贴地图效果
                            startGrowAnimation(aMap.addMarker(markerOption))
                        }
                    }

                })
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
        park_map?.onDestroy()
    }
}
