package org.rutebanken.tiamat.config;

import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.KeycloakDeploymentBuilder;
import org.keycloak.adapters.OIDCHttpFacade;
import org.keycloak.representations.adapters.config.AdapterConfig;

import se.samtrafiken.aws.config.CloudConfiguration;
import se.samtrafiken.aws.config.CloudConfigurationFactory;

public class KeycloakAwsConfigResolver implements KeycloakConfigResolver {

    private final static CloudConfiguration CONFIG = CloudConfigurationFactory.getInstance();
    private static KeycloakDeployment keycloakDeployment;

    KeycloakAwsConfigResolver() {
    }

    @Override
    public KeycloakDeployment resolve(OIDCHttpFacade.Request request) {
        if (keycloakDeployment == null) {
            AdapterConfig adapterConfig = new AdapterConfig();
            adapterConfig.setRealm("master");
            adapterConfig.setResource("tiamat");
            adapterConfig.setPublicClient(true);
            adapterConfig.setAuthServerUrl(CONFIG.getString("se.samtrafiken.nsr.keycloak.auth-url"));
            adapterConfig.setSslRequired("external");
            keycloakDeployment = KeycloakDeploymentBuilder.build(adapterConfig);
        }
        return keycloakDeployment;
    }


}
