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
import net.opengis.gml._3.AbstractSurfaceType;
import net.opengis.gml._3.DirectPositionListType;
import net.opengis.gml._3.LinearRingType;
import net.opengis.gml._3.MultiSurfaceType;
import net.opengis.gml._3.ObjectFactory;
import net.opengis.gml._3.PolygonType;
import net.opengis.gml._3.SurfacePropertyType;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiPolygon;
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
import java.util.stream.Collectors;

@Component
public class MultiSurfaceConverter extends BidirectionalConverter<MultiPolygon, MultiSurfaceType> {

    private static final Logger logger = LoggerFactory.getLogger(MultiSurfaceConverter.class);

    private static final ObjectFactory openGisObjectFactory = new ObjectFactory();

    private static final AtomicLong multiSurfaceIdCounter = new AtomicLong();

    private final GeometryFactory geometryFactory;

    private final DoubleValuesToCoordinateSequence doubleValuesToCoordinateSequence;

    @Autowired
    public MultiSurfaceConverter(GeometryFactory geometryFactory, DoubleValuesToCoordinateSequence doubleValuesToCoordinateSequence) {
        this.geometryFactory = geometryFactory;
        this.doubleValuesToCoordinateSequence = doubleValuesToCoordinateSequence;
    }

    @Override
    public MultiPolygon convertFrom(MultiSurfaceType multiSurfaceType, Type<MultiPolygon> type, MappingContext mappingContext) {
        if (multiSurfaceType == null || multiSurfaceType.getSurfaceMember() == null || multiSurfaceType.getSurfaceMember().isEmpty()) {
            logger.warn("Cannot convert MultiSurfaceType - null or empty surface members");
            return null;
        }

        List<Polygon> polygons = new ArrayList<>();

        for (SurfacePropertyType surfaceProperty : multiSurfaceType.getSurfaceMember()) {
            JAXBElement<? extends AbstractSurfaceType> abstractSurface = surfaceProperty.getAbstractSurface();
            if (abstractSurface != null && abstractSurface.getValue() instanceof PolygonType polygonType) {
                Polygon polygon = convertPolygonType(polygonType);
                if (polygon != null) {
                    polygons.add(polygon);
                }
            }
        }

        if (polygons.isEmpty()) {
            logger.warn("No valid polygons found in MultiSurfaceType");
            return null;
        }

        return geometryFactory.createMultiPolygon(polygons.toArray(new Polygon[0]));
    }

    private Polygon convertPolygonType(PolygonType polygonType) {
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
                        .toArray(LinearRing[]::new);
            }

            return new Polygon(exteriorLinearRing, interiorHoles, geometryFactory);
        }

        return null;
    }

    private List<Double> extractValues(AbstractRingPropertyType abstractRingPropertyType) {
        return Optional.of(abstractRingPropertyType)
                .map(AbstractRingPropertyType::getAbstractRing)
                .map(JAXBElement::getValue)
                .map(abstractRing -> ((LinearRingType) abstractRing))
                .map(LinearRingType::getPosList)
                .map(DirectPositionListType::getValue)
                .orElse(null);
    }

    @Override
    public MultiSurfaceType convertTo(MultiPolygon multiPolygon, Type<MultiSurfaceType> type, MappingContext mappingContext) {
        if (multiPolygon == null || multiPolygon.getNumGeometries() == 0) {
            return null;
        }

        MultiSurfaceType multiSurfaceType = new MultiSurfaceType()
                .withId("GEN-MultiSurfaceType-" + multiSurfaceIdCounter.incrementAndGet());

        for (int i = 0; i < multiPolygon.getNumGeometries(); i++) {
            Polygon polygon = (Polygon) multiPolygon.getGeometryN(i);
            PolygonType polygonType = convertToPolygonType(polygon, i);
            if (polygonType != null) {
                SurfacePropertyType surfaceProperty = new SurfacePropertyType()
                        .withAbstractSurface(openGisObjectFactory.createPolygon(polygonType));
                multiSurfaceType.getSurfaceMember().add(surfaceProperty);
            }
        }

        return multiSurfaceType;
    }

    private PolygonType convertToPolygonType(Polygon polygon, int index) {
        Optional<Coordinate[]> optionalCoordinates = Optional.ofNullable(polygon)
                .map(Polygon::getExteriorRing)
                .map(LineString::getCoordinates)
                .filter(coordinates -> coordinates.length > 0);

        if (optionalCoordinates.isPresent()) {
            List<Double> values = toList(optionalCoordinates.get());
            return new PolygonType()
                    .withId("GEN-PolygonType-" + multiSurfaceIdCounter.get() + "-" + index)
                    .withExterior(of(values))
                    .withInterior(ofInteriorRings(polygon));
        }

        return null;
    }

    private List<AbstractRingPropertyType> ofInteriorRings(Polygon polygon) {
        List<AbstractRingPropertyType> list = new ArrayList<>();
        for (int n = 0; n < polygon.getNumInteriorRing(); n++) {
            if (polygon.getInteriorRingN(n).getCoordinates() != null) {
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