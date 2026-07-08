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

package org.rutebanken.tiamat.importer.modifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;

/**
 * Remove certain words from strings.
 * Typically words with brackets from stop place or quay names.
 */
@Component
public class WordsRemover {
    private static final Logger logger = LoggerFactory.getLogger(WordsRemover.class);
    private final List<Pattern> patterns = new ArrayList<>();

    public WordsRemover() {
        this(Arrays.asList("båt", "buss", "tog", "bussterminal", "bybanestopp"));
    }
    public WordsRemover(List<String> words) {

        for(String word : words) {
            patterns.add(Pattern.compile("\\("+word+"\\)\\s*$", Pattern.CASE_INSENSITIVE));
            patterns.add(Pattern.compile("\\["+word+"\\]\\s*$", Pattern.CASE_INSENSITIVE));
            patterns.add(Pattern.compile(",\\s"+word+"\\s*$", Pattern.CASE_INSENSITIVE));
        }
        logger.info("Patterns: {}", patterns.stream().map(pattern -> "\""+pattern+"\"").collect(toList()));
    }

    public String remove(String name) {
        String returnString = name;
        for(Pattern pattern : patterns) {
            Matcher matcher = pattern.matcher(returnString);
            if(matcher.find()) {
                String replaced = matcher.replaceAll("").trim();
                logger.info("Changed name '{}' to '{}' using pattern: {}", returnString, replaced, pattern.pattern());
                returnString = replaced;
            }
        }
        return returnString.trim();
    }
}
