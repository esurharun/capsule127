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

    long stTime = 0;

    String lastTriedPass = "";

    public void printStatus() {


        String format = "Running on %s p/s , tried %s so far. Last tried pass is %s";

        long diff = TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - stTime) ;

        String mess = String.format(format, (double) ( tryCount / diff), tryCount, lastTriedPass);

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


            stTime = System.nanoTime();

            while (!stop && hashes.size() > 0) {

                Wordlist wl = queue.poll();

                if (wl != null) {
                    Vector<String> passws = wl.getVector();


                    for (String pass : passws) {

                        Boolean found = false;

                        for (IHash hash : hashes) {

                            tryCount++;

                            IHashGenerator ihg = hash.hash_type().generators()[0];

                            String generated = ihg.generate(hash.user(), pass, hash.salt());

                            lastTriedPass = pass;

                            if (generated.equals(hash.hash())) {

                                hm.markHashAsFound(hash, pass);

                                found = true;

                            }

                        }

                        if (found)
                            hashes = hm.getHashes();

                    }

                    ni.hi.getAtomicLong("tryCount").addAndGet(tryCount);


                    long diff = System.nanoTime() - timeBeforeLastStatus;

                    diff = TimeUnit.NANOSECONDS.toSeconds(diff);

                    if (diff > 60) {

                        printStatus();
                        timeBeforeLastStatus = System.nanoTime();
                    }

                } else {


                    AnsiConsole.out().println("Active wordlist finished... waiting for new passes to try.");

                    printStatus();

                    Thread.sleep(10000);
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
