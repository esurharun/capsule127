package com.capsule127.hash;

/**
 * Created by marcus on 15/01/14.
 */
public class Common {
    public static byte[] hex2byte_array(String hex) {


        int len = hex.length();
        byte[] bSalt = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            bSalt[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i+1), 16));
        }

        return bSalt;

    }
}
