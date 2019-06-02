package sunnn.sunsite.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5工具类
 *
 * @author ASun
 */
public class MD5s {

    private static final char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * 获取MD5的直接方法
     *
     * @param src 源字符串
     * @return 散列之后的MD5字符串
     */
    public static String getMD5(String src) {
        return byteToString(MD5Encoder(src));
    }

    /**
     * 获取MD5的长整型序列
     *
     * @param src 源字符串
     * @return 散列之后的MD5长整形
     */
    public static long getMD5Sequence(String src) {
        return byteToLong(MD5Encoder(src));
    }

    /**
     * 计算MD5序列
     *
     * @param src 源字符串
     * @return MD5序列
     */
    private static byte[] MD5Encoder(String src) {
        MessageDigest encoder;
        try {
            encoder = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        encoder.update(src.getBytes());
        return encoder.digest();
    }

    /**
     * 将MD5序列转为字符串
     *
     * @param code MD5序列
     * @return 转换之后的字符串
     */
    public static String byteToString(byte[] code) {
        if (code == null)
            return null;

        StringBuilder res = new StringBuilder();
        for (byte num : code) {
//            int num = up(aCode);
            res.append(hexDigits[(num >> 4) & 0x0f]).append(hexDigits[num & 0x0f]);
        }
        return res.toString();
    }

    /**
     * 将MD5序列转为64位长整型
     *
     * @param code MD5序列
     * @return 转换之后的整形数字
     */
    public static long byteToLong(byte[] code) {
        if (code == null)
            return -1;
        long res = 0;
        for (int i = 4; i < 12; ++i) {
            res = res << 8;
            res += code[i];
        }
        return res & Long.MAX_VALUE;
    }

    @Deprecated
    private static int up(byte code) {  //code + 256是会返回int的噢
        return code < 0 ? code + 256 : code;
    }
}

