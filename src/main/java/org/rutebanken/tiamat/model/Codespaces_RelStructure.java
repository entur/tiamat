package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class Codespaces_RelStructure
        extends OneToManyRelationshipStructure {
    protected List<Object> codespaceRefOrCodespace;

    public List<Object> getCodespaceRefOrCodespace() {
        if (codespaceRefOrCodespace == null) {
            codespaceRefOrCodespace = new ArrayList<Object>();
        }
        return this.codespaceRefOrCodespace;
    }

}
