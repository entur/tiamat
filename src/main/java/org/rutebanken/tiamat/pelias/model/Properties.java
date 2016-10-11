
package org.rutebanken.tiamat.pelias.model;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "id",
    "gid",
    "layer",
    "source",
    "name",
    "housenumber",
    "street",
    "postalcode",
    "country_a",
    "country",
    "region",
    "county",
    "locality",
    "neighbourhood",
    "confidence",
    "distance",
    "label"
})
public class Properties {

    @JsonProperty("id")
    private String id;
    @JsonProperty("gid")
    private String gid;
    @JsonProperty("layer")
    private String layer;
    @JsonProperty("source")
    private String source;
    @JsonProperty("name")
    private String name;
    @JsonProperty("housenumber")
    private String housenumber;
    @JsonProperty("street")
    private String street;
    @JsonProperty("postalcode")
    private String postalcode;
    @JsonProperty("country_a")
    private String countryA;
    @JsonProperty("country")
    private String country;
    @JsonProperty("region")
    private String region;
    @JsonProperty("county")
    private String county;
    @JsonProperty("locality")
    private String locality;

    @JsonProperty("localadmin")
    private String localadmin;

    @JsonProperty("neighbourhood")
    private String neighbourhood;
    @JsonProperty("confidence")
    private double confidence;
    @JsonProperty("distance")
    private double distance;
    @JsonProperty("label")
    private String label;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<>();
    /**
     *
     * @return
     *     The id
     */
    @JsonProperty("id")
    public String getId() {
        return id;
    }

    /**
     *
     * @param id
     *     The id
     */
    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     * @return
     *     The gid
     */
    @JsonProperty("gid")
    public String getGid() {
        return gid;
    }

    /**
     *
     * @param gid
     *     The gid
     */
    @JsonProperty("gid")
    public void setGid(String gid) {
        this.gid = gid;
    }

    /**
     *
     * @return
     *     The layer
     */
    @JsonProperty("layer")
    public String getLayer() {
        return layer;
    }

    /**
     *
     * @param layer
     *     The layer
     */
    @JsonProperty("layer")
    public void setLayer(String layer) {
        this.layer = layer;
    }

    /**
     *
     * @return
     *     The source
     */
    @JsonProperty("source")
    public String getSource() {
        return source;
    }

    /**
     *
     * @param source
     *     The source
     */
    @JsonProperty("source")
    public void setSource(String source) {
        this.source = source;
    }

    /**
     *
     * @return
     *     The name
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     *     The name
     */
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     *     The housenumber
     */
    @JsonProperty("housenumber")
    public String getHousenumber() {
        return housenumber;
    }

    /**
     *
     * @param housenumber
     *     The housenumber
     */
    @JsonProperty("housenumber")
    public void setHousenumber(String housenumber) {
        this.housenumber = housenumber;
    }

    /**
     *
     * @return
     *     The street
     */
    @JsonProperty("street")
    public String getStreet() {
        return street;
    }

    /**
     *
     * @param street
     *     The street
     */
    @JsonProperty("street")
    public void setStreet(String street) {
        this.street = street;
    }

    /**
     *
     * @return
     *     The postalcode
     */
    @JsonProperty("postalcode")
    public String getPostalcode() {
        return postalcode;
    }

    /**
     *
     * @param postalcode
     *     The postalcode
     */
    @JsonProperty("postalcode")
    public void setPostalcode(String postalcode) {
        this.postalcode = postalcode;
    }

    /**
     *
     * @return
     *     The countryA
     */
    @JsonProperty("country_a")
    public String getCountryA() {
        return countryA;
    }

    /**
     *
     * @param countryA
     *     The country_a
     */
    @JsonProperty("country_a")
    public void setCountryA(String countryA) {
        this.countryA = countryA;
    }

    /**
     *
     * @return
     *     The country
     */
    @JsonProperty("country")
    public String getCountry() {
        return country;
    }

    /**
     *
     * @param country
     *     The country
     */
    @JsonProperty("country")
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     *
     * @return
     *     The region
     */
    @JsonProperty("region")
    public String getRegion() {
        return region;
    }

    /**
     *
     * @param region
     *     The region
     */
    @JsonProperty("region")
    public void setRegion(String region) {
        this.region = region;
    }

    /**
     *
     * @return
     *     The county
     */
    @JsonProperty("county")
    public String getCounty() {
        return county;
    }

    /**
     *
     * @param county
     *     The county
     */
    @JsonProperty("county")
    public void setCounty(String county) {
        this.county = county;
    }

    /**
     *
     * @return
     *     The locality
     */
    @JsonProperty("locality")
    public String getLocality() {
        return locality;
    }

    /**
     *
     * @param locality
     *     The locality
     */
    @JsonProperty("locality")
    public void setLocality(String locality) {
        this.locality = locality;
    }

    /**
     *
     * @return
     *     The neighbourhood
     */
    @JsonProperty("neighbourhood")
    public String getNeighbourhood() {
        return neighbourhood;
    }

    /**
     *
     * @param neighbourhood
     *     The neighbourhood
     */
    @JsonProperty("neighbourhood")
    public void setNeighbourhood(String neighbourhood) {
        this.neighbourhood = neighbourhood;
    }

    /**
     *
     * @return
     *     The confidence
     */
    @JsonProperty("confidence")
    public double getConfidence() {
        return confidence;
    }

    /**
     *
     * @param confidence
     *     The confidence
     */
    @JsonProperty("confidence")
    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    /**
     *
     * @return
     *     The distance
     */
    @JsonProperty("distance")
    public double getDistance() {
        return distance;
    }

    /**
     *
     * @param distance
     *     The distance
     */
    @JsonProperty("distance")
    public void setDistance(double distance) {
        this.distance = distance;
    }

    /**
     *
     * @return
     *     The label
     */
    @JsonProperty("label")
    public String getLabel() {
        return label;
    }

    @JsonProperty("localadmin")
    public String getLocaladmin() {
        return localadmin;
    }

    public void setLocaladmin(String localadmin) {
        this.localadmin = localadmin;
    }

    /**
     *
     * @param label
     *     The label
     */
    @JsonProperty("label")
    public void setLabel(String label) {
        this.label = label;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
