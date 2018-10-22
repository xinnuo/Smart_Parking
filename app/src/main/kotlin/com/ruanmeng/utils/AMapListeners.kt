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
package com.ruanmeng.utils

import com.amap.api.maps.AMap
import com.amap.api.maps.model.CameraPosition

fun AMap.setOnCameraChangeListener(init: __OnCameraChangeListener.() -> Unit) {
    val listener = __OnCameraChangeListener()
    listener.init()
    setOnCameraChangeListener(listener)
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