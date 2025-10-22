package org.rutebanken.tiamat.model.vehicle;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import net.opengis.gml._3.MultiSurfaceType;
import net.opengis.gml._3.PolygonType;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.rutebanken.netex.OmitNullsToStringStyle;
import org.rutebanken.tiamat.model.AllModesEnumeration;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.Zone_VersionStructure;

@Entity
@Getter
@Setter
public class Deck extends Zone_VersionStructure {
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "label_value")),
            @AttributeOverride(name = "lang", column = @Column(name = "label_lang", length = 5))
    })
    @Embedded
    private EmbeddableMultilingualString label;

    @OneToMany(cascade = CascadeType.ALL)
    private List<PassengerSpace> deckSpaces;

    @OneToMany(cascade = CascadeType.ALL)
    private List<SpotRow> spotRows;


    // TODO - TBD
//    protected DeckLevelRefStructure deckLevelRef;
//    protected SpotColumns_RelStructure spotColumns;
//    protected DeckPathJunctionRefs_RelStructure deckPathJunctions;
//    protected DeckPathLinkRefs_RelStructure deckPathLinks;
//    protected DeckNavigationPaths_RelStructure deckNavigationPaths;

}
