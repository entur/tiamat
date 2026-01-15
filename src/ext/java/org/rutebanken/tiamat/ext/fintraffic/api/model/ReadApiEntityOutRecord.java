package org.rutebanken.tiamat.ext.fintraffic.api.model;

/**
 * Represents an entity read from the Read API Database table
 * @param type Entity type
 * @param xml Entity XML content as byte array
 */
public record ReadApiEntityOutRecord(String type, byte[] xml) {}
