# Tiamat - Stop Place Register

## Project Overview

**Tiamat** is a comprehensive Stop Place Register system used nationally in Norway and other locations. It manages stop places (transit stations, bus stops, etc.) and related geographic/transportation data in the NeTEx (Network Timetable Exchange) format.

### Key Information
- **Organization**: Entur AS (Norwegian public transport authority)
- **Repository**: https://github.com/entur/tiamat
- **License**: EUPL-1.2 with modifications
- **Main Language**: Java 21
- **Framework**: Spring Boot 3.x
- **Build Tool**: Maven
- **Main Package**: `org.rutebanken.tiamat`

## Technology Stack

### Core Technologies
- **Java 21** (JDK 21.0.1+)
- **Spring Boot** (latest 3.x version from parent superpom)
- **Hibernate 6.5.3** (with Spatial extensions for PostGIS)
- **PostgreSQL + PostGIS** (geographic database)
- **Jersey** (JAX-RS REST framework)
- **GraphQL Java 20.9** (GraphQL API)
- **Hazelcast 5.2.5** (distributed caching and coordination)

### Key Dependencies
- **netex-java-model 2.0.15**: NeTEx format models
- **GeoTools 30.2**: Geospatial processing
- **JTS (Java Topology Suite) 1.20.0**: Geometric operations
- **Flyway**: Database migration management
- **Jackson**: JSON/XML serialization
- **Testcontainers 1.21.3**: Integration testing with real PostgreSQL

### Infrastructure & Cloud
- **Google Cloud Platform** (GCS for blob storage, PubSub for events)
- **AWS** (S3 support, Secrets Manager)
- **Docker & Docker Compose**: Local development and deployment
- **Kubernetes**: Production deployment with Hazelcast clustering
- **Terraform**: Infrastructure as code
- **Helm**: Kubernetes package management

## Project Structure

```
tiamat/
├── src/main/java/org/rutebanken/tiamat/
│   ├── TiamatApplication.java           # Spring Boot main class
│   ├── auth/                            # Authentication & authorization
│   ├── changelog/                       # Entity change event publishing
│   ├── config/                          # Spring configuration
│   ├── diff/                            # Version comparison utilities
│   ├── dtoassembling/                   # DTO assembly
│   ├── exporter/                        # NeTEx export functionality
│   ├── filter/                          # Request/response filters
│   ├── general/                         # General utilities
│   ├── geo/                             # Geographic/spatial operations
│   ├── hazelcast/                       # Hazelcast cache configuration
│   ├── importer/                        # NeTEx import functionality
│   │   ├── matching/                    # Stop place matching logic
│   │   ├── merging/                     # Stop place/quay merging
│   │   └── filter/                      # Import filtering
│   ├── jersey/                          # Jersey/JAX-RS configuration
│   ├── lock/                            # Distributed locking
│   ├── model/                           # JPA entity models
│   ├── netex/                           # NeTEx specific logic
│   ├── repository/                      # Spring Data JPA repositories
│   ├── rest/                            # REST endpoints
│   │   └── graphql/                     # GraphQL API implementation
│   ├── service/                         # Business logic services
│   ├── time/                            # Time/versioning utilities
│   └── versioning/                      # Entity versioning
├── src/main/resources/
│   ├── db/migration/                    # Flyway SQL migrations (V*.sql)
│   ├── application.properties           # Default configuration
│   └── application-local.properties     # Local dev configuration
├── src/test/                            # Integration and unit tests
├── api/                                 # API proxy configurations
├── docker-compose/                      # Docker compose setups
├── helm/                                # Kubernetes Helm charts
├── terraform/                           # Infrastructure definitions
├── scripts/                             # Utility scripts
├── pom.xml                              # Maven build configuration
└── docker-compose.yml                   # Local development environment

Total: ~706 Java source files across 73 packages
```

## Core Functionality

### 1. NeTEx Import System
- **Purpose**: Imports stop place data from NeTEx XML format
- **Features**:
  - Multiple import types: `MERGE`, `INITIAL`, `ID_MATCH`, `MATCH`
  - Automatic ID generation (gap-less NSR IDs)
  - Stop place matching by name, coordinates, and original IDs
  - Intelligent quay merging based on proximity and bearing
  - Bad data quality handling with pre-processing steps
  - XML schema validation
  - Parallel processing for INITIAL imports
- **Endpoint**: `POST /services/stop_places/netex`

### 2. NeTEx Export System

