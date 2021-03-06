package com.capsule127.hash.oracle;

import com.capsule127.hash.Common;
import com.capsule127.hash.IHashGenerator;
import org.bouncycastle.util.encoders.Hex;

import java.security.MessageDigest;

/**
 * Created by marcus on 12/01/14.
 */
public class Oracle11HashGenerator implements IHashGenerator {


    @Override
    public String generate(String user, String password, String salt) throws Exception {

        if (salt == null) {
            throw  new Exception("Salt is invalid.");
        }



        return generate(password.getBytes("UTF-8"), Common.hex2byte_array(salt));
    }

    @Override
    public String generate(byte[] input, byte[] salt) throws Exception {

        MessageDigest md = MessageDigest.getInstance("SHA-1");

        byte[] bData = new byte[input.length + salt.length];
        System.arraycopy(input, 0, bData,0,input.length);
        System.arraycopy(salt,0,bData,input.length,salt.length);

        md.update(bData);
        byte[] hash = md.digest();


        return new String(Hex.encode(hash)).toUpperCase();
    }

    @Override
    public String description() {
        return "Oracle >= 11g Java Hash Generator";
    }

    @Override
    public boolean test() {

        String hash01 = "71752CE0530476A8B2E0DD218AE59CB71B211D7E1DB70EE23BFB23BDFD48";

        try {
            return generate(null,"ZK3002",hash01.substring(40,60)).equalsIgnoreCase("71752CE0530476A8B2E0DD218AE59CB71B211D7E");
        } catch (Exception e) {
            return false;
        }
    }
}
