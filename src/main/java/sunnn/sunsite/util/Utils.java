package sunnn.sunsite.util;

public class Utils {

    public static boolean isIllegalPageParam(int page, int size) {
        return page < 0 || size < 1;
    }

}