#### Synchronous Export
- **Endpoint**: `GET /services/stop_places/v1/netex`
- **Query Parameters**:
  - `q`: Search by name or ID
  - `stopPlaceType`: Filter by type (RAIL_STATION, etc.)
  - `municipalityReference`, `countyReference`: Geographic filters
  - `idList`: Specific stop place IDs
  - `versionValidity`: ALL, CURRENT, FUTURE_CURRENT
  - `size`, `page`: Pagination
  - Export modes: `topographicPlaceExportMode`, `tariffZoneExportMode`, `groupOfStopPlacesExportMode`

#### Asynchronous Export
- **Endpoints**:
  - `POST /services/stop_places/v1/netex/export/initiate`: Start export job
  - `GET /services/stop_places/v1/netex/export`: Check status
  - `GET /services/stop_places/v1/netex/export/{jobId}/content`: Download
- **Storage**: Google Cloud Storage (gzipped XML)
- **Use Case**: Large dataset exports (thousands of stop places)

### 3. GraphQL API
- **Endpoint**: `/services/stop_places/v1/graphql`
- **GraphiQL UI**: Available in dev environments
- **Capabilities**:
  - **Queries**: Stop places, topographic places, path links, tariff zones, fare zones, groups
  - **Mutations**: Create/update entities, merge stop places/quays
  - **Functions**: Named operations like merging, validation
- **Implementation**: Custom schema in `StopPlaceRegisterGraphQLSchema`

### 4. Entity Versioning
- **Full version history** for all entities
- **Change tracking** with user attribution
- **Diff tool** to compare versions
- **Validity periods** for temporal data
- **Version strategies**: Time-based, manual versioning

### 5. Geographic Features
- **Topographic place lookup**: Automatic assignment based on polygon intersection
- **Tariff zone lookup**: Automatic population from geographic boundaries
- **Coordinate transformations**: Multiple coordinate reference systems
- **Spatial queries**: PostGIS-powered geographic searches

### 6. ID Management System
- **Automatic ID generation**: Gap-less NSR IDs using distributed locks (Hazelcast)
- **ID mapping table**: Tracks old→new ID conversions after import
- **Configurable prefixes**: `ValidPrefixList` controls which IDs Tiamat generates
- **Components**:
  - `IdentifiedEntityListener`: Pre-persist hook
  - `NetexIdAssigner`: Determines if ID generation needed
  - `NetexIdProvider`: Generates or validates IDs
  - `GaplessIdGeneratorService`: Hazelcast-based ID generation

## Domain Model (Key Entities)

All entities are versioned and support NeTEx format:

- **StopPlace**: Main stop location (station, bus stop, etc.)
  - Contains multiple Quays
  - Has StopPlaceType, coordinates, name, etc.
- **Quay**: Individual boarding point within a StopPlace
- **TopographicPlace**: Geographic/administrative areas
- **TariffZone**: Fare zones for pricing
- **FareZone**: Alternative fare zone representation
- **GroupOfStopPlaces**: Logical grouping of stops
- **GroupOfTariffZones**: Logical grouping of tariff zones
- **PathLink**: Pedestrian connections between stops
- **Parking**: Parking facilities at stops
- **Tag**: Flexible tagging system

## Development Setup

### Prerequisites
- Java 21 JDK
- Maven 3+
- Docker & Docker Compose
- PostgreSQL with PostGIS (via Docker)

### Local Development Steps

1. **Start Supporting Services**:
```bash
docker compose up -d
```
This starts PostgreSQL on port 37432.

2. **Run Tiamat**:

**Option A - IntelliJ IDEA**:
- Open `TiamatApplication.java`
- Right-click → Run
- Edit run configuration, set Active Profiles: `local,local-blobstore,local-changelog`

**Option B - Command Line**:
```bash
mvn spring-boot:run
```

3. **Access**:
- Tiamat API: http://localhost:37888
- GraphiQL: http://localhost:37888/services/stop_places/graphql
- Database: localhost:37432 (user: tiamat, password: tiamat)

### Spring Profiles

**Required Profiles** (choose one from each category):

**Storage**:
- `local-blobstore`: Local filesystem storage
- `gcs-blobstore`: Google Cloud Storage
- `rutebanken-blobstore`: With sub-profiles:
  - `in-memory-blobstore`
  - `local-disk-blobstore`
  - `s3-blobstore`

**Changelog**:
- `local-changelog`: Log to stdout
- `activemq`: JMS-based messaging
- `google-pubsub`: GCP PubSub

**Always Include**: `local` (for local development)

