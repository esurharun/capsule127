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
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Created by marcus on 13/01/14.
 */
public class Cracker implements Runnable {


    private static Logger logger() {
        return Logger.getLogger("Cracker");
    }


    private BlockingQueue<Runnable> bq;// = new ArrayBlockingQueue<Runnable>(16);

    private int threadSize;

    private Boolean isRunning = false;

    private Boolean stop = false;

    private Boolean stopThreads = false;

    private Boolean suspended = false;

    private IHashTypeDescription hashTypeDescription;
    private String dictQueueName;

    public Cracker(IHashTypeDescription hashTypeDescription, String dictQueueName, int threadSize) {
        this.hashTypeDescription = hashTypeDescription;
        this.dictQueueName = dictQueueName;
        this.threadSize = threadSize;

        bq = new ArrayBlockingQueue<Runnable>(threadSize*2);
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

        long diff = TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - stTime);

        if (diff == 0)
            diff = 1;


        long networkCount = (networkTryCount - lastNetworkTryCount);

        long networkPower = networkCount == 0 ? 0 :
                networkCount / (TimeUnit.NANOSECONDS.toSeconds(networkTryCountRetrieveTime)
                        - TimeUnit.NANOSECONDS.toSeconds(lastNetworkTryCountRetrieveTime));

        String mess = String.format(format, (double) (tryCount / diff), hashCount, wlSize, tryCount / 1000, lastTriedPass,
                networkPower == 0 ? "[Unknown]" : "" + networkPower);

        AnsiConsole.out.println(Util.Colorize(Ansi.Color.YELLOW, "CRACKER: ") + mess);
    }

    @Override
    public void run() {

        isRunning = true;
        stopThreads = false;

        long timeBeforeLastStatus = 0;
        try {

            for (int i = 0; i < threadSize; i++) {
                new Thread(new WorkerThread()).start();
            }

            // Adding any instance would be enough to spread accross to all
            NodeInstance ni = NodeInstanceFactory.instances.elementAt(0);

            final HashesMap hm = ni.getHashesMapFor(hashTypeDescription);

            Queue<Wordlist> queue = ni.hi.getQueue(dictQueueName + ".wlqueue");

            final List<IHash>[] hashes = new List[]{hm.getHashes()};

            hashCount = hashes[0].size();

            stTime = System.nanoTime();

            while (!stop && hashes[0].size() > 0) {

                Wordlist wl = queue.poll();

                if (wl != null) {

                    wlSize = queue.size();

                    if (suspended) {

                        AnsiConsole.out.println("Resuming...");

                        suspended = false;

                    }

                    Vector<String> passws = wl.getVector();


                    for (String pass : passws) {

                        final String pass_to_go = pass;

                        bq.put(new Runnable() {
                            @Override
                            public void run() {
                                Boolean found = false;

                                String generated = null;

                                for (IHash hash : hashes[0]) {

                                    tryCount++;

                                    IHashGenerator ihg = hash.hash_type().generators()[0];

                                    if (hash.hash_type().requiresUserOrSaltPerGeneration() || generated == null)
                                        try {
                                            generated = ihg.generate(hash.user(), pass_to_go, hash.salt());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }


                                    lastTriedPass = pass_to_go;

                                    if (generated.equals(hash.hash())) {

                                        hm.markHashAsFound(hash, pass_to_go);

                                        found = true;

                                    }

                                }

                                if (found) {
                                    hashes[0] = hm.getHashes();
                                    hashCount = hashes[0].size();
                                }
                            }
                        });

                         


                    }


                    lastNetworkTryCount = networkTryCount;
                    networkTryCount = ni.hi.getAtomicLong("tryCount").addAndGet(tryCount - lastNotifiedTryCount);
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

            if (hashes[0].size() == 0) {
                Cracker.logger().info("No hash left on " + hashTypeDescription.name() + " hashes list");
            }

        } catch (Exception ex) {

            Cracker.logger().severe("Cracker stopped due to an exception: " + ex.getMessage());

            ex.printStackTrace();

        }

        while (bq.size() > 0) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        stopThreads = true;

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        AnsiConsole.out().println("Cracker stopped.");

        stop = true;
        isRunning = false;

        App._cracker = null;


    }

    private class WorkerThread implements Runnable {

        @Override
        public void run() {


            while (!stopThreads) {
                try {
                    bq.take().run();
                } catch (InterruptedException e) {
                    //e.printStackTrace();
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }

        }
    }
}
