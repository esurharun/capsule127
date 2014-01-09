package com.capsule127.hazelcast;

import asg.cliche.Command;
import asg.cliche.Param;
import asg.cliche.Shell;
import asg.cliche.ShellFactory;
import com.capsule127.cli.Util;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.LogEvent;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import java.util.logging.Level;

/**
 * Created by marcus on 09/01/14.
 */
public class C127Logger implements ILogger {


    public static Level currLevel = Level.INFO;

    public static final String[] const_log_levels = new String[] { "DEBUG", "INFO", "WARNING", "SEVERE" };

    @Command(name = "log-level", description = "Sets log level for printout", abbrev = "logl")
    public static void set_log_level(@Param(name = "level", description = "One of DEBUG, INFO, WARNING, SEVERE")String log_level) {

        for (String i : const_log_levels) {

            if (i.equalsIgnoreCase(log_level)) {

                if (i.equalsIgnoreCase("DEBUG")) {
                    currLevel = Level.FINEST;
                } else if (i.equalsIgnoreCase("INFO")) {
                    currLevel = Level.INFO;
                } else if (i.equalsIgnoreCase("WARNING")) {
                    currLevel = Level.WARNING;
                } else if (i.equalsIgnoreCase("SEVERE")) {
                    currLevel = Level.SEVERE;
                }

            }

        }

    }

    private static final String INFO_HEADER = Util.Colorize(Ansi.Color.YELLOW,"[INFO]");
    private static final String DEBUG_HEADER = Util.Colorize(Ansi.Color.BLUE,"[DEBUG]");
    private static final String SEVERE_HEADER = Util.Colorize(Ansi.Color.RED,"[SEVERE]");
    private static final String WARNING_HEADER = Util.Colorize(Ansi.Color.MAGENTA,"[WARNING]");


    private void print(String s) {

        AnsiConsole.out().println(s);
    }

    @Override
    public void info(String s) {


        if (currLevel == Level.ALL || currLevel == Level.FINE || currLevel == Level.FINER
                || currLevel == Level.FINEST || currLevel == Level.INFO)
        print(INFO_HEADER + " " + s);
    }

    @Override
    public void finest(String s) {

        if (currLevel == Level.ALL || currLevel == Level.FINE || currLevel == Level.FINER
                || currLevel == Level.FINEST)
        print(DEBUG_HEADER + " " + s);
    }

    @Override
    public void finest(Throwable throwable) {

        finest(throwable.getMessage());
    }

    @Override
    public void finest(String s, Throwable throwable) {

        finest(s+" : "+throwable.getMessage());
    }

    @Override
    public boolean isFinestEnabled() {
        return false;
    }

    @Override
    public void severe(String s) {

            print(SEVERE_HEADER + " " + s);
    }

    @Override
    public void severe(Throwable throwable) {

        severe(throwable.getMessage());
    }

    @Override
    public void severe(String s, Throwable throwable) {

        severe(s + " : "+throwable.getMessage());
    }

    @Override
    public void warning(String s) {

        if (currLevel == Level.ALL || currLevel == Level.FINE || currLevel == Level.FINER
                || currLevel == Level.FINEST || currLevel == Level.INFO
                || currLevel == Level.WARNING)
            print(WARNING_HEADER + " " + s);


    }

    @Override
    public void warning(Throwable throwable) {

        warning(throwable.getMessage());
    }

    @Override
    public void warning(String s, Throwable throwable) {

        warning(s+" "+throwable.getMessage());
    }

    @Override
    public void log(Level level, String s) {

        if (level == Level.FINE || level == Level.FINER || level == Level.FINEST) {
            finest(s);
        } else if (level == Level.INFO) {
            info(s);
        } else if (level == Level.SEVERE) {
            severe(s);
        } else if (level == Level.WARNING) {
            warning(s);
        }
    }

    @Override
    public void log(Level level, String s, Throwable throwable) {

        log(level,s+" "+throwable.getMessage());
    }

    @Override
    public void log(LogEvent logEvent) {
        log(logEvent.getLogRecord().getLevel(),
                logEvent.getLogRecord().getSourceClassName()+" : "+logEvent.getLogRecord().getMessage());
    }

    @Override
    public Level getLevel() {
        return currLevel;
    }

    @Override
    public boolean isLoggable(Level level) {
        return true;
    }
}
