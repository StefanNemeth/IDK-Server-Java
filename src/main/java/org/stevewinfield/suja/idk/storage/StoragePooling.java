/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.storage;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import org.apache.log4j.Logger;
import org.stevewinfield.suja.idk.Bootloader;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class StoragePooling {
    private static final Logger logger = Logger.getLogger(StoragePooling.class);

    private BoneCP boneCP;
    private BoneCPConfig boneCPConfig;

    private Map<String, StorageDriver> storageDrivers;

    public StoragePooling() {
        registerDefaultDrivers();
    }

    public boolean getStoragePooling() {
        StorageDriver storageDriver = storageDrivers.get(Bootloader.getSettings().getProperty("idk.database.driver", "mysql"));
        if (storageDriver == null) {
            logger.error("Could not find database driver");
            return false;
        }

        try {
            Driver driver = (Driver) Class.forName(storageDriver.getDriverClass(), true, Bootloader.getCustomClassLoader()).newInstance();
            DriverManager.registerDriver(new DelegatingDriver(driver));
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException e) {
            logger.error("Failed to find JDBC class", e);
            return false;
        }

        Thread.currentThread().setContextClassLoader(Bootloader.getCustomClassLoader());

        boneCPConfig = new BoneCPConfig();
        boneCPConfig.setJdbcUrl(storageDriver.getConnectionString());
        boneCPConfig.setMinConnectionsPerPartition(5);
        boneCPConfig.setMaxConnectionsPerPartition(10);
        boneCPConfig.setConnectionTimeout(1000, TimeUnit.DAYS);
        boneCPConfig.setPartitionCount(1);
        boneCPConfig.setUsername(storageDriver.getUsername());
        boneCPConfig.setPassword(storageDriver.getPassword());
        boneCPConfig.setClassLoader(Bootloader.getCustomClassLoader());
        try {
            boneCP = new BoneCP(boneCPConfig);
        } catch (final Exception e) {
            logger.error("Couldn't create SQL Connection!", e);
            return false;
        }
        return true;
    }

    public void registerStorageDriver(StorageDriver storageDriver) {
        storageDrivers.put(storageDriver.getDriverName(), storageDriver);
    }

    public BoneCP getBoneCP() {
        return boneCP;
    }

    private void registerDefaultDrivers() {
        storageDrivers = new HashMap<>();
        registerStorageDriver(new MySQLStorageDriver());
        registerStorageDriver(new HSQLDBStorageDriver());
    }
}
