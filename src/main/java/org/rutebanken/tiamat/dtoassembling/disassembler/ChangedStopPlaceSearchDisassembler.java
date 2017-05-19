package org.rutebanken.tiamat.dtoassembling.disassembler;

import org.rutebanken.tiamat.dtoassembling.dto.ChangedStopPlaceSearchDto;
import org.rutebanken.tiamat.repository.ChangedStopPlaceSearch;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

@Component
public class ChangedStopPlaceSearchDisassembler {
    public static final int PER_PAGE_DEFAULT = 1000;

    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSXXXX";

    private static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);


    public ChangedStopPlaceSearch disassemble(ChangedStopPlaceSearchDto dto) {
        Instant from = mapFrom(dto.from);
        Instant to = mapTo(dto.to);

        int perPage = dto.perPage > 0 ? dto.perPage : PER_PAGE_DEFAULT;

        Pageable pageable = new PageRequest(dto.page, perPage);
        return new ChangedStopPlaceSearch(from, to, pageable);
    }


    private Instant mapFrom(String from) {
        Instant fromInstant;
        if (from == null) {
            fromInstant = Instant.EPOCH;
        } else {
            fromInstant = Instant.from(FORMATTER.parse(from));
        }
        return fromInstant;
    }

    public Instant mapTo(String to) {
        Instant toInstant;
        if (to == null) {
            toInstant = Instant.now();
        } else {
            toInstant = Instant.from(FORMATTER.parse(to));
        }
        return toInstant;
    }

}
