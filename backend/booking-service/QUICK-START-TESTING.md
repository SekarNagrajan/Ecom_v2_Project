# Quick Start Testing Guide - Booking View API

## Prerequisites

1. **Database**: Ensure your ECOM database is running with booking data
2. **Application**: Start the booking-service application
3. **Authentication**: Obtain a valid JWT token

## Testing the View Endpoint

### Step 1: Get a JWT Token

Authenticate with your auth service to get a JWT token:
```bash
# Example - adjust based on your auth endpoint
curl -X POST "http://localhost:8080/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"your_username","password":"your_password"}'
```

Save the token from the response.

### Step 2: Test the View Endpoint

#### Test 1: Search by Online Booking Number

```bash
curl -X GET "http://localhost:8080/api/v1/booking/view?onlineBookNo=BKON1234" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

**Expected Result**: Complete booking details with all nested data

#### Test 2: Search by Booking Number

```bash
curl -X GET "http://localhost:8080/api/v1/booking/view?bookingNumber=INDMUM09817121" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

#### Test 3: Search by Status

```bash
curl -X GET "http://localhost:8080/api/v1/booking/view?status=D" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

**Expected Result**: First draft booking found for the customer

#### Test 4: Test Error Cases

**Missing Parameters:**
```bash
curl -X GET "http://localhost:8080/api/v1/booking/view" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```
**Expected**: 400 Bad Request - "At least one parameter is required"

**Missing Token:**
```bash
curl -X GET "http://localhost:8080/api/v1/booking/view?onlineBookNo=BKON1234" \
  -H "Content-Type: application/json"
```
**Expected**: 401 Unauthorized - "Missing or invalid JWT token"

**Non-existent Booking:**
```bash
curl -X GET "http://localhost:8080/api/v1/booking/view?onlineBookNo=INVALID123" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```
**Expected**: 404 Not Found - "Booking not found"

### Step 3: Test with Postman

1. **Import the Collection**:
   - Create a new request in Postman
   - Method: GET
   - URL: `http://localhost:8080/api/v1/booking/view`
   - Add query parameters as needed

2. **Set Headers**:
   - Key: `Authorization`
   - Value: `Bearer YOUR_JWT_TOKEN`
   - Key: `Content-Type`
   - Value: `application/json`

3. **Add Query Parameters**:
   - `onlineBookNo`: BKON1234 (or your test booking number)

4. **Send Request** and verify the response

### Step 4: Verify Response Structure

The response should include:

```json
{
  "onlineBookNo": "...",
  "agencyId": "...",
  // ... other master fields
  "customers": [
    {
      "customerType": "BP",
      "customerCode": "...",
      // ... customer fields
    }
  ],
  "containers": [
    {
      "cargoNo": 1,
      "equipmentType": "D",
      // ... container fields
      "reefer": {  // Only if equipmentType='R'
        "setTemp": 8,
        // ... reefer fields
      }
    }
  ],
  "commodities": [
    {
      "cargoNo": 1,
      // ... commodity fields
      "dangerousGoods": [  // Only if applicable
        {
          "unNo": "3480",
          // ... DG fields
        }
      ]
    }
  ],
  "routes": [
    {
      "legNo": 1,
      // ... route fields
    }
  ],
  "files": []
}
```

## Testing Checklist

- [ ] Endpoint is accessible
- [ ] JWT authentication works correctly
- [ ] Search by `onlineBookNo` returns correct booking
- [ ] Search by `bookingNumber` returns correct booking
- [ ] Search by `status` returns correct booking
- [ ] Customer filtering works (only returns bookings for authenticated customer)
- [ ] All master booking fields are populated correctly
- [ ] Customers array is populated with correct data
- [ ] Containers array includes all containers
- [ ] Reefer settings appear when equipmentType='R'
- [ ] Commodities array includes all commodities
- [ ] Dangerous goods appear when applicable
- [ ] Routes array includes all route legs
- [ ] Date/time fields are in correct format
- [ ] 400 error when no parameters provided
- [ ] 401 error when token is missing/invalid
- [ ] 404 error when booking not found
- [ ] 403 error when trying to access another customer's booking

## Sample Test Data

You can use the following data for testing (adjust to your actual database):

### Create Test Booking (using POST /api/v1/booking/create)

```json
{
  "originPortId": "INNSA",
  "finalPortId": "AEJEA",
  "loadPortId": "INNSA",
  "dischargePortId": "AEJEA",
  "cargoReadyDate": "2025-09-10",
  "preCarriage": "C",
  "onCarriage": "N",
  "status": "D",
  "routeId": 18272,
  "customers": [
    {
      "customerType": "BP",
      "customerCode": "20001374",
      "countryId": "CN",
      "customerName": "Test Customer",
      "address": "Test Address",
      "city": "Test City",
      "zipCode": "123456",
      "telephone": "1234567890",
      "email": "test@example.com"
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
      "containerNo": "TEST123"
    }
  ],
  "commodities": [
    {
      "cargoNo": 1,
      "containerNo": "TEST123",
      "itemNo": 1,
      "commodityGroup": 90,
      "hsCode": "9004909000",
      "packageType": "PACK",
      "packageQty": 50,
      "cargoVolume": 12000,
      "volumnUnit": "M",
      "nettwt": 7500
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
      "loadType": "L",
      "dischType": "L",
      "dischargeBound": "N"
    }
  ]
}
```

Then use the returned `onlineBookNo` to test the view endpoint.

## Troubleshooting

### Issue: 503 Service Unavailable
**Cause**: Database connection issue  
**Solution**: Check database connectivity and configuration

### Issue: 401 Unauthorized
**Cause**: Invalid or expired JWT token  
**Solution**: Re-authenticate to get a new token

### Issue: 404 Not Found
**Cause**: Booking doesn't exist or belongs to different customer  
**Solution**: 
- Verify booking exists in database
- Check customer code in JWT matches booking's customer

### Issue: Empty arrays in response
**Cause**: No related data exists for the booking  
**Solution**: This is expected if the booking has no customers/containers/etc.

### Issue: Null reefer settings
**Cause**: Container equipmentType is not 'R' or no reefer data exists  
**Solution**: This is expected behavior

### Issue: Empty dangerousGoods array
**Cause**: Commodity has no dangerous goods or dgFlag is 'N'  
**Solution**: This is expected behavior

## Performance Testing

For load testing, use tools like JMeter or Apache Bench:

```bash
# Example with Apache Bench (ab)
ab -n 100 -c 10 -H "Authorization: Bearer YOUR_TOKEN" \
   "http://localhost:8080/api/v1/booking/view?onlineBookNo=BKON1234"
```

Monitor:
- Response times
- Database query performance
- Memory usage
- CPU usage

## Next Steps

After successful testing:
1. Review the implementation in `BookingViewService.java`
2. Add unit tests for the service methods
3. Add integration tests for the controller
4. Consider implementing caching if needed
5. Monitor production performance
6. Gather user feedback

## Support

For issues or questions:
1. Check the `BOOKING-VIEW-API.md` for detailed API documentation
2. Review the `IMPLEMENTATION-SUMMARY.md` for technical details
3. Check application logs for error messages
4. Verify database schema matches entity definitions

