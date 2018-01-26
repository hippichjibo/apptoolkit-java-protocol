package com.jibo.rom.sdk.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by alexz on 23.10.17.
 */
public class Util {
    /*
        * This String util method removes single or double quotes
    * from a string if its quoted.
    * for input string = "mystr1" output will be = mystr1
    * for input string = 'mystr2' output will be = mystr2
    *
        * @param String value to be unquoted.
    * @return value unquoted, null if input is null.
    *
    */
    public static String unquote(String s) {

        if (s != null
                && ((s.startsWith("\"") && s.endsWith("\""))
                || (s.startsWith("'") && s.endsWith("'")))) {

            s = s.substring(1, s.length() - 1);
        }
        return s;
    }

    public static String md5(String dataString) {
        try {

            if (dataString == null || dataString.isEmpty()) return "";

            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(dataString.getBytes());
            byte digestBytes[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < digestBytes.length; i++) {
                if (Integer.toHexString(0xFF & digestBytes[i]).length() == 1) {
                    hexString.append('0');
                }
                hexString.append(Integer.toHexString(0xFF & digestBytes[i]));
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
        }
        return "";
    }

    public static String sha1(String input) throws NoSuchAlgorithmException {
        MessageDigest mDigest = MessageDigest.getInstance("SHA1");
        byte[] result = mDigest.digest(input.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }

    public static void byte2hex(byte b, StringBuffer buf) {
        char[] hexChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
                '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        int high = ((b & 0xf0) >> 4);
        int low = (b & 0x0f);
        buf.append(hexChars[high]);
        buf.append(hexChars[low]);
    }

    public static String toHexString(byte[] block) {
        StringBuffer buf = new StringBuffer();
        int len = block.length;
        for (int i = 0; i < len; i++) {
            byte2hex(block[i], buf);
            if (i < len - 1) {
                buf.append(":");
            }
        }
        return buf.toString();
    }

}
