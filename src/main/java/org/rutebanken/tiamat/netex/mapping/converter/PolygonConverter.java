package org.rutebanken.tiamat.netex.mapping.converter;

import com.vividsolutions.jts.geom.*;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import net.opengis.gml._3.*;
import org.rutebanken.tiamat.geo.DoubleValuesToCoordinateSequence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class PolygonConverter extends BidirectionalConverter<PolygonType, Polygon> {
    private static final net.opengis.gml._3.ObjectFactory openGisObjectFactory = new ObjectFactory();

    private final GeometryFactory geometryFactory;

    private final DoubleValuesToCoordinateSequence doubleValuesToCoordinateSequence;

    @Autowired
    public PolygonConverter(GeometryFactory geometryFactory, DoubleValuesToCoordinateSequence doubleValuesToCoordinateSequence) {
        this.geometryFactory = geometryFactory;
        this.doubleValuesToCoordinateSequence = doubleValuesToCoordinateSequence;
    }

    @Override
    public Polygon convertTo(PolygonType polygonType, Type<Polygon> type) {

        Optional<List<Double>> optionalExteriorValues = Optional.ofNullable(polygonType)
                .map(PolygonType::getExterior)
                .map(this::extractValues);

        Optional<List<List<Double>>> interiorValues = Optional.ofNullable(polygonType)
                .map(PolygonType::getInterior)
                .map(list -> list.stream()
                        .map(this::extractValues)
                        .collect(Collectors.toList()))
                .filter(list -> !list.isEmpty());


        if (optionalExteriorValues.isPresent()) {
            List<Double> exteriorValues = optionalExteriorValues.get();

            CoordinateSequence exteriorCoordinateSequence = doubleValuesToCoordinateSequence.convert(exteriorValues);
            LinearRing exteriorLinearRing = new LinearRing(exteriorCoordinateSequence, geometryFactory);

            LinearRing[] interiorHoles = null;

            if (interiorValues.isPresent()) {
                interiorHoles = interiorValues.get().stream()
                        .map(doubleValuesToCoordinateSequence::convert)
                        .map(coordinateSequence -> new LinearRing(coordinateSequence, geometryFactory))
                        .toArray(size -> new LinearRing[size]);
            }

            return new Polygon(exteriorLinearRing, interiorHoles, geometryFactory);
        }


        return null;
    }

    public List<Double> extractValues(AbstractRingPropertyType abstractRingPropertyType) {
        return Optional.of(abstractRingPropertyType)
                .map(AbstractRingPropertyType::getAbstractRing)
                .map(JAXBElement::getValue)
                .map(abstractRing -> ((LinearRingType) abstractRing))
                .map(LinearRingType::getPosList)
                .map(DirectPositionListType::getValue)
                .get();
    }

    @Override
    public PolygonType convertFrom(Polygon polygon, Type<PolygonType> type) {

        Optional<Coordinate[]> optionalCoordinates = Optional.ofNullable(polygon)
                .map(Polygon::getCoordinates)
                .filter(coordinates -> coordinates.length > 0);


        if (optionalCoordinates.isPresent()) {
            List<Double> values = toList(optionalCoordinates.get());
            return new PolygonType()
                    .withExterior(new AbstractRingPropertyType()
                            .withAbstractRing(openGisObjectFactory.createLinearRing(
                                    new LinearRingType()
                                            .withPosList(
                                                    new DirectPositionListType().withValue(values)))));
        }

        return null;
    }

    private List<Double> toList(Coordinate[] coordinates) {
        List<Double> values = new ArrayList<>(coordinates.length*2);
        for (Coordinate coordinate : coordinates) {
            values.add(coordinate.x);
            values.add(coordinate.y);
        }
        return values;
    }
}
