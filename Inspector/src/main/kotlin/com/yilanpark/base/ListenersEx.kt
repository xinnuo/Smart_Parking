/**
 * created by 小卷毛, 2018/11/16
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
package com.yilanpark.base

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.view.animation.Animation

fun Animation.setAnimationListener(init: __AnimationListener.() -> Unit) {
    val listener = __AnimationListener()
    listener.init()
    setAnimationListener(listener)
}

fun Application.registerActivityLifecycleListener(init: __ActivityLifecycleCallbacks.() -> Unit) {
    val listener = __ActivityLifecycleCallbacks()
    listener.init()
    registerActivityLifecycleCallbacks(listener)
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

class __ActivityLifecycleCallbacks : Application.ActivityLifecycleCallbacks {

    private var _onActivityPaused: ((Activity?) -> Unit)? = null

    override fun onActivityPaused(activity: Activity?) {
        _onActivityPaused?.invoke(activity)
    }

    fun onActivityPaused(listener: (Activity?) -> Unit) {
        _onActivityPaused = listener
    }

    private var _onActivityResumed: ((Activity?) -> Unit)? = null

    override fun onActivityResumed(activity: Activity?) {
        _onActivityResumed?.invoke(activity)
    }

    fun onActivityResumed(listener: (Activity?) -> Unit) {
        _onActivityResumed = listener
    }

    private var _onActivityStarted: ((Activity?) -> Unit)? = null

    override fun onActivityStarted(activity: Activity?) {
        _onActivityStarted?.invoke(activity)
    }

    fun onActivityStarted(listener: (Activity?) -> Unit) {
        _onActivityStarted = listener
    }

    private var _onActivityDestroyed: ((Activity?) -> Unit)? = null

    override fun onActivityDestroyed(activity: Activity?) {
        _onActivityDestroyed?.invoke(activity)
    }

    fun onActivityDestroyed(listener: (Activity?) -> Unit) {
        _onActivityDestroyed = listener
    }

    private var _onActivitySaveInstanceState: ((Activity?) -> Unit)? = null

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
        _onActivitySaveInstanceState?.invoke(activity)
    }

    fun onActivitySaveInstanceState(listener: (Activity?) -> Unit) {
        _onActivitySaveInstanceState = listener
    }

    private var _onActivityStopped: ((Activity?) -> Unit)? = null

    override fun onActivityStopped(activity: Activity?) {
        _onActivityStopped?.invoke(activity)
    }

    fun onActivityStopped(listener: (Activity?) -> Unit) {
        _onActivityStopped = listener
    }

    private var _onActivityCreated: ((Activity?) -> Unit)? = null

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        _onActivityCreated?.invoke(activity)
    }

    fun onActivityCreated(listener: (Activity?) -> Unit) {
        _onActivityCreated = listener
    }

}
