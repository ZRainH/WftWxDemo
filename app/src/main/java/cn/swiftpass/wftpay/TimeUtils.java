package cn.swiftpass.wftpay;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {

    /**
     * 获取日期
     *
     * @return
     */
    public static String getTime() {
        return getDateToString(System.currentTimeMillis(),"yyyyMMddHHmmss");
    }

    public static String getDateTime(){
        return getDateToString(System.currentTimeMillis(),"yyyy-MM-dd HH:mm:ss");
    }

    public static String[] getOrderAndExpireTime() {
        String[] time = new String[2];
        long f = 30 * 60 * 1000;  // 30分钟
        //订单时间
        long orderTime = System.currentTimeMillis();
        //订单过期时间
        long timeExpire = orderTime + f;

        time[0] = getDateToString(orderTime,"yyyyMMddHHmmss");
        time[1] = getDateToString(timeExpire,"yyyyMMddHHmmss");

        return time;
    }

    private static String getDateToString(long milSecond, String pattern) {
        Date date = new Date(milSecond);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }
}
