package org.stevewinfield.suja.idk.storage;

public interface StorageDriver {
    public String getDriverName();

    public String getDriverClass();

    public String getConnectionString();

    public String getUsername();

    public String getPassword();
}
