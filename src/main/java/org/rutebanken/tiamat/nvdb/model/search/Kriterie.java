
package org.rutebanken.tiamat.nvdb.model.search;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "lokasjon",
    "objektTyper"
})
public class Kriterie {

    @JsonProperty("objektTyper")
    private List<ObjektType> objektType = new ArrayList<ObjektType>();

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Kriterie(SokeLokasjon lokasjon, List<ObjektType> objektType) {
        this.lokasjon = lokasjon;
        this.objektType = objektType;
    }

    private SokeLokasjon lokasjon;

    public Kriterie(List<ObjektType> objektType) {
        this.objektType = objektType;
    }

    public Kriterie() {
    }

    @JsonProperty("lokasjon")
    public SokeLokasjon getLokasjon() {
        return lokasjon;
    }

    @JsonProperty("lokasjon")
    public void setLokasjon(SokeLokasjon lokasjon) {
        this.lokasjon = lokasjon;
    }

        /**
         *
         * @return
         *     The objektType
         */
    @JsonProperty("objektTyper")
    public List<ObjektType> getObjektType() {
        return objektType;
    }

    /**
     * 
     * @param objektType
     *     The objektType
     */
    @JsonProperty("objektTyper")
    public void setObjektType(List<ObjektType> objektType) {
        this.objektType = objektType;
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
