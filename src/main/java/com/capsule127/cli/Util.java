package com.capsule127.cli;

import org.fusesource.jansi.Ansi;

import java.util.regex.Pattern;

/**
 * Created by marcus on 09/01/14.
 */
public class Util {



    public static String Colorize(Ansi.Color _color,String _text) {
        return Ansi.ansi().fg(_color).a(_text).reset().toString();
    }


    public static Boolean validateIpAndPort(String _desc) {

        String[] descs = _desc.split(":");

        if (descs.length != 2)
            return false;

        return validateIp(descs[0]) && validePortNum(descs[1]);


    }

    public static Boolean validateIp(String _ip) {
        String IPADDRESS_PATTERN =
                "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";


        return Pattern.compile(IPADDRESS_PATTERN).matcher(_ip).matches();
    }

    public static Boolean validePortNum(String _pNum) {


        try {
            int val = Integer.parseInt(_pNum);

            return val < 65535 && val > 0;
        } catch (Exception e) {
            return false;
        }
    }

}
