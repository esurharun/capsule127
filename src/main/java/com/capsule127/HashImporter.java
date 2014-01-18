package com.capsule127;

import asg.cliche.Command;
import asg.cliche.Param;
import com.capsule127.hash.IHash;
import com.capsule127.hash.IHashTypeDescription;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;
import org.fusesource.jansi.AnsiConsole;

import java.io.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by marcus on 12/01/14.
 */
public class HashImporter {


    private static Logger logger() {
        return Logger.getLogger("HashImporter");
    }

    @Command(name = "hashes-import", description = "Imports hashes from the file specified to the cloud", abbrev = "hsi")
    public static void hashes_import(@Param(name = "File location", description = "Points out the path of the hashes file") String fileLoc) {

        if (!Common.check_node_started("No node has started yet, cannot import hashes."))
            return;



        // Adding any instance would be enough to spread accross to all
        NodeInstance ni = NodeInstanceFactory.instances.elementAt(0);

        File file = new File(fileLoc);

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            Pattern p = Pattern.compile("(.*)\\$(.*)\\#(.*)");

            String line = null;
            while ((line = br.readLine()) != null) {

                Matcher m = p.matcher(line);

                if (m.matches()) {

                    String abbrev = m.group(1);
                    String user = m.group(2);
                    String hash = m.group(3);

                    for (IHashTypeDescription iht : App.supportedHashTypes) {

                        if (iht.abbrev().equals(abbrev)) {

                            IHash hash_object = iht.fromTextLine(user,hash);

                            HashesMap hm = ni.getHashesMapFor(iht);

                            hm.addHash(hash_object);

                            logger().info("Added "+hash_object.user());

                        }
                    }


                }

            }

        } catch (IOException e) {
            logger().warning(fileLoc+": "+e.getMessage());
            return;
        }

    }



}
