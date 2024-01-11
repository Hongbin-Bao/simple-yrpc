package com.simple;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Hongbin BAO
 * @Date 2024/1/11 20:30
 */
public class DateUtil {

    public static Date get(String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return  sdf.parse(pattern);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

}
