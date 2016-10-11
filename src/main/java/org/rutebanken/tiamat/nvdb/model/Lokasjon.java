
package org.rutebanken.tiamat.nvdb.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    "egengeometri",
    "geometriUtm33",
    "geometriWgs84",
    "geometriForenkletUtm33",
    "geometriForenkletWgs84",
    "kommune",
    "fylke",
    "region",
    "vegAvdeling",
    "politiDistrikt",
    "kontraktsOmrade",
    "riksvegruter",
    "veglenker",
    "vegReferanser"
})
public class Lokasjon {

    @JsonProperty("egengeometri")
    private Boolean egengeometri;
    @JsonProperty("geometriUtm33")
    private String geometriUtm33;
    @JsonProperty("geometriWgs84")
    private String geometriWgs84;
    @JsonProperty("geometriForenkletUtm33")
    private String geometriForenkletUtm33;
    @JsonProperty("geometriForenkletWgs84")
    private String geometriForenkletWgs84;
    @JsonProperty("kommune")
    private Kommune kommune;
    @JsonProperty("fylke")
    private Fylke fylke;
    @JsonProperty("region")
    private Region region;
    @JsonProperty("vegAvdeling")
    private VegAvdeling vegAvdeling;
    @JsonProperty("politiDistrikt")
    private PolitiDistrikt politiDistrikt;
    @JsonProperty("kontraktsOmrade")
    private List<KontraktsOmrade> kontraktsOmrade = new ArrayList<KontraktsOmrade>();
    @JsonProperty("riksvegruter")
    private List<Riksvegruter> riksvegruter = new ArrayList<Riksvegruter>();
    @JsonProperty("veglenker")
    private List<Veglenker> veglenker = new ArrayList<Veglenker>();
    @JsonProperty("vegReferanser")
    private List<VegReferanser> vegReferanser = new ArrayList<VegReferanser>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The egengeometri
     */
    @JsonProperty("egengeometri")
    public Boolean getEgengeometri() {
        return egengeometri;
    }

    /**
     * 
     * @param egengeometri
     *     The egengeometri
     */
    @JsonProperty("egengeometri")
    public void setEgengeometri(Boolean egengeometri) {
        this.egengeometri = egengeometri;
    }

    /**
     * 
     * @return
     *     The geometriUtm33
     */
    @JsonProperty("geometriUtm33")
    public String getGeometriUtm33() {
        return geometriUtm33;
    }

    /**
     * 
     * @param geometriUtm33
     *     The geometriUtm33
     */
    @JsonProperty("geometriUtm33")
    public void setGeometriUtm33(String geometriUtm33) {
        this.geometriUtm33 = geometriUtm33;
    }

    /**
     * 
     * @return
     *     The geometriWgs84
     */
    @JsonProperty("geometriWgs84")
    public String getGeometriWgs84() {
        return geometriWgs84;
    }

    /**
     * 
     * @param geometriWgs84
     *     The geometriWgs84
     */
    @JsonProperty("geometriWgs84")
    public void setGeometriWgs84(String geometriWgs84) {
        this.geometriWgs84 = geometriWgs84;
    }

    /**
     * 
     * @return
     *     The geometriForenkletUtm33
     */
    @JsonProperty("geometriForenkletUtm33")
    public String getGeometriForenkletUtm33() {
        return geometriForenkletUtm33;
    }

    /**
     * 
     * @param geometriForenkletUtm33
     *     The geometriForenkletUtm33
     */
    @JsonProperty("geometriForenkletUtm33")
    public void setGeometriForenkletUtm33(String geometriForenkletUtm33) {
        this.geometriForenkletUtm33 = geometriForenkletUtm33;
    }

    /**
     * 
     * @return
     *     The geometriForenkletWgs84
     */
    @JsonProperty("geometriForenkletWgs84")
    public String getGeometriForenkletWgs84() {
        return geometriForenkletWgs84;
    }

