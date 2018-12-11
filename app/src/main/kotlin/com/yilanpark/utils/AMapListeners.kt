/**
 * created by 小卷毛, 2018/10/22
 * Copyright (c) 2018, 416143467@qq.com All Rights Reserved.
 * #                   *********                            #
 * #                  ************                          #
 * #                  *************                         #
 * #                 **  ***********                        #
 * #                ***  ****** *****                       #
 * #                *** *******   ****                      #
 * #               ***  ********** ****                     #
 * #              ****  *********** ****                    #
 * #            *****   ***********  *****                  #
 * #           ******   *** ********   *****                #
 * #           *****   ***   ********   ******              #
 * #          ******   ***  ***********   ******            #
 * #         ******   **** **************  ******           #
 * #        *******  ********************* *******          #
 * #        *******  ******************************         #
 * #       *******  ****** ***************** *******        #
 * #       *******  ****** ****** *********   ******        #
 * #       *******    **  ******   ******     ******        #
 * #       *******        ******    *****     *****         #
 * #        ******        *****     *****     ****          #
 * #         *****        ****      *****     ***           #
 * #          *****       ***        ***      *             #
 * #            **       ****        ****                   #
 */
package com.yilanpark.utils

import com.amap.api.maps.AMap
import com.amap.api.maps.model.CameraPosition
import com.amap.api.services.core.PoiItem
import com.amap.api.services.poisearch.PoiResult
import com.amap.api.services.poisearch.PoiSearch

fun AMap.setOnCameraChangeListener(init: __OnCameraChangeListener.() -> Unit) {
    val listener = __OnCameraChangeListener()
    listener.init()
    setOnCameraChangeListener(listener)
}

fun PoiSearch.setOnPoiSearchListener(init: __OnPoiSearchListener.() -> Unit) {
    val listener = __OnPoiSearchListener()
    listener.init()
    setOnPoiSearchListener(listener)
}

class __OnCameraChangeListener : AMap.OnCameraChangeListener {

    private var _onCameraChangeFinish: ((CameraPosition?) -> Unit)? = null

    override fun onCameraChangeFinish(position: CameraPosition?) {
        _onCameraChangeFinish?.invoke(position)
    }

    fun onCameraChangeFinish(listener: (CameraPosition?) -> Unit) {
        _onCameraChangeFinish = listener
    }

    private var _onCameraChange: ((CameraPosition?) -> Unit)? = null

    override fun onCameraChange(position: CameraPosition?) {
        _onCameraChange?.invoke(position)
    }

    fun onCameraChange(listener: (CameraPosition?) -> Unit) {
        _onCameraChange = listener
    }

}

class __OnPoiSearchListener : PoiSearch.OnPoiSearchListener {

    private var _onPoiItemSearched: ((PoiItem?, Int) -> Unit)? = null

    override fun onPoiItemSearched(poiItem: PoiItem?, code: Int) {
        _onPoiItemSearched?.invoke(poiItem, code)
    }

    fun onPoiItemSearched(listener: (PoiItem?, Int) -> Unit) {
        _onPoiItemSearched = listener
    }

    private var _onPoiSearched: ((PoiResult?, Int) -> Unit)? = null

    override fun onPoiSearched(result: PoiResult?, code: Int) {
        _onPoiSearched?.invoke(result, code)
    }

    fun onPoiSearched(listener: (PoiResult?, Int) -> Unit) {
        _onPoiSearched = listener
    }

}