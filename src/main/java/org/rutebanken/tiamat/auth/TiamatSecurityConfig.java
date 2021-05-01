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

package org.rutebanken.tiamat.auth;


import org.entur.oauth2.MultiIssuerAuthenticationManagerResolver;

import org.rutebanken.tiamat.filter.CorsResponseFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration()
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class TiamatSecurityConfig extends WebSecurityConfigurerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(TiamatSecurityConfig.class);

	private final MultiIssuerAuthenticationManagerResolver multiIssuerAuthenticationManagerResolver;

	public TiamatSecurityConfig(MultiIssuerAuthenticationManagerResolver multiIssuerAuthenticationManagerResolver) {
		this.multiIssuerAuthenticationManagerResolver = multiIssuerAuthenticationManagerResolver;
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedHeaders(Arrays.asList("Origin", "Accept", "X-Requested-With", "Content-Type", "Access-Control-Request-Method", "Access-Control-Request-Headers", "Authorization", "x-correlation-id"));
		configuration.addAllowedOrigin("*");
		configuration.setAllowedMethods(Arrays.asList("GET", "PUT", "POST", "DELETE"));
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		logger.info("Configuring HttpSecurity");
		http.cors(withDefaults())
				.csrf().disable()
				.addFilterBefore(new CorsResponseFilter(), ChannelProcessingFilter.class)
				.authorizeRequests()
				.anyRequest()
				.permitAll()
				.and()
				.oauth2ResourceServer().authenticationManagerResolver(this.multiIssuerAuthenticationManagerResolver)
				.and()
				.oauth2Client();

	}


}