# Enlil - Project Documentation for Claude AI

## Project Overview

**Enlil** is a backend application for a lightweight real-time deviation system, serving as the "father of [nirgali](https://github.com/entur/nirgali)" and named after the Sumerian "lord of the winds".

**Primary Purpose**: Manage and serve real-time public transport deviation messages, cancellations, and extra journeys through both GraphQL and SIRI (Service Interface for Real Time Information) protocols.

## Architecture

### Technology Stack

- **Language**: Java 21
- **Framework**: Spring Boot 3.x
  - Spring Web
  - Spring GraphQL
  - Spring Security
  - Spring Cloud GCP
- **Database**: Google Cloud Firestore (NoSQL document database)
- **Build Tool**: Maven
- **Container**: Docker (Alpine-based with Liberica OpenJDK 21)
- **Deployment**: Kubernetes (Helm charts), Terraform for infrastructure

### Key Dependencies

- `siri-java-model` (2.0.1) - SIRI XML data model
- `spring-cloud-gcp-starter-data-firestore` - Firestore integration
- JAXB for XML marshalling/unmarshalling
- Logback with Logstash encoder for structured logging
- Micrometer with Prometheus for metrics
- Testcontainers for integration testing

## Project Structure

```
enlil/
├── src/main/java/org/entur/enlil/
│   ├── EnlilApplication.java           # Spring Boot main class
│   ├── configuration/                  # Application configuration
│   ├── graphql/                        # GraphQL controllers and data fetchers
│   ├── housekeeping/                   # Background jobs and cleanup tasks
│   ├── model/                          # Domain entities
│   │   ├── EstimatedVehicleJourneyEntity.java
│   │   ├── PtSituationElementEntity.java
│   │   └── FramedVehicleJourneyRef.java
│   ├── repository/                     # Data access layer
│   │   ├── firestore/                  # Firestore-specific implementations
│   │   ├── EstimatedVehicleJourneyRepository.java
│   │   └── SituationElementRepository.java
│   ├── security/                       # Authentication and authorization
│   │   ├── spi/                        # Security service interfaces
│   │   ├── model/                      # Security domain models
│   │   ├── EnlilSecurityConfiguration.java
│   │   ├── EnturSecurityConfiguration.java
│   │   ├── LocalSecurityConfiguration.java
│   │   ├── DefaultEnlilAuthorizationService.java
│   │   └── EnturUserContextService.java
│   └── siri/                           # SIRI protocol implementation
│       ├── resource/                   # REST endpoints
│       │   └── SiriDeliveryResource.java
│       ├── service/                    # Business logic
│       │   ├── EstimatedTimetableDeliveryService.java
│       │   ├── SituationExchangeDeliveryService.java
│       │   └── filter/                 # Data filtering
│       │       └── OpenExpiredMessagesFilter.java
│       ├── mapper/                     # Entity to SIRI mapping
│       │   ├── EstimatedVehicleJourneyEntityToSiriMapper.java
│       │   └── SituationElementEntityToSiriMapper.java
│       ├── helpers/                    # Utility classes
│       │   ├── SiriObjectFactory.java
│       │   └── DateMapper.java
│       └── error/                      # Exception handling
│           └── InvalidServiceRequestException.java
├── src/main/resources/
│   ├── graphql/
│   │   └── schema.graphqls             # GraphQL schema definition
│   ├── application.properties          # Configuration
│   └── logback.xml                     # Logging configuration
├── helm/                               # Kubernetes Helm charts
├── terraform/                          # Infrastructure as code
├── Dockerfile                          # Container definition
├── pom.xml                             # Maven build configuration
└── README.md                           # User-facing documentation
```

## Core Functionality

### 1. GraphQL API (`/graphql`)

The GraphQL API provides CRUD operations for authorized users to manage deviation messages within their assigned codespace/organization.

**Main Types**:
- **SituationElement**: Deviation/incident messages (delays, service changes, etc.)
- **Cancellation**: Trip cancellations
- **Extrajourney**: Additional/replacement trips

**Key Operations**:

```graphql
# Queries
query {
  userContext                    # Get current user's permissions and codespaces
  situationElements(codespace, authority)  # Fetch deviation messages
  cancellations(codespace, authority)      # Fetch cancellations
  extrajourneys(codespace, authority, showCompletedTrips)  # Fetch extra journeys
}

# Mutations
mutation {
  createOrUpdateSituationElement(codespace, authority, input)
  createOrUpdateCancellation(codespace, authority, input)
  createOrUpdateExtrajourney(codespace, authority, input)
}
```

**Authorization Model**:
- Codespace-based access control
- Permissions: MESSAGES, CANCELLATIONS, EXTRAJOURNEYS
- Admin role support

### 2. SIRI REST Endpoint (`/siri`)

Exposes data in SIRI XML format via HTTP POST requests for integration with public transport systems.

**Supported SIRI Services**:
- **SIRI-SX (Situation Exchange)**: Delivers situation messages (deviations, incidents)
- **SIRI-ET (Estimated Timetable)**: Delivers cancellations and extra journeys

The service accepts SIRI XML requests and returns corresponding SIRI XML responses with data retrieved from Firestore.

### 3. Data Persistence

**Firestore Collections**:
- Situation elements (deviation messages)
- Estimated vehicle journeys (cancellations and extra journeys)

**Key Features**:
- Multi-tenancy via codespace/authority organization
- Automatic expiration handling (expiresAtEpochMs)
- Document-based NoSQL storage for flexible schema

## Development Setup

### Local Development

1. **Prerequisites**:
   - Java 21
   - Maven 3.x
   - Firestore emulator (configured at `127.0.0.1:8081`)

