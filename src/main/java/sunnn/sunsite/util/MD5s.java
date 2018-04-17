package sunnn.sunsite.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5s {

    private static final char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String getMD5(String src) {
        return byteToString(MD5Encoder(src));
    }

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
