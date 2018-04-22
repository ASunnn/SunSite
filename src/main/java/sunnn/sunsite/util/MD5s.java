package sunnn.sunsite.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 计算MD5
 * @author ASun
 */
public class MD5s {

    private static final char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * 获取MD5的直接方法
     * @param src   源字符串
     * @return  加密之后的MD5字符串
     */
    public static String getMD5(String src) {
        return byteToString(MD5Encoder(src));
    }

    /**
     * 计算MD5序列
     * @param src   源字符串
     * @return  MD5序列
     */
    private static byte[] MD5Encoder(String src) {
        MessageDigest encoder = null;
        try {
            encoder = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        encoder.update(src.getBytes());
        return encoder.digest();
    }

    /**
     * 将MD5序列转为字符串
     * @param code  MD5序列
     * @return  转换之后的字符串
     */
    private static String byteToString(byte[] code) {
        if(code == null)
            return null;

        StringBuilder res = new StringBuilder();
        for (byte aCode : code) {
            int num = aCode;
            if (num < 0)
                num += 256;
            res.append(hexDigits[num / 16]).append(hexDigits[num % 16]);
        }
        return res.toString();
    }

}
