

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


    "contactPerson",
    "email",
    "phone",
    "fax",
    "url",
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
