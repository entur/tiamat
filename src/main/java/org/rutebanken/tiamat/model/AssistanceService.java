package org.rutebanken.tiamat.model;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import java.util.List;

@Entity
public class AssistanceService extends LocalService {

    @ElementCollection(targetClass = AssistanceFacilityEnumeration.class)
    @LazyCollection(LazyCollectionOption.FALSE)
    @Enumerated(EnumType.STRING)
    protected List<AssistanceFacilityEnumeration> assistanceFacilityList;

    @Enumerated(EnumType.STRING)
    protected AssistanceAvailabilityEnumeration assistanceAvailability;

    public List<AssistanceFacilityEnumeration> getAssistanceFacilityList() {
        return assistanceFacilityList;
    }

    public AssistanceAvailabilityEnumeration getAssistanceAvailability() {
        return assistanceAvailability;
    }

    public void setAssistanceFacilityList(List<AssistanceFacilityEnumeration> assistanceFacilityList) {
        this.assistanceFacilityList = assistanceFacilityList;
    }

    public void setAssistanceAvailability(AssistanceAvailabilityEnumeration assistanceAvailability) {
        this.assistanceAvailability = assistanceAvailability;
    }
}
