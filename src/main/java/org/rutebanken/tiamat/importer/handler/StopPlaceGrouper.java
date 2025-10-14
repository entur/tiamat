package org.rutebanken.tiamat.importer.handler;

import org.rutebanken.tiamat.model.StopPlace;

import java.util.List;

public record StopPlaceGrouper(List<StopPlace> parents, List<StopPlace> children, List<StopPlace> monomodals) {}
