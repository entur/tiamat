package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class Connections {

    protected List<Access> access;
    protected List<Connection> connection;
    protected List<DefaultConnection> defaultConnection;
    protected List<SiteConnection> siteConnection;

    public List<Access> getAccess() {
        if (access == null) {
            access = new ArrayList<Access>();
        }
        return this.access;
    }

    public List<Connection> getConnection() {
        if (connection == null) {
            connection = new ArrayList<Connection>();
        }
        return this.connection;
    }

    public List<DefaultConnection> getDefaultConnection() {
        if (defaultConnection == null) {
            defaultConnection = new ArrayList<DefaultConnection>();
        }
        return this.defaultConnection;
    }

    public List<SiteConnection> getSiteConnection() {
        if (siteConnection == null) {
            siteConnection = new ArrayList<SiteConnection>();
        }
        return this.siteConnection;
    }

}
