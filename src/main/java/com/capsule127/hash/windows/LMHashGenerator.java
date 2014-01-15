package com.capsule127.hash.windows;

import com.capsule127.hash.Common;
import com.capsule127.hash.IHashGenerator;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.MessageDigest;
import java.util.Locale;

/**
 * Created by marcus on 15/01/14.
 */
public class LMHashGenerator implements IHashGenerator {


    @Override
    public String generate(String user, String password, String salt) throws Exception {

        if (password == null) {
            throw  new Exception("Password is invalid.");
        }




        return generate(password.getBytes("UTF-8"), Common.hex2byte_array(salt));
    }

    private static void oddParity(final byte[] bytes) {
        for (int i = 0; i < bytes.length; i++) {
            final byte b = bytes[i];
            final boolean needsParity = (((b >>> 7) ^ (b >>> 6) ^ (b >>> 5) ^ (b >>> 4) ^ (b >>> 3)
                    ^ (b >>> 2) ^ (b >>> 1)) & 0x01) == 0;
            if (needsParity) {
                bytes[i] |= (byte) 0x01;
            } else {
                bytes[i] &= (byte) 0xfe;
            }
        }
    }

    private static Key createDESKey(final byte[] bytes, final int offset) {
        final byte[] keyBytes = new byte[7];
        System.arraycopy(bytes, offset, keyBytes, 0, 7);
        final byte[] material = new byte[8];
        material[0] = keyBytes[0];
        material[1] = (byte) (keyBytes[0] << 7 | (keyBytes[1] & 0xff) >>> 1);
        material[2] = (byte) (keyBytes[1] << 6 | (keyBytes[2] & 0xff) >>> 2);
        material[3] = (byte) (keyBytes[2] << 5 | (keyBytes[3] & 0xff) >>> 3);
        material[4] = (byte) (keyBytes[3] << 4 | (keyBytes[4] & 0xff) >>> 4);
        material[5] = (byte) (keyBytes[4] << 3 | (keyBytes[5] & 0xff) >>> 5);
        material[6] = (byte) (keyBytes[5] << 2 | (keyBytes[6] & 0xff) >>> 6);
        material[7] = (byte) (keyBytes[6] << 1);
        oddParity(material);
        return new SecretKeySpec(material, "DES");
    }

    @Override
    public String generate(byte[] input, byte[] salt) throws Exception {


        final byte[] oemPassword =  new String(input,"UTF-8").toUpperCase(Locale.US).getBytes("US-ASCII");
        final int length = Math.min(oemPassword.length, 14);
        final byte[] keyBytes = new byte[14];
        System.arraycopy(oemPassword, 0, keyBytes, 0, length);
        final Key lowKey = createDESKey(keyBytes, 0);
        final Key highKey = createDESKey(keyBytes, 7);
        final byte[] magicConstant = "KGS!@#$%".getBytes("US-ASCII");
        final Cipher des = Cipher.getInstance("DES/ECB/NoPadding");
        des.init(Cipher.ENCRYPT_MODE, lowKey);
        final byte[] lowHash = des.doFinal(magicConstant);
        des.init(Cipher.ENCRYPT_MODE, highKey);
        final byte[] highHash = des.doFinal(magicConstant);
        final byte[] lmHash = new byte[16];
        System.arraycopy(lowHash, 0, lmHash, 0, 8);
        System.arraycopy(highHash, 0, lmHash, 8, 8);




        return  new String(Hex.encode(lmHash)).toUpperCase();

    }

    @Override
    public String description() {
        return "LanMan Java Hash Generator";
    }

    @Override
    public boolean test() {

        String[][] tests = new String[][] {
                {"e52cac67419a9a224a3b108f3fa6cb6d","password"}

        };

        for (String[] pairs : tests) {

            try {

                String hash = generate(null,pairs[1],pairs[0].substring(6,14));
                if (!hash.equalsIgnoreCase(pairs[0])) {

                    System.out.println("Expected: "+pairs[0]+" Got: "+hash);

                    return false;

                }
            } catch (Exception e) {


                e.printStackTrace();

                return false;
            }


        }


        return true;
    }
}

