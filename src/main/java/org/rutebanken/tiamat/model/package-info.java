
@GenericGenerator(name = "sequence_per_table_generator",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {
                @Parameter(name = SequenceStyleGenerator.CONFIG_PREFER_SEQUENCE_PER_ENTITY, value = "true"),
                @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "10")
        })
package org.rutebanken.tiamat.model;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;