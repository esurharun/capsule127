package com.capsule127;

import asg.cliche.Command;
import asg.cliche.Param;
import com.bethecoder.ascii_table.ASCIITable;
import com.capsule127.hash.IHash;
import com.capsule127.hash.IHashTypeDescription;
import com.hazelcast.core.DistributedObject;
import com.sun.org.apache.xerces.internal.dom.AttrNSImpl;
import org.fusesource.jansi.AnsiConsole;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * Created by marcus on 13/01/14.
 */
public class CloudCommands {



    private static Logger logger() {
        return Logger.getLogger("DictImporter");
    }
    @Command(name = "show-objects", description = "Shows list of distributed objects on the cloud", abbrev = "so")
    public static void show_distributed_objects() {


        if (NodeInstanceFactory.instances.size() == 0) {

            logger().warning("No node has started yet");

            return;

        }


        // Adding any instance would be enough to spread accross to all
        NodeInstance ni = NodeInstanceFactory.instances.elementAt(0);

        AnsiConsole.out().println("Object name\tType\t\tSize");

        String[] tableHeaders = new String[] {
                "Object name",
                "Type"
//                ,"Count"
        };

        Vector<String[]> tableData = new Vector<String[]>();

        Vector<String> names = new Vector<String>();

        for (DistributedObject dob : ni.hi.getDistributedObjects()) {

            String name = dob.getName();

            if (names.indexOf(name) == -1) {

                //System.out.println(name+" - "+dob.getPartitionKey()+" - "+dob.getServiceName());

                String[] parts = name.split("\\.");

                if (parts.length == 2) {


                    int type = parts[1].equals("wlqueue") ? 1:
                            parts[1].equals("hashmap") ? 2 : 99;

    //                long size = 0;
    //                if (type == 1) {
    //                    size = ni.hi.getMap(name).size();
    //                } else if (type == 2) {
    //                    size = ni.hi.getQueue(name).size();
    //                }

                    tableData.add(new String[] {
                            parts[0],
                            (type == 1 ? "Wordlist Queue" :
                                    type == 2 ? "Hashes list" : "Unknown")
                    });


                }

                names.add(name);
            }
        }

        if (tableData.size() > 0) {
            String[][] sTableData  = new String[tableData.size()][];

            tableData.copyInto(sTableData);

            AnsiConsole.out.println(ASCIITable.getInstance().getTable(tableHeaders,sTableData));
        }


    }

    @Command(name = "dump-hashes", description = "Dumps uncracked hashes from cloud to local file",abbrev = "dh")
    public void dump_hashes(@Param(name = "Hash type", description = "Specifies hash type such as SHA1, MD5 etc.") String hashType,
                            @Param(name= "File name", description = "File path to save")
                            String fileName) {


        if (NodeInstanceFactory.instances.size() == 0) {

            logger().warning("No node has started yet");

            return;

        }

        NodeInstance ni = NodeInstanceFactory.instances.elementAt(0);


        IHashTypeDescription _iht =null;

        for (IHashTypeDescription iht : App.supportedHashTypes) {

            if (iht.abbrev().equalsIgnoreCase(hashType)) {
                _iht = iht;
                break;
            }
        }

        if (_iht == null) {

            logger().warning(hashType + " is not a valid hash type name.");

            return;

        }


        try {

            FileWriter fw = new FileWriter(fileName,true);
            BufferedWriter bw = new BufferedWriter(fw);

            int c = 0;
            for (IHash ih : ni.getHashesMapFor(_iht).getHashes()) {


                String line = String.format("%s$%s#%s\n", ih.hash_type().abbrev(), ih.user(),
                        ih.salt() != null ? ih.hash() + ih.salt() : ih.hash());

                bw.write(line);
                c++;

            }

            AnsiConsole.out.println(c+" hashes dumped to : "+fileName+" successfully.");

            bw.flush();
            bw.close();

        } catch (Exception ex) {

            logger().warning("Error during dumping hashes: "+ex.getMessage());

        }



    }
}
