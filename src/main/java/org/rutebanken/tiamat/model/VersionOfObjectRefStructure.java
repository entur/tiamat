package org.rutebanken.tiamat.model;

import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLType;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@MappedSuperclass
public @GraphQLType class VersionOfObjectRefStructure implements Serializable {

    @GraphQLField
    protected String ref;

    @GraphQLField
    protected String version;

    public String getRef() {
        return ref;
    }

    public void setRef(String value) {
        this.ref = value;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String value) {
        this.version = value;
    }
}
