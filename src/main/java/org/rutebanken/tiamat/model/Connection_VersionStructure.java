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

public class Connection_VersionStructure
        extends Transfer_VersionStructure {

    protected ExternalObjectRefStructure externalConnectionLinkRef;
    protected ConnectionEndStructure from;
    protected ConnectionEndStructure to;
    protected Boolean transferOnly;

    public ExternalObjectRefStructure getExternalConnectionLinkRef() {
        return externalConnectionLinkRef;
    }

    public void setExternalConnectionLinkRef(ExternalObjectRefStructure value) {
        this.externalConnectionLinkRef = value;
    }

    public ConnectionEndStructure getFrom() {
        return from;
    }

    public void setFrom(ConnectionEndStructure value) {
        this.from = value;
    }

    public ConnectionEndStructure getTo() {
        return to;
    }

    public void setTo(ConnectionEndStructure value) {
        this.to = value;
    }

    public Boolean isTransferOnly() {
        return transferOnly;
    }

    public void setTransferOnly(Boolean value) {
        this.transferOnly = value;
    }

}
