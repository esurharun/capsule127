package com.capsule127.hash.mysql;

import com.capsule127.hash.IHashGenerator;
import org.bouncycastle.util.encoders.Hex;

import java.security.MessageDigest;

/**
 * Created by marcus on 15/01/14.
 */
public class MysqlSha1HashGenerator  implements IHashGenerator {


    @Override
    public String generate(String user, String password, String salt) throws Exception {

        if (password == null) {
            throw  new Exception("Password is invalid.");
        }

        if (password.startsWith("*"))
            password = password.substring(1);

        return generate(password.getBytes("UTF-8"),new byte[0]);
    }

    @Override
    public String generate(byte[] input, byte[] salt) throws Exception {



        MessageDigest md = MessageDigest.getInstance("SHA-1");

        md.reset();
        md.update(input);

        byte[] fp = md.digest();

        md.reset();
        md.update(fp);



        return new String(Hex.encode(md.digest())).toUpperCase();

    }

    @Override
    public String description() {
        return "Mysql v > 4.1 Java Hash Generator";
    }

    @Override
    public boolean test() {

        String[][] tests = new String[][] {
                {"6C8989366EAF75BB670AD8EA7A7FC1176A95CEF4","mypass"},
                {"12e76a751efa43a177049262a2ee36da327d8e50","this_is_a_random_string"},
                {"*5AD8F88516BD021DD43F171E2C785C69F8E54ADB", "tere"},
                {"*2c905879f74f28f8570989947d06a8429fb943e6", "verysecretpassword"},
                {"*A8A397146B1A5F8C8CF26404668EFD762A1B7B82", "________________________________"},
                {"*F9F1470004E888963FB466A5452C9CBD9DF6239C", "12345678123456781234567812345678"},
                {"*97CF7A3ACBE0CA58D5391AC8377B5D9AC11D46D9", "' OR 1 /*'"},
                {"*2470C0C06DEE42FD1618BB99005ADCA2EC9D1E19", "password"},
                {"*7534F9EAEE5B69A586D1E9C1ACE3E3F9F6FCC446", "5"},
                {"*be1bdec0aa74b4dcb079943e70528096cca985f8", ""},
                {"*0D3CED9BEC10A777AEC23CCC353A8C08A633045E", "abc"},
                {"*18E70DF2758EE4C0BD954910E5808A686BC38C6A", "VAwJsrUcrchdG9"},
                {"*440F91919FD39C01A9BC5EDB6E1FE626D2BFBA2F", "lMUXgJFc2rNnn"},
                {"*171A78FB2E228A08B74A70FE7401C807B234D6C9", "TkUDsVJC"},
                {"*F7D70FD3341C2D268E98119ED2799185F9106F5C", "tVDZsHSG"}
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
