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
    val password_change_sub = "$baseIp/password_change_sub.rm"   //修改密码

    val index_data = "$baseIp/index_data.rm"                 //首页
    val index_price_info = "$baseIp/index_price_info.rm"     //车位信息
    val find_carshopp_data = "$baseIp/find_carshopp_data.rm" //车品商城

    val userinfo_uploadhead_sub = "$baseIp/userinfo_uploadhead_sub.rm" //修改头像
    val nickName_change_sub = "$baseIp/nickName_change_sub.rm"         //修改昵称
    val sex_change_sub = "$baseIp/sex_change_sub.rm"                   //修改性别
    val user_msg_data = "$baseIp/user_msg_data.rm"                     //个人资料
    val add_car = "$baseIp/add_car.rm"                                 //添加车牌
    val car_list_data = "$baseIp/car_list_data.rm"                     //车牌列表
    val user_balance = "$baseIp/user_balance.rm"                       //余额
    val goodsorder_list_data = "$baseIp/goodsorder_list_data.rm"       //订单列表
    val add_invoice = "$baseIp/add_invoice.rm"                         //开发票

    val recharge_request = "$baseIp/recharge_request.rm"           //充值
    val goodsorder_pay = "$baseIp/goodsorder_pay.rm"               //订单支付
    val goodsorder_paybalance = "$baseIp/goodsorder_paybalance.rm" //余额支付

    val help_center = "$baseIp/help_center.rm"             //帮助
    val leave_message_sub = "$baseIp/leave_message_sub.rm" //意见反馈
}