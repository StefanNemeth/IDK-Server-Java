package org.stevewinfield.suja.idk.storage;

import org.stevewinfield.suja.idk.Bootloader;

public class MySQLStorageDriver implements StorageDriver {

    @Override
    public String getDriverName() {
        return "mysql";
    }

    @Override
    public String getDriverClass() {
        return "com.mysql.jdbc.Driver";
    }

    @Override
    public String getConnectionString() {
        return "jdbc:mysql://" +
                Bootloader.getSettings().getProperty("idk.mysql.host") + ":" +
                Bootloader.getSettings().getProperty("idk.mysql.port", "3306") + "/" +
                Bootloader.getSettings().getProperty("idk.mysql.database");
    }

    @Override
    public String getUsername() {
        return Bootloader.getSettings().getProperty("idk.mysql.user");
    }

    @Override
    public String getPassword() {
        return Bootloader.getSettings().getProperty("idk.mysql.password");
    }

}
