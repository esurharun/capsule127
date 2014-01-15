package com.capsule127;

import com.capsule127.cli.Util;
import com.capsule127.hash.IHash;
import com.capsule127.hash.IHashGenerator;
import com.capsule127.hash.IHashTypeDescription;
import com.capsule127.wordlist.Wordlist;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import java.util.List;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Created by marcus on 13/01/14.
 */
public class Cracker implements Runnable {


    private static Logger logger() {
        return Logger.getLogger("Cracker");
    }

    /// EOF STATIC METHODS


    private Boolean isRunning = false;

    private Boolean stop = false;

    private Boolean suspended = false;

    private IHashTypeDescription hashTypeDescription;
    private String dictQueueName;

    public Cracker(IHashTypeDescription hashTypeDescription, String dictQueueName) {
        this.hashTypeDescription = hashTypeDescription;
        this.dictQueueName = dictQueueName;
    }

    public void stop() {

        if (!isRunning)
            return;

        AnsiConsole.out().println("Cracker stopping.. please wait..");
        stop = true;
    }


    long tryCount = 0;
    long lastNotifiedTryCount = 0;
    long networkTryCount = 0;
    long lastNetworkTryCount = 0;
    long networkTryCountRetrieveTime = 0;
    long lastNetworkTryCountRetrieveTime = 0;

    long stTime = 0;

    long hashCount = 0;
    long wlSize = 0;

    String lastTriedPass = "";

    public void printStatus() {


        String format = "Running on %s p/s, hash count = %s, wordlist chunks = %s, tried pass count= %sK, curr pass = %s \n" +
                "Network power %s p/s";

        long diff = TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - stTime) ;

        if ( diff == 0)
            diff =1;

        long networkCount = (networkTryCount-lastNetworkTryCount);

        long networkPower = networkCount == 0 ? 0 :
                TimeUnit.NANOSECONDS.toSeconds(networkTryCountRetrieveTime-lastNetworkTryCountRetrieveTime)/networkCount;

        String mess = String.format(format, (double) ( tryCount  / diff), hashCount, wlSize, tryCount/1000, lastTriedPass,
                networkPower);

        AnsiConsole.out.println(Util.Colorize(Ansi.Color.YELLOW, "CRACKER: ") + mess);
    }

    @Override
    public void run() {

        isRunning = true;

        long timeBeforeLastStatus = 0;
        try {


            // Adding any instance would be enough to spread accross to all
            NodeInstance ni = NodeInstanceFactory.instances.elementAt(0);

            HashesMap hm = ni.getHashesMapFor(hashTypeDescription);

            Queue<Wordlist> queue = ni.hi.getQueue(dictQueueName + ".wlqueue");

            List<IHash> hashes = hm.getHashes();

            hashCount = hashes.size();


            stTime = System.nanoTime();

            while (!stop && hashes.size() > 0) {

                Wordlist wl = queue.poll();

                if (wl != null) {

                    wlSize = queue.size();

                    if (suspended) {

                        AnsiConsole.out.println("Resuming...");

                        suspended = false;

                    }

                    Vector<String> passws = wl.getVector();


                    for (String pass : passws) {

                        Boolean found = false;

                        String generated = null;

                        for (IHash hash : hashes) {

                            tryCount++;

                            IHashGenerator ihg = hash.hash_type().generators()[0];

                            if (hash.hash_type().requiresUserOrSaltPerGeneration() || generated == null)
                                generated = ihg.generate(hash.user(), pass, hash.salt());


                            lastTriedPass = pass;

                            if (generated.equals(hash.hash())) {

                                hm.markHashAsFound(hash, pass);

                                found = true;

                            }

                        }

                        if (found) {
                            hashes = hm.getHashes();
                            hashCount = hashes.size();
                        }

                    }

                    lastNetworkTryCount = networkTryCount;
                    networkTryCount = ni.hi.getAtomicLong("tryCount").addAndGet(tryCount-lastNotifiedTryCount);
                    lastNetworkTryCountRetrieveTime = networkTryCountRetrieveTime;
                    networkTryCountRetrieveTime = System.nanoTime();
                    lastNotifiedTryCount = tryCount;


                    long diff = System.nanoTime() - timeBeforeLastStatus;

                    diff = TimeUnit.NANOSECONDS.toSeconds(diff);

                    if (diff > 60) {

                        printStatus();
                        timeBeforeLastStatus = System.nanoTime();
                    }

                } else {


                    if (!suspended) {

                        AnsiConsole.out().println("Active wordlist finished... SUSPENDING");

                        printStatus();

                        suspended = true;

                        Thread.sleep(10000);
                    }
                }


            }

            if (hashes.size() == 0) {
                Cracker.logger().info("No hash left on " + hashTypeDescription.name() + " hashes list");
            }

        } catch (Exception ex) {

            Cracker.logger().severe("Cracker stopped due to an exception: " + ex.getMessage());

            ex.printStackTrace();

        }

        AnsiConsole.out().println("Cracker stopped.");

        stop = true;
        isRunning = false;

        App._cracker = null;


    }
}
