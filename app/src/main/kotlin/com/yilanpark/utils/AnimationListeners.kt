/**
 * created by 小卷毛, 2018/10/23
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

import android.view.animation.Animation

fun Animation.setAnimationListener(init: __AnimationListener.() -> Unit) {
    val listener = __AnimationListener()
    listener.init()
    setAnimationListener(listener)
}

class __AnimationListener : Animation.AnimationListener {

    private var _onAnimationRepeat: ((Animation?) -> Unit)? = null

    override fun onAnimationRepeat(animation: Animation?) {
        _onAnimationRepeat?.invoke(animation)
    }

    fun onAnimationRepeat(listener: (Animation?) -> Unit) {
        _onAnimationRepeat = listener
    }

    private var _onAnimationEnd: ((Animation?) -> Unit)? = null

    override fun onAnimationEnd(animation: Animation?) {
        _onAnimationEnd?.invoke(animation)
    }

    fun onAnimationEnd(listener: (Animation?) -> Unit) {
        _onAnimationEnd = listener
    }

    private var _onAnimationStart: ((Animation?) -> Unit)? = null

    override fun onAnimationStart(animation: Animation?) {
        _onAnimationStart?.invoke(animation)
    }

    fun onAnimationStart(listener: (Animation?) -> Unit) {
        _onAnimationStart = listener
    }

}