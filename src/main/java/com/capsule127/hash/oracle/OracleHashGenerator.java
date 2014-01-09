package com.capsule127.hash.oracle;

import com.capsule127.hash.IHashGenerator;
import org.bouncycastle.util.encoders.Hex;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Created by marcus on 09/01/14.
 */
public class OracleHashGenerator implements IHashGenerator {


    private static final byte[] keyBytes =
            {
                    (byte) 0x01, (byte) 0x23, (byte) 0x45, (byte) 0x67, (byte) 0x89, (byte) 0xab, (byte) 0xcd, (byte) 0xef
            };
    private static final IvParameterSpec ips = new IvParameterSpec(new byte[8]);

    static Cipher des_cipher;
    static byte[] encryptedBytes;
    static SecretKey key;


    static {
        try {
            des_cipher = Cipher.getInstance("DES/CBC/NoPadding");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.exit(0);
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            System.exit(0);
        }
        key = new SecretKeySpec(keyBytes, "DES");

    }

    @Override
    public String generate(String user, String password, String salt) throws Exception {

        // instructions see http://www.sans.org/reading_room/special/?id=oracle_pass&ref=911
        // 1. Concatenate the username and the password to produce a plaintext string;
        // 2. Convert the plaintext string to uppercase characters;
        // 3. Convert the plaintext string to multi-byte storage format; ASCII characters have the high byte set to 0x00;
        byte[] input = (user + password).toUpperCase().getBytes("utf-16be");

        //4.Encrypt the plaintext string (padded with 0s if necessary to the next even block length)
        //using the DES algorithm in cipher block chaining (CBC) mode with a fixed key value of
        //0x0123456789ABCDEF;
        input = Arrays.copyOf(input, ((input.length + 7) / 8) * 8); // Pad with zeros

        return generate(input, null);
    }

    @Override
    public String generate(byte[] input, byte[] salt) throws Exception {


        des_cipher.init(Cipher.ENCRYPT_MODE, key, ips);
        encryptedBytes = des_cipher.doFinal(input);

        //5.Encrypt the plaintext string again with DES-CBC, but using the last block of the output
        //of the previous step (ignoring parity bits) as the encryption key.
        // Don't need to set parity - done behind the scenes by the JCE
        Key encryptedKey = new SecretKeySpec(encryptedBytes, encryptedBytes.length - 8, 8, "DES");
        des_cipher.init(Cipher.ENCRYPT_MODE, encryptedKey, ips);
        byte[] encryptedPw = des_cipher.doFinal(input);

        String hex = new String(Hex.encode(encryptedPw));

        hex = hex.substring(hex.length() - 16);

        return hex.toUpperCase();

    }
}
