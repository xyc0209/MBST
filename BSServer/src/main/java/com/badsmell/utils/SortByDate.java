package com.badsmell.utils;
import org.joda.time.DateTime;
import java.util.Comparator;


/**
 * @description: used to sort log
 * @author: xyc
 * @date: 2023-03-08 09:59
 */
public class SortByDate implements Comparator {
    public int compare(Object o1, Object o2) {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分");
        DateTime begin = null;
        DateTime end = null;
        String f1 = (String)o1;
        String f2 = (String)o2;
        String[] lists1 = f1.split("\\|", 12);
        String[] lists2 = f2.split("\\|", 12);
        begin = DateTime.parse(lists1[0]);
        end = DateTime.parse(lists2[0]);
        if (begin.isAfter(end)) {
            return 1;
        } else {
            return -1;
        }

    }
}

