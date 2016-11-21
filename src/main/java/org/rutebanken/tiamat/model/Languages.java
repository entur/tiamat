

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class Languages {

    protected List<LanguageUsageStructure> languageUsage;

    public List<LanguageUsageStructure> getLanguageUsage() {
        if (languageUsage == null) {
            languageUsage = new ArrayList<LanguageUsageStructure>();
        }
        return this.languageUsage;
    }

}
