package org.stevewinfield.suja.idk.dedicated.commands;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ThreadedCommandReader extends Thread {
    private static final Logger logger = Logger.getLogger(ThreadedCommandReader.class);
    private DedicatedServerCommandHandler dedicatedServerCommandHandler;

    public ThreadedCommandReader(String name, DedicatedServerCommandHandler dedicatedServerCommandHandler) {
        super(name);
        this.dedicatedServerCommandHandler = dedicatedServerCommandHandler;
    }

    public void run() {
        BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(System.in));

        String line;

        try {
            while ((line = bufferedreader.readLine()) != null) {
                dedicatedServerCommandHandler.handle(line);
            }
        } catch (IOException e) {
            logger.error("Exception handling console input", e);
        }
    }
}