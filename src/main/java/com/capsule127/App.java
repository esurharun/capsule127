package com.capsule127;

/**
 * Created by marcus on 09/01/14.
 */

import asg.cliche.ShellFactory;
import asg.cliche.example.HelloWorld;
import com.capsule127.cli.Commands;
import jline.console.ConsoleReader;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import java.io.IOException;
import java.io.PrintWriter;


public class App {




    public static void main(String[] args) throws IOException {



        ShellFactory.createConsoleShell(Ansi.ansi().fg(Ansi.Color.CYAN).a("c127> ").reset().toString(), Ansi.ansi().fg(Ansi.Color.GREEN).a(Commands.logo).reset().toString(), new Commands())
                .commandLoop(); // and three.




//        ConsoleReader reader = new ConsoleReader();
//
//
//
//
//
//        reader.setPrompt(Ansi.ansi().fg(Ansi.Color.CYAN).a("c127>").reset().toString());
//
//        String line;
//
//        PrintWriter out = new PrintWriter(reader.getOutput());
//
//        while ((line = reader.readLine()) != null) {
//
//
//            out.flush();
//
//            if (line.equalsIgnoreCase("quit")) {
//                break;
//            }
//
//        }



    }


}
