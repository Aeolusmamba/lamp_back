package com.home.lamp.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CurrentTimeMillis {
    public static long getCurrentTimeMillis(String time, String pattern) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern); //首先定义待转换的时间格式
        Date date = sdf.parse(time); //将带转换的时间字符串转换为date类型，然后使用getTime即可获取对应的时间戳
        return date.getTime(); //如果是Date类型的时间，直接使用date.getTime就可以获得其对应的毫秒级时间戳：
    }
}
