package com.luo.kill.server.utils;

import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 生成订单code
 */
public class RandomUtil {

    private static SimpleDateFormat dateFormatOne = new SimpleDateFormat("yyyyMMddHHmmssSS");

    private static final ThreadLocalRandom random = ThreadLocalRandom.current();

    public static String generateOrderCode() {
        return dateFormatOne.format(DateTime.now().toDate() + generateNumber(4));
    }

    public static String generateNumber(final int num) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < num; i++) {
            sb.append(random.nextInt(9));
        }

        return sb.toString();
    }
}
