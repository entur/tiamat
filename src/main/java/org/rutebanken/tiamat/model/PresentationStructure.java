

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


public class PresentationStructure {

    protected byte[] colour;
    protected String colourName;
    protected byte[] textColour;
    protected String textColourName;
    protected String textFont;
    protected String textFontName;
    protected String textLanguage;
    protected InfoLinks infoLinks;

    public byte[] getColour() {
        return colour;
    }

    public void setColour(byte[] value) {
        this.colour = value;
    }

    public String getColourName() {
        return colourName;
    }

    public void setColourName(String value) {
        this.colourName = value;
    }

    public byte[] getTextColour() {
        return textColour;
    }

    public void setTextColour(byte[] value) {
        this.textColour = value;
    }

    public String getTextColourName() {
        return textColourName;
    }

    public void setTextColourName(String value) {
        this.textColourName = value;
    }

    public String getTextFont() {
        return textFont;
    }

    public void setTextFont(String value) {
        this.textFont = value;
    }

    public String getTextFontName() {
        return textFontName;
    }

    public void setTextFontName(String value) {
        this.textFontName = value;
    }

    public String getTextLanguage() {
        return textLanguage;
    }

    public void setTextLanguage(String value) {
        this.textLanguage = value;
    }

    public InfoLinks getInfoLinks() {
        return infoLinks;
    }

    public void setInfoLinks(InfoLinks value) {
        this.infoLinks = value;
    }

}
