package org.rutebanken.tiamat.model;

import java.math.BigInteger;


public class NoticeAssignment_DerivedViewStructure
        extends DerivedViewStructure {

    protected MultilingualStringEntity name;
    protected NoticeRefStructure noticeRef;
    protected String mark;
    protected String markUrl;
    protected PublicityChannelEnumeration publicityChannel;
    protected Boolean advertised;
    protected MultilingualStringEntity text;
    protected String publicCode;
    protected String shortCode;
    protected PrivateCodeStructure privateCode;
    protected TypeOfNoticeRefStructure typeOfNoticeRef;
    protected Boolean canBeAdvertised;
    protected BigInteger order;

    public MultilingualStringEntity getName() {
        return name;
    }

    public void setName(MultilingualStringEntity value) {
        this.name = value;
    }

    public NoticeRefStructure getNoticeRef() {
        return noticeRef;
    }

    public void setNoticeRef(NoticeRefStructure value) {
        this.noticeRef = value;
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

    public BigInteger getOrder() {
        return order;
    }

    public void setOrder(BigInteger value) {
        this.order = value;
    }

}
