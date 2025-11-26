
# Keycloak Setup Guide

This guide will help you set up Keycloak, configure a realm, and secure your application using Keycloak's authentication and authorization services.
This guide is tested with Keycloak version 23
## Table of Contents

- [Prerequisites](#prerequisites)
- [Step 1: Install Keycloak](#step-1-install-keycloak)
- [Step 2: Start Keycloak](#step-2-start-keycloak)
- [Step 3: Access Keycloak Admin Console](#step-3-access-keycloak-admin-console)
- [Step 4: Create a Realm](#step-4-create-a-realm)
- [Step 5: Create a Client](#step-5-create-a-client)
- [Step 6: Configure Roles and Users](#step-6-configure-users-and-roles)
- [Step 7: Configure Tiamat](#step-7-configure-tiamat)
- [Step 8: Testing the Setup](#step-8-testing-the-setup)
- [Step 9: Useful Links](#step-9-useful-links)

---

## Prerequisites

- **Java 11** or higher
- **Keycloak** download package or **Docker** (for containerized setup)
- Basic understanding of OIDC (OpenID Connect) and Keycloak concepts like Realms, Clients, Users, and Roles.

---

## Step 1: Install Keycloak

There are two ways to install Keycloak: using Docker or downloading and running it manually.

### Option 1: Using Docker

To run Keycloak with Docker, run the following command:

```bash
docker run -d --name keycloak -p 8082:8080 -e KEYCLOAK_USER=admin -e KEYCLOAK_PASSWORD=admin jboss/keycloak
```

### Option 2: Manual Installation

1. Download Keycloak from the official website: [https://www.keycloak.org/downloads](https://www.keycloak.org/downloads)
2. Extract the downloaded archive.
3. Navigate to the Keycloak folder in your terminal and run the following command to start Keycloak:
   ```bash
   ./bin/standalone.sh
   ```

---

## Step 2: Start Keycloak

After installation, Keycloak should be running on `http://localhost:8082`.

You can start and stop Keycloak using these commands:

- Start:
  ```bash
  ./bin/standalone.sh
  ```
- Stop:
  ```bash
  ./bin/jboss-cli.sh --connect command=:shutdown
  ```

---

## Step 3: Access Keycloak Admin Console

1. Open your browser and go to: `http://localhost:8082`
2. Click on the **Administration Console** link.
3. Log in with the default admin credentials you set during the installation:
   - **Username**: `admin`
   - **Password**: `admin`

---

## Step 4: Create a Realm

A **realm** in Keycloak is the equivalent of a tenant or domain in which you manage users, credentials, roles, and groups.

1. Once logged in, click on the **Master** dropdown (top-left corner) and select **Add Realm**.
2. Give your realm a name (e.g., `entur`), then click **Create**.

---

## Step 5: Create a Client

A **client** in Keycloak represents an application that users will authenticate with.

1. Go to the **Clients** section (in the left menu).
2. Click **Create**.
3. In General setting, set the **Client type** set to **openid-connect**.
4. In the **Client ID** field, give your client a name (e.g., `abzu`).
5. Click **Next**
6. In Capability Config, leave the default settings and click **Next**.
7. In Login Settings, set **Valid Redirect URIs** to `*` and web origins to `*`. and click **Save**.


### Client Settings:
1. Go to Roles tab and create roles `viewStops` `deleteStops` and `editStops`.
2. Save the changes.

---

## Step 6: Configure Users and Roles


### Create Users and configure Roles
1. Go to the **Users** section.
2. Click **Add User** and provide details like username and email.
3. Once the user is created, go to the **Credentials** tab and set a password for the user (uncheck the **Temporary** option to make the password permanent).
4. In the **Role Mappings** tab, assign roles to the user by selecting roles from the **Available Roles** list and clicking **Add Selected**. i.e. `viewStops` `deleteStops` and `editStops`.
5. In the Attributes tab, add a follwoing attributes to the user.
   - `role_assignments` with value `{"r":"deleteStops","o":"RB"}`
   - `role_assignments` with value `{"r":"editStops","o":"RB","e":{"EntityType":["*"]}}`

### Configure Client Roles
1. Go to the **Clients** section and select the client you created (e.g., `abzu`).
2. Click on the **Client Scopes** tab.
3. Click on the **abzu-dedicated** client-scope, click on the **Add Mapper** and select **By Configuration**.
4. Select User Attribute and set the following values:
   - **Name**: `role_assignments`
   - **User Attribute**: `role_assignments`
   - **Token Claim Name**: `role_assignments`
   - **Claim JSON Type**: `String`
   - **Add to ID token**: `ON`
   - **Add to access token**: `ON`
   - **Add to userinfo**: `ON`
   - **Add to token introspection**: `ON`
   - **Multivalued**: `ON`
   - **Aggregate attribute valuese**: `OFF`
5. Click **Save**.
6. To test click on Client Details and select the **Client Scopes** tab. Click on the **Evaluate** button and select/write username in **Usesr** and click on and click on **Generate Access Token**. You should see the roles in the token.
     ```text
        ...
        ...
        ...
        "resource_access": {
         "abzu": {
                "roles": [
                         "viewStops",
                         "editStops",
                         "deleteStops"
                         ]
                },
          "account": {
           "roles": [
              "manage-account",
              "manage-account-links",
              "view-profile"
             ]
          }
        },
       "scope": "openid email profile kcAudience",
       "sid": "xxxxxxxxxxxx",
       "role_assignments": [
             "{\"r\":\"deleteStops\",\"o\":\"RB\"}",
             "{\"r\":\"editStops\",\"o\":\"RB\",\"e\":{\"EntityType\":[\"*\"]}}"
        ],
     ...
     ...
     ...
  ```

## Step 7: Configure Tiamat and Abzu

### Tiamat Application Properties

You need to configure your application to use Keycloak for authentication. 

```properties
authorization.enabled = true
# Keycloak Configuration
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8082/realms/entur
```

#### Configure Abzu
Abzu is client application that uses Keycloak for authentication. You need to configure Abzu to use Keycloak.
https://github.com/entur/abzu
Update dev.json file with the following configuration.
```properties
{
 "tiamatBaseUrl": "http://localhost:1888/services/stop_places/graphql",
 "OTPUrl": "https://api.dev.entur.io/journey-planner/v2/graphql",
 "baatTokenProxyEndpoint": "https://api.dev.entur.io/baat-token-proxy/v1/token",
 "tiamatEnv": "development",
 "netexPrefix": "NSR",
 "hostname": "stoppested.dev.entur.org",
 "googleApiKey": "AIzaSyB_8bdt2skRkdsyQO19m_gqTzr0_2gqo8U",
 "claimsNamespace": "role_assignments",
 "preferredNameNamespace": "name",
 "oidcConfig": {
   "authority": "http://localhost:8082/realms/entur",
   "client_id": "abzu",
   "extraQueryParams": {
    "audience": "abzu"
   }
  }
}
```

---

## Step 8: Testing the Setup

1. Run your abzu and tiamat applications.
2. Click on Login button from abzu; this will redirect you to Keycloak for login.
3. After successful authentication, you should be redirected back to your application with the proper security context.
4. Search for a stop e.g. Oslo, and you should see the edit button.
5. Edit the stop e.g. add/change description and save the changes.

---

## Step 9: Useful Links

- [Keycloak Documentation](https://www.keycloak.org/documentation)
- [OpenID Connect (OIDC) Protocol](https://openid.net/connect/)
- [Keycloak GitHub Repository](https://github.com/keycloak/keycloak)
  
---

Note: This guide assumes you have a basic understanding of Keycloak and OIDC concepts. For more detailed information, refer to the official Keycloak documentation. 