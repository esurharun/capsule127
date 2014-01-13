package com.capsule127;

import asg.cliche.ShellFactory;
import gnu.trove.map.hash.THashMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * Created by marcus on 09/01/14.
 */
public class Settings {

    private static final THashMap<String, String> map = new THashMap<String, String>();


    public static final String OPT_WG = "WORKGROUP";
    public static final String OPT_WG_PASS = "WORKGROUP_PASS";
    public static final String OPT_EM_SIZE = "ELASTIC_MEMORY_SIZE";
    public static final String OPT_USE_LOCAL_STORAGE = "USE_LOCAL_STORAGE";
    public static final String OPT_WL_MAX_CH_SIZE = "WORDLIST_MAX_CHUNK_SIZE";

    private static final Vector<OptChangeListener> optChangeListeners = new Vector<OptChangeListener>();


    private static final String[] const_keys = new String[]{
            OPT_WG, OPT_WG_PASS, OPT_EM_SIZE, OPT_USE_LOCAL_STORAGE, OPT_WL_MAX_CH_SIZE
    };
    private static final String[] const_vals = new String[]{
            "C127", "C127", "128", "false", "2560"
    };

    private static void fill_empty_ones() {
        int c = 0;
        for (String k : const_keys) {
            setIfAbsent(k, const_vals[c]);
            c++;
        }

    }

    static {
        fill_empty_ones();
    }


    static {

        optChangeListeners.add(integerCheckListener(OPT_WL_MAX_CH_SIZE));

        optChangeListeners.add(new OptChangeListener() {


            @Override
            public String key() {
                return OPT_EM_SIZE;
            }

            @Override
            public boolean beforeChange(String value) {

                try {
                    Integer.parseInt(value);

                    System.setProperty("hazelcast.elastic.memory.total.size", value);


                    return true;

                } catch (NumberFormatException ex) {

                    Logger.getAnonymousLogger().warning("Invalid memory size value: " + value);

                }

                return false;

            }
        });


        optChangeListeners.add(new OptChangeListener() {
            @Override
            public String key() {
                return OPT_USE_LOCAL_STORAGE;
            }

            @Override
            public boolean beforeChange(String newValue) {

                if (!(newValue.equalsIgnoreCase("true") || newValue.equalsIgnoreCase("false"))) {

                    Logger.getAnonymousLogger().warning("Invalid value: "+newValue);
                    return false;
                }

                boolean b = Boolean.parseBoolean(newValue);

                if (b) {
                    LocalStorageManager.open();
                } else
                    LocalStorageManager.close();

                return true;
            }
        });


    }


    public static void set(String key, String value) {

        boolean put_ok = true;
        for (OptChangeListener ocl : optChangeListeners) {
            if (ocl.key().equals(key)) {
                put_ok = ocl.beforeChange(value);
            }
        }

        if (put_ok) {
            map.put(key, value);

            //ShellFactory.io.println(key+" = "+value);

        }
    }

    public static void setIfAbsent(String key, String value) {
        if (!map.containsKey(key))
            set(key, value);
    }

    public static String get(String key) {
        return map.get(key);
    }

    public static Set<String> keySet() {
        return map.keySet();
    }


    public static void save_to_file(String fileName) {

        File f = new File(fileName);

        Properties props = new Properties();

        for (String key : map.keySet()) {
            props.put(key, map.get(key));
        }

        try {
            props.store(new FileOutputStream(f), null);

            ShellFactory.io.println("Settings saved to " + fileName);
        } catch (IOException e) {

            ShellFactory.io.outputException(e);
            //e.printStackTrace();
        }

    }


    public static void load_from_file(String fileName) {

        File f = new File(fileName);

        Properties pro = new Properties();

        try {
            pro.load(new FileInputStream(f));

            map.clear();


            for (String key : pro.stringPropertyNames()) {

                set(key, pro.getProperty(key));
            }

            fill_empty_ones();


            ShellFactory.io.println("Settings loaded from " + fileName);

        } catch (IOException e) {
            ShellFactory.io.outputException(e);
        }

    }


    private interface OptChangeListener {

        public String key();

        public boolean beforeChange(String newValue);

    }


    private static OptChangeListener integerCheckListener(String key) {

        final String _key = key;

        return new OptChangeListener() {
            @Override
            public String key() {
                return _key;
            }

            @Override
            public boolean beforeChange(String value) {


                try {
                    Integer.parseInt(value);


                    return true;

                } catch (NumberFormatException ex) {

                    Logger.getAnonymousLogger().warning("Invalid value: " + value);

                }

                return false;        }
        };
    }



}
