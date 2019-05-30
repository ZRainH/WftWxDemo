package com.pay.dome

import java.text.SimpleDateFormat
import java.util.Date

object TimeUtils {

    /**
     * 获取日期
     *
     * @return
     */
    val time: String
        get() = getDateToString(System.currentTimeMillis(), "yyyyMMddHHmmss")

    val dateTime: String
        get() = getDateToString(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss")

    // 30分钟
    //订单时间
    //订单过期时间
    val orderAndExpireTime: Array<String?>
        get() {
            val time = arrayOfNulls<String>(2)
            val f = (30 * 60 * 1000).toLong()
            val orderTime = System.currentTimeMillis()
            val timeExpire = orderTime + f

            time[0] = getDateToString(orderTime, "yyyyMMddHHmmss")
            time[1] = getDateToString(timeExpire, "yyyyMMddHHmmss")

            return time
        }

    private fun getDateToString(milSecond: Long, pattern: String): String {
        val date = Date(milSecond)
        val format = SimpleDateFormat(pattern)
        return format.format(date)
    }
}
