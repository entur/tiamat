
package org.rutebanken.tiamat.pelias.model;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "geocoding",
    "type",
    "features",
    "bbox"
})
public class ReverseLookupResult {

    @JsonProperty("geocoding")
    private Geocoding geocoding;
    @JsonProperty("type")
    private String type;
    @JsonProperty("features")
    private List<Feature> features = new ArrayList<>();
    @JsonProperty("bbox")
    private List<Double> bbox = new ArrayList<>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<>();

    /**
     * 
     * @return
     *     The geocoding
     */
    @JsonProperty("geocoding")
    public Geocoding getGeocoding() {
        return geocoding;
    }

    /**
     * 
     * @param geocoding
     *     The geocoding
     */
    @JsonProperty("geocoding")
    public void setGeocoding(Geocoding geocoding) {
        this.geocoding = geocoding;
    }

    /**
     * 
     * @return
     *     The type
     */
    @JsonProperty("type")
    public String getType() {
        return type;
    }

    /**
     * 
     * @param type
     *     The type
     */
    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 
     * @return
     *     The features
     */
    @JsonProperty("features")
    public List<Feature> getFeatures() {
        return features;
    }

    /**
     * 
     * @param features
     *     The features
     */
    @JsonProperty("features")
    public void setFeatures(List<Feature> features) {
        this.features = features;
    }

    /**
     * 
     * @return
     *     The bbox
     */
    @JsonProperty("bbox")
    public List<Double> getBbox() {
        return bbox;
    }

    /**
     * 
     * @param bbox
     *     The bbox
     */
    @JsonProperty("bbox")
    public void setBbox(List<Double> bbox) {
        this.bbox = bbox;
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
