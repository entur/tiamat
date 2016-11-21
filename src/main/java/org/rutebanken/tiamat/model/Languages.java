package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class Languages {

    protected List<LanguageUsageStructure> languageUsage;

    public List<LanguageUsageStructure> getLanguageUsage() {
        if (languageUsage == null) {
            languageUsage = new ArrayList<LanguageUsageStructure>();
        }
        return this.languageUsage;
    }

}