    /**
     * 
     * @param geometriForenkletWgs84
     *     The geometriForenkletWgs84
     */
    @JsonProperty("geometriForenkletWgs84")
    public void setGeometriForenkletWgs84(String geometriForenkletWgs84) {
        this.geometriForenkletWgs84 = geometriForenkletWgs84;
    }

    /**
     * 
     * @return
     *     The kommune
     */
    @JsonProperty("kommune")
    public Kommune getKommune() {
        return kommune;
    }

    /**
     * 
     * @param kommune
     *     The kommune
     */
    @JsonProperty("kommune")
    public void setKommune(Kommune kommune) {
        this.kommune = kommune;
    }

    /**
     * 
     * @return
     *     The fylke
     */
    @JsonProperty("fylke")
    public Fylke getFylke() {
        return fylke;
    }

    /**
     * 
     * @param fylke
     *     The fylke
     */
    @JsonProperty("fylke")
    public void setFylke(Fylke fylke) {
        this.fylke = fylke;
    }

    /**
     * 
     * @return
     *     The region
     */
    @JsonProperty("region")
    public Region getRegion() {
        return region;
    }

    /**
     * 
     * @param region
     *     The region
     */
    @JsonProperty("region")
    public void setRegion(Region region) {
        this.region = region;
    }

    /**
     * 
     * @return
     *     The vegAvdeling
     */
    @JsonProperty("vegAvdeling")
    public VegAvdeling getVegAvdeling() {
        return vegAvdeling;
    }

    /**
     * 
     * @param vegAvdeling
     *     The vegAvdeling
     */
    @JsonProperty("vegAvdeling")
    public void setVegAvdeling(VegAvdeling vegAvdeling) {
        this.vegAvdeling = vegAvdeling;
    }

    /**
     * 
     * @return
     *     The politiDistrikt
     */
    @JsonProperty("politiDistrikt")
    public PolitiDistrikt getPolitiDistrikt() {
        return politiDistrikt;
    }

    /**
     * 
     * @param politiDistrikt
     *     The politiDistrikt
     */
    @JsonProperty("politiDistrikt")
    public void setPolitiDistrikt(PolitiDistrikt politiDistrikt) {
        this.politiDistrikt = politiDistrikt;
    }

    /**
     * 
     * @return
     *     The kontraktsOmrade
     */
    @JsonProperty("kontraktsOmrade")
    public List<KontraktsOmrade> getKontraktsOmrade() {
        return kontraktsOmrade;
    }

    /**
     * 
     * @param kontraktsOmrade
     *     The kontraktsOmrade
     */
    @JsonProperty("kontraktsOmrade")
    public void setKontraktsOmrade(List<KontraktsOmrade> kontraktsOmrade) {
        this.kontraktsOmrade = kontraktsOmrade;
    }

    /**
     * 
     * @return
     *     The riksvegruter
     */
    @JsonProperty("riksvegruter")
    public List<Riksvegruter> getRiksvegruter() {
        return riksvegruter;
    }

    /**
     * 
     * @param riksvegruter
     *     The riksvegruter
     */
    @JsonProperty("riksvegruter")
    public void setRiksvegruter(List<Riksvegruter> riksvegruter) {
        this.riksvegruter = riksvegruter;
    }

    /**
     * 
     * @return
     *     The veglenker
     */
    @JsonProperty("veglenker")
    public List<Veglenker> getVeglenker() {
        return veglenker;
    }

    /**
     * 
     * @param veglenker
     *     The veglenker
     */
    @JsonProperty("veglenker")
    public void setVeglenker(List<Veglenker> veglenker) {
        this.veglenker = veglenker;
    }

    /**
     * 
     * @return
     *     The vegReferanser
     */
    @JsonProperty("vegReferanser")
    public List<VegReferanser> getVegReferanser() {
        return vegReferanser;
    }

    /**
     * 
     * @param vegReferanser
     *     The vegReferanser
     */
    @JsonProperty("vegReferanser")
    public void setVegReferanser(List<VegReferanser> vegReferanser) {
        this.vegReferanser = vegReferanser;
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
