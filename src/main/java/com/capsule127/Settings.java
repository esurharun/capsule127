package com.capsule127;

import asg.cliche.ShellFactory;
import gnu.trove.map.hash.THashMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by marcus on 09/01/14.
 */
public class Settings {

    public static final THashMap<String, String> map = new THashMap<String, String>();

    public static void save_to_file(String fileName) {

        File f = new File(fileName);

        Properties props = new Properties();

        for (String key :  map.keySet()) {
            props.put(key,map.get(key));
        }

        try {
            props.store(new FileOutputStream(f),null);

            ShellFactory.io.println("Settings saved to "+fileName);
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

                    map.put(key,pro.getProperty(key));
            }

            ShellFactory.io.println("Settings loaded from "+fileName);

        } catch (IOException e) {
            ShellFactory.io.outputException(e);
        }



    }

}
