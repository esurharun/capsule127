package com.capsule127;

import asg.cliche.Command;
import asg.cliche.Param;
import com.capsule127.hash.IHash;
import com.capsule127.hash.IHashTypeDescription;
import com.capsule127.wordlist.Wordlist;
import org.fusesource.jansi.AnsiConsole;

import java.util.Queue;

/**
 * Created by marcus on 17/01/14.
 */
public class Benchmark {


    @Command(name = "fill-random-hashes", description = "Fills random hashes for specified hash type and wordlist", abbrev = "frh")
    public void fill_random_hashes(@Param(name = "hash_type", description = "Hash type to fill on")String hashType,
                                @Param(name = "count", description = "Count of hash to fill on")int count) {

        if (!Common.check_node_started("No node has started yet, cannot fill random hashes."))
            return;


        IHashTypeDescription _iht = Common.getHashTypebyAbbrev(hashType, true);

        if (_iht == null) {

            return;

        }

        // Adding any instance would be enough to spread accross to all
        NodeInstance ni = NodeInstanceFactory.instances.elementAt(0);


        AnsiConsole.out.println("Collecting..");

        HashesMap hm = ni.getHashesMapFor(_iht);

        for (int i=0;i<count;i++) {

            String user = "user"+i;
            String pass = Common.generateRandomPassword(16);

            try {
                IHash ih = _iht.fromTextLine(user, _iht.generators()[0].generate(user,pass,pass ));

                hm.addHash(ih);


            } catch (Exception e) {

                AnsiConsole.out.println("Could not create random hash: "+e.getMessage());

                break;
            }


        }

        AnsiConsole.out.println("DONE.");



    }

    @Command(name = "fill-random-passes", description = "Fills random passes to wordlist queue", abbrev = "frp")
    public void fill_random_passes(@Param(name = "queue_name", description = "Queue name to fill on")String dictQueueName,
                                   @Param(name = "count", description = "Count of hash to fill on")int count) {

        if (!Common.check_node_started("No node has started yet, cannot fill random passes."))
            return;


        // Adding any instance would be enough to spread accross to all
        NodeInstance ni = NodeInstanceFactory.instances.elementAt(0);


        AnsiConsole.out.println("Collecting..");


        Queue<Wordlist> q = ni.hi.getQueue(dictQueueName+".wlqueue");

        Wordlist wl = new Wordlist();

        int limit = Integer.parseInt(Settings.get(Settings.OPT_WL_MAX_WORD_SIZE_PER_CH));

        for (int i=0;i<=count;i++) {

            if (((i+1) % limit) == 0) {
                q.add(wl);
                wl = new Wordlist();
            }

            String pass = Common.generateRandomPassword(16);

            wl.getVector().add(pass);

        }

        q.add(wl);

        AnsiConsole.out.println("DONE.");



    }

    @Command(name = "fill-random-passes", description = "Fills random passes to wordlist queue", abbrev = "frp")
    public void fill_random_passes(@Param(name = "count", description = "Count of hash to fill on")int count) {

        fill_random_passes(Settings.get(Settings.OPT_WL_DEFAULT_NAME),count);



    }


}
