/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class Connections {

    protected List<Access> access;
    protected List<Connection> connection;
    protected List<SiteConnection> siteConnection;

    public List<Access> getAccess() {
        if (access == null) {
            access = new ArrayList<>();
        }
        return this.access;
    }

    public List<Connection> getConnection() {
        if (connection == null) {
            connection = new ArrayList<>();
        }
        return this.connection;
    }

    public List<SiteConnection> getSiteConnection() {
        if (siteConnection == null) {
            siteConnection = new ArrayList<>();
        }
        return this.siteConnection;
    }

}
