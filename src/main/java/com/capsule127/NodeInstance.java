package com.capsule127;

import com.capsule127.cli.Util;
import com.capsule127.hash.IHash;
import com.capsule127.hash.IHashTypeDescription;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * Created by marcus on 12/01/14.
 */
public class NodeInstance {


    public HazelcastInstance hi;
    public Vector<HashesMap> hashesMaps = new Vector<HashesMap>();


    public HashesMap getHashesMapFor(IHashTypeDescription hashType) {

        for (HashesMap hm : hashesMaps) {

            if (hm.getHashType().name().equals(hashType.name())) {
                return hm;
            }
        }

        HashesMap hm = new HashesMap(hashType,hi,LocalStorageManager.storageConnection);

        hashesMaps.add(hm);

        return hm;

    }

    private MessageListener jackPotListener = null;

    public void registerForJackpots() {


        if (jackPotListener == null)
            jackPotListener = new JackPotListener();

        hi.getTopic("HASH.FOUND").addMessageListener(jackPotListener);

    }

    public void publishJackpot(IHash hash,String password) {


        String line = String.format("%s : %s$%s#%s", password, hash.hash_type().abbrev(), hash.user(),
                hash.salt() != null ? hash.hash() + hash.salt() : hash.hash());

        AnsiConsole.out.println(Util.Colorize(Ansi.Color.MAGENTA,"LOCAL JACKPOT!!! ")+" "+line);

        hi.getTopic("HASH.FOUND").publish(line);

    }




    class JackPotListener implements MessageListener<String> {


        private static final String fName = "capsule127.jackpot";

        @Override
        public synchronized void onMessage(Message<String> stringMessage) {


            try {

                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

                FileWriter fw = new FileWriter(fName,true);
                BufferedWriter bw = new BufferedWriter(fw);


                String line = String.format("%s : %s : %s\n", sdf.format(new Date(stringMessage.getPublishTime())), stringMessage.getPublishingMember().getUuid(), stringMessage.getMessageObject());

                AnsiConsole.out.println(Util.Colorize(Ansi.Color.MAGENTA,"REMOTE JACKPOT!!! ")+" "+line);

                bw.write(line);

                bw.close();
                fw.close();


            } catch (IOException e) {

                Logger.getAnonymousLogger().warning("Cannot dump found pass to file "+fName+" : "+e.getMessage());

                e.printStackTrace();
            }


        }
    }
}
