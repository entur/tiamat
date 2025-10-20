package org.rutebanken.tiamat.model.vehicle;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.rutebanken.netex.OmitNullsToStringStyle;
import org.rutebanken.tiamat.model.DataManagedObjectStructure;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;

@Entity
@Getter
@Setter
public class DeckPlan extends DataManagedObjectStructure {
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "name_value")),
            @AttributeOverride(name = "lang", column = @Column(name = "name_lang", length = 5))
    })
    @Embedded
    private EmbeddableMultilingualString name;

    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "description_value")),
            @AttributeOverride(name = "lang", column = @Column(name = "description_lang", length = 5))
    })
    @Embedded
    private EmbeddableMultilingualString description;

    @Enumerated(EnumType.STRING)
    private VehicleOrientationEnumeration orientation;

    @Transient
    private ValidityConditions_RelStructure configurationConditions;

    // TODO - TBD
//    private DeckLevels_RelStructure deckLevels;

    @OneToMany(cascade = CascadeType.ALL)
    private Set<Deck> decks = new HashSet<>();


}
