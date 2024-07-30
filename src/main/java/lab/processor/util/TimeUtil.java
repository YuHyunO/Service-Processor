package lab.processor.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {
    public static final String YYYY = "yyyy";
    public static final String MMDD = "MMdd";
    public static final String YYYYMM = "yyyyMM";
    public static final String YYYYMMDD = "yyyyMMdd";
    public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    public static final String YYYYMMDDHHMMSSFFF = "yyyyMMddHHmmssSSS";

    public static String getFormattedTime(Date date, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date);
    }

}
