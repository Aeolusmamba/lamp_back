package com.home.lamp.tools;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

//根据格式获取当前时间，传入空串就返回毫秒，传入pattern: yyyy-MM-dd HH:mm:ss就返回规格化时间
public class CurrentTime {
    public static String getCurrentTime(String pattern) {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        return dateFormat.format(date);
    }

    //返回时间戳（毫秒级）
    public static String getCurrentTime() {
        Date date = new Date(System.currentTimeMillis());
        long time = date.getTime();
        return Long.toString(time);
    }
}
