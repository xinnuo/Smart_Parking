/**
 * created by 小卷毛, 2018/11/12
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
package com.ruanmeng.view

import android.app.Activity
import android.content.Context
import android.view.ViewManager
import com.jude.rollviewpager.RollPagerView
import org.jetbrains.anko.custom.ankoView

/**
 * 项目名称：Smart_Parking
 * 创建人：小卷毛
 * 创建时间：2018-11-12 08:57
 */
inline fun ViewManager.rollPagerView(theme: Int = 0) = rollPagerView(theme) {}
inline fun ViewManager.rollPagerView(theme: Int = 0, init: RollPagerView.() -> Unit) = ankoView({ RollPagerView(it) }, theme, init)

inline fun Context.rollPagerView(theme: Int = 0) = rollPagerView(theme) {}
inline fun Context.rollPagerView(theme: Int = 0, init: RollPagerView.() -> Unit) = ankoView({ RollPagerView(it) }, theme, init)

inline fun Activity.rollPagerView(theme: Int = 0) = rollPagerView(theme) {}
inline fun Activity.rollPagerView(theme: Int = 0, init: RollPagerView.() -> Unit) = ankoView({ RollPagerView(it) }, theme, init)