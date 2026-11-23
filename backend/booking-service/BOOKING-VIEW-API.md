# Booking View API Documentation

## Overview
The Booking View API allows you to retrieve complete booking details including all related data such as customers, containers, commodities, routes, and file attachments.

## Endpoint

```
GET /api/v1/booking/view
```

## Authentication
This endpoint requires Bearer token authentication. Include your JWT token in the Authorization header.

```
Authorization: Bearer <your_jwt_token>
```

## Query Parameters

At least one of the following parameters is required:

| Parameter | Type | Required | Description | Example |
|-----------|------|----------|-------------|---------|
| `onlineBookNo` | string | No | Online booking number | BKON1234 |
| `bookingNumber` | string | No | Booking number (external) | INDMUM09817121 |
| `status` | string | No | Booking status (D/S/T) | D |

**Note**: The customer code is automatically extracted from the JWT token and used to filter results.

## Response Format

### Success Response (200 OK)

```json
{
  "onlineBookNo": "BKON1234",
  "agencyId": "INDMUM",
  "originPortId": "INNSA",
  "finalPortId": "AEJEA",
  "loadPortId": "INNSA",
  "dischargePortId": "AEJEA",
  "bookDate": "2025-10-14T03:30:21.022Z",
  "cargoReadyDate": "2025-09-10",
  "preCarriage": "C",
  "onCarriage": "N",
  "remarks": "string",
  "status": "D",
  "bookingNumber": "INDMUM09817121",
  "customerCode": "20121345938",
  "templateName": "MUM_AEJ_20DC",
  "rateRefNo": "INDMUM78271",
  "submittedDate": "2025-09-10",
  "routeId": 18272,
  "agencyRefNo": "AEG1232",
  "oceanFreight": "P",
  "isAmendment": "N",
  "exportRefNo": "E98211",
  "customerPO": "PO829281",
  "ensFilling": "S",
  "t2lFilling": "N",
  "ensType": "S",
  "customers": [
    {
      "customerType": "BP",
      "customerCode": "20001374",
      "countryId": "CN",
      "customerName": "ECOM TESTING",
      "address": "513B, Building A,Free Trade Financial Center",
      "address1": "Xiamen Area of China",
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
      "containerNo": "20DCTBN1",
      "reefer": {
        "cargoNo": 1,
        "containerNo": "20DCTBN1",
        "itemNo": 1,
        "setTemp": 8
      }
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
      "commodityHBLNo": "string",
      "dangerousGoods": [
        {
          "cargoNo": 1,
          "containerNo": "20DCTBN1",
          "itemNo": 1,
          "marinePollutant": "N",
          "unNo": "3480",
          "flashPt": 32,
          "flashPtUnit": "C",
          "classCode": "9"
        }
      ]
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
      "etdDate": "2025-10-14T03:30:21.022Z",
      "etaDate": "2025-10-14T03:30:21.022Z",
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
    }
  ]
}
```

## Error Responses

### 400 Bad Request
When no search parameters are provided:
```json
{
  "status": 400,
  "message": "Bad Request",
  "timestamp": "2025-10-14T03:30:21.022Z",
  "errors": [
    {
      "code": "VALIDATION_GENERIC",
      "field": null,
      "rejectedValue": null,
      "errorType": "validationError",
      "message": "At least one of onlineBookNo, bookingNumber, or status must be provided"
    }
  ]
}
```

### 401 Unauthorized
When JWT token is missing or invalid:
```json
{
  "status": 401,
  "message": "Unauthorized",
  "timestamp": "2025-10-14T03:30:21.022Z",
  "errors": [
    {
      "code": "UNAUTHORIZED",
      "errorType": "unauthorized",
      "message": "Missing or invalid JWT token"
    }
  ]
}
```

### 404 Not Found
When booking is not found:
```json
{
  "status": 404,
  "message": "Not Found",
  "timestamp": "2025-10-14T03:30:21.022Z",
  "errors": [
    {
      "code": "NOT_FOUND",
      "errorType": "notFound",
      "message": "Booking not found for given parameters"
    }
  ]
}
```

### 500 Internal Server Error
```json
{
  "status": 500,
  "message": "Internal Server Error",
  "timestamp": "2025-10-14T03:30:21.022Z",
  "errors": [
    {
      "code": "INTERNAL_ERROR",
      "errorType": "internalError",
      "message": "An unexpected error occurred while fetching the booking"
    }
  ]
}
```

