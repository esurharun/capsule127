package com.capsule127;

import com.capsule127.hash.IHashTypeDescription;
import com.hazelcast.core.HazelcastInstance;

import java.util.Vector;

/**
 * Created by marcus on 12/01/14.
 */
public class NodeInstance {


    public HazelcastInstance hi;
    public Vector<HashesMap> hashesMaps = new Vector<HashesMap>();


    public HashesMap getHashesMapFor(IHashTypeDescription hashType) {

        for (HashesMap hm : hashesMaps) {

            if (hm.getHashType().name().equals(hashType.name())) {
                return hm;
            }
        }

        HashesMap hm = new HashesMap(hashType,hi,LocalStorageManager.storageConnection);

        hashesMaps.add(hm);

        return hm;

    }
}
