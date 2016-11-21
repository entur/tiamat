

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


public class CodespaceStructure
    extends EntityStructure
{

    protected String xmlns;
    protected String xmlnsUrl;
    protected String description;
    protected String dataSourceRef;

    public String getXmlns() {
        return xmlns;
    }

    public void setXmlns(String value) {
        this.xmlns = value;
    }

    public String getXmlnsUrl() {
        return xmlnsUrl;
    }

    public void setXmlnsUrl(String value) {
        this.xmlnsUrl = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    public String getDataSourceRef() {
        return dataSourceRef;
    }

    public void setDataSourceRef(String value) {
        this.dataSourceRef = value;
    }

}