### 503 Service Unavailable
```json
{
  "status": 503,
  "message": "Service Unavailable",
  "timestamp": "2025-10-14T03:30:21.022Z",
  "errors": [
    {
      "code": "SERVICE_UNAVAILABLE",
      "errorType": "serviceUnavailable",
      "message": "Report database is currently unavailable"
    }
  ]
}
```

## Example Usage

### Using cURL

```bash
# Search by onlineBookNo
curl -X GET "http://localhost:8080/api/v1/booking/view?onlineBookNo=BKON1234" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Search by bookingNumber
curl -X GET "http://localhost:8080/api/v1/booking/view?bookingNumber=INDMUM09817121" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Search by status
curl -X GET "http://localhost:8080/api/v1/booking/view?status=D" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Combined search
curl -X GET "http://localhost:8080/api/v1/booking/view?onlineBookNo=BKON1234&status=D" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Using JavaScript (Fetch API)

```javascript
const token = 'YOUR_JWT_TOKEN';
const onlineBookNo = 'BKON1234';

fetch(`http://localhost:8080/api/v1/booking/view?onlineBookNo=${onlineBookNo}`, {
  method: 'GET',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  }
})
.then(response => response.json())
.then(data => console.log(data))
.catch(error => console.error('Error:', error));
```

### Using Python (requests)

```python
import requests

token = 'YOUR_JWT_TOKEN'
url = 'http://localhost:8080/api/v1/booking/view'
headers = {
    'Authorization': f'Bearer {token}'
}
params = {
    'onlineBookNo': 'BKON1234'
}

response = requests.get(url, headers=headers, params=params)
if response.status_code == 200:
    booking = response.json()
    print(booking)
else:
    print(f"Error: {response.status_code}")
    print(response.json())
