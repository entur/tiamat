package org.rutebanken.tiamat.model;

public class TelephoneContactStructure {

    protected String telNationalNumber;
    protected String telExtensionNumber;
    protected String telCountryCode;

    public String getTelNationalNumber() {
        return telNationalNumber;
    }

    public void setTelNationalNumber(String value) {
        this.telNationalNumber = value;
    }

    public String getTelExtensionNumber() {
        return telExtensionNumber;
    }

    public void setTelExtensionNumber(String value) {
        this.telExtensionNumber = value;
    }

    public String getTelCountryCode() {
        return telCountryCode;
    }

    public void setTelCountryCode(String value) {
        this.telCountryCode = value;
    }

}
