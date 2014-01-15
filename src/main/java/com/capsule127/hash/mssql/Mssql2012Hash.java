package com.capsule127.hash.mssql;

import com.capsule127.hash.IHash;
import com.capsule127.hash.IHashGenerator;
import com.capsule127.hash.IHashTypeDescription;

/**
 * Created by marcus on 15/01/14.
 */
public class Mssql2012Hash implements IHashTypeDescription {
    @Override
    public String name() {
        return "MSSQL2012";
    }

    @Override
    public String description() {
        return "Mssql 2012 hash";
    }

    @Override
    public String abbrev() {
        return "MS2012";
    }

    @Override
    public IHashGenerator[] generators() {
        return new IHashGenerator[] {
                new Mssql2012HashGenerator()
        };
    }

    @Override
    public IHash fromTextLine(final String userColumn, final String hashColumn) {
        return new IHash() {
            @Override
            public IHashTypeDescription hash_type() {
                return Mssql2012Hash.this;
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
