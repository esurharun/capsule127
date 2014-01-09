package com.capsule127.cli;

import asg.cliche.Command;
import asg.cliche.Param;
import asg.cliche.ShellFactory;
import com.capsule127.Settings;

import java.util.StringTokenizer;

/**
 * Created by marcus on 09/01/14.
 */
public class Commands {


    public static final String logo =
            "                                       .__            ____ ________  _________ \n"+
            "  ____  _____   ______    ______ __ __ |  |    ____  /_   |\\_____  \\ \\______  \\\n"+
            "_/ ___\\ \\__  \\  \\____ \\  /  ___/|  |  \\|  |  _/ __ \\  |   | /  ____/     /    /\n"+
            "\\  \\___  / __ \\_|  |_> > \\___ \\ |  |  /|  |__\\  ___/  |   |/       \\    /    / \n"+
            " \\___  >(____  /|   __/ /____  >|____/ |____/ \\___  > |___|\\_______ \\  /____/  \n"+
            "     \\/      \\/ |__|         \\/                   \\/               \\/          \n\n"+
            " Password cracker for clouds... \n";

    @Command(name = "set", description = "Sets the parameter", abbrev = "s") // two,
    public void set(@Param(name = "Key-value pair with = sign",description = "Use list as parameter to list all" ) String __pair) {


        if (__pair.trim().equals("list")) {

            for (String key : Settings.map.keySet()) {

                ShellFactory.io.println(key+"\t=\t"+Settings.map.get(key));

            }

            return;
        }

        String[] vals = __pair.split("=");

        if (vals.length != 2) {

            ShellFactory.io.printlnErr("Invalid pair: "+__pair);
            return;
        }


        Settings.map.put(vals[0],vals[1]);

        ShellFactory.io.println(vals[0]+" = "+vals[1]);

    }



    @Command(name = "load_settings", description = "Loads settings from file", abbrev = "lose")
    public void load_settings(@Param(name = "File name", description = "File name to load settings from")String _fileName) {

        Settings.load_from_file(_fileName);
    }

    @Command(name = "save_settings", description = "Saves settings to file", abbrev = "sase")
    public void save_settings(@Param(name = "File name", description = "File name to save settings from")String _fileName) {

        Settings.save_to_file(_fileName);
    }
}
