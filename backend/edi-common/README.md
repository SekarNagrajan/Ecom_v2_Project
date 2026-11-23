# EDI Common Entities Module

This module contains shared EDI (Electronic Data Interchange) entities that are used across multiple services in the ECOMAPI platform.

## Purpose

The `edi-common` module provides a centralized location for EDI-related JPA entities, avoiding code duplication between `booking-service` and `sibl-service`.

## Entities Included

This module contains **23 EDI entity classes**:

### EDI Configuration
- `EdiDocConfig` - EDI document configuration
- `EdiDocStatus` - EDI document status tracking
- `EdiReaderMaster` - EDI reader master data

### EDI Temp Booking
- `EdiTempBooking` - Main booking information
- `EdiTempBookingAgency` - Agency routing information
- `EdiTempBookingBody` - Booking body/description
- `EdiTempBookingCargo` - Cargo/container details
- `EdiTempBookingChargeNew` - Charge details (new)
- `EdiTempBookingCharges` - Charge information
- `EdiTempBookingCommodity` - Commodity information
- `EdiTempBookingConf` - Booking confirmation/cutoffs
- `EdiTempBookingContacts` - Contact information
- `EdiTempBookingCust` - Customer/party information
- `EdiTempBookingError` - Error tracking
- `EdiTempBookingFooter` - BL footer information
- `EdiTempBookingHaulage` - Haulage/transport details
- `EdiTempBookingHeader` - BL header information
- `EdiTempBookingImo` - IMO/DG (Dangerous Goods) details
- `EdiTempBookingInsurance` - Insurance details
- `EdiTempBookingLeg` - Route leg information
- `EdiTempBookingReference` - Reference numbers
- `EdiTempBookingRemarks` - Remarks/notes
- `EdiTempBookingRouting` - Routing details

## Package Structure

```
com.solverminds.edi.entity
├── EdiDocConfig.java
├── EdiDocStatus.java
├── EdiReaderMaster.java
├── EdiTempBooking.java
├── EdiTempBookingAgency.java
├── ... (and 18 more entities)
```

## Usage

### In Maven Projects

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.svm.ecom</groupId>
    <artifactId>edi-common</artifactId>
    <version>${project.version}</version>
</dependency>
```

### In Java Code

Import the entities:

```java
import com.solverminds.edi.entity.EdiDocConfig;
import com.solverminds.edi.entity.EdiTempBooking;
// ... other entities as needed
```

### With Spring Data JPA

Create repositories in your service:

```java
import com.solverminds.edi.entity.EdiDocConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EdiDocConfigRepository extends JpaRepository<EdiDocConfig, Integer> {
    // Your custom query methods
}
```

## Entity Scanning

Make sure your Spring Boot application scans the EDI entity package. Add this to your main application class:

```java
@SpringBootApplication
@EntityScan(basePackages = {
    "com.solverminds.booking.entity",  // Your service's entities
    "com.solverminds.edi.entity"       // EDI common entities
})
@EnableJpaRepositories(basePackages = {
    "com.solverminds.booking.repo",    // Your service's repositories
    "com.solverminds.edi.repo"         // If you create EDI repositories
})
public class YourServiceApplication {
    // ...
}
```

## Dependencies

This module requires:
- Spring Boot Starter Data JPA
- JPA API (javax.persistence)

## Services Using This Module

- **booking-service** - For EDI booking integration
- **sibl-service** - For SIBL EDI processing

## Building

```bash
mvn clean install
```

## Database Schema

All entities map to the `dbo` schema in SQL Server. The table definitions are based on the EDITables specification.

## Notes

- All entities implement `Serializable`
- All entities include `equals()`, `hashCode()`, and `toString()` methods
- Primary keys use appropriate generation strategies
- varchar(max) columns are mapped with `columnDefinition = "varchar(max)"`