### Docker Compose Profiles

- **Default**: PostgreSQL with PostGIS
- **`aws`**: Adds LocalStack for AWS development

Example:
```bash
docker compose --profile aws up
```

## Build & Test

### Build
```bash
mvn clean install
```
Requires `/deployments/data` directory with write permissions.

### Run Tests
Integration tests use Testcontainers (Docker required):
```bash
mvn test
```

### Build Docker Image
```bash
docker build -t tiamat .
```
Or use Jib for containerization without Docker daemon:
```bash
mvn jib:build
```

## Database Management

### Flyway Migrations
- **Location**: `src/main/resources/db/migration/`
- **Naming**: `V{version}__{description}.sql` (e.g., `V42__refactor_tariff_zone_ref.sql`)
- **Execution**: Automatic on application startup
- **Schema Version Table**: `schema_version`

### Manual Database Operations
```sql
-- Truncate for clean slate (development only!)
TRUNCATE stop_place CASCADE;
TRUNCATE quay CASCADE;
TRUNCATE topographic_place CASCADE;
```

## Security & Authentication

### OAuth2/OIDC
- Supports **Keycloak** and **Auth0**
- JWT-based authentication
- Resource server configuration: `spring.security.oauth2.resourceserver.jwt.issuer-uri`
- See `Keycloak_Setup_Guide.md` for detailed setup

### Authorization
- **Entity-level permissions** via `permission-store-proxy` helper
- **Role-based access control**
- Configurable with `authorization.enabled` property
- Security context inheritance for parallel processing

## Multi-Instance Support

- **Hazelcast** distributed memory grid for:
  - Distributed caching
  - ID generation coordination
  - Lock management
  - Cross-instance communication
- **Kubernetes-aware** configuration
- **Performance monitoring** via Hazelcast metrics

## Import/Export Details

### Import Types
1. **INITIAL**: First-time import, generates new IDs, uses parallel processing
2. **MERGE**: Merges with existing data, intelligent matching
3. **ID_MATCH**: Matches by existing IDs
4. **MATCH**: General matching strategy

### Import Process
1. XML validation (optional)
2. Unmarshalling to Java objects
3. Filtering (by type, zone, etc.)
4. Matching existing entities
5. Merging/updating
6. ID assignment
7. Persistence
8. Changelog event publishing

### Security Context for INITIAL Import
Set JVM parameter for parallel processing:
```bash
-Dspring.security.strategy=MODE_INHERITABLETHREADLOCAL
```

## Configuration Properties

Key properties (see README.md for full example):

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5436/tiamat
spring.datasource.username=tiamat
spring.datasource.password=tiamat

# NeTEx
netex.validPrefix=NSR
netex.profile.version=1.12:NO-NeTEx-stops:1.4
netex.import.enabled.types=MERGE,INITIAL,ID_MATCH,MATCH

# Blob Storage
blobstore.local.folder=/tmp/local-gcs-storage/tiamat/export

# Server
server.port=1888

# Validation
publicationDeliveryUnmarshaller.validateAgainstSchema=false
publicationDeliveryStreamingOutput.validateAgainstSchema=false

# Hazelcast
hazelcast.performance.monitoring.enabled=true

# Authorization
authorization.enabled=true

# Profiles
spring.profiles.active=local,local-blobstore,local-changelog
```

## API Endpoints

### REST
- `POST /services/stop_places/netex`: Import NeTEx
- `GET /services/stop_places/v1/netex`: Sync export
- `POST /services/stop_places/v1/netex/export/initiate`: Start async export
- `GET /services/stop_places/v1/netex/export`: Get export status
- `GET /services/stop_places/v1/netex/export/{jobId}/content`: Download export

### GraphQL
- `POST /services/stop_places/v1/graphql`: GraphQL endpoint
- GraphiQL UI available in dev environments

### Actuator
- `/actuator/health`: Health check
- `/actuator/metrics`: Prometheus metrics
- `/actuator/info`: Application info

## CI/CD

### GitHub Actions
- **Workflows**: `.github/workflows/`
  - `entur-push.yml`: Main build/deploy workflow
  - `ci.yaml`: Continuous integration
- **Build Badge**: Shows on README

### Deployment
- **Docker Image**: Built with Jib plugin
- **Container Registry**: Configured via env vars
- **Kubernetes**: Helm charts in `helm/`
- **Terraform**: Infrastructure in `terraform/`

## Related Projects

- **Abzu**: ReactJS frontend for Tiamat (https://github.com/entur/abzu)
- **tiamat-scripts**: Collection of useful queries and scripts (https://github.com/entur/tiamat-scripts)
- **netex-java-model**: NeTEx format Java bindings
- **rutebanken-helpers**: Shared libraries (storage, auth, organization)

## Testing

### Test Structure
- **Integration Tests**: `src/test/java/.../rest/graphql/`
  - GraphQL API tests
  - Import/export tests
  - Entity permission tests
- **Test Containers**: Real PostgreSQL instances for integration tests
- **Test Profiles**: Separate application properties for testing

### Running Specific Tests
```bash
# All tests
mvn test

