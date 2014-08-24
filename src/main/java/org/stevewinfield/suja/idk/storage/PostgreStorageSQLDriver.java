package org.stevewinfield.suja.idk.storage;

import org.stevewinfield.suja.idk.Bootloader;

public class PostgreStorageSQLDriver implements StorageDriver {
    @Override
    public String getDriverName() {
        return "postgresql";
    }

    @Override
    public String getDriverClass() {
        return "org.postgresql.Driver";
    }

    @Override
    public String getConnectionString() {
        return "jdbc:postgresql://" +
                Bootloader.getSettings().getProperty("idk.postgresql.host") + ":" +
                Bootloader.getSettings().getProperty("idk.postgresql.port", "5432") + "/" +
                Bootloader.getSettings().getProperty("idk.postgresql.database");
    }

    @Override
    public String getUsername() {
        return Bootloader.getSettings().getProperty("idk.postgresql.user");
    }

    @Override
    public String getPassword() {
        return Bootloader.getSettings().getProperty("idk.postgresql.password");
    }
}
