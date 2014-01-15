package com.capsule127;

import com.capsule127.hash.IHash;
import com.capsule127.hash.IHashTypeDescription;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.MapLoader;
import com.hazelcast.core.MapStore;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by marcus on 12/01/14.
 *
 * HashesMap works as follows;
 *
 * Map name sets as [HashName].hashmap and user name is the key with hash;salt pair value.
 *
 *
 */
public class HashesMap {

    private IHashTypeDescription hashType;
    private HazelcastInstance   hcInstance;
    private Connection          persistenceConnection;


    public HashesMap(IHashTypeDescription hashType, HazelcastInstance hcInstance, Connection persistenceConnection) {
        this.hashType = hashType;
        this.hcInstance = hcInstance;
        this.persistenceConnection = persistenceConnection;



        setUp();
    }


    public IHashTypeDescription getHashType() {
        return hashType;
    }

    private Map<String,String> getMap() {
        return hcInstance.getMap(mapName());
    }

    public void addHash(String user, String hash, String salt) {

       getMap().put(user,salt == null || salt.trim().length() == 0 ? hash : hash+";"+salt);

    }

    public void addHash(IHash _hash) {
        addHash(_hash.user(), _hash.hash(), _hash.salt());
    }

    public void markHashAsFound(IHash _hash, String password) {

        for (NodeInstance ni : NodeInstanceFactory.instances) {
            ni.publishJackpot(_hash,password);
        }

        getMap().remove(_hash.user());

    }

    public List<IHash> getHashes() {

        Vector<IHash> ret = new Vector<IHash>();

        Map<String, String> map = getMap();

        for (String key : map.keySet()) {

            String hash_salt = map.get(key);


            final String _user = key;

            final String[] h_s = split_hash_value_with_salt(hash_salt);

            ret.add(new IHash() {
                @Override
                public IHashTypeDescription hash_type() {
                    return hashType;
                }

                @Override
                public String user() {
                    return _user;
                }

                @Override
                public String hash() {
                    return h_s[0];
                }

                @Override
                public String salt() {
                    return h_s[1];
                }



            });

        }

       return ret;

    }


    private String mapName() {
        return hashType.name()+".hashmap";
    }

    private void setUp() {

        if (persistenceConnection != null) {

            MapStoreConfig msc = new MapStoreConfig();

            msc.setImplementation(new HashesMapStore());
            msc.setEnabled(true);
            msc.setWriteDelaySeconds(0);

            MapConfig mc = hcInstance.getConfig().getMapConfig(mapName());

            boolean newConfig = false;

            if (mc == null) {
                mc = new MapConfig(mapName());
                newConfig = true;
            }

            mc.setMapStoreConfig(msc);

            if (newConfig)
                hcInstance.getConfig().addMapConfig(mc);
        }

    }

    public String[] split_hash_value_with_salt(String value) {

        if (value == null)
            return new String[] {"",""};

        String[] vals = value.split(";");

        if (vals.length == 2)
            return vals;

        return new String[] { vals[0], vals.length == 1 ? "" : vals[1]};

    }


    /*
            Implemented only for hsqldb.
     */
    private class HashesMapStore implements MapStore<String,String> {

        public HashesMapStore() {

            // Initialization procedure
            try {
                persistenceConnection.createStatement().execute("CREATE TABLE HASHES (TYPE VARCHAR(64),USER VARCHAR(128), HASH LONGVARCHAR, SALT LONGVARCHAR);");
            } catch (SQLException e) {
                // Assuming it exists
                //e.printStackTrace();
            }

        }


        private Logger logger() {
            return Logger.getLogger("HashesMapStore:"+hashType.name());
        }
        private void execute_sql(String sql)  {

            try {

                logger().fine("SQL: "+sql);

                Statement st = persistenceConnection.createStatement();

                st.execute(sql);

                st.close();
            } catch (SQLException ex) {
                logger().warning("SQL_PROBLEM: "+ex.getMessage());
            }
        }



        @Override
        public void store(String s, String s2) {

            delete(s);

            String[] hash_and_salt = split_hash_value_with_salt(s2);

            execute_sql("INSERT INTO HASHES (TYPE,USER,HASH,SALT) VALUES ("+
                            "'"+hashType.name()+"',"+
                    "'"+s+"',"+
                                    "'"+hash_and_salt[0]+"',"+
                                    "'"+hash_and_salt[1]+"'"+
                                    ");"
            );

        }

        @Override
        public void storeAll(Map<String, String> stringStringMap) {

            for (String key : stringStringMap.keySet()) {

                store(key, stringStringMap.get(key));
            }

        }

        @Override
        public void delete(String s) {


            String[] hash_and_salt = split_hash_value_with_salt(s);

            execute_sql("DELETE FROM HASHES WHERE "+
                    " TYPE = '"+hashType.name()+"' AND"+
                            " USER = '"+s+"';"
            );
        }

        @Override
        public void deleteAll(Collection<String> strings) {

            for (String key : strings) {
                delete(key);
            }

        }

        @Override
        public String load(String s) {
            return null;
        }

        @Override
        public Map<String, String> loadAll(Collection<String> strings) {
            return null;
        }

        @Override
        public Set<String> loadAllKeys() {
            return null;
        }
    }






}
