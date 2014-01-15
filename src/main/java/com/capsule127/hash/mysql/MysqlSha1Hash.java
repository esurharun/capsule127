package com.capsule127.hash.mysql;

import com.capsule127.hash.IHash;
import com.capsule127.hash.IHashGenerator;
import com.capsule127.hash.IHashTypeDescription;

/**
 * Created by marcus on 15/01/14.
 */
public class MysqlSha1Hash  implements IHashTypeDescription {
    @Override
    public String name() {
        return "MYSQLSHA1";
    }

    @Override
    public String description() {
        return "Mysql Hashes >= 4.1.1";
    }

    @Override
    public String abbrev() {
        return "MY5";
    }

    @Override
    public IHashGenerator[] generators() {
        return new IHashGenerator[] {
                new MysqlPre41HashGenerator()
        };
    }

    @Override
    public IHash fromTextLine(final String userColumn, final String hashColumn) {

        return new IHash() {
            @Override
            public IHashTypeDescription hash_type() {
                return MysqlSha1Hash.this;
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