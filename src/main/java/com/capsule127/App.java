package com.capsule127;

/**
 * Created by marcus on 09/01/14.
 */

import asg.cliche.ShellFactory;
import com.capsule127.cli.Commands;
import com.capsule127.cli.Util;
import com.capsule127.hash.IHashTypeDescription;
import com.capsule127.hash.oracle.Oracle11Hash;
import com.capsule127.hash.oracle.OracleHash;
import com.capsule127.hazelcast.C127Logger;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.io.IOException;


public class App {


    public static Cracker _cracker = null;

    public static IHashTypeDescription[] supportedHashTypes = new IHashTypeDescription[] {
            new OracleHash(),
            new Oracle11Hash()
    };


    static int termCountLeft = 7;

    public static void main(String[] args) throws IOException {


        System.setProperty("hazelcast.logging.class", "com.capsule127.hazelcast.C127LoggerFactory");
        System.setProperty("hazelcast.elastic.memory.enabled","true");

        SignalHandler sh = new SignalHandler() {
            @Override
            public void handle(Signal signal) {

                if (_cracker != null) {

                    _cracker.stop();

                } else {

                    if (termCountLeft == 0) {
                        System.exit(0);
                    }

                    AnsiConsole.out.println("Use "+ Util.Colorize(Ansi.Color.CYAN,"exit")+ " command to log off otherwise "+Util.Colorize(Ansi.Color.YELLOW, ""+termCountLeft)+" term signal required to terminate");

                    termCountLeft--;
                }
            }
        };

        Signal.handle(new Signal("TERM"), sh);
        Signal.handle(new Signal("INT"),sh);
        Signal.handle(new Signal("ABRT"),sh);


        ShellFactory.createConsoleShell(Ansi.ansi().fg(Ansi.Color.CYAN).a("c127> ").reset().toString(),
                Ansi.ansi().fg(Ansi.Color.GREEN).a(Commands.logo).reset().toString(),
                new Commands(),
                new NodeInstanceFactory(),
                new C127Logger(),
                new HashImporter(),
                new DictImporter(),
                new CloudCommands(),
                new CrackerCommands()
                )
                .commandLoop(); // and three.


    }


}
