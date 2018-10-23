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
 * 项目名称：Smart_Parking
 * 创建人：小卷毛
 * 创建时间：2018-10-19 15:23
 */
data class CommonData(
        //车牌列表
        var carNo: String = "",
        var carStatus: String = "",
        var carType: String = "",
        var mycarId: String = "",

        //车牌列表
        var parkName: String = "",
        var paddress: String = "",
        var pprovince: String = "",
        var pcity: String = "",
        var pdistrict: String = "",
        var ptownship: String = "",
        var plat: String = "",
        var plng: String = "",
        var ptype: String = "",
        var publicPark: String = "",
        var publicParkingId: String = "",
        var pcost: String = "",
        var totleSum: String = "",
        var vacancySum: String = "",
        var chargeExplain: String = "",
        var parkTelephone: String = "",

        //账单列表
        var goodsOrderId: String = "",
        var status: String = "",
        var startDate: String = "",
        var endDate: String = "",
        var paySum: String = "",
        var LateFee: String = "",
        var daddress: String = ""
) : Serializable