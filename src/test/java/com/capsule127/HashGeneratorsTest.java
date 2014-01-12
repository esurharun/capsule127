package com.capsule127;

import com.capsule127.hash.IHashGenerator;
import com.capsule127.hash.IHashTypeDescription;
import com.capsule127.hash.oracle.Oracle11Hash;
import com.capsule127.hash.oracle.OracleHash;
import org.junit.Test;

/**
 * Created by marcus on 12/01/14.
 */
public class HashGeneratorsTest {


    private static final IHashTypeDescription[] hashTypes = new IHashTypeDescription[]{

            new OracleHash(),
            new Oracle11Hash()
    };


    @Test
    public void testGenerators() {

        for (IHashTypeDescription ihtd : hashTypes) {

            for (IHashGenerator gen : ihtd.generators()) {

                String info = ihtd.description()+" : "+gen.description();

                System.out.println("Testing: "+info);

                org.junit.Assert.assertTrue(info, gen.test());
            }

        }

    }

}
