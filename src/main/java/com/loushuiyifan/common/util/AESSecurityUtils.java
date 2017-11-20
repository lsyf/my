package com.loushuiyifan.common.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;


public class AESSecurityUtils {
    private static final int KEY_BIT = 128;

    private static final String TYPE = "AES";

    private static final String DEFAULT_MODE = "ECB";

    private static final String DEFAULT_PADDING = "PKCS5Padding";
    // private static final String DEFAULT_PADDING = "ZeroBytePadding";


    private static final Key getKey(String key) {
        byte[] keys = key.getBytes();
        byte[] keyBTmp = new byte[KEY_BIT / 8];

        for (int i = 0; i < keys.length && i < keyBTmp.length; i++) {
            keyBTmp[i] = keys[i];
        }
        return new SecretKeySpec(keyBTmp, TYPE);
    }

    private static final String getSecurityType(String mode, String padding) {
        StringBuffer sbuf = new StringBuffer(TYPE);
        sbuf.append("/").append(mode);
        sbuf.append("/").append(padding);
        return sbuf.toString();
    }

    public static String encode(String str, String key, String mode,
                                String padding) {
        Key secretKey = getKey(key);
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(getSecurityType(mode, padding));
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] b = cipher.doFinal(str.getBytes());
            return parseByte2HexStr(b);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decode(String str, String key, String mode,
                                String padding) {
        Key secretKey = getKey(key);
        Cipher cipher;
        try {
            byte[] b = parseHexStr2Byte(str);
            cipher = Cipher.getInstance(getSecurityType(mode, padding));
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(b));
        } catch (Exception e) {
            // throw new ApplicationException("AES解密错误", e);
        }
        return null;
    }

    /**
     * 将二进制转换成16进制
     *
     * @param buf
     * @return
     */
    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 将16进制转换为二进制
     *
     * @param hexStr
     * @return
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    public static String encode(String str, String key)

    {
        return encode(str, key, DEFAULT_MODE, DEFAULT_PADDING);
    }

    public static String decode(String str, String key)

    {
        return decode(str, key, DEFAULT_MODE, DEFAULT_PADDING);
    }


}
