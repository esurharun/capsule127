package com.capsule127.hash.mssql;

import com.capsule127.hash.IHash;
import com.capsule127.hash.IHashGenerator;
import com.capsule127.hash.IHashTypeDescription;

/**
 * Created by marcus on 15/01/14.
 */
public class Mssql2005Hash implements IHashTypeDescription {
    @Override
    public String name() {
        return "MSSQL2005";
    }

    @Override
    public String description() {
        return "Mssql 2005 - 2008 hash";
    }

    @Override
    public String abbrev() {
        return "MS2005";
    }

    @Override
    public IHashGenerator[] generators() {
        return new IHashGenerator[] {
                new Mssql2005HashGenerator()
        };
    }

    @Override
    public IHash fromTextLine(final String userColumn, final String hashColumn) {
        return new IHash() {
            @Override
            public IHashTypeDescription hash_type() {
                return Mssql2005Hash.this;
            }

            @Override
            public String user() {
                return userColumn;
            }

            @Override
            public String hash() {
                return hashColumn.substring(14);
            }

            @Override
            public String salt() {
                return hashColumn.substring(6,14);
            }
        };
    }

    @Override
    public boolean requiresUserOrSaltPerGeneration() {
        return true;
    }
}