```

## Field Descriptions

### Master Booking Fields

| Field | Type | Description |
|-------|------|-------------|
| `onlineBookNo` | string | Unique booking identifier (primary key) |
| `agencyId` | string | Agency code |
| `originPortId` | string | Origin port code |
| `finalPortId` | string | Final destination port code |
| `loadPortId` | string | Loading port code |
| `dischargePortId` | string | Discharge port code |
| `bookDate` | datetime | Booking creation date |
| `cargoReadyDate` | date | Date when cargo is ready |
| `preCarriage` | string | Pre-carriage type (C/M/N) |
| `onCarriage` | string | On-carriage type (C/M/N) |
| `remarks` | string | Booking remarks |
| `status` | string | Booking status: D (Draft), S (Submitted), T (Template) |
| `bookingNumber` | string | External booking number |
| `customerCode` | string | Customer identifier |
| `templateName` | string | Template name (if status is T) |
| `rateRefNo` | string | Rate reference number |
| `submittedDate` | date | Date when booking was submitted |
| `routeId` | integer | Route identifier |
| `agencyRefNo` | string | Agency reference number |
| `oceanFreight` | string | Ocean freight type: P (Prepaid), C (Collect) |
| `isAmendment` | string | Amendment flag: Y (Yes), N (No) |
| `exportRefNo` | string | Export reference number |
| `customerPO` | string | Customer purchase order number |
| `ensFilling` | string | ENS filling type: S (Single), P (Partial), N (NA) |
| `t2lFilling` | string | T2L filling flag: Y (Yes), N (No) |
| `ensType` | string | ENS type: S (Straight B/L), C (Consolidated) |

### Customer Fields

| Field | Type | Description |
|-------|------|-------------|
| `customerType` | string | Customer role: AP (Agreement Party), BP (Booking Party), BY (Buyer), CN (Consignee), FW (Freight Forwarder), N1 (Notify Party 1), NT (Notify), SH (Shipper), SL (Seller) |
| `customerCode` | string | Customer code |
| `countryId` | string | ISO country code (2 characters) |
| `customerName` | string | Customer name |
| `address` | string | Primary address |
| `address1` | string | Additional address line |
| `city` | string | City |
| `zipCode` | string | ZIP/postal code |
| `telephone` | string | Telephone number |
| `email` | string | Email address |

### Container Fields

| Field | Type | Description |
|-------|------|-------------|
| `cargoNo` | integer | Cargo sequence number |
| `oogFlag` | string | Out of gauge flag: Y/N |
| `equipmentQty` | integer | Number of equipment units |
| `equipmentType` | string | Equipment type: D (Dry), R (Reefer) |
| `equipmentSize` | string | Equipment size code (e.g., 20DC, 40DC) |
| `socFlag` | string | Shipper-owned container flag: Y/N |
| `dgFlag` | string | Dangerous goods flag: Y/N |
| `equipmentStatus` | string | Equipment status: F (Full), M (MT) |
| `cargoWeight` | integer | Cargo weight |
| `weightUnit` | string | Weight unit: K (KG), T (Ton) |
| `nor` | string | Non-operating Reefer: Y (used as dry), N (cooling active) |
| `containerNo` | string | Container number or identifier |

### Reefer Settings (nested in containers when equipmentType='R')

| Field | Type | Description |
|-------|------|-------------|
| `cargoNo` | integer | Cargo sequence number |
| `containerNo` | string | Container number |
| `itemNo` | integer | Item sequence number |
| `setTemp` | integer | Set temperature |

### Commodity Fields

| Field | Type | Description |
|-------|------|-------------|
| `cargoNo` | integer | Cargo sequence number |
| `containerNo` | string | Container number |
| `itemNo` | integer | Item sequence number |
| `commodityGroup` | integer | Commodity group code |
| `hsCode` | string | Harmonized System code |
| `packageType` | string | Package type |
| `packageQty` | integer | Number of packages |
| `cargoVolume` | integer | Cargo volume |
| `volumnUnit` | string | Volume unit: M/F |
| `nettwt` | integer | Net weight |
| `commodityHBLNo` | string | House B/L number |

### Dangerous Goods (nested in commodities when applicable)

| Field | Type | Description |
|-------|------|-------------|
| `cargoNo` | integer | Cargo sequence number |
| `containerNo` | string | Container number |
| `itemNo` | integer | Item sequence number |
| `marinePollutant` | string | Marine pollutant flag: Y/N |
| `unNo` | string | UN number |
| `flashPt` | integer | Flash point temperature |
| `flashPtUnit` | string | Flash point unit: C/F |
| `classCode` | string | Dangerous goods class code |

### Route Fields

| Field | Type | Description |
|-------|------|-------------|
| `legNo` | integer | Route leg sequence number |
| `loadBound` | string | Load bound indicator |
| `serviceCode` | string | Service code |
| `vesselCode` | string | Vessel code |
| `voyageCode` | string | Voyage code |
| `serviceType` | string | Service type |
| `loadPortId` | string | Loading port ID |
| `loadTerminal` | string | Loading terminal |
| `dischargePortId` | string | Discharge port ID |
| `dischargeTerminal` | string | Discharge terminal |
| `etdDate` | datetime | Estimated time of departure |
| `etaDate` | datetime | Estimated time of arrival |
| `loadType` | string | Load type |
| `dischType` | string | Discharge type |
| `groupCallId` | string | Group call ID |
| `dischargeCallId` | string | Discharge call ID |
| `loadCallId` | string | Load call ID |
| `dischargeBound` | string | Discharge bound indicator |

### File Attachment Fields

| Field | Type | Description |
|-------|------|-------------|
| `fileCategory` | string | File category (e.g., DGC, VGM) |
| `fileName` | string | File name |

## Implementation Details

### Service Layer
The view endpoint is implemented using the following components:

1. **BookingController** - REST controller handling HTTP requests
2. **BookingRepository** - Repository layer for data access
3. **BookingViewService** - Service layer that:
   - Fetches master booking data from `ecombooking` table
   - Retrieves related customers from `ecombooking_customer` table
   - Fetches containers from `ecombooking_container` table
   - Gets commodities from `ecombooking_commodity` table
   - Retrieves routes from `ecombooking_route` table
   - Fetches reefer settings when applicable
   - Gets dangerous goods details when applicable
   - Retrieves file attachments

### Database Tables
- `ecombooking` - Master booking data
- `ecombooking_customer` - Customer/party details
- `ecombooking_container` - Container information
- `ecombooking_commodity` - Commodity details
- `ecombooking_route` - Route/vessel schedules
- `ecombooking_reefer` - Reefer container settings
- `ecombooking_dg` - Dangerous goods information

### Security
- JWT-based authentication required
- Customer code is extracted from JWT token
- Only bookings belonging to the authenticated customer are returned
- Cross-customer data access is prevented

## Notes

1. **Nested Data**: Reefer settings are nested under containers when `equipmentType='R'`, and dangerous goods are nested under commodities when applicable.

2. **Optional Fields**: Many fields are optional and may be `null` in the response.

3. **Date Formats**: 
   - DateTime fields use ISO 8601 format: `YYYY-MM-DDTHH:mm:ss.sssZ`
   - Date fields use ISO 8601 date format: `YYYY-MM-DD`

4. **Performance**: The endpoint performs multiple database queries to fetch all related data. For optimal performance, ensure appropriate database indexes are in place.

5. **Pagination**: Currently, the endpoint returns a single booking. If you need to search and return multiple bookings, consider using the `/api/v1/booking/summary` endpoint instead.

