package com.capsule127;

import com.capsule127.hash.IHashTypeDescription;
import org.apache.commons.lang3.RandomStringUtils;
import org.fusesource.jansi.AnsiConsole;

/**
 * Created by marcus on 17/01/14.
 */
public class Common {


    public static boolean check_node_started(String printIfNot) {
        if (NodeInstanceFactory.instances.size() == 0) {

            AnsiConsole.out.println(printIfNot);

            return false;

        }

        return true;
    }

    public static IHashTypeDescription getHashTypebyAbbrev(String abbrev
            , boolean warn) {
        IHashTypeDescription _iht =null;

        for (IHashTypeDescription iht : App.supportedHashTypes) {

            if (iht.abbrev().equalsIgnoreCase(abbrev)) {
                _iht = iht;
                break;
            }
        }

        if (_iht == null && warn)
            AnsiConsole.out.println(abbrev+" is not a valid hash type");

        return _iht;
    }

    public static long random(int max) {

        return (System.nanoTime() % max);
    }

    public static String generateRandomPassword(int maxLength) {

        String charset = Settings.get(Settings.OPT_DEF_CHARSET);

        return RandomStringUtils.random((int)random(maxLength),charset);

    }
}
