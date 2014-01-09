package com.capsule127.cli;

import org.fusesource.jansi.Ansi;

/**
 * Created by marcus on 09/01/14.
 */
public class Util {



    public static String Colorize(Ansi.Color _color,String _text) {
        return Ansi.ansi().fg(_color).a(_text).reset().toString();
    }

}
