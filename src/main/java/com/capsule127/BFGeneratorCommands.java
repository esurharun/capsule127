package com.capsule127;

import asg.cliche.Command;
import asg.cliche.Param;

/**
 * Created by marcus on 18/01/14.
 */
public class BFGeneratorCommands {


    @Command(name="bf-generator", description = "Generates brute-force dictionary", abbrev = "bf")
    public static void bf_kit(@Param(name = "min_len", description = "Minimum length of strings to be generated")int minLen,
                              @Param(name = "max_len", description = "Maximum length of strings to be generated")int maxLen,
                              @Param(name = "wordlist", description = "Wordlist queue name")String dictQueueName,
                              @Param(name = "charset", description = "Charset")String charset ) {

        if (!Common.check_node_started("No node has started yet, cannot generate brute-force dictionaries."))
            return;

        BFGenerator  bfg = new BFGenerator(minLen,maxLen,charset,dictQueueName);

        App._bfGenerator = bfg;

        bfg.run();



    }

    @Command(name="bf-generator", description = "Generates brute-force dictionary", abbrev = "bf")
    public static void bf_kit(@Param(name = "min_len", description = "Minimum length of strings to be generated")int minLen,
                              @Param(name = "max_len", description = "Maximum length of strings to be generated")int maxLen) {
        bf_kit(minLen,maxLen,Settings.get(Settings.OPT_WL_DEFAULT_NAME),Settings.get(Settings.OPT_DEF_CHARSET));

    }
}
