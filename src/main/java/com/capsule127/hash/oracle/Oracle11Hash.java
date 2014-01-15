package com.capsule127.hash.oracle;

import com.capsule127.hash.IHash;
import com.capsule127.hash.IHashGenerator;
import com.capsule127.hash.IHashTypeDescription;

/**
 * Created by marcus on 12/01/14.
 */
public class Oracle11Hash implements IHashTypeDescription {
    @Override
    public String name() {
        return "ORACLE11";
    }

    @Override
    public String description() {
        return "Oracle SHA1 Hashes >= 11g";
    }

    @Override
    public String abbrev() {
        return "O11";
    }

    @Override
    public IHashGenerator[] generators() {
        return new IHashGenerator[] {
                new Oracle11HashGenerator()
        };
    }

    @Override
    public IHash fromTextLine(final String userColumn, final String hashColumn) {

        return new IHash() {
            @Override
            public IHashTypeDescription hash_type() {
                return Oracle11Hash.this;
            }

            @Override
            public String user() {
                return userColumn;
            }

            @Override
            public String hash() {
                return hashColumn.substring(0,40);
            }

            @Override
            public String salt() {
                return hashColumn.substring(40,60);
            }
        };
    }

    @Override
    public boolean requiresUserOrSaltPerGeneration() {
        return true;
    }


}
