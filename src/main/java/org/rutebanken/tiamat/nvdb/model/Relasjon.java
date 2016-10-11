
package org.rutebanken.tiamat.nvdb.model;

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
    "rel",
    "kardinalitet",
    "uri",
    "typeId",
    "typeNavn"
})
public class Relasjon {

    @JsonProperty("rel")
    private String rel;
    @JsonProperty("kardinalitet")
    private String kardinalitet;
    @JsonProperty("uri")
    private String uri;
    @JsonProperty("typeId")
    private Integer typeId;
    @JsonProperty("typeNavn")
    private String typeNavn;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The rel
     */
    @JsonProperty("rel")
    public String getRel() {
        return rel;
    }

    /**
     * 
     * @param rel
     *     The rel
     */
    @JsonProperty("rel")
    public void setRel(String rel) {
        this.rel = rel;
    }

    /**
     * 
     * @return
     *     The kardinalitet
     */
    @JsonProperty("kardinalitet")
    public String getKardinalitet() {
        return kardinalitet;
    }

    /**
     * 
     * @param kardinalitet
     *     The kardinalitet
     */
    @JsonProperty("kardinalitet")
    public void setKardinalitet(String kardinalitet) {
        this.kardinalitet = kardinalitet;
    }

    /**
     * 
     * @return
     *     The uri
     */
    @JsonProperty("uri")
    public String getUri() {
        return uri;
    }

    /**
     * 
     * @param uri
     *     The uri
     */
    @JsonProperty("uri")
    public void setUri(String uri) {
        this.uri = uri;
    }

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
     *     The typeNavn
     */
    @JsonProperty("typeNavn")
    public String getTypeNavn() {
        return typeNavn;
    }

    /**
     * 
     * @param typeNavn
     *     The typeNavn
     */
    @JsonProperty("typeNavn")
    public void setTypeNavn(String typeNavn) {
        this.typeNavn = typeNavn;
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
