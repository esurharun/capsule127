package com.capsule127.hash.mssql;

import com.capsule127.hash.Common;
import com.capsule127.hash.IHashGenerator;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

import java.security.MessageDigest;

/**
 * Created by marcus on 15/01/14.
 */
public class Mssql2012HashGenerator implements IHashGenerator {


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



        MessageDigest md = MessageDigest.getInstance("SHA-512");

        md.reset();
        md.update(total);

        byte[] fp = md.digest();


        return  "0x0200"+((new String(Hex.encode(salt)))+new String(Hex.encode(fp))).toUpperCase();

    }

    @Override
    public String description() {
        return "Mssql 2012 Java Hash Generator";
    }

    @Override
    public boolean test() {


        String[][] tests = new String[][] {

                {"0x0200F733058A07892C5CACE899768F89965F6BD1DED7955FE89E1C9A10E27849B0B213B5CE92CC9347ECCB34C3EFADAF2FD99BFFECD8D9150DD6AACB5D409A9D2652A4E0AF16","Password1!"},
                {"0x02006BF4AB05873FF0C8A4AFD1DC5912CBFDEF62E0520A3353B04E1184F05C873C9C76BBADDEAAC1E9948C7B6ABFFD62BFEFD7139F17F6AFE10BE0FEE7A178644623067C2423", "carlos"},
                {"0x0200935819BA20F1C7289CFF2F8FF9F0E40DA5E6D04986F988CFE6603DA0D2BC0160776614763198967D603FBD8C103151A15E70D18E7B494C7F13F16804A7A4EB206084E632", "test"},
                {"0x0200570AC969EF7C6CCB3312E8BEDE1D635EB852C06496957F0FA845B20FCD1C7C457474A5B948B68C47C2CB704D08978871F532C9EB11199BB5F56A06AC915C3799DB8A64C1", "test1"},
                {"0x0200A56045DBCD848E297FA8D06E7579D62B7129928CA0BC5D232A7320972EF5A5455C01411B8D3A7FF3D18A55058A12FAEE5DA410AFE6CE61FF5C39E5FF57CD3EDD57DB1C3B", "test2"},
                {"0x02008AC3B9DC7B67EF9D3C1D25D8007A4B957D5BD61D71E5E9DA08D9F8F012EDDAD168E1CADD93D4627433FBFEE8BCF6CBB42D5B9A31886FC5FF7F970B164F4B5815E03D6DE7", "jhl9mqe5"}

        };

        for (String[] pairs : tests) {

            try {

                String hash = generate(null,pairs[1],pairs[0].substring(6,14));
                if (!hash.equalsIgnoreCase(pairs[0])) {

                    System.out.println("Expected: "+pairs[0]+" \nGot: "+hash);



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