# Specific test class
mvn test -Dtest=GraphQLResourceStopPlaceIntegrationTest

# With increased memory
export MAVEN_OPTS='-Xms256m -Xmx1712m -Xss256m'
mvn test
```

## Common Tasks

### Import Data
```bash
curl -XPOST -H"Content-Type: application/xml" \
  -d@my-netex-file.xml \
  http://localhost:1997/services/stop_places/netex
```

### Export Data
```bash
# Query by name
curl "http://localhost:1888/services/stop_places/v1/netex?q=Oslo"

# With filters
curl "http://localhost:1888/services/stop_places/v1/netex?stopPlaceType=RAIL_STATION&size=100"
```

### GraphQL Query
```bash
curl -X POST http://localhost:1888/services/stop_places/v1/graphql \
  -H "Content-Type: application/json" \
  -d '{"query": "{ stopPlace(id: \"NSR:StopPlace:123\") { name { value } } }"}'
```

## Troubleshooting

### Memory Issues
Increase JVM memory for imports:
```bash
export MAVEN_OPTS='-Xms256m -Xmx1712m -Xss256m -XX:NewSize=64m -XX:MaxNewSize=128m'
```

### Parallel Import Failures
Enable security context inheritance:
```bash
-Dspring.security.strategy=MODE_INHERITABLETHREADLOCAL
```

### Testcontainers Issues
Ensure Docker is running and accessible. See: https://www.testcontainers.org/supported_docker_environment/

### Database Connection Issues
Check PostgreSQL is running via Docker Compose:
```bash
docker compose ps
docker compose logs postgres
```

## Development Notes

### Code Style
- Clean, minimal comments (code should be self-documenting)
- Follow existing patterns in the codebase
- Use Spring dependency injection
- Leverage Hibernate for ORM

### Adding New Entities
1. Create model class in `model/` package
2. Add repository in `repository/`
3. Create service in `service/`
4. Add GraphQL schema definitions
5. Create Flyway migration for database schema
6. Add tests

### Schema Changes
1. Create new Flyway migration: `V{next}__{description}.sql`
2. Test migration locally
3. Commit with related code changes
4. Migration runs automatically on deployment

## Key Patterns

### ID Generation Flow
```
Entity Create → IdentifiedEntityListener (@PrePersist)
  → NetexIdAssigner.assignNetexId()
    → NetexIdProvider.getOrGenerateId()
      → GaplessIdGeneratorService.getNextIdFor() [Hazelcast]
```

### Import Flow
```
REST Endpoint → PublicationDeliveryImporter
  → Pre-filters (type, zone, etc.)
    → Matching (name, coordinates, IDs)
      → Merging (if needed)
        → ID Assignment
          → Persistence
            → Changelog Publishing
```

### Export Flow
```
REST/GraphQL Query → ExporterService
  → Repository Query (with filters)
    → DTO Assembly
      → NeTEx Marshalling (with validation)
        → Stream or Storage Upload
```

## Performance Considerations

- **HikariCP**: Connection pooling (default max: 40)
- **Hibernate Caching**: Hazelcast-backed L2 cache
- **Batch Processing**: Configured batch sizes for bulk operations
- **Async Exports**: Use for large datasets (>1000 stop places)
- **Database Indexes**: On netex_id, version, parent relationships

## Monitoring & Metrics

- **Prometheus**: Metrics exposed via `/actuator/metrics`
- **Micrometer**: Metric collection framework
- **Hazelcast Monitoring**: Performance metrics for distributed operations
- **Logback**: Structured logging with logstash encoder

---

## Quick Reference

**Port**: 37888 (local dev), 1888 (standard), 8777 (container)
**Default DB**: PostgreSQL on localhost:37432
**Main Package**: org.rutebanken.tiamat
**Config**: application.properties, application-local.properties
**Migrations**: src/main/resources/db/migration/
**Frontend**: Abzu (separate repo)
