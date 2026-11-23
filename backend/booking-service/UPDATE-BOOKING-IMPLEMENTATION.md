# Update Booking API Implementation

## Overview
This document describes the implementation of the update booking functionality for the booking service.

## API Endpoint

### PUT /api/v1/booking/{onlineBookNo}

**Description:** Updates an existing booking identified by `onlineBookNo`. The endpoint supports updating all booking details including nested structures like customers, containers, commodities, routes, reefer settings, and dangerous goods.

**Path Parameter:**
- `onlineBookNo` (required) - The booking number to update (e.g., "BKON1234")

**Request Body:**
The request body uses the same structure as the create booking API (`BookingRequestDTO`).

**Example Request:**
```json
PUT /api/v1/booking/BKON1442
Content-Type: application/json
Authorization: Bearer <JWT_TOKEN>

{
  "originPortId": "INNSA",
  "finalPortId": "AEJEA",
  "loadPortId": "INNSA",
  "dischargePortId": "AEJEA",
  "cargoReadyDate": "2025-09-09",
  "preCarriage": "N",
  "onCarriage": "N",
  "status": "D",
  "customerCode": "20121345938",
  "routeId": 18272,
  "oceanFreight": "P",
  "isAmendment": "N",
  "ensFilling": "N",
  "t2lFilling": "N",
  "ensType": "S",
  "customers": [
    {
      "customerType": "BP",
      "customerCode": "20001374",
      "countryId": "CN",
      "customerName": "ECOM TESTING",
      "address": "513B, Building A,Free Trade Financial Center",
      "city": "Beijiao, China",
      "zipCode": "603103",
      "telephone": "86-38-00138000",
      "email": "email@solverminds.com"
    }
  ],
  "containers": [
    {
      "cargoNo": 1,
      "oogFlag": "N",
      "equipmentQty": 1,
      "equipmentType": "D",
      "equipmentSize": "40DC",
      "socFlag": "N",
      "dgFlag": "N",
      "equipmentStatus": "F",
      "cargoWeight": 10000,
      "weightUnit": "K",
      "nor": "N",
      "containerNo": "20DCTBN1"
    }
  ],
  "commodities": [
    {
      "cargoNo": 1,
      "containerNo": "20DCTBN1",
      "itemNo": 1,
      "commodityGroup": 90,
      "hsCode": "9004909000",
      "packageType": "PACK",
      "packageQty": 50,
      "cargoVolume": 12000,
      "volumnUnit": "M",
      "nettwt": 7500,
      "commodityHBLNo": ""
    }
  ],
  "routes": [
    {
      "legNo": 1,
      "loadBound": "N",
      "serviceCode": "PRO",
      "vesselCode": "PRO",
      "voyageCode": "101",
      "serviceType": "M",
      "loadPortId": "INNSA",
      "loadTerminal": "INNSABMCT0",
      "dischargePortId": "AEJEA",
      "dischargeTerminal": "AEJEA01",
      "etdDate": "2025-09-19 03:00:00",
      "etaDate": "2025-09-26 02:00:00",
      "loadType": "L",
      "dischType": "L",
      "groupCallId": "GrpCall-498839",
      "dischargeCallId": "GrpCall-498842",
      "loadCallId": "GrpCall-498839",
      "dischargeBound": "N"
    }
  ],
  "files": [
    {
      "fileCategory": "DGC",
      "fileName": "BKON1442_DGC.jpg"
    },
    {
      "fileCategory": "VGM",
      "fileName": "BKON1442_VGM.jpg"
    }
  ]
}
```

**Response (200 OK):**
```json
{
  "onlineBookNo": "BKON1442",
  "status": "D",
  "message": "Booking updated successfully"
}
```

