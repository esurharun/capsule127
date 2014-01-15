package com.capsule127.wordlist;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Vector;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * Created by marcus on 12/01/14.
 */
public class WordlistSerializer implements StreamSerializer<Wordlist> {


//    public byte[] compress(byte[] data) throws IOException {
//
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        GZIPOutputStream gos = new GZIPOutputStream(bos);
//
//        gos.write(data);
//
//        gos.close();
//
//        bos.close();
//
//        return bos.toByteArray();
//
//    }
//
//    public byte[] decompress(byte[] data) throws IOException {
//
//        ByteArrayInputStream bis = new ByteArrayInputStream(data);
//        GZIPInputStream gis = new GZIPInputStream(bis);
//
//        byte[] unc = new byte[gis.available()];
//
//        gis.read(unc);
//
//
//        return unc;
//
//    }


    public static byte[] compress(byte[] data) throws IOException {
        Deflater deflater = new Deflater();
        deflater.setInput(data);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);

        deflater.finish();
        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer); // returns the generated code... index
            outputStream.write(buffer, 0, count);
        }
        outputStream.close();
        byte[] output = outputStream.toByteArray();

        //System.out.println("Original: " + data.length / 1024 + " Kb");
        //System.out.println("Compressed: " + output.length / 1024 + " Kb");
        return output;
    }

    public static byte[] decompress(byte[] data) throws IOException, DataFormatException {
        Inflater inflater = new Inflater();
        inflater.setInput(data);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        while (!inflater.finished()) {
            int count = inflater.inflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        outputStream.close();
        byte[] output = outputStream.toByteArray();

        //System.out.println("Original: " + data.length);
        //System.out.println("Compressed: " + output.length);
        return output;
    }

    public byte[] wordlistToByteArr(Wordlist wordlist) throws IOException {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        if (wordlist.getType() == Wordlist.TYPE_STATIC)
            new ObjectOutputStream(bos).writeObject(wordlist.getVector());
        else if (wordlist.getType() == Wordlist.TYPE_DYNAMC) {
            IDynamicWordlistBuilderParams p = new IDynamicWordlistBuilderParams();
            p.setClassName(wordlist.getDynamicWordListBuilderClassName());
            p.setParams(wordlist.getDynamicWordListBuilderParams());

            new ObjectOutputStream(bos).writeObject(p);

        }


        byte[] before_comp = bos.toByteArray();

        byte[] compressed = compress(before_comp);


        //System.out.println("Size bc: " + before_comp.length + " ac: " + compressed.length);

        return compressed;


    }

    public Wordlist byteArrToWordList(byte[] data) throws Exception {

        byte[] uncompressed = decompress(data);

        ByteArrayInputStream bis = new ByteArrayInputStream(uncompressed);


        ObjectInputStream ois = new ObjectInputStream(bis);

        Wordlist wl = null;

        Object o = ois.readObject();

        // STATIC TYPE
        if (o.getClass().getName().equals(Vector.class.getName())) {
            Vector<String> words = (Vector<String>) o;
            wl = new Wordlist(words);

        } else if (o.getClass().getName().equals(IDynamicWordlistBuilderParams.class.getName())) {

            IDynamicWordlistBuilderParams idwlbp = (IDynamicWordlistBuilderParams) o;

            wl = new Wordlist(idwlbp.getClassName(), idwlbp.getParams());


        }


        return wl;

    }

    @Override
    public void write(ObjectDataOutput objectDataOutput, Wordlist wordlist) throws IOException {


        objectDataOutput.write(wordlistToByteArr(wordlist));


    }

    @Override
    public Wordlist read(ObjectDataInput objectDataInput) throws IOException {



        final InputStream is = (InputStream) objectDataInput;

        byte[] read = new byte[is.available()];

        is.read(read);

        try {
            return byteArrToWordList(read);
        } catch (Exception e) {
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
