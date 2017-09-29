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

public class Notice_VersionStructure
        extends DataManagedObjectStructure {

    protected MultilingualStringEntity name;
    protected MultilingualStringEntity text;
    protected String publicCode;
    protected String shortCode;
    protected PrivateCodeStructure privateCode;
    protected TypeOfNoticeRefStructure typeOfNoticeRef;
    protected Boolean canBeAdvertised;
    protected MultilingualStringEntity driverDisplayText;
    protected DeliveryVariants_RelStructure variants;

    public MultilingualStringEntity getName() {
        return name;
    }

    public void setName(MultilingualStringEntity value) {
        this.name = value;
    }

    public MultilingualStringEntity getText() {
        return text;
    }

    public void setText(MultilingualStringEntity value) {
        this.text = value;
    }

    public String getPublicCode() {
        return publicCode;
    }

    public void setPublicCode(String value) {
        this.publicCode = value;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String value) {
        this.shortCode = value;
    }

    public PrivateCodeStructure getPrivateCode() {
        return privateCode;
    }

    public void setPrivateCode(PrivateCodeStructure value) {
        this.privateCode = value;
    }

    public TypeOfNoticeRefStructure getTypeOfNoticeRef() {
        return typeOfNoticeRef;
    }

    public void setTypeOfNoticeRef(TypeOfNoticeRefStructure value) {
        this.typeOfNoticeRef = value;
    }

    public Boolean isCanBeAdvertised() {
        return canBeAdvertised;
    }

    public void setCanBeAdvertised(Boolean value) {
        this.canBeAdvertised = value;
    }

    public MultilingualStringEntity getDriverDisplayText() {
        return driverDisplayText;
    }

    public void setDriverDisplayText(MultilingualStringEntity value) {
        this.driverDisplayText = value;
    }

    public DeliveryVariants_RelStructure getVariants() {
        return variants;
    }

    public void setVariants(DeliveryVariants_RelStructure value) {
        this.variants = value;
    }

}
