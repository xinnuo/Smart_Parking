/**
 * created by 小卷毛, 2018/10/27
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

import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * 根据包名判断是否有安装该App
 * 高德：com.autonavi.minimap
 * 百度：com.baidu.BaiduMap
 * 腾讯：com.tencent.map
 * 谷歌：com.google.android.apps.maps
 *
 * @param packageName 包名
 */
fun Context.isAvilible(packageName: String) = kotlin.run {
    val pinfo = packageManager.getInstalledPackages(0)
    return@run pinfo.any { it.packageName.equals(packageName, ignoreCase = true) }
}

/**
 * 启动高德App进行导航 URL接口：androidamap://navi
 * https://lbs.amap.com/api/amap-mobile/guide/android/navigation
 *
 * @param sourceApplication 必填 第三方调用应用名称。如 amap
 * @param lat               必填 纬度
 * @param lon               必填 经度
 * @param dev               必填 是否偏移(0:lat 和 lon 是已经加密后的,不需要国测加密; 1:需要国测加密)
 * @param style             必填 导航方式(0 速度快; 1 费用少; 2 路程短; 3 不走高速；4 躲避拥堵；5 不走高速且避免收费；6 不走高速且躲避拥堵；7 躲避收费和拥堵；8 不走高速躲避收费和拥堵))
 * @param poiname           非必填 POI 名称
 */
fun Context.toAMapNavigation(
        sourceApplication: String,
        lat: String,
        lon: String,
        dev: String = "0",
        style: String = "0",
        poiname: String = "") {
    val uriString = "androidamap://navi?sourceApplication=$sourceApplication" +
            "&lat=$lat" +
            "&lon=$lon" +
            "&dev=$dev" +
            "&style=$style" +
            if (poiname.isEmpty()) "" else "&poiname=$poiname"

    startActivity(Intent().apply {
        action = Intent.ACTION_VIEW
        addCategory(Intent.CATEGORY_DEFAULT)
        data = Uri.parse(uriString)
    })
}

/**
 * 启动高德App进行路线规划 URL接口：androidamap://route/plan
 * http://lbs.amap.com/api/amap-mobile/guide/android/route
 *
 * @param sourceApplication 必填 第三方调用应用名称。如 amap
 * @param dlat              必填 终点纬度
 * @param dlon              必填 终点经度
 * @param dev               必填 起终点是否偏移(0:lat 和 lon 是已经加密后的,不需要国测加密; 1:需要国测加密)
 * @param t                 必填 t = 0（驾车）= 1（公交）= 2（步行）= 3（骑行）= 4（火车）= 5（长途客车）
 * @param dname             非必填 终点名称
 * @param slat              非必填 起点纬度
 * @param slon              非必填 起点经度
 * @param sname             非必填 起点名称
 * @param rideType          非必填 仅当t=3时该参数生效，rideType = elebike（电动车）= bike/为空（自行车）
 * @param sid               非必填 起点的POIID
 * @param did               非必填 终点的POIID
 */
fun Context.toAMapRoute(
        sourceApplication: String,
        dlat: String,
        dlon: String,
        dname: String = "",
        dev: String = "0",
        t: String = "0",
        slat: String = "",
        slon: String = "",
        sname: String = "",
        rideType: String = "",
        sid: String = "",
        did: String = "") {
    val uriString = "androidamap://route/plan?sourceApplication=$sourceApplication" +
            "&dlat=$dlat" +
            "&dlon=$dlon" +
            "&dev=$dev" +
            "&t=$t" +
            if (dname.isEmpty()) "" else "&dname=$dname" +
            if (slat.isEmpty()) "" else "&slat=$slat" +
            if (slon.isEmpty()) "" else "&slon=$slon" +
            if (sname.isEmpty()) "" else "&sname=$sname" +
            if (rideType.isEmpty()) "" else "&rideType=$rideType" +
            if (sid.isEmpty()) "" else "&sid=$sid" +
            if (did.isEmpty()) "" else "&did=$did"

    startActivity(Intent().apply {
        action = Intent.ACTION_VIEW
        addCategory(Intent.CATEGORY_DEFAULT)
        data = Uri.parse(uriString)
    })
}

/**
 * 百度路线规划 URL接口：baidumap://map/direction
 * http://lbsyun.baidu.com/index.php?title=uri/api/android
 *
 * @param origin             起点名称或经纬度，或者可同时提供名称和经纬度，此时经纬度优先级高，将作为导航依据，名称只负责展示。
 *                           origin和destination二者至少一个有值（默认值是当前定位地址）
 *                           经纬度:       39.9761,116.3282(注意：坐标先纬度，后经度)
 *                           经纬度和名称: name:天安门|latlng:39.98871,116.43234|addr:北京市东城区东长安街(注意：坐标先纬度，后经度)
 * @param destination        终点名称或经纬度，或者可同时提供名称和经纬度，此时经纬度优先级高，将作为导航依据，名称只负责展示。
 * @param src                必填 统计来源，格式为：andr.companyName.appName不传此参数，不保证服务
 * @param mode               非必填 导航模式，可选transit（公交）、driving（驾车）、walking（步行）和riding（骑行）.默认:driving
 * @param coord_type         非必填 坐标类型，可选参数，默认为bd09经纬度坐标。
 *                                 允许的值为bd09ll、bd09mc、gcj02、wgs84。
 *                                 bd09ll表示百度经纬度坐标，
 *                                 bd09mc表示百度墨卡托坐标，
 *                                 gcj02表示经过国测局加密的坐标，
 *                                 wgs84表示gps获取的坐标
 * @param target             非必填 0 图区，1 详情，只针对公交检索有效
 * @param sy                 公交检索策略，只针对mode字段填写transit情况下有效，值为数字。
 *                           0：推荐路线
 *                           2：少换乘
 *                           3：少步行
 *                           4：不坐地铁
 *                           5：时间短
 *                           6：地铁优先
 * @param index              公交结果结果项，只针对公交检索，值为数字，从0开始
 * @param viaPoints          途经点参数，内容为json格式，需要把内容encode后拼接到url中
 * @param region             城市名或县名
 * @param origin_region      起点所在城市或县
 * @param destination_region 终点所在城市或县
 *
 *
 *
 */
fun Context.toBaiduDirection(
        destination: String,
        src: String,
        mode: String = "driving",
        coord_type: String = "bd09ll",
        target: String = "0",
        origin: String = "",
        sy: String = "",
        index: String = "",
        viaPoints: String = "",
        region: String = "",
        origin_region: String = "",
        destination_region: String = "") {

    val uriString = "baidumap://map/direction?mode=$mode" +
            "&src=$src" +
            "&destination=$destination" +
            "&target=$target" +
            "&coord_type=$coord_type" +
            if (origin.isEmpty()) "" else "&origin=$origin" +
            if (sy.isEmpty()) "" else "&sy=$sy" +
            if (index.isEmpty()) "" else "&index=$index" +
            if (viaPoints.isEmpty()) "" else "&viaPoints=$viaPoints" +
            if (region.isEmpty()) "" else "&region=$region" +
            if (origin_region.isEmpty()) "" else "&origin_region=$origin_region" +
            if (destination_region.isEmpty()) "" else "&destination_region=$destination_region"

    startActivity(Intent().apply {
        action = Intent.ACTION_VIEW
        addCategory(Intent.CATEGORY_DEFAULT)
        data = Uri.parse(uriString)
    })
}