2. **Configuration**:
   - Application configured for local Firestore emulator by default
   - See `application.properties` for settings

3. **Build & Run**:
   ```bash
   ./mvnw clean install
   ./mvnw spring-boot:run
   ```

4. **Code Formatting**:
   - Uses Prettier Java plugin
   - Auto-formats on `validate` phase
   - Line width: 90, tab width: 2, spaces (not tabs)
   - Skip with `-PprettierSkip` profile

### Testing

- JUnit 5 for unit tests
- Testcontainers for integration tests with Firestore emulator
- Spring GraphQL Test support
- Spring Security Test support
- Snapshot testing with `java-snapshot-testing`
- DataFaker for test data generation

### CI/CD

- GitHub Actions workflow (`.github/workflows/push.yml`)
- Prettier formatting validation in CI (check mode)
- SonarQube integration
- JaCoCo code coverage

## Security

**Authentication & Authorization**:
- OAuth2-based authentication (via `entur.helpers:oauth2`)
- Organization/codespace-based access control
- Permission store integration for role management
- Multiple security configurations:
  - `EnturSecurityConfiguration`: Production OAuth2 setup
  - `LocalSecurityConfiguration`: Local development
  - `EnlilSecurityConfiguration`: Core security rules

**Key Security Classes**:
- `EnlilAuthorizationService`: Authorization logic
- `UserContextService`: User context and permissions
- `Codespace`, `Permission`: Security domain models

## Infrastructure

### Containerization

- Based on Bellsoft Liberica OpenJDK 21 Alpine
- Uses `tini` as init system for proper signal handling
- Non-root user (`appuser`) for security
- Single JAR deployment

### Kubernetes Deployment

- Helm charts in `helm/enlil/`
- Terraform configuration for GCP infrastructure
- Configured for GCP Firestore in production

## Code Quality & Standards

- **Formatting**: Prettier Java (enforced in CI)
- **License**: EUPL 1.2 (European Union Public License)
- **Code Coverage**: JaCoCo reports
- **Static Analysis**: SonarQube
- **Package Structure**: Clear separation of concerns (model, repository, service, resource)

## Integration Points

### External Systems

1. **Nirgali**: Frontend application consuming the GraphQL API
2. **SIRI Consumers**: External systems requesting SIRI-SX/ET data
3. **Permission Store**: Authorization and role management
4. **Google Cloud Firestore**: Data persistence

### Data Flow

1. **GraphQL Flow**:
   - User authenticates via OAuth2
   - Queries/mutations validated against codespace permissions
   - Data persisted to Firestore with codespace/authority organization

2. **SIRI Flow**:
   - External system sends SIRI XML request
   - Service retrieves data from Firestore
   - Entities mapped to SIRI model
   - SIRI XML response generated and returned

## Key Concepts

### Codespace & Authority

Multi-tenancy is achieved through codespace and authority parameters:
- **Codespace**: Namespace for data isolation (e.g., organization ID)
- **Authority**: Sub-organization or dataset identifier

### Validity Periods

Messages and journeys have time-based validity:
- `validityPeriod.startTime` / `validityPeriod.endTime` for situation elements
- `expiresAtEpochMs` for cancellations/extra journeys
- Automatic filtering of expired messages

### Vehicle Journey References

Uniquely identify specific trips:
- `FramedVehicleJourneyRef`:
  - `dataFrameRef`: Operating day
  - `datedVehicleJourneyRef`: Specific journey identifier

## Development Tips

1. **Making Changes**:
   - Run `./mvnw prettier:write` before committing (or let Maven auto-format)
   - Ensure tests pass: `./mvnw test`
   - Integration tests require Firestore testcontainer

2. **Adding New GraphQL Types**:
   - Update `schema.graphqls`
   - Create corresponding Java models
   - Implement data fetchers in `graphql/` package
   - Add repository methods if needed

3. **Modifying SIRI Mappings**:
   - Update mappers in `siri/mapper/`
   - Consider both directions: entity → SIRI and SIRI → entity
   - Test with real SIRI XML examples

4. **Security Changes**:
   - Verify changes against all security configurations
   - Test both authenticated and unauthenticated scenarios
   - Check permission enforcement for all operations

## Common Tasks

### Add a New Field to SituationElement

1. Update `PtSituationElementEntity.java`
2. Update GraphQL schema in `schema.graphqls` (both type and input)
3. Update `SituationElementEntityToSiriMapper.java` if SIRI-exposed
4. Run tests and update snapshots if needed

### Add a New GraphQL Query

1. Define query in `schema.graphqls`
2. Create data fetcher in `graphql/` package
3. Implement repository method if needed
4. Add security checks for codespace access
5. Write tests

### Debug SIRI Issues

1. Enable debug logging in `logback.xml`
2. Capture request/response XML
3. Validate against SIRI XSD schema
4. Check mapper implementations
5. Verify Firestore data structure

## Related Resources

- [Nirgali Frontend](https://github.com/entur/nirgali)
- [SIRI Standard](http://www.siri.org.uk/)
- [Entur Organization](https://github.com/entur)
- [Spring Cloud GCP Documentation](https://spring.io/projects/spring-cloud-gcp)
- [GraphQL Java Documentation](https://www.graphql-java.com/)

## Notes for AI Assistants

- **Code Style**: Follow existing patterns; Prettier enforces formatting
- **Testing**: Integration tests use Testcontainers; snapshot tests capture expected output
- **Security**: Always consider codespace/authority isolation and permission checks
- **SIRI Compliance**: Changes to SIRI mappings must maintain spec compliance
- **Backward Compatibility**: GraphQL schema changes should be additive when possible
