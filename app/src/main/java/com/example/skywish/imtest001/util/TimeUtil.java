package com.example.skywish.imtest001.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by skywish on 2015/7/2.
 */
public class TimeUtil {

    public static String getHourAndMin(long time) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(new Date(time));
    }

    public static String getTime(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm");
        return format.format(new Date(time));
    }

    /**
     * 获取聊天时间：因为sdk的时间默认到秒故应该乘1000
     * @Title: getChatTime
     * @Description: TODO
     * @param @param timesamp
     * @param @return
     * @return String
     * @throws
     */
    public static String getChatTime(long timesamp) {
        long clearTime = timesamp * 1000;
        String result = "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        Date today = new Date(System.currentTimeMillis());
        Date otherDay = new Date(clearTime);
        int temp = Integer.parseInt(sdf.format(today)) - Integer.parseInt(sdf.format(otherDay));

        switch (temp) {
            case 0:
                result = "今天 " + getHourAndMin(clearTime);
                break;
            case 1:
                result = "昨天 " + getHourAndMin(clearTime);
                break;
            case 2:
                result = "前天 " + getHourAndMin(clearTime);
                break;
            default:
                result = getTime(clearTime);
                break;
        }

        return result;
    }
}
