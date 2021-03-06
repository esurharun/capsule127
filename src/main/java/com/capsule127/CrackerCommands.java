package com.capsule127;

import asg.cliche.Command;
import asg.cliche.Param;
import com.capsule127.hash.IHashTypeDescription;

import java.util.logging.Logger;

/**
 * Created by marcus on 15/01/14.
 */
public class CrackerCommands {

    /// STATIC METHODS

    @Command(name = "launch-cracker", description = "Launches cracker with specified hash type", abbrev = "lc")
    public static void launch_cracker(@Param(name = "type", description = "Hash type")String hashType,
                                      @Param(name = "wordlist", description = "Wordlist queue name")String wordlist) {


        if (!Common.check_node_started("No node has started yet, cannot start cracker."))
            return;



        IHashTypeDescription _iht =null;

        for (IHashTypeDescription iht : App.supportedHashTypes) {

            if (iht.abbrev().equalsIgnoreCase(hashType)) {
                _iht = iht;
                break;
            }
        }

        if (_iht == null) {

            Logger.getLogger("Cracker").warning(hashType+" is not a valid hash type name.");

            return;

        }

        if (App._cracker != null) {

            Logger.getLogger("Cracker").warning("A cracker instance is already running. ");

            return;

        }


        App._cracker = new Cracker(_iht,wordlist, Integer.parseInt(Settings.get(Settings.OPT_CR_THREAD_COUNT)));

       // new Thread(App._cracker).start();

        App._cracker.run();



    }

    @Command(name = "launch-cracker", description = "Launches cracker with specified hash type", abbrev = "lc")
    public static void launch_cracker(@Param(name = "type", description = "Hash type")String hashType) {

        launch_cracker(hashType, Settings.get(Settings.OPT_WL_DEFAULT_NAME));



    }
}
