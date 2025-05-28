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

package org.rutebanken.tiamat.netex.mapping.converter;


import jakarta.xml.bind.JAXBElement;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import net.opengis.gml._3.AbstractRingPropertyType;
import net.opengis.gml._3.DirectPositionListType;
import net.opengis.gml._3.LinearRingType;
import net.opengis.gml._3.ObjectFactory;
import net.opengis.gml._3.PolygonType;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;
import org.rutebanken.tiamat.geo.DoubleValuesToCoordinateSequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;


@Component
public class PolygonConverter extends BidirectionalConverter<Polygon, PolygonType> {

    private static final Logger logger = LoggerFactory.getLogger(PolygonConverter.class);

    private static final net.opengis.gml._3.ObjectFactory openGisObjectFactory = new ObjectFactory();

    private static final AtomicLong polygonIdCounter = new AtomicLong();

    private final GeometryFactory geometryFactory;

    private final DoubleValuesToCoordinateSequence doubleValuesToCoordinateSequence;

    @Autowired
    public PolygonConverter(GeometryFactory geometryFactory, DoubleValuesToCoordinateSequence doubleValuesToCoordinateSequence) {
        this.geometryFactory = geometryFactory;
        this.doubleValuesToCoordinateSequence = doubleValuesToCoordinateSequence;
    }

    @Override
    public Polygon convertFrom(PolygonType polygonType, Type<Polygon> type, MappingContext mappingContext) {

        Optional<List<Double>> optionalExteriorValues = Optional.ofNullable(polygonType)
                .map(PolygonType::getExterior)
                .map(this::extractValues);

        Optional<List<List<Double>>> interiorValues = Optional.ofNullable(polygonType)
                .map(PolygonType::getInterior)
                .map(list -> list.stream()
                        .map(this::extractValues)
                        .toList())
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

        logger.warn("Cannot convert polygon from PolygonType. Cannot find exterior values: {}", polygonType);

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
    public PolygonType convertTo(Polygon polygon, Type<PolygonType> type, MappingContext mappingContext) {

        Optional<Coordinate[]> optionalCoordinates = Optional.ofNullable(polygon)
                .map(Polygon::getExteriorRing)
                .map(LineString::getCoordinates)
                .filter(coordinates -> coordinates.length > 0);


        if (optionalCoordinates.isPresent()) {
            List<Double> values = toList(optionalCoordinates.get());
            return new PolygonType()
                    .withId("GEN-PolygonType-" + polygonIdCounter.incrementAndGet())
                    .withExterior(of(values))
                    .withInterior(ofInteriorRings(polygon));
        }

        return null;
    }

    private List<AbstractRingPropertyType> ofInteriorRings(Polygon polygon) {
        List<AbstractRingPropertyType> list = new ArrayList<>();
        for(int n = 0; n < polygon.getNumInteriorRing(); n++) {
            if(polygon.getInteriorRingN(n).getCoordinates() != null) {
                List<Double> values = toList(polygon.getInteriorRingN(n).getCoordinates());
                list.add(of(values));
            }

        }
        return list;
    }

    private AbstractRingPropertyType of(List<Double> values) {
        return new AbstractRingPropertyType()
                .withAbstractRing(openGisObjectFactory.createLinearRing(
                        new LinearRingType()
                                .withPosList(
                                        new DirectPositionListType().withValue(values))));
    }

    private List<Double> toList(Coordinate[] coordinates) {
        List<Double> values = new ArrayList<>(coordinates.length * 2);
        for (Coordinate coordinate : coordinates) {
            values.add(coordinate.y);
            values.add(coordinate.x);
        }
        return values;
    }
}
