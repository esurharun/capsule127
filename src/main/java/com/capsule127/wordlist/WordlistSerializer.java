package com.capsule127.wordlist;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;
import net.jpountz.lz4.LZ4BlockInputStream;
import net.jpountz.lz4.LZ4BlockOutputStream;
import net.jpountz.lz4.LZ4Factory;

import java.io.*;
import java.util.Vector;

/**
 * Created by marcus on 12/01/14.
 */
public class WordlistSerializer implements StreamSerializer<Wordlist> {

    class IDynamicWordlistBuilderParams implements Serializable {

        private String className;
        private Object[] params;

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public Object[] getParams() {
            return params;
        }

        public void setParams(Object[] params) {
            this.params = params;
        }
    }

    @Override
    public void write(ObjectDataOutput objectDataOutput, Wordlist wordlist) throws IOException {



        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        LZ4BlockOutputStream blockOs = new LZ4BlockOutputStream(bos,256, LZ4Factory.fastestJavaInstance().fastCompressor());

        if (wordlist.getType() == Wordlist.TYPE_STATIC)
            new ObjectOutputStream(blockOs).writeObject(wordlist.getVector());
        else if (wordlist.getType() == Wordlist.TYPE_DYNAMC)
        {
            IDynamicWordlistBuilderParams p = new IDynamicWordlistBuilderParams();
            p.setClassName(wordlist.getDynamicWordListBuilderClassName());
            p.setParams(wordlist.getDynamicWordListBuilderParams());

            new ObjectOutputStream(blockOs).writeObject(p);

        }


        objectDataOutput.write(bos.toByteArray());



    }

    @Override
    public Wordlist read(ObjectDataInput objectDataInput) throws IOException {

        final InputStream is = (InputStream) objectDataInput;
        LZ4BlockInputStream blockIs = new LZ4BlockInputStream(is,LZ4Factory.fastestJavaInstance().fastDecompressor());


        try {

            Wordlist wl = null;

            Object o = new ObjectInputStream(blockIs).readObject();

            // STATIC TYPE
            if (o.getClass().getName().equals(Vector.class.getName())) {
                Vector<String> words =  (Vector<String>) o;
                wl = new Wordlist(words);

            } else if (o.getClass().getName().equals(IDynamicWordlistBuilderParams.class.getName())) {

                IDynamicWordlistBuilderParams idwlbp = (IDynamicWordlistBuilderParams) o;

                wl = new Wordlist(idwlbp.getClassName(), idwlbp.getParams());


            }


            return wl;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();


        }

        return new Wordlist();
    }

    @Override
    public int getTypeId() {
        return 10;
    }

    @Override
    public void destroy() {

    }
}
