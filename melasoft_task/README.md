## To Run

```bash
# Windows
mvnw.cmd clean package 
java -jar target\melasoft_task-0.0.1-SNAPSHOT.jar     

# Mac/Linux
./mvnw clean package 
java -jar target/melasoft_task-0.0.1-SNAPSHOT.jar     
```

Example:
```bash
java -jar target\melasoft_task-0.0.1-SNAPSHOT.jar INV-2026-0001 DE 129273398 FR 40303265045
```

Example (Windows):
```
.\mvnw.cmd clean package 
java -jar target\melasoft_task-0.0.1-SNAPSHOT.jar INV-2026-0001 DE 129273398 FR 40303265045
```



# Design Decisions

## Architecture

- The project is structured using a layered architecture:
  - **Client layer:** Responsible only for communication with the VIES SOAP service.
  - **Service layer:** Contains the business logic for invoice validation.
  - **Model layer:** Holds invoice and request/response models.
- Responsibilities are separated to keep the business logic independent from the SOAP communication details.

## Configuration

- Externalized all configurable values into `application.properties`.
- Used Spring Boot `@ConfigurationProperties` (`ViesClientProperties`) instead of multiple `@Value` annotations to group VIES-related configuration in a single class.
- Configurable properties include:
  - Connection timeout
  - Receive timeout
  - Retry count
  - Retry delay

## SOAP Client

- Apache CXF is used to generate SOAP client classes from the provided WSDL.
- XML Schema validation is enabled for SOAP responses.
- Connection and receive timeouts are configured on the CXF `HTTPConduit`.

## Error Handling

- Distinguished between business responses and technical failures.
- An invalid VAT number is treated as a normal response (`CheckVatResponse.isValid() == false`).
- SOAP faults representing invalid user input (`INVALID_INPUT`) are propagated immediately.
- XML schema validation failures are wrapped in a custom `SchemaValidationException`.
- Other unexpected communication errors are propagated to the caller.

## Retry Mechanism

- Implemented retry logic inside the VIES client.
- Retryable VIES fault codes are defined using `ViesFaultEnum`.
- The client automatically retries when receiving temporary service errors:
  - `GLOBAL_MAX_CONCURRENT_REQ`
  - `MS_MAX_CONCURRENT_REQ`
  - `SERVICE_UNAVAILABLE`
  - `MS_UNAVAILABLE`
  - `TIMEOUT`
- Maximum retry count and retry delay are configurable through `application.properties`.

## Logging

- SLF4J is used for application logging.
- Each VAT validation request logs:
  - Country code
  - VAT number
  - Validation result
- When both VAT numbers are valid, the returned company names and addresses are logged.
- Retry attempts and the corresponding VIES fault codes are logged as warnings.

## Dependency Injection

- Spring dependency injection is used throughout the application.
- Configuration values are injected via `ViesClientProperties`.
- Business logic depends on the `ViesClient` interface instead of its implementation, promoting loose coupling.

## Testing

- Unit tests are implemented using **JUnit 5** and **Mockito**.
- `InvoiceService` is tested by mocking the `ViesClient`.
- Covered scenarios include:
  - Both VAT numbers are valid.
  - Buyer VAT number is invalid.
  - Seller VAT number is invalid.
  - Both VAT numbers are invalid.
- The SOAP client is designed to be mockable by injecting the generated `CheckVatPortType`, enabling isolated unit testing.

## Extensibility

- The application currently runs as a Spring Boot command-line application using `CommandLineRunner`.
- The service layer is independent of the execution mechanism, making it straightforward to expose the same functionality through REST controllers or other interfaces in the future without modifying the business logic.