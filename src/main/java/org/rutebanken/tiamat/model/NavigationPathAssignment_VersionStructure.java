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

import javax.xml.bind.JAXBElement;


public class NavigationPathAssignment_VersionStructure
        extends StopAssignment_VersionStructure {

    protected JAXBElement<? extends ConnectionRefStructure> connectionRef;
    protected JAXBElement<? extends SiteRefStructure> siteRef;
    protected NavigationPathRefStructure navigationPathRef;

    public JAXBElement<? extends ConnectionRefStructure> getConnectionRef() {
        return connectionRef;
    }

    public void setConnectionRef(JAXBElement<? extends ConnectionRefStructure> value) {
        this.connectionRef = value;
    }

    public JAXBElement<? extends SiteRefStructure> getSiteRef() {
        return siteRef;
    }

    public void setSiteRef(JAXBElement<? extends SiteRefStructure> value) {
        this.siteRef = value;
    }

    public NavigationPathRefStructure getNavigationPathRef() {
        return navigationPathRef;
    }

    public void setNavigationPathRef(NavigationPathRefStructure value) {
        this.navigationPathRef = value;
    }

}
