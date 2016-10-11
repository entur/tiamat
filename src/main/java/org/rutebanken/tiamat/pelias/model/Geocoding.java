
package org.rutebanken.tiamat.pelias.model;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "version",
    "attribution",
    "query",
    "engine",
    "timestamp"
})
public class Geocoding {

    @JsonProperty("version")
    private String version;
    @JsonProperty("attribution")
    private String attribution;
    @JsonProperty("query")
    private Query query;
    @JsonProperty("engine")
    private Engine engine;
    @JsonProperty("timestamp")
    private long timestamp;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<>();

    /**
     * 
     * @return
     *     The version
     */
    @JsonProperty("version")
    public String getVersion() {
        return version;
    }

    /**
     * 
     * @param version
     *     The version
     */
    @JsonProperty("version")
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * 
     * @return
     *     The attribution
     */
    @JsonProperty("attribution")
    public String getAttribution() {
        return attribution;
    }

    /**
     * 
     * @param attribution
     *     The attribution
     */
    @JsonProperty("attribution")
    public void setAttribution(String attribution) {
        this.attribution = attribution;
    }

    /**
     * 
     * @return
     *     The query
     */
    @JsonProperty("query")
    public Query getQuery() {
        return query;
    }

    /**
     * 
     * @param query
     *     The query
     */
    @JsonProperty("query")
    public void setQuery(Query query) {
        this.query = query;
    }

    /**
     * 
     * @return
     *     The engine
     */
    @JsonProperty("engine")
    public Engine getEngine() {
        return engine;
    }

    /**
     * 
     * @param engine
     *     The engine
     */
    @JsonProperty("engine")
    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    /**
     * 
     * @return
     *     The timestamp
     */
    @JsonProperty("timestamp")
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * 
     * @param timestamp
     *     The timestamp
     */
    @JsonProperty("timestamp")
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
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
