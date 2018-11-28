/**
 * created by 小卷毛, 2018/10/19
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
package com.ruanmeng.model

import java.io.Serializable

/**
 * 项目名称：Park_Inspector
 * 创建人：小卷毛
 * 创建时间：2018-10-24 17:01
 */
data class CommonData(
        //消息列表
        var sendDate: String = "",
        var content: String = "",
        //分组列表
        var userGroupId: String = "",
        var groupName: String = "",
        //打卡记录
        var signinDay: String = "",
        var stype: String = "",
        var createDate: String = "",
        //车位列表
        var paddress: String = "",
        var pprovince: String = "",
        var pcity: String = "",
        var pdistrict: String = "",
        var ptownship: String = "",
        var plat: String = "",
        var plng: String = "",
        var deviceId: String = "",
        var parkingNo: String = "",
        var parkingStatus: String = "",
        var parkingId: String = "",
        var publicParkingId: String = ""
) : Serializable