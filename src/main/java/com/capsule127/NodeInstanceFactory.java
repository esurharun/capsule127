package com.capsule127;

import asg.cliche.Command;
import asg.cliche.Param;
import asg.cliche.ShellFactory;
import com.capsule127.cli.Util;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.Config;
import com.hazelcast.core.*;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import sun.print.resources.serviceui;

import java.util.Vector;

/**
 * Created by marcus on 09/01/14.
 */
public class NodeInstanceFactory {


    public static Vector<NodeInstance> instances = new Vector<NodeInstance>();

    @Command(name = "node-start", description = "Starts a new node on network", abbrev = "ns")
    public static void node_start() {



        String WG = Settings.get(Settings.OPT_WG);
        String WG_PASS = Settings.get(Settings.OPT_WG_PASS);


        node_start(WG,WG_PASS);
    }


    @Command(name = "node-start", description = "Starts a new node on network", abbrev = "ns")
    public static void node_start(@Param(name= "workgroup", description = "Workgroup name")String group,
                                  @Param(name = "workgroup_pass", description = "Workgroup password") String group_pass) {


//        ClientConfig cc = new ClientConfig();


//
//        String ext_nodes;
//        if ((ext_nodes = Settings.map.get("EXT_NODES")) != null) {
//
//            String[] nodes = ext_nodes.split(";");
//
//            for (String node : nodes) {
//                cc.addAddress(node);
//            }
//        }

        Config cfg = new Config();

        if (group != null && group.trim().length() > 0) {
            cfg.getGroupConfig().setName(group);
            if (group_pass != null)
                cfg.getGroupConfig().setPassword(group_pass);
        }



        NodeInstance ni = new NodeInstance();
        ni.hi = Hazelcast.newHazelcastInstance(cfg);



        instances.add(ni);

    }


    @Command(name = "node-list", description = "Lists started nodes on local computer", abbrev = "nl")
    public static void node_list() {

        int c = 0;
        for (NodeInstance ni : instances) {


            ShellFactory.io.println(Util.Colorize(Ansi.Color.YELLOW, "[" + c + "] ") + ni.hi.getName());

            c++;


        }
    }

    @Command(name = "node-list-all", description = "Lists connected nodes on network", abbrev = "nla")
    public static void node_list_all() {


        if (instances.size() == 0)
            return;



        Vector<String> sAddresses = new Vector<String>();


        for (NodeInstance ni : instances) {



            for (Member m : ni.hi.getCluster().getMembers()) {
                  String s = m.getInetSocketAddress().getAddress().getHostAddress()+
                            ":"+m.getInetSocketAddress().getPort();

                if (!sAddresses.contains(s))
                    sAddresses.add(s);
            }


        }

        for (String sAddress : sAddresses) {
            AnsiConsole.out.println(" > " + sAddress);
        }
    }

    @Command(name = "node-shutdown", description = "Shuts down node with id", abbrev = "nsd")
    public static void node_shutdown(@Param(name = "id", description = "Node id") int idx) {

        if (instances.size() == 0)
            return;

        NodeInstance ni = instances.elementAt(idx);

        if (ni != null) {

            AnsiConsole.out.println("Shutting down: " + Util.Colorize(Ansi.Color.CYAN, ni.hi.getName()));

            ni.hi.shutdown();

            instances.removeElementAt(idx);
        }
    }

    @Command(name = "node-shutdown-all", description = "Shuts down all nodes started on local computer", abbrev = "nsda")
    public static void node_shutdown_all() {

        if (instances.size() == 0)
            return;

        while (instances.size() > 0) {
            node_shutdown(0);
        }


    }

    @Command(name = "add-node", description = "Adds node to node list to connect on startup", abbrev = "an")
    public static void add_node(@Param(name = "node", description = "IP:Port description of node to add on instance startup")String node) {


        if (!Util.validateIpAndPort(node)) {

            AnsiConsole.out.println("Invalid ip:port => "+node);
            return;

        }

        String currNodes = Settings.get("EXT_NODES");

        currNodes = currNodes == null ? "" : currNodes;

        Settings.set("EXT_NODES", currNodes + node + ";");


    }
}
