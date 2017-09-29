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


public class OrganisationPart_VersionStructure
        extends DataManagedObjectStructure {

    protected MultilingualStringEntity name;
    protected MultilingualStringEntity shortName;
    protected MultilingualStringEntity description;
    protected String publicCode;
    protected PrivateCodeStructure privateCode;
    protected ContactStructure contactDetails;

    protected JAXBElement<? extends OrganisationRefStructure> organisationRef;
    protected TypeOfOrganisationPartRef typeOfOrganisationPartRef;
    protected AdministrativeZones_RelStructure administrativeZones;

    public MultilingualStringEntity getName() {
        return name;
    }

    public void setName(MultilingualStringEntity value) {
        this.name = value;
    }

    public MultilingualStringEntity getShortName() {
        return shortName;
    }

    public void setShortName(MultilingualStringEntity value) {
        this.shortName = value;
    }

    public MultilingualStringEntity getDescription() {
        return description;
    }

    public void setDescription(MultilingualStringEntity value) {
        this.description = value;
    }

    public String getPublicCode() {
        return publicCode;
    }

    public void setPublicCode(String value) {
        this.publicCode = value;
    }

    public PrivateCodeStructure getPrivateCode() {
        return privateCode;
    }

    public void setPrivateCode(PrivateCodeStructure value) {
        this.privateCode = value;
    }

    public ContactStructure getContactDetails() {
        return contactDetails;
    }

    public void setContactDetails(ContactStructure value) {
        this.contactDetails = value;
    }

    public JAXBElement<? extends OrganisationRefStructure> getOrganisationRef() {
        return organisationRef;
    }

    public void setOrganisationRef(JAXBElement<? extends OrganisationRefStructure> value) {
        this.organisationRef = value;
    }

    public TypeOfOrganisationPartRef getTypeOfOrganisationPartRef() {
        return typeOfOrganisationPartRef;
    }

    public void setTypeOfOrganisationPartRef(TypeOfOrganisationPartRef value) {
        this.typeOfOrganisationPartRef = value;
    }

    public AdministrativeZones_RelStructure getAdministrativeZones() {
        return administrativeZones;
    }

    public void setAdministrativeZones(AdministrativeZones_RelStructure value) {
        this.administrativeZones = value;
    }

}
