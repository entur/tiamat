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


public class NoticeAssignment_VersionStructure
        extends Assignment_VersionStructure {

    protected NoticeRefStructure noticeRef;
    protected GeneralGroupOfEntitiesRefStructure groupOfNoticesRef;
    protected VersionOfObjectRefStructure noticedObjectRef;
    protected JAXBElement<? extends LinkSequenceRefStructure> linkSequenceRef;
    protected JAXBElement<? extends CommonSectionRefStructure> commonSectionRef;
    protected PointInSequenceRefStructure startPointInPatternRef;
    protected PointInSequenceRefStructure endPointInPatternRef;
    protected String mark;
    protected String markUrl;
    protected PublicityChannelEnumeration publicityChannel;
    protected Boolean advertised;

    public NoticeRefStructure getNoticeRef() {
        return noticeRef;
    }

    public void setNoticeRef(NoticeRefStructure value) {
        this.noticeRef = value;
    }

    public GeneralGroupOfEntitiesRefStructure getGroupOfNoticesRef() {
        return groupOfNoticesRef;
    }

    public void setGroupOfNoticesRef(GeneralGroupOfEntitiesRefStructure value) {
        this.groupOfNoticesRef = value;
    }

    public VersionOfObjectRefStructure getNoticedObjectRef() {
        return noticedObjectRef;
    }

    public void setNoticedObjectRef(VersionOfObjectRefStructure value) {
        this.noticedObjectRef = value;
    }

    public JAXBElement<? extends LinkSequenceRefStructure> getLinkSequenceRef() {
        return linkSequenceRef;
    }

    public void setLinkSequenceRef(JAXBElement<? extends LinkSequenceRefStructure> value) {
        this.linkSequenceRef = value;
    }

    public JAXBElement<? extends CommonSectionRefStructure> getCommonSectionRef() {
        return commonSectionRef;
    }

    public void setCommonSectionRef(JAXBElement<? extends CommonSectionRefStructure> value) {
        this.commonSectionRef = value;
    }

    public PointInSequenceRefStructure getStartPointInPatternRef() {
        return startPointInPatternRef;
    }

    public void setStartPointInPatternRef(PointInSequenceRefStructure value) {
        this.startPointInPatternRef = value;
    }

    public PointInSequenceRefStructure getEndPointInPatternRef() {
        return endPointInPatternRef;
    }

    public void setEndPointInPatternRef(PointInSequenceRefStructure value) {
        this.endPointInPatternRef = value;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String value) {
        this.mark = value;
    }

    public String getMarkUrl() {
        return markUrl;
    }

    public void setMarkUrl(String value) {
        this.markUrl = value;
    }

    public PublicityChannelEnumeration getPublicityChannel() {
        return publicityChannel;
    }

    public void setPublicityChannel(PublicityChannelEnumeration value) {
        this.publicityChannel = value;
    }

    public Boolean isAdvertised() {
        return advertised;
    }

    public void setAdvertised(Boolean value) {
        this.advertised = value;
    }

}
