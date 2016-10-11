package org.rutebanken.tiamat.nvdb.model.search;

public class SokeLokasjon {

    private String bbox;
    private String srid;

    public SokeLokasjon(String bbox, String srid) {
        this.bbox = bbox;
        this.srid = srid;
    }

    public String getBbox() {
        return bbox;
    }

    public void setBbox(String bbox) {
        this.bbox = bbox;
    }

    public String getSrid() {
        return srid;
    }

    public void setSrid(String srid) {
        this.srid = srid;
    }

}
