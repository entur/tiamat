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

import org.hibernate.MappingException;
import org.hibernate.generator.GeneratorCreationContext;
import org.hibernate.id.OptimizableGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

import java.lang.reflect.Member;
import java.util.Properties;

/**
 * Derives a per-entity sequence name by converting the entity's simple class name
 * from CamelCase to snake_case and appending "_seq" (e.g. StopPlace → stop_place_seq).
 * Replaces the deprecated {@code @GenericGenerator} with {@code type = SequenceStyleGenerator.class}
 * and {@code CONFIG_SEQUENCE_PER_ENTITY_SUFFIX = "_seq"}.
 *
 * Hibernate invokes the 3-arg constructor reflectively during bootstrap (via @IdGeneratorType),
 * then calls callConfigure() which delegates to configure() below. The {@code member} parameter
 * is part of Hibernate's required signature — without it, Hibernate falls back to its
 * ManagedBeanRegistry path and Spring tries to autowire the generator as a bean, which fails
 * because @PerTableSequence is an annotation type, not a bean.
 */
public class PerTableSequenceGenerator extends SequenceStyleGenerator {

    private final String sequenceName;
    private final int allocationSize;

    @SuppressWarnings("unused")
    public PerTableSequenceGenerator(PerTableSequence config, Member member, GeneratorCreationContext ctx) {
        String simpleName = ctx.getPersistentClass().getMappedClass().getSimpleName();
        // Match only lowercase→uppercase boundaries so class names containing an underscore
        // (e.g. InstalledEquipment_VersionStructure) don't produce a doubled "__".
        this.sequenceName = simpleName.replaceAll("(?<=[a-z])(?=[A-Z])", "_").toLowerCase() + "_seq";
        this.allocationSize = config.allocationSize();
        // configure() is NOT called here — Hibernate calls it via callConfigure() after instantiation
    }

    @Override
    public void configure(GeneratorCreationContext creationContext, Properties parameters) throws MappingException {
        parameters.setProperty(SEQUENCE_PARAM, sequenceName);
        parameters.setProperty(OptimizableGenerator.INCREMENT_PARAM, String.valueOf(allocationSize));
        parameters.setProperty(OptimizableGenerator.INITIAL_PARAM, "1");
        super.configure(creationContext, parameters);
    }
}
