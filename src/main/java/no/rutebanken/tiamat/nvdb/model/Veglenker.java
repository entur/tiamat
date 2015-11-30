
package no.rutebanken.tiamat.nvdb.model;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "id",
    "fra",
    "til",
    "direction",
    "felt",
    "sidepos"
})
public class Veglenker {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("fra")
    private Double fra;
    @JsonProperty("til")
    private Double til;
    @JsonProperty("direction")
    private String direction;
    @JsonProperty("felt")
    private Object felt;
    @JsonProperty("sidepos")
    private String sidepos;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The id
     */
    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    /**
     * 
     * @param id
     *     The id
     */
    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 
     * @return
     *     The fra
     */
    @JsonProperty("fra")
    public Double getFra() {
        return fra;
    }

    /**
     * 
     * @param fra
     *     The fra
     */
    @JsonProperty("fra")
    public void setFra(Double fra) {
        this.fra = fra;
    }

    /**
     * 
     * @return
     *     The til
     */
    @JsonProperty("til")
    public Double getTil() {
        return til;
    }

    /**
     * 
     * @param til
     *     The til
     */
    @JsonProperty("til")
    public void setTil(Double til) {
        this.til = til;
    }

    /**
     * 
     * @return
     *     The direction
     */
    @JsonProperty("direction")
    public String getDirection() {
        return direction;
    }

    /**
     * 
     * @param direction
     *     The direction
     */
    @JsonProperty("direction")
    public void setDirection(String direction) {
        this.direction = direction;
    }

    /**
     * 
     * @return
     *     The felt
     */
    @JsonProperty("felt")
    public Object getFelt() {
        return felt;
    }

    /**
     * 
     * @param felt
     *     The felt
     */
    @JsonProperty("felt")
    public void setFelt(Object felt) {
        this.felt = felt;
    }

    /**
     * 
     * @return
     *     The sidepos
     */
    @JsonProperty("sidepos")
    public String getSidepos() {
        return sidepos;
    }

    /**
     * 
     * @param sidepos
     *     The sidepos
     */
    @JsonProperty("sidepos")
    public void setSidepos(String sidepos) {
        this.sidepos = sidepos;
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
