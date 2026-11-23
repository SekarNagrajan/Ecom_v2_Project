# Booking Common Entities Module

This module contains shared Booking entities that are used across multiple services in the ECOMAPI platform.

## Purpose

The `booking-common` module provides a centralized location for Booking-related JPA entities, avoiding code duplication between `booking-service` and `sibl-service`. These entities are designed for **VIEW purposes only** - no inserts or updates are performed.

## Entities Included

This module contains **13 JPA entity classes** based on the Data Dictionary Booking V1.3:

### Main Booking Entity
- `Booking` - Main booking table (PK: `book_no`) - To maintain all booking master data

### Related Entities
- `BookingAgency` - Booking related agency details (transhipment agencies, POL/POD agencies)
- `BookingCargo` - Container/cargo details (equipment, temperature, weight, seals)
- `BookingChargeNew` - Booking charges and rates
- `BookingCommodity` - Commodity details (HS codes, packages, weights, dimensions)
- `BookingConf` - Booking confirmation tab data (shipper, forwarder, pickup details)
- `BookingContacts` - Export/Import contact information
- `BookingCust` - Customer party details (shipper, consignee, notify party)
- `BookingHaulage` - Haulage details (legacy - not in use)
- `BookingImo` - DG (Dangerous Goods) / IMO details
- `BookingLeg` - Voyage leg/routing details
- `BookingReference` - EDI reference numbers
- `BookingRouting` - Detailed routing information (origin, POL, transhipments, POD, delivery)

## Package Structure

```
com.solverminds.booking.common.entity
├── Booking.java
├── BookingAgency.java
├── BookingCargo.java
├── BookingChargeNew.java
├── BookingCommodity.java
├── BookingConf.java
├── BookingContacts.java
├── BookingCust.java
├── BookingHaulage.java
├── BookingImo.java
├── BookingLeg.java
├── BookingReference.java
└── BookingRouting.java
```

## Technology Stack

- **Java**: 8
- **Spring Boot**: 2.7.x
- **JPA/Hibernate**: javax.persistence annotations
- **Lombok**: @Getter, @Setter, @NoArgsConstructor, @AllArgsConstructor, @Builder

## Usage

### In Maven Projects

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.svm.ecom</groupId>
    <artifactId>booking-common</artifactId>
    <version>1.0.0</version>
</dependency>
```

### In Java Code

Import the entities:

```java
import com.solverminds.booking.common.entity.Booking;
import com.solverminds.booking.common.entity.BookingCargo;
import com.solverminds.booking.common.entity.BookingCommodity;
// ... other entities as needed
```

### With Spring Data JPA

Create repositories in your service:

```java
import com.solverminds.booking.common.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, String> {
    List<Booking> findByAgencyId(String agencyId);
    List<Booking> findByStatus(String status);
    List<Booking> findByVesselCodeAndVoyageCode(String vesselCode, String voyageCode);
}
```

## Entity Scanning

Make sure your Spring Boot application scans the Booking entity package:

```java
@SpringBootApplication
@EntityScan(basePackages = {
    "com.solverminds.booking.common.entity",  // Booking common entities
    "com.solverminds.edi.entity"              // EDI common entities (if needed)
})
@EnableJpaRepositories(basePackages = {
    "com.yourservice.repository"       // Your service's repositories
})
public class YourServiceApplication {
    // ...
}
```

## Entity Relationships

All child entities have a `@ManyToOne` relationship to the main `Booking` entity via the `book_no` foreign key:

```
Booking (book_no PK)
├── BookingAgency (cid PK, book_no FK)
├── BookingCargo (cid PK, book_no FK)
├── BookingChargeNew (cid PK, book_no FK)
├── BookingCommodity (cid PK, book_no FK)
├── BookingConf (book_no PK/FK)  <-- 1:1 relationship
├── BookingContacts (cid PK, book_no FK)
├── BookingCust (cid PK, book_no FK)
├── BookingHaulage (cid PK, book_no FK)
├── BookingImo (id PK, book_no FK)
├── BookingLeg (id PK, book_no FK)
├── BookingReference (id PK, book_no FK)
└── BookingRouting (id PK, book_no FK)
```

## Dependencies

This module requires:
- Spring Boot Starter Data JPA
- JPA API (javax.persistence)
- Lombok

## Services Using This Module

- **booking-service** - For booking view operations
- **sibl-service** - For SI/BL booking reference

## Building

```bash
mvn clean install
```

## Database Schema

All entities map to the `Documentation` database in SQL Server. The table definitions are based on the Data Dictionary Booking V1.3.

## Design Decisions

1. **Lombok Usage**: Using `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder` (NOT `@Data`)
2. **Lazy Loading**: All `@ManyToOne` relationships use `FetchType.LAZY`
3. **Unidirectional Relationships**: Only child → parent relationships defined
4. **equals/hashCode**: Based only on primary key
5. **No Jackson Annotations**: Module is framework-agnostic
6. **varchar(max) Support**: Mapped using `columnDefinition = "varchar(max)"`

## Notes

- All entities implement `Serializable`
- All entities include `equals()`, `hashCode()`, and `toString()` methods
- Primary keys use appropriate generation strategies (IDENTITY for auto-increment, none for natural keys)
- Column definitions match exact database column names
- Business descriptions from data dictionary included as Javadoc comments
