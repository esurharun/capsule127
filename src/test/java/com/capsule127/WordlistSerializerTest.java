package com.capsule127;

import com.capsule127.wordlist.Wordlist;
import com.capsule127.wordlist.WordlistSerializer;
import com.hazelcast.nio.serialization.ObjectDataOutputStream;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by marcus on 15/01/14.
 */
public class WordlistSerializerTest {


    @Test
    public void testWriteRead() throws Exception {

        Wordlist wordlist = new Wordlist();

        wordlist.getVector().add("aaaa");
        wordlist.getVector().add("bbbb");
        wordlist.getVector().add("cccc");

        WordlistSerializer wls = new WordlistSerializer();

        byte[] data = wls.wordlistToByteArr(wordlist);

        Wordlist wordlist2 = wls.byteArrToWordList(data);


        Assert.assertEquals(wordlist.getVector(),wordlist2.getVector());

        Wordlist bf_wordlist = new Wordlist("com.capsule127.wordlist.BruteForceWordlistBuilder",new Object[] {
            new String("abcdefgh"),
                new Long(0),
                new Long(1000)
        });

        int before_serialization_wc = bf_wordlist.getVector().size();

        System.out.println("Word count for bf: "+before_serialization_wc);

        data = wls.wordlistToByteArr(bf_wordlist);

        Wordlist bf_wordlist2 = wls.byteArrToWordList(data);

        int after_deserialization_wc = bf_wordlist2.getVector().size();

        System.out.println("After deserialization word count for bf: "+after_deserialization_wc);

        Assert.assertEquals(bf_wordlist.getVector(),bf_wordlist2.getVector());

    }
}
