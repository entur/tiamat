package no.rutebanken.tiamat.ifopt.transfer.dto;

import java.util.List;

public class StopPlaceDto extends BaseDto {

    public String stopPlaceType;
    public List<QuayDto> quays;

    public String municipality;
    public String county;
}
