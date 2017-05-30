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
