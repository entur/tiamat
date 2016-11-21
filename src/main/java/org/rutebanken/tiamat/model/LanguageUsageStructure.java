

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


    "language",
public class LanguageUsageStructure {

    protected String language;
    protected List<LanguageUseEnumeration> languageUse;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String value) {
        this.language = value;
    }

    public List<LanguageUseEnumeration> getLanguageUse() {
        if (languageUse == null) {
            languageUse = new ArrayList<LanguageUseEnumeration>();
        }
        return this.languageUse;
    }

}
