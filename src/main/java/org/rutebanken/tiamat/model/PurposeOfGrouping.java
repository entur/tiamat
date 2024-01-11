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

package org.rutebanken.tiamat.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        indexes = {
                @Index(name = "purpose_of_grouping_name_value_index", columnList = "name_value")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "purpose_of_grouping_netex_id_version_constraint", columnNames = {"netexId"}),
                @UniqueConstraint(name = "purpose_of_grouping_name_value_constraint", columnNames = {"name_value"})
        }
)
public class PurposeOfGrouping
        extends PurposeOfGrouping_ValueStructure {


}
