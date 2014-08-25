package org.stevewinfield.suja.idk.dedicated.commands;

import org.apache.log4j.Logger;

public interface IDedicatedServerCommand {
    public void execute(String[] args, Logger logger);

    public String getName();
}
