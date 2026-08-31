package org.rutebanken.tiamat.ext.fintraffic;

public final class FintrafficConstants {

    public static final String AREA_CODE_REGEX = "[A-ZÅÄÖ]{3}";
    // 3 digits for normal municipalities, 4 digits for special areas like Haaparanta and Eurooppa
    public static final String MUNICIPALITY_CODE_REGEX = "\\d{3,4}";

    private FintrafficConstants() {}

    public static boolean isValidAreaCode(String code) {
        return code != null && code.matches(AREA_CODE_REGEX);
    }

    public static boolean isValidMunicipalityCode(String code) {
        return code != null && code.matches(MUNICIPALITY_CODE_REGEX);
    }

    public static void validateAreaCodes(String[] areaCodes) {
        if (areaCodes != null) {
            for (String code : areaCodes) {
                if (!isValidAreaCode(code)) {
                    throw new IllegalArgumentException("Invalid areaCode: " + code);
                }
            }
        }
    }

    public static void validateMunicipalityCodes(String[] municipalityCodes) {
        if (municipalityCodes != null) {
            for (String code : municipalityCodes) {
                if (!isValidMunicipalityCode(code)) {
                    throw new IllegalArgumentException("Invalid municipalityCode: " + code);
                }
            }
        }
    }
}
