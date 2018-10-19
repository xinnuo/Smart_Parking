package com.ruanmeng.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.MyLocationStyle
import com.ruanmeng.base.BaseFragment
import com.ruanmeng.smart_parking.R
import kotlinx.android.synthetic.main.fragment_park.*

class ParkFragment : BaseFragment() {

    private lateinit var aMap: AMap

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
                isRotateGesturesEnabled = true    //旋转手势
                setLogoBottomMargin(-50)          //隐藏logo
            }

            isTrafficEnabled = true        //实时交通状况
            mapType = AMap.MAP_TYPE_NORMAL //矢量地图模式
            isMyLocationEnabled = true     //触发定位并显示定位层
            showIndoorMap(true)            //设置是否显示室内地图
            moveCamera(CameraUpdateFactory.zoomTo(16f)) //缩放级别

            myLocationStyle = MyLocationStyle().apply {
                //定位一次，且将视角移动到地图中心点
                myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE)
                interval(5000)                     //设置连续定位模式下的定位间隔
                strokeColor(Color.TRANSPARENT)     //设置定位蓝点精度圆圈的边框颜色
                radiusFillColor(Color.TRANSPARENT) //设置定位蓝点精度圆圈的填充颜色
                showMyLocation(true)               //设置是否显示定位小蓝点
                anchor(0.5f, 0.9f)                 //设置定位蓝点图标的锚点
                //设置定位蓝点的icon图标方法
                myLocationIcon(BitmapDescriptorFactory.fromResource(R.mipmap.index_icon03))
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
        park_map?.onDestroy()
    }
}
