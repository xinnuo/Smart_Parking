/**
 * created by 小卷毛, 2018/10/30
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

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Environment
import android.util.DisplayMetrics
import android.view.WindowManager
import android.widget.ImageView

/**
 * 检查是否存在SDCard
 */
fun hasSdcard() = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED

/**
 * 以数据流的方式将Resources下的图片显示，
 * 防止内存溢出
 */
@Suppress("DEPRECATION")
fun Context.getImgFromSD(iv: ImageView, resID: Int) = kotlin.run {
    iv.background = BitmapDrawable(
            resources,
            BitmapFactory.decodeStream(
                    resources.openRawResource(resID),
                    null,
                    BitmapFactory.Options().apply {
                        inPreferredConfig = Bitmap.Config.RGB_565
                        inPurgeable = true
                        inInputShareable = true
                    }))
}

/**
 * 屏幕宽度，单位：px
 */
fun Context.getScreenWidth() = kotlin.run {
    val manager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = manager.defaultDisplay
    val outMetrics = DisplayMetrics()
    display.getMetrics(outMetrics)
    outMetrics.widthPixels
}

/**
 * 屏幕高度，单位：px
 */
fun Context.getScreenHeight() = kotlin.run {
    val manager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = manager.defaultDisplay
    val outMetrics = DisplayMetrics()
    display.getMetrics(outMetrics)
    outMetrics.heightPixels
}
