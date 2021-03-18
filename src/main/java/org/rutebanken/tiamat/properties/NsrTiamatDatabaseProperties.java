package org.rutebanken.tiamat.properties;


import se.samtrafiken.aws.config.CloudConfiguration;
import se.samtrafiken.aws.config.CloudConfigurationFactory;

/**
 * This class holds database properties.
 *
 * @author micael.vesterlund
 */
public class NsrTiamatDatabaseProperties {

    private static final CloudConfiguration CONFIG = CloudConfigurationFactory.getInstance();

    public static String getDbUrl() {
        return "jdbc:sqlserver://" + getDbHost() + ":1433";
    }

    public static String getDbDriver() {
        return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    }

    public static String getDbHost() {
        return CONFIG.getString("se.samtrafiken.nsr.tiamat.DBHost");
    }

    public static String getDbUser() {
        return CONFIG.getString("se.samtrafiken.nsr.tiamat.DBUser");
    }

    public static String getDbPassword() {
        return CONFIG.getString("se.samtrafiken.nsr.tiamat.DBPassword");
    }
}
