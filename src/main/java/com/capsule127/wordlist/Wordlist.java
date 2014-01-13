package com.capsule127.wordlist;

import com.capsule127.Settings;

import java.io.*;
import java.util.Queue;
import java.util.Vector;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

/**
 * Created by marcus on 12/01/14.
 */
public class Wordlist {


    private Vector<String> list = null;

    public static int TYPE_STATIC = 0;
    public static int TYPE_DYNAMC = 1;

    private int type;

    private String dynamicWordListBuilderClassName;
    private Object[] dynamicWordListBuilderParams;

    public Wordlist(Vector<String> list) {
        this.list = list;

        type = TYPE_STATIC;
    }

    public Wordlist() {
        this.list = new Vector<String>();

        type = TYPE_STATIC;
    }

    public Wordlist(String _dynamicWordListBuilderClassName, Object[] params) {

        this.dynamicWordListBuilderClassName = _dynamicWordListBuilderClassName;
        this.dynamicWordListBuilderParams = params;

        type = TYPE_DYNAMC;
    }

    public int getType() {
        return type;
    }

    public Vector<String> getVector() {

        if (type == TYPE_STATIC) {
            return list;
        } else if (type == TYPE_DYNAMC) {

            try {
                Class cl = Class.forName(dynamicWordListBuilderClassName);

                IDynamicWordlistBuilder idwl = (IDynamicWordlistBuilder) cl.newInstance();


                return idwl.getList(dynamicWordListBuilderParams);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return new Vector<String>(0);
    }

    public String getDynamicWordListBuilderClassName() {
        return dynamicWordListBuilderClassName;
    }

    public void setDynamicWordListBuilderClassName(String dynamicWordListBuilderClassName) {
        this.dynamicWordListBuilderClassName = dynamicWordListBuilderClassName;
    }

    public Object[] getDynamicWordListBuilderParams() {
        return dynamicWordListBuilderParams;
    }

    public void setDynamicWordListBuilderParams(Object[] dynamicWordListBuilderParams) {
        this.dynamicWordListBuilderParams = dynamicWordListBuilderParams;
    }

    private static Logger logger() {
        return Logger.getLogger("Wordlist");
    }


    public static void load_from_file(String _filePath,Queue<Wordlist> q) {

        Wordlist ret = new Wordlist();

        File file = new File(_filePath);

        try {
            BufferedReader br = null;

            if (_filePath.endsWith("gz")) {

                GZIPInputStream gzis = new GZIPInputStream(new FileInputStream(file));

                br = new BufferedReader(new InputStreamReader(gzis));


            } else {
                br = new BufferedReader(new FileReader(file));
            }

            Vector<String> list = new Vector<String>();

            int limit = Integer.parseInt(Settings.get(Settings.OPT_WL_MAX_CH_SIZE));

            int c = 0;
            String line = null;
            while ((line = br.readLine()) != null) {
                list.add(line);
                c++;

                if (c % limit == 0) {

                    Wordlist wl = new Wordlist(list);

                    q.add(wl);

                    list.removeAllElements();
                }
            }

            Wordlist wl = new Wordlist(list);

            q.add(wl);

            logger().info(c+" passes added");

            br.close();


        } catch (IOException e) {
            Logger.getAnonymousLogger().warning(_filePath+ ": " + e.getMessage());

        }

    }



}
