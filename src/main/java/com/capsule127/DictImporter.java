package com.capsule127;

import asg.cliche.Command;
import asg.cliche.Param;
import com.capsule127.wordlist.Wordlist;

import java.util.Queue;
import java.util.logging.Logger;

/**
 * Created by marcus on 12/01/14.
 */
public class DictImporter {

    private static Logger logger() {
        return Logger.getLogger("DictImporter");
    }


    @Command(name = "dict-import", description = "Imports dictionary from the file specified to the cloud", abbrev = "di")
    public static void dict_import(@Param(name = "Queue name", description = "Wordlist queue name") String dictQueueName,
                                   @Param(name = "File location", description = "Points out the path of file includes text dictionary") String fileLoc) {

        if (NodeInstanceFactory.instances.size() == 0) {

            logger().warning("No node has started yet, cannot import dictionaries");

            return;

        }


        // Adding any instance would be enough to spread accross to all
        NodeInstance ni = NodeInstanceFactory.instances.elementAt(0);

        Queue<Wordlist> q = ni.hi.getQueue(dictQueueName+".wlqueue");

        Wordlist.load_from_file(fileLoc,q);



    }
}
