package com.capsule127;

import com.capsule127.wordlist.BruteForceWordlistBuilder;
import com.capsule127.wordlist.Wordlist;
import org.fusesource.jansi.AnsiConsole;

import java.util.concurrent.BlockingQueue;

/**
 * Created by marcus on 18/01/14.
 */
public class BFGenerator implements Runnable {

    int minLen;
    int maxLen;
    String charset;
    String wordlist;

    private Boolean isRunning = false;
    private Boolean stop = false;
    private Boolean suspended = false;

    public BFGenerator(int minLen, int maxLen, String charset, String wordlist) {
        this.minLen = minLen;
        this.maxLen = maxLen;
        this.charset = charset;
        this.wordlist = wordlist;
    }

    public void stop() {

        if (!isRunning)
            return;

        AnsiConsole.out().println("Cracker stopping.. please wait..");
        stop = true;
    }

    @Override
    public void run() {




        isRunning = true;

        // Adding any instance would be enough to spread accross to all
        NodeInstance ni = NodeInstanceFactory.instances.elementAt(0);

        BlockingQueue<Wordlist> queue = ni.hi.getQueue(wordlist + ".wlqueue");

        long totalNum = BruteForceWordlistBuilder.getTotalNumOfWords(charset.toCharArray(), minLen, maxLen);

        long wordCount = Integer.parseInt(Settings.get(Settings.OPT_WL_MAX_WORD_SIZE_PER_CH));

        int maxQueueSize = Integer.parseInt(Settings.get(Settings.OPT_WL_QUEUE_MAX_SIZE));



        for (long st=0;st<totalNum && !stop;) {

            while (queue.size() >= maxQueueSize) {

                if (stop) {
                    break;
                }


                try {

                    if (!suspended) {
                        AnsiConsole.out.println("SUSPENDED until wordlist queue gets drained.");
                        suspended = true;
                    }
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            suspended = false;

            Wordlist wl = new Wordlist("com.capsule127.wordlist.BruteForceWordlistBuilder",new Object[] {
                    charset,
                    new Long(st),
                    new Long(st+wordCount > totalNum ? totalNum : st+wordCount)
            });

            try {
                queue.put(wl);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            st += wordCount;
        }

        AnsiConsole.out().println("BruteForce generator stopped.");

        isRunning = false;
        stop = false;

        App._bfGenerator = null;

    }
}
