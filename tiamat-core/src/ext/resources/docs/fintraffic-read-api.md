# Read API

## Overview

The Read API is an optional extension for Tiamat that provides fast, read-only access to Stop Place data in NeTEx XML format. This API is designed for external clients that need efficient access to Stop Place data without impacting Tiamat's internal data model or performance.

**Key Features:**
- Read-only REST API for querying Stop Places by transport mode and area code
- Dedicated PostgreSQL cache table for optimized read operations
- Event-driven real-time cache updates
- Nightly batch cache repopulation for consistency
- No changes to Tiamat's core data structures or Hibernate layer

## Architecture

### Cache Table

The API uses a dedicated PostgreSQL cache table `ext_fintraffic_netex_entity` that stores:
- Stop Place entities as NeTEx XML
- Related entities (ScheduledStopPoint, PassengerStopAssignment, Parking)
- Search keys (JSONB)
- Entity status (CURRENT, STALE, DELETED)
- Version and timestamp information for consistency checks

### Update Mechanisms

The cache is kept synchronized through two complementary mechanisms:

#### 1. Event-Driven Updates (Real-time)

When a StopPlace or Parking entity is created, updated, or deleted in Tiamat:

1. **Event Detection**: `EntityChangedListener` triggers `FintrafficEntityChangedPublisher` within a Spring transaction
2. **XML Marshalling**: `ReadApiNetexMarshallingService` converts the entity to NeTEx XML format and generates search keys
3. **ServiceFrame Elements**: For StopPlace entities, additional elements (ScheduledStopPoint, PassengerStopAssignment) are automatically created
4. **Cache Update**: `NetexRepository` uses manual transaction control with READ_COMMITTED isolation to upsert data
5. **Dependency Management**: Updates dependent entities first, marking them as DELETED if their parent changed
6. **Upsert Operation**: Uses `INSERT ... ON CONFLICT DO UPDATE` based on version and timestamp
7. **Retry Logic**: Retry mechanism attempts operations up to 3 times with exponential backoff on failure

**Performance**: Updates complete within milliseconds, providing near real-time cache synchronization.

#### 2. Batch Update (Nightly Task)

The batch update task runs as an `ApplicationRunner` and operates in four distinct steps:

**Step 1: Mark as STALE**
- Marks all existing cache entities as STALE using READ_COMMITTED transaction
- Ensures clean slate for batch processing

**Step 2: Process StopPlaces**
- Streams StopPlace entities from Hibernate in batches of 1000
- Each batch processed within read-only REPEATABLE_READ transaction for consistent snapshots
- Marshalls entities to NeTEx XML
- Writes to cache using same upsert logic as event-driven flow

**Step 3: Process Parking**
- Repeats Step 2 pattern for Parking entities
- Maintains same batch size and transaction isolation

**Step 4: Cleanup**
- Removes all entities still marked as STALE
- Effectively deletes entities that no longer exist in Tiamat

**Performance**: Full rebuild takes less than 1 hour for ~100k StopPlaces. Comprehensive statistics provided upon completion.

**Benefits**: Avoids memory issues through streaming and batching while ensuring complete cache consistency.

## API Endpoints

### Get Stop Places

```http
GET /api/ext/fintraffic/stop_places/netex
```

**Query Parameters:**
- `transportMode` (optional): Transport mode filter (e.g., `bus`, `rail`, `tram`)
- `areaCode` (optional): Three-letter area code (e.g., `ABC`, `XYZ`)

**Response:**
- Content-Type: `application/xml`
- Returns NeTEx `PublicationDelivery` containing matching Stop Places

**Status Codes:**
- `200 OK`: Successful response with NeTEx data
- `400 Bad Request`: Invalid parameters (validation errors)

**Example:**
```bash
curl "http://localhost:8080/api/ext/fintraffic/stop_places/netex?transportMode=bus&areaCode=ABC"
```

### Validation Rules

- `transportMode`: Must be a valid transport mode string
- `areaCode`: Must be exactly 3 uppercase letters (A-Z, including Å, Ä, Ö)
  - Configurable via `application.properties`

## Configuration

### Profiles

The feature is controlled by Spring profiles:

- `fintraffic-read-api`: Enables the Read API server
- `fintraffic-update-task`: Enables the batch update task (requires `fintraffic-read-api`)

### Application Properties

```properties
# Enable background consistency checks (optional)
tiamat.ext.fintraffic.read-api-background-jobs.enabled=true
```

When enabled, periodic background tasks verify data consistency between the cache and the main database.

## Running the Read API

### Read API Server

To run Tiamat with the Read API enabled:

1. **Activate the profile:**
   ```bash
   spring.profiles.active=fintraffic-read-api
   ```

2. **Optional - Enable background consistency checks:**
   ```properties
   tiamat.ext.fintraffic.read-api-background-jobs.enabled=true
   ```

3. Start the application as normal

The API will be available at `/api/ext/fintraffic/stop_places/netex` and serve cached NeTEx data for read-only access.

### Batch Update Task

To run the batch update task that refreshes all cached data:

1. **Activate both profiles:**
   ```bash
   spring.profiles.active=fintraffic-read-api,fintraffic-update-task
   ```

2. Start the application

**Important**: The batch update task will:
- Mark all existing cached entities as STALE
- Process all StopPlaces and Parkings in batches (streaming from database)
- Update the cache with current data
- Clean up stale entities
- **Automatically shut down the application** when complete

**Recommended Usage**: Run as a scheduled job (e.g., nightly cron job) separate from the main API server.

## Database Schema

### Table: `ext_fintraffic_netex_entity`

```sql
CREATE TABLE ext_fintraffic_netex_entity (
    id TEXT PRIMARY KEY,
    type TEXT NOT NULL,
    parent_refs TEXT[] NOT NULL,
    xml TEXT NOT NULL,
    version INT NOT NULL,
    changed BIGINT NOT NULL,
    status TEXT NOT NULL CHECK (status IN ('CURRENT', 'STALE', 'DELETED')),
    search_key JSONB,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## Performance Characteristics

### API Response Time
- **Typical**: < 200ms for queries returning ~50k Stop Places

### Cache Update Performance
- **Event-driven**: < 10ms per entity update
- **Batch update**: ~1 hour for 100k StopPlaces

### Resource Usage
- **Memory**: Streaming approach keeps memory usage constant regardless of dataset size
- **Database**: Separate cache table prevents impact on main Tiamat operations


## Component Overview

### Spring Components

- **`FintrafficEntityChangedPublisher`**: Listens for entity changes and triggers cache updates
- **`FintrafficApiController`**: REST controller exposing API endpoints
- **`ReadApiNetexMarshallingService`**: Converts entities to NeTEx XML format
- **`ReadApiNetexPublicationDeliveryService`**: Streams entities and builds PublicationDelivery responses
- **`NetexRepository`**: Manages database operations with proper transaction control
- **`ReadApiUpdateTask`**: ApplicationRunner for batch cache repopulation
- **`FintrafficSearchKeyService`**: Generates search keys for efficient querying

### Database Migrations

Flyway migrations are located in `org.rutebanken.tiamat.ext.fintraffic.db.migration` and only execute when the `fintraffic-read-api` profile is active.
