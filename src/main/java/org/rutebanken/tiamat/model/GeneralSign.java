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

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
public class GeneralSign
        extends SignEquipment_VersionStructure {


    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "content_value")),
            @AttributeOverride(name = "lang", column = @Column(name = "content_lang"))
    })
    @Embedded
    protected EmbeddableMultilingualString content;

    @Enumerated(value = EnumType.STRING)
    protected SignContentEnumeration signContentType;

    protected Integer numberOfFrames;

    protected Boolean lineSignage;

    protected Boolean mainLineSign;

    protected Boolean replacesRailSign;

    public EmbeddableMultilingualString getContent() {
        return content;
    }

    public void setContent(EmbeddableMultilingualString value) {
        this.content = value;
    }

    public SignContentEnumeration getSignContentType() {
        return signContentType;
    }

    public void setSignContentType(SignContentEnumeration value) {
        this.signContentType = value;
    }

    public Integer getNumberOfFrames() {
        return numberOfFrames;
    }

    public void setNumberOfFrames(Integer numberOfFrames) {
        this.numberOfFrames = numberOfFrames;
    }

    public Boolean isLineSignage() {
        return lineSignage;
    }

    public void setLineSignage(Boolean lineSignage) {
        this.lineSignage = lineSignage;
    }

    public Boolean isReplacesRailSign() {
        return replacesRailSign;
    }

    public void setReplacesRailSign(Boolean replacesRailSign) {
        this.replacesRailSign = replacesRailSign;
    }

    public Boolean isMainLineSign() {
        return mainLineSign;
    }

    public void setMainLineSign(Boolean mainLineSign) {
        this.mainLineSign = mainLineSign;
    }
}
