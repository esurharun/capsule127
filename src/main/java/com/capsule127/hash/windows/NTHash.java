package com.capsule127.hash.windows;

import com.capsule127.hash.IHash;
import com.capsule127.hash.IHashGenerator;
import com.capsule127.hash.IHashTypeDescription;

/**
 * Created by marcus on 15/01/14.
 */
public class NTHash implements IHashTypeDescription {
    @Override
    public String name() {
        return "NT Hash";
    }

    @Override
    public String description() {
        return "Windows-NT Hash";
    }

    @Override
    public String abbrev() {
        return "NT";
    }

    @Override
    public IHashGenerator[] generators() {
        return new IHashGenerator[] {
                new NTHashGenerator()
        };
    }

    @Override
    public IHash fromTextLine(final String userColumn, final String hashColumn) {
        return new IHash() {
            @Override
            public IHashTypeDescription hash_type() {
                return NTHash.this;
            }

            @Override
            public String user() {
                return userColumn;
            }

            @Override
            public String hash() {
                return hashColumn;
            }

            @Override
            public String salt() {
                return null;
            }
        };
    }

    @Override
    public boolean requiresUserOrSaltPerGeneration() {
        return false;
    }
}
