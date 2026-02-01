# Calculator Application - GitHub Copilot Instructions

## Project Overview

## Technology Stack

### Additional Libraries

### Testing

### Code Quality

## Project Structure

## Coding Conventions & Patterns

### Architecture Patterns
1. **Layered Architecture**: Controller → Service → Repository pattern
2. **DTO Pattern**: Separate DTOs for API layer, domain models for business logic
3. **Mapper Pattern**: MapStruct interfaces with `componentModel = MappingConstants.ComponentModel.SPRING`
4. **Repository Pattern**: Interface-based repositories (Cassandra, Solr, JPA)
5. **Exception Handling**: Centralized with `@RestControllerAdvice` in `GlobalExceptionHandler`

### Naming Conventions
- **Controllers**: `*Controller.java` (e.g., `StockController`, `StockChangeController`)
- **Services**: `*Service.java` (e.g., `StockTransactionService`, `ReservationService`)
- **Repositories**: `*Repository.java` with interface/implementation split
- **Mappers**: `*Mapper.java` as MapStruct interfaces
- **DTOs**: `*Dto.java` or `*Request/Response.java`
- **DAOs**: `*Dao.java` for Cassandra entities
- **Exceptions**: `*Exception.java` with custom error codes

### Code Style Guidelines
1. **Use Lombok annotations**: `@RequiredArgsConstructor`, `@Slf4j`, `@Data`, etc.
2. **Constructor Injection**: Prefer constructor injection with `@RequiredArgsConstructor`
3. **Validation**: Use Jakarta validation annotations (`@Valid`, `@Size`, `@NotNull`, etc.)
4. **Logging**: Use SLF4J via Lombok's `@Slf4j`
5. **Configuration**: Use `@ConfigurationProperties` for external config binding
6. **Async Processing**: Use context-aware executors for maintaining request context
7. **Testing**: Use `@ExtendWith(MockitoExtension.class)` and Testcontainers for integration tests

### REST API Standards
- **OpenAPI Documentation**: Use `@Operation`, `@ApiResponses`, `@ApiResponse` annotations
- **Path Variables**: Use `{locationId}` pattern for resource identification
- **Query Parameters**: Use `@RequestParam` for filtering and search options
- **Response Format**: Standard JSON responses with appropriate HTTP status codes
- **Error Handling**: Return plain text error messages with meaningful status codes

### Security Guidelines
- **Authorization**: Use `@HasTechnicalRoleOrHasAnyAuthorityAndLocationAccess` on endpoints
- **Authorities**: Define in `BettyAuthority` (e.g., `STOCK_WRITE`, `LOCATION_DATA_READ`)
- **Technical Roles**: Define in `TechnicalUserRole` (e.g., `STOCK_LOCATION`, `GOODS_RECEIVING`)
- **Authentication Context**: Extract user info from `Authentication` parameter

### Database Interaction
1. **Cassandra**: Use Spring Data Cassandra repositories with custom query methods
2. **Solr**: Use `SolrQuery` for searches, `SolrInputDocument` for indexing
3. **PostgreSQL**: Use Spring Data JPA with Hibernate (show SQL enabled)
4. **Transactions**: Use `@Transactional` where appropriate (enabled globally)

### Error Handling Pattern
```java
// Custom exceptions for domain-specific errors
throw new StockNotFoundException(locationId, articleId);
throw new StockQueryException(StockQueryErrorCode.INVALID_FILTER);
throw new BettyBundleNotFoundException(mgb);

// Handle in GlobalExceptionHandler with appropriate HTTP status
```

### Mapper Pattern
```java
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ExampleMapper {
    @Mapping(target = "targetField", source = "sourceField")
    TargetDto toDto(SourceEntity entity);
}
```

### Service Pattern
```java
@Slf4j
@RequiredArgsConstructor
@Service
public class ExampleService {
    private final ExampleRepository repository;
    private final ExampleMapper mapper;
    
    // Business logic methods
}
```

## Testing Guidelines

1. **Unit Tests**: Use Mockito for mocking dependencies
2. **Integration Tests**: Use Testcontainers for Cassandra, Solr, PostgreSQL
3. **Test Naming**: `should{ExpectedBehavior}When{StateUnderTest}`
4. **Coverage**: JaCoCo reports exclude generated code (`**/io/swagger/**`)
5. **Assertions**: Use JUnit 5 assertions (`assertEquals`, `assertTrue`, etc.)

## Common Tasks

### Adding a New REST Endpoint
1. Create DTO in `controller/dto/`
2. Create domain model in `common/model/`
3. Create MapStruct mapper in `common/mappers/`
4. Create service method in appropriate service
5. Add controller method with OpenAPI annotations
6. Add security annotations for authorization
7. Write unit and integration tests

## When Generating Code

1. Always use constructor injection with `@RequiredArgsConstructor`
2. Include validation annotations on request DTOs_*
3. Use MapStruct for DTO conversions
4. Add SLF4J logging at appropriate levels
5. Handle exceptions properly with custom exception types
6. Write comprehensive tests (unit + integration)
7. Follow existing package structure and naming conventions
