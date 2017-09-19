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

public class ContactStructure {

    protected MultilingualStringEntity contactPerson;
    protected String email;
    protected String phone;
    protected String fax;
    protected String url;
    protected MultilingualStringEntity furtherDetails;

    public MultilingualStringEntity getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(MultilingualStringEntity value) {
        this.contactPerson = value;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String value) {
        this.email = value;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String value) {
        this.phone = value;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String value) {
        this.fax = value;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String value) {
        this.url = value;
    }

    public MultilingualStringEntity getFurtherDetails() {
        return furtherDetails;
    }

    public void setFurtherDetails(MultilingualStringEntity value) {
        this.furtherDetails = value;
    }

}
