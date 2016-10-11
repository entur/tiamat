
package org.rutebanken.tiamat.nvdb.model;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Avgrenset område med ett eller flere punkt for av/påstigning av kollektivt reisemiddel.  Det defineres egne holdeplasser for hver type transportmiddel. (Noe varierende kvalitet. Vil utgå på sikt. Data overføres til Holdeplassutrustning)
 * https://www.vegvesen.no/nvdb/api/datakatalog/objekttyper/751
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "objektId",
    "objektTypeId",
    "objektTypeNavn",
    "versjonsId",
    "self",
    "definisjon",
    "startDato",
    "sluttDato",
    "egenskaper",
    "assosiasjoner",
    "modifisert",
    "lokasjon"
})
public class VegObjekt {

    @JsonProperty("objektId")
    private Integer objektId;
    @JsonProperty("objektTypeId")
    private Integer objektTypeId;
    @JsonProperty("objektTypeNavn")
    private String objektTypeNavn;
    @JsonProperty("versjonsId")
    private Integer versjonsId;
    @JsonProperty("self")
    private Self self;
    @JsonProperty("definisjon")
    private Definisjon definisjon;
    @JsonProperty("startDato")
    private String startDato;
    @JsonProperty("sluttDato")
    private String sluttDato;
    @JsonProperty("egenskaper")
    private List<Egenskap> egenskaper = new ArrayList<Egenskap>();
    @JsonProperty("assosiasjoner")
    private List<Assosiasjoner> assosiasjoner = new ArrayList<Assosiasjoner>();
    @JsonProperty("modifisert")
    private String modifisert;
    @JsonProperty("lokasjon")
    private Lokasjon lokasjon;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The objektId
     */
    @JsonProperty("objektId")
    public Integer getObjektId() {
        return objektId;
    }

    /**
     * 
     * @param objektId
     *     The objektId
     */
    @JsonProperty("objektId")
    public void setObjektId(Integer objektId) {
        this.objektId = objektId;
    }

    /**
     * 
     * @return
     *     The objektTypeId
     */
    @JsonProperty("objektTypeId")
    public Integer getObjektTypeId() {
        return objektTypeId;
    }

    /**
     * 
     * @param objektTypeId
     *     The objektTypeId
     */
    @JsonProperty("objektTypeId")
    public void setObjektTypeId(Integer objektTypeId) {
        this.objektTypeId = objektTypeId;
    }

    /**
     * 
     * @return
     *     The objektTypeNavn
     */
    @JsonProperty("objektTypeNavn")
    public String getObjektTypeNavn() {
        return objektTypeNavn;
    }

    /**
     * 
     * @param objektTypeNavn
     *     The objektTypeNavn
     */
    @JsonProperty("objektTypeNavn")
    public void setObjektTypeNavn(String objektTypeNavn) {
        this.objektTypeNavn = objektTypeNavn;
    }

    /**
     * 
     * @return
     *     The versjonsId
     */
    @JsonProperty("versjonsId")
    public Integer getVersjonsId() {
        return versjonsId;
    }

    /**
     * 
     * @param versjonsId
     *     The versjonsId
     */
    @JsonProperty("versjonsId")
    public void setVersjonsId(Integer versjonsId) {
        this.versjonsId = versjonsId;
    }

    /**
     * 
     * @return
     *     The self
     */
    @JsonProperty("self")
    public Self getSelf() {
        return self;
    }

    /**
     * 
     * @param self
     *     The self
     */
    @JsonProperty("self")
    public void setSelf(Self self) {
        this.self = self;
    }

    /**
     * 
     * @return
     *     The definisjon
     */
    @JsonProperty("definisjon")
    public Definisjon getDefinisjon() {
        return definisjon;
    }

    /**
     * 
     * @param definisjon
     *     The definisjon
     */
    @JsonProperty("definisjon")
    public void setDefinisjon(Definisjon definisjon) {
        this.definisjon = definisjon;
    }

    /**
     * 
     * @return
     *     The startDato
     */
    @JsonProperty("startDato")
    public String getStartDato() {
        return startDato;
    }

    /**
     * 
     * @param startDato
     *     The startDato
     */
    @JsonProperty("startDato")
    public void setStartDato(String startDato) {
        this.startDato = startDato;
    }

    /**
     * 
     * @return
     *     The sluttDato
     */
    @JsonProperty("sluttDato")
    public String getSluttDato() {
        return sluttDato;
    }

    /**
     * 
     * @param sluttDato
     *     The sluttDato
     */
    @JsonProperty("sluttDato")
    public void setSluttDato(String sluttDato) {
        this.sluttDato = sluttDato;
    }

    /**
     * 
     * @return
     *     The egenskaper
     */
    @JsonProperty("egenskaper")
    public List<Egenskap> getEgenskaper() {
        return egenskaper;
    }

    /**
     * 
     * @param egenskaper
     *     The egenskaper
     */
    @JsonProperty("egenskaper")
    public void setEgenskaper(List<Egenskap> egenskaper) {
        this.egenskaper = egenskaper;
    }

    /**
     * 
     * @return
     *     The assosiasjoner
     */
    @JsonProperty("assosiasjoner")
    public List<Assosiasjoner> getAssosiasjoner() {
        return assosiasjoner;
    }

    /**
     * 
     * @param assosiasjoner
     *     The assosiasjoner
     */
    @JsonProperty("assosiasjoner")
    public void setAssosiasjoner(List<Assosiasjoner> assosiasjoner) {
        this.assosiasjoner = assosiasjoner;
    }

    /**
     * 
     * @return
     *     The modifisert
     */
    @JsonProperty("modifisert")
    public String getModifisert() {
        return modifisert;
    }

    /**
     * 
     * @param modifisert
     *     The modifisert
     */
    @JsonProperty("modifisert")
    public void setModifisert(String modifisert) {
        this.modifisert = modifisert;
    }

    /**
     * 
     * @return
     *     The lokasjon
     */
    @JsonProperty("lokasjon")
    public Lokasjon getLokasjon() {
        return lokasjon;
    }

    /**
     * 
     * @param lokasjon
     *     The lokasjon
     */
    @JsonProperty("lokasjon")
    public void setLokasjon(Lokasjon lokasjon) {
        this.lokasjon = lokasjon;
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
