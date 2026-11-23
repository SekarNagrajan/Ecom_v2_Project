# Agency Locations API

## Overview

The Agency Locations endpoint provides a simple way to retrieve all agency locations (ports with agency codes) from the reportdb database.

## Endpoint

**GET** `/api/v1/register/agency-locations`

## Features

- **No Authentication Required**: Public endpoint, no JWT token needed
- **Database Fallback Logic**: Automatically tries primary query first, falls back to secondary if empty
- **Complete Data**: Returns all available agency locations in one response (no pagination)

## Request Parameters

This endpoint does not accept any parameters. It returns all agency locations.

## Response Format

```json
{
  "data": [
    {
      "portCode": "INNSA",
      "portName": "Nhava Sheva",
      "agencyCode": "INDMUM"
    },
    {
      "portCode": "AEJEA",
      "portName": "Jebel Ali",
      "agencyCode": "AEJEA1"
    }
  ]
}
```

## Usage Examples

### Get All Agency Locations
```bash
GET /api/v1/register/agency-locations
```

## Database Logic

1. **Primary Query**: Joins ports, agency, and agencylocations tables
2. **Fallback Query**: If primary returns 0 rows, uses simplified join without agencylocations table
3. **Deduplication**: Removes duplicate (portCode, agencyCode) combinations
4. **Error Handling**: Returns 500 with standardized error format if database is unavailable

## Response Codes

- **200 OK**: Success, returns agency locations
- **500 Internal Server Error**: Database error or system failure

## Notes

- All data is read from the reportdb database (read-only)
- Results are deduplicated automatically
- No pagination - all available agency locations are returned in one response
