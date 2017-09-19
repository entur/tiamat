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

package org.rutebanken.tiamat.diff.generic;

import java.util.Set;

public class GenericDiffConfig {

    /**
     * Current depth of recursive progression
     * Actually, not a configuration, but a status.
     */
    protected int depth;

    protected Set<String> ignoreFields;

    protected Set<String> identifiers;

    protected Set<Class> onlyDoEqualsCheck;

    public static GenericDiffConfigBuilder builder() {
        return new GenericDiffConfigBuilder();
    }

    public static class GenericDiffConfigBuilder {

        private final GenericDiffConfig genericDiffConfig;
        public GenericDiffConfigBuilder() {
            genericDiffConfig = new GenericDiffConfig();
        }

        /**
         * Fields to be treated as identifiers in collections (if they apply for type)
         */
        public GenericDiffConfigBuilder identifiers(Set<String> identifiers) {
            genericDiffConfig.identifiers = identifiers;
            return this;
        }

        /**
         * Do not compare these types recursively. Only check the equals method.
         */
        public GenericDiffConfigBuilder onlyDoEqualsCheck(Set<Class> onlyDoEqualsCheck ) {
            genericDiffConfig.onlyDoEqualsCheck = onlyDoEqualsCheck;
            return this;
        }

        /**
         * Common field names to ignore for all objects
         */
        public GenericDiffConfigBuilder ignoreFields(Set<String> ignoreFields) {
            genericDiffConfig.ignoreFields = ignoreFields;
            return this;
        }

        public GenericDiffConfig build() {
            return genericDiffConfig;
        }

    }

}
