package com.ruanmeng.share

import com.ruanmeng.park_inspector.BuildConfig

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

    val add_usergroup = "$baseIp/add_usergroup.rm"             //新增分组
    val find_usergroup_data = "$baseIp/find_usergroup_data.rm" //分组数据
    val update_usergroup = "$baseIp/update_usergroup.rm"       //修改分组
    val delete_usergroup = "$baseIp/delete_usergroup.rm"       //删除分组

}