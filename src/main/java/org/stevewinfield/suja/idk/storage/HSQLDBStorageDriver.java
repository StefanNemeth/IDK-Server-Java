package org.stevewinfield.suja.idk.storage;

import org.stevewinfield.suja.idk.Bootloader;

public class HSQLDBStorageDriver implements StorageDriver {
    @Override
    public String getDriverName() {
        return "hsqldb";
    }

    @Override
    public String getDriverClass() {
        return "org.hsqldb.jdbc.JDBCDriver";
    }

    @Override
    public String getConnectionString() {
        return "jdbc:hsqldb:file:" + Bootloader.getSettings().getProperty("idk.hsqldb.path");
    }

    @Override
    public String getUsername() {
        return "sa";
    }

    @Override
    public String getPassword() {
        return "";
    }
}
