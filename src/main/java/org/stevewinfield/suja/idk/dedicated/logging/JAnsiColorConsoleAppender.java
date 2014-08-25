package org.stevewinfield.suja.idk.dedicated.logging;

import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;
import org.fusesource.jansi.AnsiConsole;

import java.io.PrintStream;

/**
 * Gotten from https://github.com/mihnita/java-color-loggers, licensed under Apache 2.0 license
 * @author mihnita
 */
public class JAnsiColorConsoleAppender extends BaseColorConsoleAppender {
    String gTarget = null;
    boolean usingStdErr;

    public JAnsiColorConsoleAppender() {
        super();
    }

    public JAnsiColorConsoleAppender(Layout layout) {
        super(layout);
    }

    public JAnsiColorConsoleAppender(Layout layout, String target) {
        super(layout, target);
    }

    public void setPassThrough(boolean value) {
        System.setProperty("jansi.passthrough", value ? "true" : "false");
    }

    public void setStrip(boolean value) {
        System.setProperty("jansi.strip", value ? "true" : "false");
    }

    @Override
    protected void subAppend(LoggingEvent event) {
        PrintStream currentOutput = usingStdErr ? AnsiConsole.err : AnsiConsole.out;

        hackPatternString();
        currentOutput.print(getColour(event.getLevel()));
        currentOutput.print(getLayout().format(event));
        if(layout.ignoresThrowable()) {
            String[] throwableStrRep = event.getThrowableStrRep();
            if (throwableStrRep != null) {
                for (String aThrowableStrRep : throwableStrRep) {
                    currentOutput.print(aThrowableStrRep);
                    currentOutput.print(Layout.LINE_SEP);
                }
            }
        }

        if (immediateFlush)
            currentOutput.flush();
    }

    @Override
    protected boolean hackPatternString() {
        String theTarget = getTarget();
        if (gTarget != theTarget) // I really want to have the same object, not just equal content
            usingStdErr = SYSTEM_ERR.equalsIgnoreCase(theTarget);

        return super.hackPatternString();
    }
}
