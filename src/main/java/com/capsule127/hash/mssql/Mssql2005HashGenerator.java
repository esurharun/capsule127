package com.capsule127.hash.mssql;

import com.capsule127.hash.Common;
import com.capsule127.hash.IHashGenerator;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

import java.security.MessageDigest;

/**
 * Created by marcus on 15/01/14.
 */
public class Mssql2005HashGenerator implements IHashGenerator {


    @Override
    public String generate(String user, String password, String salt) throws Exception {

        if (password == null) {
            throw  new Exception("Password is invalid.");
        }

        if (salt == null) {
            throw new Exception("Salt is invalid");
        }



        return generate(password.getBytes("UTF-8"), Common.hex2byte_array(salt));
    }

    @Override
    public String generate(byte[] input, byte[] salt) throws Exception {


        byte[] utf16le =  new String(input,"UTF-8").getBytes("UTF-16LE");

        byte[] total = Arrays.concatenate(utf16le,salt);



        MessageDigest md = MessageDigest.getInstance("SHA-1");

        md.reset();
        md.update(total);

        byte[] fp = md.digest();


        return  "0x0100"+((new String(Hex.encode(salt)))+new String(Hex.encode(fp))).toUpperCase();

    }

    @Override
    public String description() {
        return "Mssql 2005 Java Hash Generator";
    }

    @Override
    public boolean test() {

        String[][] tests = new String[][] {
                {"0x01006ACDF9FF5D2E211B392EEF1175EFFE13B3A368CE2F94038B","password"},
                {"0x01004086CEB6BF932BC4151A1AF1F13CD17301D70816A8886908", "toto"},
                {"0x01004086CEB60ED526885801C23B366965586A43D3DEAC6DD3FD", "titi"}

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

