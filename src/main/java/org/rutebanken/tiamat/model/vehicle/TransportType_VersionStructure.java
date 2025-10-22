package org.rutebanken.tiamat.model.vehicle;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.rutebanken.tiamat.model.DataManagedObjectStructure;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.MultilingualStringEntity;
import org.rutebanken.tiamat.model.PrivateCodeStructure;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@MappedSuperclass
@Getter
@Setter
public class TransportType_VersionStructure extends DataManagedObjectStructure {
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "name_value")),
            @AttributeOverride(name = "lang", column = @Column(name = "name_lang", length = 5))
    })
    @Embedded
    private EmbeddableMultilingualString name;
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "short_name_value")),
            @AttributeOverride(name = "lang", column = @Column(name = "short_name_lang", length = 5))
    })
    @Embedded
    private EmbeddableMultilingualString shortName;
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "description_value")),
            @AttributeOverride(name = "lang", column = @Column(name = "description_lang", length = 5))
    })
    @Embedded
    private EmbeddableMultilingualString description;
    @Embedded
    private PrivateCodeStructure privateCode;
    private String euroClass;
    private Boolean reversingDirection;
    private Boolean selfPropelled;
//    @Transient
//    private List<PropulsionTypeEnumeration> propulsionTypes;
    @Enumerated(EnumType.STRING)
    private PropulsionTypeEnumeration propulsionType;
//    @Transient
//    private List<FuelTypeEnumeration> fuelTypes;
    @Enumerated(EnumType.STRING)
    private FuelTypeEnumeration fuelType;
    @Enumerated(EnumType.STRING)
    private FuelTypeEnumeration typeOfFuel;
    private BigDecimal maximumRange;
    private BigDecimal maximumVelocity;
    @Enumerated(EnumType.STRING)
    private AllPublicTransportModesEnumeration transportMode;
    @Transient
    private DeckPlanRefStructure deckPlanRef;

}
