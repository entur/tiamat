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

public class CodespaceStructure
        extends EntityStructure {

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
