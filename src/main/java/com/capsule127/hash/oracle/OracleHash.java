package com.capsule127.hash.oracle;

import com.capsule127.hash.IHashGenerator;
import com.capsule127.hash.IHashTypeDescription;

/**
 * Created by marcus on 09/01/14.
 */
public class OracleHash implements IHashTypeDescription {
    @Override
    public String name() {
        return "ORACLE";
    }

    @Override
    public String description() {
        return "Oracle DES hashes < 11g";
    }

    @Override
    public String abbrev() {
        return "O";
    }

    @Override
    public IHashGenerator[] generators() {
        return new IHashGenerator[]{new OracleHashGenerator()};
    }
}
