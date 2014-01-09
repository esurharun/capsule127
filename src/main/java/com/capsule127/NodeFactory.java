package com.capsule127;

import asg.cliche.Command;
import asg.cliche.Param;
import asg.cliche.ShellFactory;
import com.capsule127.cli.Util;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import sun.print.resources.serviceui;

import java.util.Vector;

/**
 * Created by marcus on 09/01/14.
 */
public class NodeFactory {


    private static Vector<HazelcastInstance> instances = new Vector<HazelcastInstance>();

    @Command(name = "node-start", description = "Starts a new node on network", abbrev = "ns")
    public static void node_start() {

        HazelcastInstance hi = Hazelcast.newHazelcastInstance();

        instances.add(hi);

    }

    @Command(name = "node-list", description = "Lists started nodes on local computer", abbrev = "nl")
    public static void node_list() {

        int c = 0;
        for (HazelcastInstance hi : instances) {


            ShellFactory.io.println(Util.Colorize(Ansi.Color.YELLOW, "[" + c + "] ") + hi.getName());

            c++;


        }
    }

    @Command(name = "node-shutdown", description = "Shuts down node with id", abbrev = "nsd")
    public static void node_shutdown(@Param(name = "id", description = "Node id") int idx) {

        if (instances.size() == 0)
            return;

        HazelcastInstance hi = instances.elementAt(idx);

        if (hi != null) {

            AnsiConsole.out.println("Shutting down: " + Util.Colorize(Ansi.Color.CYAN, hi.getName()));

            hi.shutdown();

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
}
