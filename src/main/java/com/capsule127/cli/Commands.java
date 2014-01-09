package com.capsule127.cli;

import asg.cliche.Command;
import asg.cliche.Param;
import asg.cliche.ShellFactory;
import com.capsule127.NodeFactory;
import com.capsule127.Settings;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

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
                    " Password cracker for cloud... \n";

    @Command(name = "set", description = "Sets the parameter", abbrev = "s") // two,
    public void set(@Param(name = "Key=value pair", description = "Use list as parameter to list all") String __pair) {


        String[] vals = __pair.split("=");

        if (vals.length != 2) {

            ShellFactory.io.printlnErr("Invalid pair: "+__pair);
            return;
        }


        Settings.map.put(vals[0],vals[1]);

        ShellFactory.io.println(vals[0]+" = "+vals[1]);

    }


    @Command(name = "set", description = "Lists the parameters", abbrev = "s") // two,
    public void set() {

        for (String key : Settings.map.keySet()) {

            ShellFactory.io.println(key + "\t=\t" + Settings.map.get(key));

        }
    }

    @Command
    public void exit() {

        AnsiConsole.out.println("Quitting...");

        NodeFactory.node_shutdown_all();

        AnsiConsole.out.println(Util.Colorize(Ansi.Color.MAGENTA, "Bye!!!"));

        System.exit(0);

    }


    @Command(name = "load-settings", description = "Loads settings from file", abbrev = "ls")
    public void load_settings(@Param(name = "File name", description = "File name to load settings from")String _fileName) {

        Settings.load_from_file(_fileName);
    }

    @Command(name = "save-settings", description = "Saves settings to file", abbrev = "ss")
    public void save_settings(@Param(name = "File name", description = "File name to save settings from")String _fileName) {

        Settings.save_to_file(_fileName);
    }


}
