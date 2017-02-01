package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


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
