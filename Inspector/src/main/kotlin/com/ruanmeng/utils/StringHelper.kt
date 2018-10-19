package com.ruanmeng.utils

import java.util.regex.Pattern

/**
 * 姓名替换，保留姓氏
 * 如果姓名为空 或者 null ,返回空 ；否则，返回替换后的字符串；
 */
fun String.nameReplaceWithStar(): String = when {
    isNullOrEmpty() -> ""
    else -> replaceAction("(?<=[\\u4e00-\\u9fa5]{" + (if (length > 3) "2" else "1") + "})[\\u4e00-\\u9fa5](?=[\\u4e00-\\u9fa5]{0})")
}

/**
 * 手机号号替换，保留前三位和后四位
 * 如果手机号为空 或者 null ,返回空 ；否则，返回替换后的字符串；
 */
fun String.phoneReplaceWithStar(): String = when {
    isNullOrEmpty() -> ""
    length < 7 -> this
    else -> replaceAction("(?<=\\d{3})\\d(?=\\d{4})")
}

/**
 * 身份证号替换，保留前四位和后四位
 * 如果身份证号为空 或者 null ,返回空 ；否则，返回替换后的字符串；
 */
fun String.idCardReplaceWithStar(): String = when {
    isNullOrEmpty() -> ""
    length < 8 -> this
    else -> replaceAction("(?<=\\d{4})\\d(?=\\d{4})")
}

/**
 * 银行卡替换，保留后四位
 * 如果银行卡号为空 或者 null ,返回空 ；否则，返回替换后的字符串；
 */
fun String.bankCardReplaceWithStar(): String = when {
    isNullOrEmpty() -> ""
    length < 4 -> this
    else -> replaceAction("(?<=\\d{0})\\d(?=\\d{4})")
}

/**
 * 银行卡替换，保留前六位和后四位
 * 如果银行卡号为空 或者 null ,返回空 ；否则，返回替换后的字符串；
 */
fun String.bankCardReplaceHeaderWithStar(): String = when {
    isNullOrEmpty() -> ""
    length < 10 -> this
    else -> replaceAction( "(?<=\\d{6})\\d(?=\\d{4})")
}

/**
 * 实际替换动作
 */
fun String.replaceAction(regular: String): String = replace(regular.toRegex(), "*")

/**
 * 判断字符串是否为整数和小数
 */
fun String.isNumeric(): Boolean {
    val pattern = Pattern.compile("-?[0-9]+.?[0-9]+")
    return pattern.matcher(this).matches()
}

/**
 * 手机号校验
 */
fun String.isMobile(): Boolean {
    if (length != 11) return false
    val regex = "^((1[3|5|8][0-9])|(14[5|7])|(16[6])|(17[0|1|3|5|6|7|8])|(19[8|9]))\\d{8}$"
    val p = Pattern.compile(regex)
    return p.matcher(this).matches()
}

/**
 * 传真校验
 */
fun String.isFax(): Boolean {
    val regex = "^((\\d{7,8})|(0\\d{2,3}-\\d{7,8}))$"
    val p = Pattern.compile(regex)
    return p.matcher(this).matches()
}

/**
 * 固话校验
 */
fun String.isTel(): Boolean {
    val regex = "^((\\d{7,8})|(0\\d{2,3}-\\d{7,8})|(400-\\d{3}-\\d{4})|(1[3456789]\\\\d{9}))$" //固话、400固话、匹配手机

    /*val reg = "(?:(\\(\\+?86\\))(0[0-9]{2,3}\\-?)?([2-9][0-9]{6,7})+(\\-[0-9]{1,4})?)|" +
                "(?:(86-?)?(0[0-9]{2,3}\\-?)?([2-9][0-9]{6,7})+(\\-[0-9]{1,4})?)";*/
    val p = Pattern.compile(regex)
    return p.matcher(this).matches()
}

/**
 * 邮箱校验
 */
fun String.isEmail(): Boolean {
    // val regex = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$"
    val regex = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$"

    val p = Pattern.compile(regex)
    return p.matcher(this).matches()
}

/**
 * 网址校验
 */
fun String.isWeb(): Boolean {
    val regex = "(http://|ftp://|https://|www){0,1}[^\u4e00-\u9fa5\\s]*?\\.(com|net|cn|me|tw|fr)[^\u4e00-\u9fa5\\s]*"
    // val regex = "^([hH][tT]{2}[pP]:/*|[hH][tT]{2}[pP][sS]:/*|[fF][tT][pP]:/*)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\/])+(\\?{0,1}(([A-Za-z0-9-~]+\\={0,1})([A-Za-z0-9-~]*)\\&{0,1})*)$"

    val p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
    return p.matcher(this).matches()
}