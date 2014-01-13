package com.capsule127;

import asg.cliche.Command;
import com.bethecoder.ascii_table.ASCIITable;
import com.hazelcast.core.DistributedObject;
import com.sun.org.apache.xerces.internal.dom.AttrNSImpl;
import org.fusesource.jansi.AnsiConsole;

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
}
