
package org.rutebanken.tiamat.nvdb.model.search;

import com.fasterxml.jackson.annotation.*;
import org.rutebanken.tiamat.nvdb.model.VegObjekt;

import javax.annotation.Generated;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "typeId",
    "statistikk",
    "vegObjekter"
})
public class Resultater {

    @JsonProperty("typeId")
    private Integer typeId;
    @JsonProperty("statistikk")
    private Statistikk statistikk;
    @JsonProperty("vegObjekter")
    private List<VegObjekt> vegObjekter = new ArrayList<>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The typeId
     */
    @JsonProperty("typeId")
    public Integer getTypeId() {
        return typeId;
    }

    /**
     * 
     * @param typeId
     *     The typeId
     */
    @JsonProperty("typeId")
    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    /**
     * 
     * @return
     *     The statistikk
     */
    @JsonProperty("statistikk")
    public Statistikk getStatistikk() {
        return statistikk;
    }

    /**
     * 
     * @param statistikk
     *     The statistikk
     */
    @JsonProperty("statistikk")
    public void setStatistikk(Statistikk statistikk) {
        this.statistikk = statistikk;
    }

    /**
     * 
     * @return
     *     The vegObjekter
     */
    @JsonProperty("vegObjekter")
    public List<VegObjekt> getVegObjekter() {
        return vegObjekter;
    }

    /**
     * 
     * @param vegObjekter
     *     The vegObjekter
     */
    @JsonProperty("vegObjekter")
    public void setVegObjekter(List<VegObjekt> vegObjekter) {
        this.vegObjekter = vegObjekter;
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
