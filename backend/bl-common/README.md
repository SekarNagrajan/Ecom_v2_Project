# BL Common Entities Module

Shared Bill of Lading (BL) JPA entities and repositories for the Documentation database. Used by services that need to read from `bl`, `bl_cargo`, `bl_commodity`, `bl_cust`, and `bl_header` tables (view-only).

## Primary keys

- **bl**: `bl_no` (varchar 17) is the **primary key**. It is the only table where `bl_no` is the PK.
- **bl_header**: `bl_no` (PK, FK to bl).
- **bl_cust**: `cid` (bigint, identity) is the PK; `bl_no` is FK to bl.
- **bl_cargo**: Composite PK `(bl_no, eqpId)`; `bl_no` is FK to bl.
- **bl_commodity**: Composite PK `(bl_no, eqpId)`; `bl_no`/`eqpId` are FK to bl_cargo.

So **bl_no is the primary key only for the main `bl` table**. In all other BL tables, `bl_no` is a **foreign key** referencing `bl.bl_no`.

## Purpose

- **bl-common** holds BL-related entities and Spring Data JPA repositories so **booking-common** stays focused on booking tables and BL tables are in a separate module.
- Used by **sibl-service** (and any other service) that queries the Documentation DB by BL number.

## Entities

| Entity         | Table       | Description                    |
|----------------|-------------|--------------------------------|
| `Bl`           | `bl`        | Bill of Lading master          |
| `BlHeader`     | `bl_header` | BL header / party references   |
| `BlCust`       | `bl_cust`   | BL customer/party details      |
| `BlCargo`      | `bl_cargo`  | BL container/cargo lines       |
| `BlCommodity`  | `bl_commodity` | BL commodity lines          |

Composite keys: `BlCargoId`, `BlCommodityId`.

## Packages

- `com.solverminds.bl.common.entity` – JPA entities
- `com.solverminds.bl.common.repository` – Spring Data JPA repositories

## Usage

Add the dependency:

```xml
<dependency>
    <groupId>com.svm.ecom</groupId>
    <artifactId>bl-common</artifactId>
    <version>1.0.0</version>
</dependency>
```

Documentation DB config (e.g. in **common-security** `DocumentationDataConfig`) must:

- Scan `com.solverminds.bl.common.entity` for entities
- Scan `com.solverminds.bl.common.repository` for repositories

## Services Using This Module

- **sibl-service** – view by `blNumber` (BlRepository, BlCargoRepository, etc.)
- **common-security** – provides Documentation DB config and depends on bl-common so BL repos are on the classpath
