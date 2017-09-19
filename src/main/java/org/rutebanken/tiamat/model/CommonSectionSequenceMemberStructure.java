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

import javax.xml.bind.JAXBElement;


public class CommonSectionSequenceMemberStructure
        extends AbstractGroupMember_VersionedChildStructure {

    protected CommonSectionRefStructure parentCommonSectionRef;
    protected JAXBElement<? extends LinkSequenceRefStructure> linkSequenceRef;

    public CommonSectionRefStructure getParentCommonSectionRef() {
        return parentCommonSectionRef;
    }

    public void setParentCommonSectionRef(CommonSectionRefStructure value) {
        this.parentCommonSectionRef = value;
    }

    public JAXBElement<? extends LinkSequenceRefStructure> getLinkSequenceRef() {
        return linkSequenceRef;
    }

    public void setLinkSequenceRef(JAXBElement<? extends LinkSequenceRefStructure> value) {
        this.linkSequenceRef = value;
    }

}
