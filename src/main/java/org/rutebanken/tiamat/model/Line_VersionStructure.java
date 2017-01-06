package org.rutebanken.tiamat.model;

public class Line_VersionStructure
        extends DataManagedObjectStructure {

    protected MultilingualStringEntity name;
    protected MultilingualStringEntity shortName;
    protected MultilingualStringEntity description;
    protected AllVehicleModesOfTransportEnumeration transportMode;
    protected TransportSubmodeStructure transportSubmode;
    protected String url;
    protected String publicCode;
    protected PrivateCodeStructure privateCode;
    protected ExternalObjectRefStructure externalLineRef;
    protected AuthorityRefStructure authorityRef;
    protected OperatorRefStructure operatorRef;
    protected TransportOrganisationRefs_RelStructure additionalOperators;
    protected ModeRefs_RelStructure otherModes;
    protected OperationalContextRefStructure operationalContextRef;
    protected TypeOfLineRefStructure typeOfLineRef;
    protected ExternalObjectRefStructure externalProductCategoryRef;
    protected Boolean monitored;
    protected RouteRefs_RelStructure routes;
    protected GroupOfLinesRefStructure representedByGroupRef;
    protected PresentationStructure presentation;
    protected PresentationStructure alternativePresentation;
    protected AccessibilityAssessment accessibilityAssessment;
    protected AllowedLineDirections_RelStructure allowedDirections;
    protected NoticeAssignments_RelStructure noticeAssignments;

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

    public MultilingualStringEntity getDescription() {
        return description;
    }

    public void setDescription(MultilingualStringEntity value) {
        this.description = value;
    }

    public AllVehicleModesOfTransportEnumeration getTransportMode() {
        return transportMode;
    }

    public void setTransportMode(AllVehicleModesOfTransportEnumeration value) {
        this.transportMode = value;
    }

    public TransportSubmodeStructure getTransportSubmode() {
        return transportSubmode;
    }

    public void setTransportSubmode(TransportSubmodeStructure value) {
        this.transportSubmode = value;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String value) {
        this.url = value;
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

    public ExternalObjectRefStructure getExternalLineRef() {
        return externalLineRef;
    }

    public void setExternalLineRef(ExternalObjectRefStructure value) {
        this.externalLineRef = value;
    }

    public AuthorityRefStructure getAuthorityRef() {
        return authorityRef;
    }

    public void setAuthorityRef(AuthorityRefStructure value) {
        this.authorityRef = value;
    }

    public OperatorRefStructure getOperatorRef() {
        return operatorRef;
    }

    public void setOperatorRef(OperatorRefStructure value) {
        this.operatorRef = value;
    }

    public TransportOrganisationRefs_RelStructure getAdditionalOperators() {
        return additionalOperators;
    }

    public void setAdditionalOperators(TransportOrganisationRefs_RelStructure value) {
        this.additionalOperators = value;
    }

    public ModeRefs_RelStructure getOtherModes() {
        return otherModes;
    }

    public void setOtherModes(ModeRefs_RelStructure value) {
        this.otherModes = value;
    }

    public OperationalContextRefStructure getOperationalContextRef() {
        return operationalContextRef;
    }

    public void setOperationalContextRef(OperationalContextRefStructure value) {
        this.operationalContextRef = value;
    }

    public TypeOfLineRefStructure getTypeOfLineRef() {
        return typeOfLineRef;
    }

    public void setTypeOfLineRef(TypeOfLineRefStructure value) {
        this.typeOfLineRef = value;
    }

    public ExternalObjectRefStructure getExternalProductCategoryRef() {
        return externalProductCategoryRef;
    }

    public void setExternalProductCategoryRef(ExternalObjectRefStructure value) {
        this.externalProductCategoryRef = value;
    }

    public Boolean isMonitored() {
        return monitored;
    }

    public void setMonitored(Boolean value) {
        this.monitored = value;
    }

    public RouteRefs_RelStructure getRoutes() {
        return routes;
    }

    public void setRoutes(RouteRefs_RelStructure value) {
        this.routes = value;
    }

    public GroupOfLinesRefStructure getRepresentedByGroupRef() {
        return representedByGroupRef;
    }

    public void setRepresentedByGroupRef(GroupOfLinesRefStructure value) {
        this.representedByGroupRef = value;
    }

    public PresentationStructure getPresentation() {
        return presentation;
    }

    public void setPresentation(PresentationStructure value) {
        this.presentation = value;
    }

    public PresentationStructure getAlternativePresentation() {
        return alternativePresentation;
    }

    public void setAlternativePresentation(PresentationStructure value) {
        this.alternativePresentation = value;
    }

    public AccessibilityAssessment getAccessibilityAssessment() {
        return accessibilityAssessment;
    }

    public void setAccessibilityAssessment(AccessibilityAssessment value) {
        this.accessibilityAssessment = value;
    }

    public AllowedLineDirections_RelStructure getAllowedDirections() {
        return allowedDirections;
    }

    public void setAllowedDirections(AllowedLineDirections_RelStructure value) {
        this.allowedDirections = value;
    }

    public NoticeAssignments_RelStructure getNoticeAssignments() {
        return noticeAssignments;
    }

    public void setNoticeAssignments(NoticeAssignments_RelStructure value) {
        this.noticeAssignments = value;
    }

}
