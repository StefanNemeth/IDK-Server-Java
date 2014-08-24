/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.storage;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import org.apache.log4j.Logger;
import org.stevewinfield.suja.idk.Bootloader;

import java.util.concurrent.TimeUnit;

public class StoragePooling {
    private static Logger logger = Logger.getLogger(StoragePooling.class);

    private BoneCP boneCP;
    private BoneCPConfig boneCPConfig;

    public boolean getStoragePooling() {
        boneCPConfig = new BoneCPConfig();
        boneCPConfig.setJdbcUrl("jdbc:mysql://" + Bootloader.getSettings().getProperty("idk.mysql.host") + "/" + Bootloader.getSettings().getProperty("idk.mysql.database"));
        boneCPConfig.setMinConnectionsPerPartition(5);
        boneCPConfig.setMaxConnectionsPerPartition(10);
        boneCPConfig.setConnectionTimeout(1000, TimeUnit.DAYS);
        boneCPConfig.setPartitionCount(1);
        boneCPConfig.setUsername(Bootloader.getSettings().getProperty("idk.mysql.user"));
        boneCPConfig.setPassword(Bootloader.getSettings().getProperty("idk.mysql.password"));
        try {
            boneCP = new BoneCP(boneCPConfig);
        } catch (final Exception e) {
            logger.error("Couldn't create SQL Connection!", e);
            return false;
        }
        return true;
    }

    public BoneCP getBoneCP() {
        return boneCP;
    }
}
