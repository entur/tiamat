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

public class DestinationDisplay_VersionStructure
        extends DataManagedObjectStructure {

    protected MultilingualStringEntity name;
    protected MultilingualStringEntity shortName;
    protected MultilingualStringEntity sideText;
    protected MultilingualStringEntity frontText;
    protected MultilingualStringEntity driverDisplayText;
    protected String shortCode;
    protected String publicCode;
    protected PrivateCodeStructure privateCode;


    public MultilingualStringEntity getName() {
        return name;
    }

    public void setName(MultilingualStringEntity value) {
        this.name = value;
    }

    public MultilingualStringEntity getShortName() {
        return shortName;
    }

    public void setShortName(MultilingualStringEntity value) {
        this.shortName = value;
    }

    public MultilingualStringEntity getSideText() {
        return sideText;
    }

    public void setSideText(MultilingualStringEntity value) {
        this.sideText = value;
    }

    public MultilingualStringEntity getFrontText() {
        return frontText;
    }

    public void setFrontText(MultilingualStringEntity value) {
        this.frontText = value;
    }

    public MultilingualStringEntity getDriverDisplayText() {
        return driverDisplayText;
    }

    public void setDriverDisplayText(MultilingualStringEntity value) {
        this.driverDisplayText = value;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String value) {
        this.shortCode = value;
    }

    public String getPublicCode() {
        return publicCode;
    }

    public void setPublicCode(String value) {
        this.publicCode = value;
    }

    public PrivateCodeStructure getPrivateCode() {
        return privateCode;
    }

    public void setPrivateCode(PrivateCodeStructure value) {
        this.privateCode = value;
    }

}
