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

public class VersionFrameDefaultsStructure {

    protected DataSourceRefStructure defaultDataSourceRef;
    protected LocaleStructure defaultLocale;
    protected String defaultLocationSystem;
    protected SystemOfUnits defaultSystemOfUnits;
    protected String defaultCurrency;

    public DataSourceRefStructure getDefaultDataSourceRef() {
        return defaultDataSourceRef;
    }

    public void setDefaultDataSourceRef(DataSourceRefStructure value) {
        this.defaultDataSourceRef = value;
    }

    public LocaleStructure getDefaultLocale() {
        return defaultLocale;
    }

    public void setDefaultLocale(LocaleStructure value) {
        this.defaultLocale = value;
    }

    public String getDefaultLocationSystem() {
        return defaultLocationSystem;
    }

    public void setDefaultLocationSystem(String value) {
        this.defaultLocationSystem = value;
    }

    public SystemOfUnits getDefaultSystemOfUnits() {
        return defaultSystemOfUnits;
    }

    public void setDefaultSystemOfUnits(SystemOfUnits value) {
        this.defaultSystemOfUnits = value;
    }

    public String getDefaultCurrency() {
        return defaultCurrency;
    }

    public void setDefaultCurrency(String value) {
        this.defaultCurrency = value;
    }

}
