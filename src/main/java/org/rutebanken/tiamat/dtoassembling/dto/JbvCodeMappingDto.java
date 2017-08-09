package org.rutebanken.tiamat.dtoassembling.dto;

public class JbvCodeMappingDto {
    public String originalId;
    public String platform;
    public String netexId;

    private static final String SEPARATOR = ",";
    private static final String CODE_PLATFORM_SEPARATOR = ":";

    public JbvCodeMappingDto(String originalId, String platform, String netexId) {
        this.originalId = originalId;
        this.netexId = netexId;
        this.platform = platform;
    }

    public String toCsvString() {
        return originalId + CODE_PLATFORM_SEPARATOR + platform + SEPARATOR + netexId;
    }

}
