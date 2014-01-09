/*
 * This file is part of the Cliche project, licensed under MIT License.
 * See LICENSE.txt file in root folder of Cliche sources.
 */

package asg.cliche;

import asg.cliche.util.Strings;
import jline.console.ConsoleReader;
import jline.console.completer.Completer;
import jline.console.completer.StringsCompleter;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import java.io.*;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

/**
 * Console IO subsystem.
 * This is also one of special command handlers and is responsible
 * for logging (duplicating output) and execution of scripts.
 *
 * @author ASG
 */
public class ConsoleIO implements Input, Output, ShellManageable {


    ConsoleReader reader;

    private PrintWriter out;

    public ConsoleIO() {



        try {


            AnsiConsole.systemInstall();

            reader = new ConsoleReader();
            out = new PrintWriter(reader.getOutput());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void updateWithShell(Shell shell) {

        Vector<String> commands = new Vector<String>();

        for (ShellCommand sc : shell.getCommandTable().getCommandTable()) {
            commands.add(sc.getName());
        }

        reader.addCompleter(new StringsCompleter(commands));
    }

    public String readCommand(List<String> path) {
        try {
            String prompt = Strings.joinStrings(path, false, '/');
            switch (inputState) {
                case USER: 
                    return readUsersCommand(prompt);
                case SCRIPT:
                    String command = readCommandFromScript(prompt);
                    if (command != null) {
                        return command;
                    } else {
                        closeScript();
                        return readUsersCommand(prompt);
                    }
            }
            return readUsersCommand(prompt);
        } catch (IOException ex) {
            throw new Error(ex);
        }
    }

    private static final String USER_PROMPT_SUFFIX = "> ";
    private static final String FILE_PROMPT_SUFFIX = "$ ";

    private static enum InputState { USER, SCRIPT }

    private InputState inputState = InputState.USER;

    private String readUsersCommand(String prompt) throws IOException {


        String command = reader.readLine(prompt);
        if (log != null) {
            log.println(command);
        }
        return command;
    }

    private BufferedReader scriptReader = null;

    private String readCommandFromScript(String prompt) throws IOException {
        String command = scriptReader.readLine();

        return command;
    }

    private void closeScript() throws IOException {
        if (scriptReader != null) {
            scriptReader.close();
            scriptReader = null;
        }
        inputState = InputState.USER;
    }

//    @Command(description="Reads commands from file")
//    public void runScript(
//            @Param(name="filename", description="Full file name of the script")
//                String filename
//            ) throws FileNotFoundException {
//
//        scriptReader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
//        inputState = InputState.SCRIPT;
//    }


    public void outputHeader(String text) {
        if (text != null) {
            println(text);
        }
    }

    public void output(Object obj, OutputConversionEngine oce) {
        if (obj == null) {
            return;
        } else {
            obj = oce.convertOutput(obj);
        }

        if (obj.getClass().isArray()) {
            int length = Array.getLength(obj);
            for (int i = 0; i < length; i++) {
                output(Array.get(obj, i), 0, oce);
            }
        } else if (obj instanceof Collection) {
            for (Object elem : (Collection)obj) {
                output(elem, 0, oce);
            }
        } else {
            output(obj, 0, oce);
        }
    }

    private void output(Object obj, int indent, OutputConversionEngine oce) {
        if (obj == null) {
            return;
        }

        if (obj != null) {
            obj = oce.convertOutput(obj);
        }

        for (int i = 0; i < indent; i++) {
            print("\t");
        }

        if (obj == null) {
            println("(null)");
        } else if (obj.getClass().isPrimitive() || obj instanceof String) {
            println(obj);
        } else if (obj.getClass().isArray()) {
            println("Array");
            int length = Array.getLength(obj);
            for (int i = 0; i < length; i++) {
                output(Array.get(obj, i), indent + 1, oce);
            }
        } else if (obj instanceof Collection) {
            println("Collection");
            for (Object elem : (Collection)obj) {
                output(elem, indent + 1, oce);
            }
        } else if (obj instanceof Throwable) {
            println(obj); // class and its message
            ((Throwable)obj).printStackTrace(out);
        } else {
            println(obj);
        }
    }

    public void print(Object x) {
        out.print(x);
        if (log != null) {
            log.print(x);
        }
    }

    public void println(Object x) {
        out.println(x);
        if (log != null) {
            log.println(x);
        }
    }

    public void printErr(Object x) {
        out.print(Ansi.ansi().fg(Ansi.Color.RED).toString()+x+Ansi.ansi().reset());
        if (log != null) {
            log.print(x);
        }
    }

    public void printlnErr(Object x) {
        out.println(Ansi.ansi().fg(Ansi.Color.RED).toString()+x+Ansi.ansi().reset());
        if (log != null) {
            log.println(x);
        }
    }

    public void outputException(String input, TokenException error) {
        int errIndex = error.getToken().getIndex();
        while (errIndex-- > 0) {
            printErr("-");
        }
        for (int i = 0; i < error.getToken().getString().length(); i++) {
            printErr("^");
        }
        printlnErr("");
        printlnErr(error);
    }

    public void outputException(Throwable e) {
        printlnErr(e.getMessage());
        if (e.getCause() != null) {
            printlnErr(e.getCause());
        }
    }
    
    private PrintStream log = null;

    private boolean isLoggingEnabled() {
        return log != null;
    }

    private int loopCounter = 0;

    public void cliEnterLoop() {
        if (isLoggingEnabled()) {
            loopCounter++;
        }
    }

    public void cliLeaveLoop() {
        if (isLoggingEnabled()) {
            loopCounter--;
        }
        if (loopCounter < 0) {
            //disableLogging();
        }
    }



}
