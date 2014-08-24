package org.stevewinfield.suja.idk.storage;

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
        return "jdbc:hsqldb:mem:hallo";
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
