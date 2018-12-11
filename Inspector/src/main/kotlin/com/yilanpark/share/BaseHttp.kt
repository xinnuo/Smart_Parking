package com.yilanpark.share

import com.yilanpark.BuildConfig

/**
 * 项目名称：Park_Inspector
 * 创建人：小卷毛
 * 创建时间：2018-10-24 11:38
 */
object BaseHttp {

    private val baseUrl = BuildConfig.API_HOST
    private val baseIp = "$baseUrl/api"
    val baseImg = "$baseUrl/"

    val register_sub = "$baseIp/register_sub.rm"                 //注册
    val login_sub = "$baseIp/login_sub.rm"                       //登录
    val pwd_forget_sub = "$baseIp/pwd_forget_sub.rm"             //忘记密码
    val identify_get = "$baseIp/identify_get.rm"                 //注册验证码
    val identify_getbyforget = "$baseIp/identify_getbyforget.rm" //忘记验证码
    val password_change_sub = "$baseIp/password_change_sub.rm"   //修改密码

    val msg_list_data = "$baseIp/msg_list_data.rm"             //消息
    val add_usergroup = "$baseIp/add_usergroup.rm"             //新增分组
    val find_usergroup_data = "$baseIp/find_usergroup_data.rm" //分组数据
    val update_usergroup = "$baseIp/update_usergroup.rm"       //修改分组
    val delete_usergroup = "$baseIp/delete_usergroup.rm"       //删除分组
    val add_parking_group = "$baseIp/add_parking_group.rm"     //移动分组
    val find_parking_info = "$baseIp/find_parking_info.rm"     //车位监控
    val get_parking_details = "$baseIp/get_parking_details.rm" //车位详情
    val find_ctn_parking = "$baseIp/find_ctn_parking.rm"       //车位预览
    val add_abnormal = "$baseIp/add_abnormal.rm"               //异常上传
    val add_punchClock = "$baseIp/add_punchClock.rm"           //打卡
    val punchClock_data = "$baseIp/punchClock_data.rm"         //打卡记录
    val add_scaninfo = "$baseIp/add_scaninfo.rm"               //车位上传
    val add_uposition = "$baseIp/add_uposition.rm"             //位置上传
    val goodsorder_ppay = "$baseIp/goodsorder_ppay.rm"         //支付

}