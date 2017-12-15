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

package org.rutebanken.tiamat.importer;

import org.rutebanken.tiamat.importer.matching.TransactionalMatchingAppendingStopPlaceImporter;
import org.rutebanken.tiamat.importer.matching.StopPlaceIdMatcher;
import org.rutebanken.tiamat.importer.merging.TransactionalMergingStopPlacesImporter;
import org.rutebanken.tiamat.importer.initial.ParallelInitialStopPlaceImporter;

/**
 * Different netex import types. Mainly related to behaviour of stop places import.
 * See also https://github.com/entur/tiamat-scripts/tree/master/initial_import_example_legacy
 * for an example of initial data import.
 */
public enum ImportType {

    /**
     * This import type will, for stops, apply merging logic to merge nearby stops and quays.
     * Merge was implemented because we had duplicates of stop places and quays from different providers.
     * See {@link TransactionalMergingStopPlacesImporter}
     */
    MERGE,

    /**
     * This import type will import everything directly without any merging logic.
     * See {@link ParallelInitialStopPlaceImporter}
     */
    INITIAL,

    /**
     * Matching already existing stops on ID only. See imported-id.
     * See also {@link StopPlaceIdMatcher}
     */
    ID_MATCH,

    /**
     * Matching already existing stops on ID or nearby location. Append tariff zones and IDs.
     * See {@link TransactionalMatchingAppendingStopPlaceImporter}
     */
    MATCH
}
