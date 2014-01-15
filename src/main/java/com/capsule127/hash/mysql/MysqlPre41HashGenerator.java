package com.capsule127.hash.mysql;

import com.capsule127.hash.IHashGenerator;
import org.bouncycastle.util.encoders.Hex;

import java.security.MessageDigest;

/**
 * Created by marcus on 15/01/14.
 */
public class MysqlPre41HashGenerator implements IHashGenerator {


    @Override
    public String generate(String user, String password, String salt) throws Exception {

        if (password == null) {
            throw  new Exception("Password is invalid.");
        }

        return generate(password.getBytes("UTF-8"),new byte[0]);
    }

    @Override
    public String generate(byte[] input, byte[] salt) throws Exception {


        String str = new String(input, "UTF-8");


        int nr = 1345345333;
        int add=  7;
        int nr2 =  0x12345671;

        for (char c :str.toCharArray()) {

            if (c != ' ' && c != '\t') {

                int tmp = (int)c;
                nr ^= (((nr & 63) + add) * tmp) + (nr << 8);
                nr2 += (nr2 << 8) ^ nr;
                add += tmp;
            }

        }

        return String.format("%08x%08x", nr & ((1 << 31) -1), nr2 & ((1 << 31) - 1) );

//        MessageDigest md = MessageDigest.getInstance("SHA-1");
//
//        md.reset();
//        md.update(input);
//
//        byte[] fp = md.digest();
//
//        md.reset();
//        md.update(fp);


//        {"6C8989366EAF75BB670AD8EA7A7FC1176A95CEF4","mypass"},
//        {"12e76a751efa43a177049262a2ee36da327d8e50","this_is_a_random_string"},

        //return new String(Hex.encode(md.digest())).toUpperCase();
    }

    @Override
    public String description() {
        return "Mysql pre-4.1 Java Hash Generator";
    }

    @Override
    public boolean test() {

        String[][] tests = new String[][] {
        {"445ff82636a7ba59", "probe"},
        {"60671c896665c3fa", "a"},
        {"1acbed4a27b20da3", "hash"},
        {"77ff75006118bab8", "hacker"},
        {"1b38cd9c2f809809", "hacktivity2008"},
        {"1b38cd9c2f809809", "hacktivity 2008"},
        {"6fc81597422015a8", "johnmodule"},
        {"30f098972cc8924d", "http://guh.nu"},
        {"3fc56f6037218993", "Andrew Hintz"},
        {"697a7de87c5390b2", "drew"},
        {"1eb71cf460712b3e", "http://4tphi.net"},
        {"28ff8d49159ffbaf", "http://violating.us"},
        {"5d2e19393cc5ef67", "password"},
        {"5030573512345671", ""}
        };

        for (String[] pairs : tests) {

            try {

                String hash = generate(null,pairs[1],null);
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