**Response Codes:**
- `200 OK` - Booking updated successfully
- `400 Bad Request` - Invalid request data or validation errors
- `401 Unauthorized` - Missing or invalid JWT token
- `403 Forbidden` - Customer code mismatch (user trying to update another customer's booking)
- `404 Not Found` - Booking not found with the specified onlineBookNo
- `500 Internal Server Error` - Unexpected server error
- `503 Service Unavailable` - Database service unavailable

## Implementation Details

### Files Created/Modified

#### 1. New Service: `BookingUpdateService.java`
**Location:** `booking-service/src/main/java/com/solverminds/booking/service/BookingUpdateService.java`

**Key Methods:**
- `updateBooking()` - Main update method that orchestrates the entire update process
- `deleteChildRecords()` - Deletes all child records (customers, routes, containers, commodities, reefer, OOG, DG)
- `updateMasterBooking()` - Updates the master booking record with new data

**Update Strategy:**
1. Verify booking exists and belongs to the customer (security check)
2. Delete all existing child records in correct order
3. Update master booking record
4. Insert new child records using existing `BookingCreateService` methods
5. Increment version number for optimistic locking

#### 2. Updated Repositories
Added `deleteByEbookno(String ebookno)` methods to:
- `EcomBookingCustomerRepository.java`
- `EcomBookingRouteRepository.java`
- `EcomBookingCommodityRepository.java`

Note: `EcomBookingContainerRepository`, `EcomBookingReeferRepository`, `EcomBookingOogRepository`, and `EcomBookingDgRepository` already had these methods.

#### 3. Updated `BookingRepository.java`
- Injected `BookingUpdateService`
- Modified `updateBooking()` method to accept JWT parameters and delegate to `BookingUpdateService`

#### 4. Updated `BookingController.java`
- Enhanced update endpoint to extract `loginNameFromJwt` from JWT token
- Pass both `customerCodeFromJwt` and `loginNameFromJwt` to update service
- Added proper security validation

## Security Features

1. **JWT Authentication** - All requests require valid JWT token
2. **Customer Authorization** - Verifies customer code from JWT matches the booking's customer
3. **Audit Trail** - Records user who updated the booking and timestamp
4. **Version Control** - Increments version number for optimistic locking

## Update Process Flow

```
1. Extract JWT token and validate
2. Extract customerCode and loginName from JWT
3. Validate request body and path parameter
4. Check if onlineBookNo in path matches body (if present)
5. Call BookingUpdateService.updateBooking()
   a. Find existing booking by onlineBookNo
   b. Verify customer authorization
   c. Delete all child records:
      - Dangerous goods (DG)
      - Out-of-gauge (OOG)
      - Reefer settings
      - Commodities
      - Containers
      - Routes
      - Customers
   d. Update master booking fields:
      - Port information
      - Dates
      - Status
      - Agency details
      - Customer references
      - Version increment
      - Update timestamp and user
   e. Insert new child records:
      - Customers
      - Routes
      - Containers (with reefer/OOG/DG if applicable)
      - Commodities
6. Return success response with 200 OK
```

## Testing the API

### Prerequisites
1. Valid JWT token with customer code
2. Existing booking number (onlineBookNo)

### Sample cURL Command
```bash
curl -X PUT "http://localhost:8080/api/v1/booking/BKON1442" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d @update-booking-request.json
```

### Test Scenarios

1. **Successful Update**
   - Use valid JWT and existing booking number
   - Expect: 200 OK with updated booking details

2. **Booking Not Found**
   - Use non-existent booking number
   - Expect: 404 Not Found

3. **Unauthorized Access**
   - Try to update another customer's booking
   - Expect: 403 Forbidden

4. **Invalid JWT**
   - Use expired or invalid token
   - Expect: 401 Unauthorized

5. **Validation Errors**
   - Send invalid data (e.g., missing required fields)
   - Expect: 400 Bad Request with validation details

## Notes

1. **Reusability**: The update service reuses the `BookingCreateService` methods for inserting child records, maintaining consistency between create and update operations.

2. **Transactional**: All update operations are wrapped in a transaction (`@Transactional`), ensuring atomicity.

3. **Agency Code Lookup**: Like create, the update fetches agency code from reportDB based on origin port.

4. **Nested Structures**: 
   - Reefer settings are nested under containers (when equipmentType='R')
   - Dangerous goods details are created when dgFlag='Y'
   - OOG details are created when oogFlag='Y'

5. **Validation**: Uses the same validation as create booking (`@Valid` annotation on request body)

6. **Audit Fields**:
   - `usercr` and `datecr` remain unchanged (original creator)
   - `userup` and `dateup` are set to current user and timestamp

## Future Enhancements

1. Implement file attachment handling for the `files` array
2. Add partial update support (PATCH) for updating specific fields
3. Implement optimistic locking validation using version field
4. Add change history tracking
5. Support for bulk updates

