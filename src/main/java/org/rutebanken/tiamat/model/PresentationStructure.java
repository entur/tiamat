/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.rutebanken.tiamat.model;

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
