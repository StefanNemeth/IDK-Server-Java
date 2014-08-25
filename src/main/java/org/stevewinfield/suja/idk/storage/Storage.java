/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.storage;

import com.jolbox.bonecp.BoneCP;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class Storage {
    private static final Logger logger = Logger.getLogger(Storage.class);

    private boolean SQLException;
    private BoneCP boneCP;

    private Connection driverConnection;
    private Statement driverStatement;

    public boolean create() {
        this.SQLException = false;
        try {
            final StoragePooling Pooling = new StoragePooling();

            if (!Pooling.getStoragePooling()) {
                this.SQLException = true;
                return false;
            } else {
                this.boneCP = Pooling.getBoneCP();
                this.driverConnection = this.boneCP.getConnection();
                this.driverStatement = this.driverConnection.createStatement();
            }
        } catch (final Exception e) {
            logger.error("Failed to access database", e);
            return false;
        }
        return true;
    }

    public String readString(final String query) {
        try {
            final ResultSet result = driverStatement.executeQuery(query);
            result.first();
            return result.getString(query.split(" ")[1]);
        } catch (final Exception e) {
            logger.error("SQL Exception", e);
        }
        return null;
    }

    public Integer readInteger(final String query) {
        try {
            final ResultSet result = driverStatement.executeQuery(query);
            result.first();
            return result.getInt(query.split(" ")[1]);
        } catch (final Exception e) {
            logger.error("SQL Exception", e);
        }
        return 0;
    }

    public Integer readLastId(final String tb) {
        try {
            final ResultSet result = driverStatement.executeQuery("select last_insert_id() as last_id from " + tb);
            result.first();
            return result.getInt("last_id");
        } catch (final Exception e) {
            logger.error("SQL Exception", e);
        }
        return 0;
    }

    public PreparedStatement queryParams(final String query) {
        try {
            return driverConnection.prepareStatement(query);
        } catch (final Exception e) {
            logger.error("SQL Exception", e);
        }
        return null;
    }

    public void executeQuery(final String query) {
        try {
            driverStatement.execute(query);
        } catch (final Exception e) {
            logger.error("SQL Exception", e);
        }
    }

    public boolean entryExists(final String query) {
        try {
            final ResultSet result = driverStatement.executeQuery(query);
            return result.next();
        } catch (final Exception e) {
            logger.error("SQL Exception", e);
        }
        return false;
    }

    public int entryCount(final String q) {
        int i = 0;

        try {
            final ResultSet resSet = driverStatement.executeQuery(q);

            while (resSet.next()) {
                ++i;
            }
        } catch (final Exception e) {
            logger.error("SQL Exception", e);
        }
        return i;
    }

    public int entryCount(final PreparedStatement pStmt) {
        int i = 0;

        try {
            final ResultSet resSet = pStmt.executeQuery();
            while (resSet.next()) {
                ++i;
            }
        } catch (final Exception e) {
            logger.error("SQL Exception", e);
        }
        return i;
    }

    public ResultSet readRow(final String Query) {
        try {
            final ResultSet resSet = driverStatement.executeQuery(Query);

            if (resSet.next()) {
                return resSet;
            }
        } catch (final Exception e) {
            logger.error("SQL Exception", e);
        }
        return null;
    }

    public Boolean getSQLException() {
        return SQLException;
    }

    public void setSQLException(final Boolean flag) {
        this.SQLException = flag;
    }
}
