package com.ruanmeng.share

import com.ruanmeng.smart_parking.BuildConfig

/**
 * 项目名称：Smart_Parking
 * 创建人：小卷毛
 * 创建时间：2018-10-18 18:18
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

    val userinfo_uploadhead_sub = "$baseIp/userinfo_uploadhead_sub.rm" //修改头像
    val nickName_change_sub = "$baseIp/nickName_change_sub.rm" //修改昵称
    val sex_change_sub = "$baseIp/sex_change_sub.rm" //修改性别
    val user_msg_data = "$baseIp/user_msg_data.rm" //个人资料
    val add_car = "$baseIp/add_car.rm" //添加车牌
}