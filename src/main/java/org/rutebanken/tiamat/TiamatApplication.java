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

package org.rutebanken.tiamat;

import org.rutebanken.tiamat.auth.TiamatSecurityConfig;
import org.rutebanken.tiamat.model.StopPlace;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.transaction.annotation.EnableTransactionManagement;
@SpringBootApplication
@Configuration
@EnableTransactionManagement
@EnableCaching
@EntityScan(basePackageClasses = {StopPlace.class, Jsr310JpaConverters.class})
@ComponentScan(basePackages = { "org.entur", "org.rutebanken.tiamat"})
public class TiamatApplication {

    public static void main(String[] args) {
        SpringApplication.run(TiamatApplication.class, args);
    }
}

