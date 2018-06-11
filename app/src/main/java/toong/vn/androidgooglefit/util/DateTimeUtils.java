package toong.vn.androidgooglefit.util;

import java.text.SimpleDateFormat;

/**
 * Created by PhanVanLinh on 11/06/2018.
 * phanvanlinh.94vn@gmail.com
 */
public class DateTimeUtils {
    public static String format(long date) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return fmt.format(date);
    }
}
