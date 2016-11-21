

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


public class Codespaces_RelStructure
    extends OneToManyRelationshipStructure
{
    protected List<Object> codespaceRefOrCodespace;

    public List<Object> getCodespaceRefOrCodespace() {
        if (codespaceRefOrCodespace == null) {
            codespaceRefOrCodespace = new ArrayList<Object>();
        }
        return this.codespaceRefOrCodespace;
    }

}
