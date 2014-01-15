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

    @Command(name = "launch-cracker", description = "Launches cracker with specified hash type", abbrev = "launch")
    public static void launch_cracker(@Param(name = "type", description = "Hash type")String hashType,
                                      @Param(name = "wordlist", description = "Wordlist queue name")String wordlist) {

        if (NodeInstanceFactory.instances.size() == 0) {

            Logger.getLogger("Cracker").warning("No node has started yet, cannot start cracker.");

            return;

        }


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


        App._cracker = new Cracker(_iht,wordlist);

       // new Thread(App._cracker).start();

        App._cracker.run();



    }
}
