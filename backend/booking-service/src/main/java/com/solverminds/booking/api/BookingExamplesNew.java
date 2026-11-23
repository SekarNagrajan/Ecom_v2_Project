package com.solverminds.booking.api;

/**
 * Updated example JSON payloads with nested structure for Swagger documentation.
 * Reefer settings are nested under containers.
 * Dangerous goods are nested under commodities.
 * 
 * Note: containerNo is OPTIONAL for new bookings and will be auto-generated
 * when equipmentQty > 1. For amendments, containerNo is REQUIRED.
 */
public final class BookingExamplesNew {
    
    private BookingExamplesNew() {
        // Utility class - prevent instantiation
    }

    /**
     * Minimal booking request example with required fields only and nested structure.
     * Note: containerNo is optional for new bookings.
     */
    public static final String MINIMAL_JSON = "{\n" +
            "  \"originPortId\": \"INNSA\",\n" +
            "  \"finalPortId\": \"AEJEA\",\n" +
            "  \"loadPortId\": \"INNSA\",\n" +
            "  \"dischargePortId\": \"AEJEA\",\n" +
            "  \"cargoReadyDate\": \"2025-09-09\",\n" +
            "  \"preCarriage\": \"N\",\n" +
            "  \"onCarriage\": \"N\",\n" +
            "  \"status\": \"D\",\n" +
            "  \"customerCode\": \"20121345938\",\n" +
            "  \"routeId\": 18272,\n" +
            "  \"oceanFreight\": \"P\",\n" +
            "  \"isAmendment\": \"N\",\n" +
            "  \"ensFilling\": \"N\",\n" +
            "  \"t2lFilling\": \"N\",\n" +
            "  \"ensType\": \"S\",\n" +
            "  \"customers\": [\n" +
            "    {\n" +
            "      \"customerType\": \"BP\",\n" +
            "      \"customerCode\": \"20001374\",\n" +
            "      \"countryId\": \"CN\",\n" +
            "      \"customerName\": \"ECOM TESTING\",\n" +
            "      \"address\": \"513B, Building A,Free Trade Financial Center\",\n" +
            "      \"city\": \"Beijiao, China\",\n" +
            "      \"zipCode\": \"603103\",\n" +
            "      \"telephone\": \"86-38-00138000\",\n" +
            "      \"email\": \"email@solverminds.com\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"containers\": [\n" +
            "    {\n" +
            "      \"cargoNo\": 1,\n" +
            "      \"oogFlag\": \"N\",\n" +
            "      \"equipmentQty\": 1,\n" +
            "      \"equipmentType\": \"D\",\n" +
            "      \"equipmentSize\": \"40DC\",\n" +
            "      \"socFlag\": \"N\",\n" +
            "      \"dgFlag\": \"N\",\n" +
            "      \"equipmentStatus\": \"F\",\n" +
            "      \"cargoWeight\": 10000,\n" +
            "      \"weightUnit\": \"K\",\n" +
            "      \"nor\": \"N\",\n" +
            "      \"containerNo\": \"20DCTBN1\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"commodities\": [\n" +
            "    {\n" +
            "      \"cargoNo\": 1,\n" +
            "      \"containerNo\": \"20DCTBN1\",\n" +
            "      \"itemNo\": 1,\n" +
            "      \"commodityGroup\": \"90\",\n" +
            "      \"hsCode\": \"9004909000\",\n" +
            "      \"packageType\": \"PACK\",\n" +
            "      \"packageQty\": 50,\n" +
            "      \"cargoVolume\": 12000,\n" +
            "      \"volumnUnit\": \"M\",\n" +
            "      \"nettwt\": 7500,\n" +
            "      \"commodityHBLNo\": \"\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"routes\": [\n" +
            "    {\n" +
            "      \"legNo\": 1,\n" +
            "      \"loadBound\": \"N\",\n" +
            "      \"serviceCode\": \"PRO\",\n" +
            "      \"vesselCode\": \"PRO\",\n" +
            "      \"voyageCode\": \"101\",\n" +
            "      \"serviceType\": \"M\",\n" +
            "      \"loadPortId\": \"INNSA\",\n" +
            "      \"loadTerminal\": \"INNSABMCT0\",\n" +
            "      \"dischargePortId\": \"AEJEA\",\n" +
            "      \"dischargeTerminal\": \"AEJEA01\",\n" +
            "      \"etdDate\": \"2025-09-19T14:30:00+08:00\",\n" +
            "      \"etaDate\": \"2025-09-26T02:00:00+04:00\",\n" +
            "      \"loadType\": \"L\",\n" +
            "      \"dischType\": \"L\",\n" +
            "      \"groupCallId\": \"GrpCall-498839\",\n" +
            "      \"dischargeCallId\": \"GrpCall-498842\",\n" +
            "      \"loadCallId\": \"GrpCall-498839\",\n" +
            "      \"dischargeBound\": \"N\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"files\": [\n" +
            "    { \"fileCategory\": \"DGC\", \"fileName\": \"BKON1442_DGC.jpg\" },\n" +
            "    { \"fileCategory\": \"VGM\", \"fileName\": \"BKON1442_VGM.jpg\" }\n" +
            "  ]\n" +
            "}";

    /**
     * Example for GET /api/v1/booking/view response.
     * Route etdDate/etaDate are in port local time with ISO 8601 offset.
     */
    public static final String VIEW_RESPONSE_200 = "{\n" +
            "  \"onlineBookNo\": \"BKON1234\",\n" +
            "  \"originPortId\": \"INNSA\",\n" +
            "  \"loadPortId\": \"INNSA\",\n" +
            "  \"dischargePortId\": \"AEJEA\",\n" +
            "  \"bookDate\": \"2025-09-10T00:00:00Z\",\n" +
            "  \"cargoReadyDate\": \"2025-09-10\",\n" +
            "  \"submittedDate\": \"2025-09-10T12:00:00Z\",\n" +
            "  \"routes\": [\n" +
            "    {\n" +
            "      \"legNo\": 1,\n" +
            "      \"loadPortId\": \"INNSA\",\n" +
            "      \"dischargePortId\": \"AEJEA\",\n" +
            "      \"etdDate\": \"2025-09-19T14:30:00+08:00\",\n" +
            "      \"etaDate\": \"2025-09-26T02:00:00+04:00\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    /**
     * Example showing equipmentQty > 1 with auto-generated container numbers.
     * containerNo is omitted - system will generate container numbers like 20DCTBN1, 20DCTBN2, 20DCTBN3.
     * Sequence resets per equipment size (e.g., 20DCTBN1, 20DCTBN2 for 20DC; 40DCTBN1, 40DCTBN2 for 40DC).
     * Each container will get a replicated commodity in EDI export.
     */
    public static final String MULTI_CONTAINER_JSON = "{\n" +
            "  \"originPortId\": \"INNSA\",\n" +
            "  \"finalPortId\": \"AEJEA\",\n" +
            "  \"loadPortId\": \"INNSA\",\n" +
            "  \"dischargePortId\": \"AEJEA\",\n" +
            "  \"cargoReadyDate\": \"2025-09-09\",\n" +
            "  \"preCarriage\": \"N\",\n" +
            "  \"onCarriage\": \"N\",\n" +
            "  \"status\": \"D\",\n" +
            "  \"customerCode\": \"20121345938\",\n" +
            "  \"routeId\": 18272,\n" +
            "  \"oceanFreight\": \"P\",\n" +
            "  \"isAmendment\": \"N\",\n" +
            "  \"ensFilling\": \"N\",\n" +
            "  \"t2lFilling\": \"N\",\n" +
            "  \"ensType\": \"S\",\n" +
            "  \"customers\": [\n" +
            "    {\n" +
            "      \"customerType\": \"BP\",\n" +
            "      \"customerCode\": \"20001374\",\n" +
            "      \"countryId\": \"CN\",\n" +
            "      \"customerName\": \"ECOM TESTING\",\n" +
            "      \"address\": \"513B, Building A,Free Trade Financial Center\",\n" +
            "      \"city\": \"Beijiao, China\",\n" +
            "      \"zipCode\": \"603103\",\n" +
            "      \"telephone\": \"86-38-00138000\",\n" +
            "      \"email\": \"email@solverminds.com\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"containers\": [\n" +
            "    {\n" +
            "      \"cargoNo\": 1,\n" +
            "      \"oogFlag\": \"N\",\n" +
            "      \"equipmentQty\": 3,\n" +
            "      \"equipmentType\": \"D\",\n" +
            "      \"equipmentSize\": \"20DC\",\n" +
            "      \"socFlag\": \"N\",\n" +
            "      \"dgFlag\": \"N\",\n" +
            "      \"equipmentStatus\": \"F\",\n" +
            "      \"cargoWeight\": 10000,\n" +
            "      \"weightUnit\": \"K\",\n" +
            "      \"nor\": \"N\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"commodities\": [\n" +
            "    {\n" +
            "      \"cargoNo\": 1,\n" +
            "      \"itemNo\": 1,\n" +
            "      \"commodityGroup\": \"30\",\n" +
            "      \"hsCode\": \"3005905000\",\n" +
            "      \"packageType\": \"BAGS\",\n" +
            "      \"packageQty\": 10,\n" +
            "      \"cargoVolume\": 222,\n" +
            "      \"volumnUnit\": \"M\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"routes\": [\n" +
            "    {\n" +
            "      \"legNo\": 1,\n" +
            "      \"loadBound\": \"N\",\n" +
            "      \"serviceCode\": \"PRO\",\n" +
            "      \"vesselCode\": \"PRO\",\n" +
            "      \"voyageCode\": \"101\",\n" +
            "      \"serviceType\": \"M\",\n" +
            "      \"loadPortId\": \"INNSA\",\n" +
            "      \"loadTerminal\": \"INNSABMCT0\",\n" +
            "      \"dischargePortId\": \"AEJEA\",\n" +
            "      \"dischargeTerminal\": \"AEJEA01\",\n" +
            "      \"etdDate\": \"2025-09-19T14:30:00+08:00\",\n" +
            "      \"etaDate\": \"2025-09-26T02:00:00+04:00\",\n" +
            "      \"loadType\": \"L\",\n" +
            "      \"dischType\": \"L\",\n" +
            "      \"groupCallId\": \"GrpCall-498839\",\n" +
            "      \"dischargeCallId\": \"GrpCall-498842\",\n" +
            "      \"loadCallId\": \"GrpCall-498839\",\n" +
            "      \"dischargeBound\": \"N\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    /**
     * Full booking request example with nested reefer and dangerous goods.
     */
    public static final String FULL_JSON = "{\n" +
            "  \"onlineBookNo\": \"BKON1234\",\n" +
            "  \"agencyId\": \"INDMUM\",\n" +
            "  \"originPortId\": \"INNSA\",\n" +
            "  \"finalPortId\": \"AEJEA\",\n" +
            "  \"loadPortId\": \"INNSA\",\n" +
            "  \"dischargePortId\": \"AEJEA\",\n" +
            "  \"bookDate\": \"2025-09-09T00:00:00Z\",\n" +
            "  \"cargoReadyDate\": \"2025-09-09\",\n" +
            "  \"preCarriage\": \"N\",\n" +
            "  \"onCarriage\": \"N\",\n" +
            "  \"remarks\": \"\",\n" +
            "  \"status\": \"D\",\n" +
            "  \"bookingNumber\": \"INDMUM09817121\",\n" +
            "  \"customerCode\": \"20121345938\",\n" +
            "  \"templateName\": \"MUM_AEJ_20DC\",\n" +
            "  \"rateRefNo\": \"INDMUM78271\",\n" +
            "  \"submittedDate\": \"2025-09-09T00:00:00Z\",\n" +
            "  \"routeId\": 18272,\n" +
            "  \"agencyRefNo\": \"AEG1232\",\n" +
            "  \"oceanFreight\": \"P\",\n" +
            "  \"isAmendment\": \"N\",\n" +
            "  \"exportRefNo\": \"E98211\",\n" +
            "  \"customerPO\": \"PO829281\",\n" +
            "  \"ensFilling\": \"N\",\n" +
            "  \"t2lFilling\": \"N\",\n" +
            "  \"ensType\": \"S\",\n" +
            "  \"customers\": [\n" +
            "    {\n" +
            "      \"customerType\": \"BP\",\n" +
            "      \"customerCode\": \"20001374\",\n" +
            "      \"countryId\": \"CN\",\n" +
            "      \"customerName\": \"ECOM TESTING\",\n" +
            "      \"address\": \"513B, Building A,Free Trade Financial Center\",\n" +
            "      \"address1\": \"Xiamen Area of China\",\n" +
            "      \"city\": \"Beijiao, China\",\n" +
            "      \"zipCode\": \"603103\",\n" +
            "      \"telephone\": \"86-38-00138000\",\n" +
            "      \"email\": \"email@solverminds.com\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"customerType\": \"AP\",\n" +
            "      \"customerCode\": \"20001374\",\n" +
            "      \"countryId\": \"CN\",\n" +
            "      \"customerName\": \"ECOM TESTING\",\n" +
            "      \"address\": \"513B, Building A,Free Trade Financial Center\",\n" +
            "      \"address1\": \"Xiamen Area of China\",\n" +
            "      \"city\": \"Beijiao, China\",\n" +
            "      \"zipCode\": \"603103\",\n" +
            "      \"telephone\": \"86-38-00138000\",\n" +
            "      \"email\": \"email@solverminds.com\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"containers\": [\n" +
            "    {\n" +
            "      \"cargoNo\": 1,\n" +
            "      \"oogFlag\": \"N\",\n" +
            "      \"equipmentQty\": 1,\n" +
            "      \"equipmentType\": \"R\",\n" +
            "      \"equipmentSize\": \"40RF\",\n" +
            "      \"socFlag\": \"N\",\n" +
            "      \"dgFlag\": \"N\",\n" +
            "      \"equipmentStatus\": \"F\",\n" +
            "      \"cargoWeight\": 10000,\n" +
            "      \"weightUnit\": \"K\",\n" +
            "      \"nor\": \"Y\",\n" +
            "      \"containerNo\": \"20RFTBN1\",\n" +
            "      \"reefer\": {\n" +
            "        \"setTemp\": 8,\n" +
            "        \"tempUnit\": \"C\",\n" +
            "        \"ventilation\": \"20\",\n" +
            "        \"humidity\": 65,\n" +
            "        \"humidityFlag\": \"Y\",\n" +
            "        \"drains\": \"O\",\n" +
            "        \"minTemp\": 5,\n" +
            "        \"maxTemp\": 10,\n" +
            "        \"o2Value\": 21,\n" +
            "        \"co2Value\": 5\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"cargoNo\": 2,\n" +
            "      \"oogFlag\": \"N\",\n" +
            "      \"equipmentQty\": 1,\n" +
            "      \"equipmentType\": \"D\",\n" +
            "      \"equipmentSize\": \"40DC\",\n" +
            "      \"socFlag\": \"N\",\n" +
            "      \"dgFlag\": \"Y\",\n" +
            "      \"equipmentStatus\": \"F\",\n" +
            "      \"cargoWeight\": 10000,\n" +
            "      \"weightUnit\": \"K\",\n" +
            "      \"nor\": \"N\",\n" +
            "      \"containerNo\": \"20DCTBN2\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"commodities\": [\n" +
            "    {\n" +
            "      \"cargoNo\": 1,\n" +
            "      \"containerNo\": \"20RFTBN1\",\n" +
            "      \"itemNo\": 1,\n" +
            "      \"commodityGroup\": \"90\",\n" +
            "      \"hsCode\": \"9004909000\",\n" +
            "      \"packageType\": \"PACK\",\n" +
            "      \"packageQty\": 50,\n" +
            "      \"cargoVolume\": 12000,\n" +
            "      \"volumnUnit\": \"M\",\n" +
            "      \"nettwt\": 7500,\n" +
            "      \"commodityHBLNo\": \"\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"cargoNo\": 2,\n" +
            "      \"containerNo\": \"20DCTBN2\",\n" +
            "      \"itemNo\": 1,\n" +
            "      \"commodityGroup\": \"90\",\n" +
            "      \"hsCode\": \"9004909000\",\n" +
            "      \"packageType\": \"PACK\",\n" +
            "      \"packageQty\": 50,\n" +
            "      \"cargoVolume\": 12000,\n" +
            "      \"volumnUnit\": \"M\",\n" +
            "      \"nettwt\": 7500,\n" +
            "      \"commodityHBLNo\": \"\",\n" +
            "      \"dangerousGoods\": {\n" +
            "        \"unNo\": \"3480\",\n" +
            "        \"classCode\": \"9\",\n" +
            "        \"flashPoint\": 32,\n" +
            "        \"flashPointUnit\": \"C\",\n" +
            "        \"isMarinePollutant\": false\n" +
            "      }\n" +
            "    }\n" +
            "  ],\n" +
            "  \"routes\": [\n" +
            "    {\n" +
            "      \"legNo\": 1,\n" +
            "      \"loadBound\": \"N\",\n" +
            "      \"serviceCode\": \"PRO\",\n" +
            "      \"vesselCode\": \"PRO\",\n" +
            "      \"voyageCode\": \"101\",\n" +
            "      \"serviceType\": \"M\",\n" +
            "      \"loadPortId\": \"INNSA\",\n" +
            "      \"loadTerminal\": \"INNSABMCT0\",\n" +
            "      \"dischargePortId\": \"AEJEA\",\n" +
            "      \"dischargeTerminal\": \"AEJEA01\",\n" +
            "      \"etdDate\": \"2025-09-19T14:30:00+08:00\",\n" +
            "      \"etaDate\": \"2025-09-26T02:00:00+04:00\",\n" +
            "      \"loadType\": \"L\",\n" +
            "      \"dischType\": \"L\",\n" +
            "      \"groupCallId\": \"GrpCall-498839\",\n" +
            "      \"dischargeCallId\": \"GrpCall-498842\",\n" +
            "      \"loadCallId\": \"GrpCall-498839\",\n" +
            "      \"dischargeBound\": \"N\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"legNo\": 2,\n" +
            "      \"loadBound\": \"N\",\n" +
            "      \"serviceCode\": \"PRO\",\n" +
            "      \"vesselCode\": \"PRO\",\n" +
            "      \"voyageCode\": \"101\",\n" +
            "      \"serviceType\": \"M\",\n" +
            "      \"loadPortId\": \"INNSA\",\n" +
            "      \"loadTerminal\": \"INNSABMCT0\",\n" +
            "      \"dischargePortId\": \"AEJEA\",\n" +
            "      \"dischargeTerminal\": \"AEJEA01\",\n" +
            "      \"etdDate\": \"2025-09-19T14:30:00+08:00\",\n" +
            "      \"etaDate\": \"2025-09-26T02:00:00+04:00\",\n" +
            "      \"loadType\": \"L\",\n" +
            "      \"dischType\": \"L\",\n" +
            "      \"groupCallId\": \"GrpCall-498839\",\n" +
            "      \"dischargeCallId\": \"GrpCall-498842\",\n" +
            "      \"loadCallId\": \"GrpCall-498839\",\n" +
            "      \"dischargeBound\": \"N\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"files\": [\n" +
            "    { \"fileCategory\": \"DGC\", \"fileName\": \"BKON1442_DGC.jpg\" },\n" +
            "    { \"fileCategory\": \"VGM\", \"fileName\": \"BKON1442_VGM.jpg\" }\n" +
            "  ]\n" +
            "}";

    // ==================== V2 API Examples ====================

    /**
     * V2 API: Example for PUT /api/v2/bookings/{bookingId} (update booking).
     * DC (Dry) containers must have empty reeferSettings — omit or null; only Reefer (R) containers use reeferSettings.
     */
    public static final String V2_UPDATE_BOOKING_JSON = "{\n" +
            "  \"originId\": \"CNNGB\",\n" +
            "  \"deliveryId\": \"ESBCN\",\n" +
            "  \"polId\": \"CNNGB\",\n" +
            "  \"podId\": \"ESBCN\",\n" +
            "  \"etd\": \"2025-09-19T03:00:00+05:30\",\n" +
            "  \"eta\": \"2025-09-26T03:00:00+05:30\",\n" +
            "  \"status\": \"D\",\n" +
            "  \"cargoReadyDate\": \"2026-01-02T08:00:00Z\",\n" +
            "  \"isAmendment\": false,\n" +
            "  \"oceanFreight\": \"P\",\n" +
            "  \"customers\": [\n" +
            "    {\n" +
            "      \"type\": \"BP\",\n" +
            "      \"code\": \"2024003615\",\n" +
            "      \"name\": \"SND SQUARE INTERNATIONAL LTD.\",\n" +
            "      \"address\": {\n" +
            "        \"lines\": [\"Floor 4, Unit 12\", \"123 Innovation Drive\"],\n" +
            "        \"locality\": \"San Francisco\",\n" +
            "        \"postalCode\": \"94103\",\n" +
            "        \"countryCode\": \"US\"\n" +
            "      },\n" +
            "      \"telephone\": \"86-38-1234567\",\n" +
            "      \"email\": \"john@doe.com\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"containers\": [\n" +
            "    {\n" +
            "      \"id\": \"20DCTBN1\",\n" +
            "      \"quantity\": 5,\n" +
            "      \"type\": \"D\",\n" +
            "      \"code\": \"20DC\",\n" +
            "      \"status\": \"F\",\n" +
            "      \"isOOG\": false,\n" +
            "      \"isSOC\": false,\n" +
            "      \"isDG\": false,\n" +
            "      \"isNOR\": true,\n" +
            "      \"commodities\": [\n" +
            "        {\n" +
            "          \"commodityGroup\": \"90\",\n" +
            "          \"hsCode\": \"9001101000\",\n" +
            "          \"packageType\": \"APBW\",\n" +
            "          \"packageQuantity\": 50,\n" +
            "          \"cargoWeight\": 10000,\n" +
            "          \"cargoWeightUnit\": \"K\",\n" +
            "          \"cargoVolume\": 120,\n" +
            "          \"cargoVolumeUnit\": \"M\",\n" +
            "          \"netWeight\": 7500\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  ],\n" +
            "  \"route\": {\n" +
            "    \"id\": 12345,\n" +
            "    \"legs\": [\n" +
            "      {\n" +
            "        \"serviceCode\": \"PAX\",\n" +
            "        \"vesselCode\": \"P07\",\n" +
            "        \"voyageCode\": \"532\",\n" +
            "        \"serviceType\": \"M\",\n" +
            "        \"loadPortId\": \"CNNGB\",\n" +
            "        \"loadBound\": \"N\",\n" +
            "        \"loadTerminalId\": \"CNNGBCTT00\",\n" +
            "        \"loadType\": \"L\",\n" +
            "        \"dischargePortId\": \"ESBCN\",\n" +
            "        \"dischargeBound\": \"N\",\n" +
            "        \"dischargeTerminalId\": \"ESBCNHPB00\",\n" +
            "        \"dischargeType\": \"L\",\n" +
            "        \"etd\": \"2025-09-19T03:00:00+05:30\",\n" +
            "        \"eta\": \"2025-09-26T03:00:00+05:30\",\n" +
            "        \"groupCallId\": \"GrpCall-6736\",\n" +
            "        \"loadCallId\": \"GrpCall-6736\",\n" +
            "        \"dischargeCallId\": \"GrpCall-6739\"\n" +
            "      }\n" +
            "    ]\n" +
            "  }\n" +
            "}";

    /**
     * V2 API: Example for PUT /api/v2/bookings/{bookingId} — update booking (DG Container).
     * Includes id (confirmed booking id) and bookingRequestId (e.g. BKON...). DG uses dangerousGoodsInfo on commodities.
     */
    public static final String V2_UPDATE_BOOKING_DG_JSON = "{\n" +
            "  \"id\": \"TSHGRTM26000361\",\n" +
            "  \"bookingRequestId\": \"BKON730\",\n" +
            "  \"originId\": \"CNNGB\",\n" +
            "  \"deliveryId\": \"ESBCN\",\n" +
            "  \"polId\": \"CNNGB\",\n" +
            "  \"podId\": \"ESBCN\",\n" +
            "  \"etd\": \"2025-09-19T03:00:00+05:30\",\n" +
            "  \"eta\": \"2025-09-26T03:00:00+05:30\",\n" +
            "  \"status\": \"D\",\n" +
            "  \"agencyId\": \"CNMNGB\",\n" +
            "  \"agencyRefNo\": \"AEG1232\",\n" +
            "  \"cargoReadyDate\": \"2026-01-02T08:00:00Z\",\n" +
            "  \"isAmendment\": false,\n" +
            "  \"oceanFreight\": \"P\",\n" +
            "  \"remarks\": \"Handle with care\",\n" +
            "  \"customers\": [\n" +
            "    {\n" +
            "      \"type\": \"BP\",\n" +
            "      \"code\": \"2024003615\",\n" +
            "      \"name\": \"SND SQUARE INTERNATIONAL LTD.\",\n" +
            "      \"address\": {\n" +
            "        \"lines\": [\"Floor 4, Unit 12\", \"123 Innovation Drive\"],\n" +
            "        \"locality\": \"San Francisco\",\n" +
            "        \"postalCode\": \"94103\",\n" +
            "        \"countryCode\": \"US\"\n" +
            "      },\n" +
            "      \"telephone\": \"86-38-1234567\",\n" +
            "      \"email\": \"john@doe.com\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"containers\": [\n" +
            "    {\n" +
            "      \"id\": \"20DCTBN1\",\n" +
            "      \"quantity\": 1,\n" +
            "      \"type\": \"D\",\n" +
            "      \"code\": \"20DC\",\n" +
            "      \"status\": \"F\",\n" +
            "      \"isOOG\": false,\n" +
            "      \"isSOC\": false,\n" +
            "      \"isDG\": true,\n" +
            "      \"isNOR\": true,\n" +
            "      \"commodities\": [\n" +
            "        {\n" +
            "          \"commodityGroup\": \"90\",\n" +
            "          \"hsCode\": \"9001101000\",\n" +
            "          \"packageType\": \"APBW\",\n" +
            "          \"packageQuantity\": 50,\n" +
            "          \"cargoWeight\": 10000,\n" +
            "          \"cargoWeightUnit\": \"K\",\n" +
            "          \"cargoVolume\": 120,\n" +
            "          \"cargoVolumeUnit\": \"M\",\n" +
            "          \"netWeight\": 7500,\n" +
            "          \"dangerousGoodsInfo\": {\n" +
            "            \"unNo\": \"2779\",\n" +
            "            \"imoClass\": \"6.1\",\n" +
            "            \"technicalName\": \"DINITRO-ORTHO-CRESOL\",\n" +
            "            \"flashPoint\": 23.00,\n" +
            "            \"flashPointUnit\": \"C\",\n" +
            "            \"emergencyContact\": {\n" +
            "              \"contactName\": \"MME MARTINE BRANCA\",\n" +
            "              \"countryCode\": \"33\",\n" +
            "              \"areaCode\": \"1\",\n" +
            "              \"localNumber\": \"23456789\"\n" +
            "            },\n" +
            "            \"outerPacking\": { \"code\": \"1G\" },\n" +
            "            \"outerPackingQuantity\": 20,\n" +
            "            \"innerPacking\": { \"code\": \"BA\" },\n" +
            "            \"innerPackingQuantity\": 1500,\n" +
            "            \"grossWeight\": 25500.00,\n" +
            "            \"netWeight\": 25000.00,\n" +
            "            \"fullPackagingCode\": \"4G/Y30/S/26\",\n" +
            "            \"isLimitedQuantity\": false,\n" +
            "            \"isExceptedQuantity\": false,\n" +
            "            \"isReportableQuantity\": false,\n" +
            "            \"isWaste\": false,\n" +
            "            \"isMarinePollutant\": true,\n" +
            "            \"segregation\": \"ACIDS\"\n" +
            "          }\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  ],\n" +
            "  \"route\": {\n" +
            "    \"id\": 12345,\n" +
            "    \"legs\": [\n" +
            "      {\n" +
            "        \"serviceCode\": \"PAX\",\n" +
            "        \"vesselCode\": \"P07\",\n" +
            "        \"voyageCode\": \"532\",\n" +
            "        \"serviceType\": \"M\",\n" +
            "        \"loadPortId\": \"CNNGB\",\n" +
            "        \"loadBound\": \"N\",\n" +
            "        \"loadTerminalId\": \"CNNGBCTT00\",\n" +
            "        \"loadType\": \"L\",\n" +
            "        \"dischargePortId\": \"ESBCN\",\n" +
            "        \"dischargeBound\": \"N\",\n" +
            "        \"dischargeTerminalId\": \"ESBCNHPB00\",\n" +
            "        \"dischargeType\": \"L\",\n" +
            "        \"etd\": \"2025-09-19T03:00:00+05:30\",\n" +
            "        \"eta\": \"2025-09-26T03:00:00+05:30\",\n" +
            "        \"groupCallId\": \"GrpCall-6736\",\n" +
            "        \"loadCallId\": \"GrpCall-6736\",\n" +
            "        \"dischargeCallId\": \"GrpCall-6739\"\n" +
            "      }\n" +
            "    ]\n" +
            "  }\n" +
            "}";

    /**
     * V2 API: Minimal booking request with required fields only.
     * Uses same DTO value style as update (status "D", type "BP", serviceType "M", loadType "L", etc.).
     */
    public static final String V2_MINIMAL_JSON = "{\n" +
            "  \"originId\": \"CNNGB\",\n" +
            "  \"deliveryId\": \"ESBCN\",\n" +
            "  \"polId\": \"CNNGB\",\n" +
            "  \"podId\": \"ESBCN\",\n" +
            "  \"etd\": \"2025-09-19T03:00:00+05:30\",\n" +
            "  \"eta\": \"2025-09-26T03:00:00+05:30\",\n" +
            "  \"status\": \"D\",\n" +
            "  \"cargoReadyDate\": \"2026-01-02T08:00:00Z\",\n" +
            "  \"isAmendment\": false,\n" +
            "  \"oceanFreight\": \"P\",\n" +
            "  \"customers\": [\n" +
            "    {\n" +
            "      \"type\": \"BP\",\n" +
            "      \"code\": \"2024003615\",\n" +
            "      \"name\": \"SND SQUARE INTERNATIONAL LTD.\",\n" +
            "      \"address\": {\n" +
            "        \"lines\": [\"Floor 4, Unit 12\", \"123 Innovation Drive\"],\n" +
            "        \"locality\": \"San Francisco\",\n" +
            "        \"postalCode\": \"94103\",\n" +
            "        \"countryCode\": \"US\"\n" +
            "      },\n" +
            "      \"telephone\": \"86-38-1234567\",\n" +
            "      \"email\": \"john@doe.com\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"containers\": [\n" +
            "    {\n" +
            "      \"id\": \"20DCTBN1\",\n" +
            "      \"quantity\": 1,\n" +
            "      \"type\": \"D\",\n" +
            "      \"code\": \"20DC\",\n" +
            "      \"status\": \"F\",\n" +
            "      \"isOOG\": false,\n" +
            "      \"isSOC\": false,\n" +
            "      \"isDG\": false,\n" +
            "      \"isNOR\": true,\n" +
            "      \"commodities\": [\n" +
            "        {\n" +
            "          \"commodityGroup\": \"90\",\n" +
            "          \"hsCode\": \"9001101000\",\n" +
            "          \"packageType\": \"APBW\",\n" +
            "          \"packageQuantity\": 50,\n" +
            "          \"cargoWeight\": 10000,\n" +
            "          \"cargoWeightUnit\": \"K\",\n" +
            "          \"cargoVolume\": 120,\n" +
            "          \"cargoVolumeUnit\": \"M\",\n" +
            "          \"netWeight\": 7500\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  ],\n" +
            "  \"route\": {\n" +
            "    \"id\": 12345,\n" +
            "    \"legs\": [\n" +
            "      {\n" +
            "        \"serviceCode\": \"PAX\",\n" +
            "        \"vesselCode\": \"P07\",\n" +
            "        \"voyageCode\": \"532\",\n" +
            "        \"serviceType\": \"M\",\n" +
            "        \"loadPortId\": \"CNNGB\",\n" +
            "        \"loadBound\": \"N\",\n" +
            "        \"loadTerminalId\": \"CNNGBCTT00\",\n" +
            "        \"loadType\": \"L\",\n" +
            "        \"dischargePortId\": \"ESBCN\",\n" +
            "        \"dischargeBound\": \"N\",\n" +
            "        \"dischargeTerminalId\": \"ESBCNHPB00\",\n" +
            "        \"dischargeType\": \"L\",\n" +
            "        \"etd\": \"2025-09-19T03:00:00+05:30\",\n" +
            "        \"eta\": \"2025-09-26T03:00:00+05:30\",\n" +
            "        \"groupCallId\": \"GrpCall-6736\",\n" +
            "        \"loadCallId\": \"GrpCall-6736\",\n" +
            "        \"dischargeCallId\": \"GrpCall-6739\"\n" +
            "      }\n" +
            "    ]\n" +
            "  }\n" +
            "}";

    /**
     * V2 API: Multiple containers with quantity > 1.
     * Container id is omitted - system auto-generates container numbers (e.g., 20DCTBN1, 20DCTBN2, 20DCTBN3).
     * Sequence resets per container code/size (e.g., 20DC gets 20DCTBN1-3, 40DC gets 40DCTBN1-2).
     * Each expanded container gets a replicated commodity in EDI export.
     * Uses same DTO value style as update (status "D", type "BP", serviceType "M", loadType "L", etc.).
     */
    public static final String V2_MULTI_CONTAINER_JSON = "{\n" +
            "  \"originId\": \"CNNGB\",\n" +
            "  \"deliveryId\": \"ESBCN\",\n" +
            "  \"polId\": \"CNNGB\",\n" +
            "  \"podId\": \"ESBCN\",\n" +
            "  \"etd\": \"2026-03-19T17:03:00+01:00\",\n" +
            "  \"eta\": \"2026-03-26T17:03:00+01:00\",\n" +
            "  \"status\": \"D\",\n" +
            "  \"cargoReadyDate\": \"2026-01-02T08:00:00Z\",\n" +
            "  \"isAmendment\": false,\n" +
            "  \"oceanFreight\": \"P\",\n" +
            "  \"customers\": [\n" +
            "    {\n" +
            "      \"type\": \"BP\",\n" +
            "      \"code\": \"2024003615\",\n" +
            "      \"name\": \"SND SQUARE INTERNATIONAL LTD.\",\n" +
            "      \"address\": {\n" +
            "        \"lines\": [\"Floor 4, Unit 12\", \"123 Innovation Drive\"],\n" +
            "        \"locality\": \"San Francisco\",\n" +
            "        \"postalCode\": \"94103\",\n" +
            "        \"countryCode\": \"US\"\n" +
            "      },\n" +
            "      \"telephone\": \"86-38-1234567\",\n" +
            "      \"email\": \"john@doe.com\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"containers\": [\n" +
            "    {\n" +
            "      \"quantity\": 3,\n" +
            "      \"type\": \"D\",\n" +
            "      \"code\": \"20DC\",\n" +
            "      \"status\": \"F\",\n" +
            "      \"isOOG\": false,\n" +
            "      \"isSOC\": false,\n" +
            "      \"isDG\": false,\n" +
            "      \"isNOR\": true,\n" +
            "      \"commodities\": [\n" +
            "        {\n" +
            "          \"commodityGroup\": \"30\",\n" +
            "          \"hsCode\": \"3005905000\",\n" +
            "          \"packageType\": \"BAGS\",\n" +
            "          \"packageQuantity\": 10,\n" +
            "          \"cargoWeight\": 5000,\n" +
            "          \"cargoWeightUnit\": \"K\",\n" +
            "          \"cargoVolume\": 222,\n" +
            "          \"cargoVolumeUnit\": \"M\"\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"quantity\": 2,\n" +
            "      \"type\": \"D\",\n" +
            "      \"code\": \"40DC\",\n" +
            "      \"status\": \"F\",\n" +
            "      \"isOOG\": false,\n" +
            "      \"isSOC\": false,\n" +
            "      \"isDG\": false,\n" +
            "      \"isNOR\": true,\n" +
            "      \"commodities\": [\n" +
            "        {\n" +
            "          \"commodityGroup\": \"30\",\n" +
            "          \"hsCode\": \"3005905000\",\n" +
            "          \"packageType\": \"BAGS\",\n" +
            "          \"packageQuantity\": 20,\n" +
            "          \"cargoWeight\": 10000,\n" +
            "          \"cargoWeightUnit\": \"K\",\n" +
            "          \"cargoVolume\": 500,\n" +
            "          \"cargoVolumeUnit\": \"M\"\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  ],\n" +
            "  \"route\": {\n" +
            "    \"id\": 12345,\n" +
            "    \"legs\": [\n" +
            "      {\n" +
            "        \"serviceCode\": \"PAX\",\n" +
            "        \"vesselCode\": \"P07\",\n" +
            "        \"voyageCode\": \"532\",\n" +
            "        \"serviceType\": \"M\",\n" +
            "        \"loadPortId\": \"CNNGB\",\n" +
            "        \"loadBound\": \"N\",\n" +
            "        \"loadTerminalId\": \"CNNGBCTT00\",\n" +
            "        \"loadType\": \"L\",\n" +
            "        \"dischargePortId\": \"ESBCN\",\n" +
            "        \"dischargeBound\": \"N\",\n" +
            "        \"dischargeTerminalId\": \"ESBCNHPB00\",\n" +
            "        \"dischargeType\": \"L\",\n" +
            "        \"etd\": \"2025-09-19T03:00:00+05:30\",\n" +
            "        \"eta\": \"2025-09-26T02:00:00+04:00\",\n" +
            "        \"groupCallId\": \"GrpCall-6736\",\n" +
            "        \"loadCallId\": \"GrpCall-6736\",\n" +
            "        \"dischargeCallId\": \"GrpCall-6739\"\n" +
            "      }\n" +
            "    ]\n" +
            "  }\n" +
            "}";

    /**
     * V2 API: Full booking request with reefer settings and Dangerous Goods.
     * Uses same DTO value style as update (status "D", type "BP", serviceType "M", loadType "L", t2lFiling "N", etc.).
     */
    public static final String V2_FULL_DG_JSON = "{\n" +
            "  \"originId\": \"CNNGB\",\n" +
            "  \"deliveryId\": \"ESBCN\",\n" +
            "  \"polId\": \"CNNGB\",\n" +
            "  \"podId\": \"ESBCN\",\n" +
            "  \"etd\": \"2025-09-19T03:00:00+05:30\",\n" +
            "  \"eta\": \"2025-09-26T03:00:00+05:30\",\n" +
            "  \"status\": \"D\",\n" +
            "  \"agencyId\": \"CNMNGB\",\n" +
            "  \"agencyRefNo\": \"AEG1232\",\n" +
            "  \"preCarriage\": \"C\",\n" +
            "  \"onCarriage\": \"C\",\n" +
            "  \"remarks\": \"Handle with care\",\n" +
            "  \"oceanFreight\": \"P\",\n" +
            "  \"templateName\": \"MUM_AEJ_20DC\",\n" +
            "  \"cargoReadyDate\": \"2026-01-02T08:00:00Z\",\n" +
            "  \"isAmendment\": false,\n" +
            "  \"contractReference\": \"CNMNGB78271\",\n" +
            "  \"exportRefNo\": \"E98211\",\n" +
            "  \"customerReference\": \"PO829281\",\n" +
            "  \"ensFilingType\": \"S\",\n" +
            "  \"ensType\": \"S\",\n" +
            "  \"t2lFiling\": \"N\",\n" +
            "  \"customers\": [\n" +
            "    {\n" +
            "      \"type\": \"BP\",\n" +
            "      \"code\": \"2024003615\",\n" +
            "      \"name\": \"SND SQUARE INTERNATIONAL LTD.\",\n" +
            "      \"address\": {\n" +
            "        \"lines\": [\"Floor 4, Unit 12\", \"123 Innovation Drive\"],\n" +
            "        \"locality\": \"San Francisco\",\n" +
            "        \"postalCode\": \"94103\",\n" +
            "        \"countryCode\": \"US\"\n" +
            "      },\n" +
            "      \"telephone\": \"+880-1234567\",\n" +
            "      \"email\": \"john@doe.com\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"type\": \"AP\",\n" +
            "      \"code\": \"2024003615\",\n" +
            "      \"name\": \"SND SQUARE INTERNATIONAL LTD.\",\n" +
            "      \"address\": {\n" +
            "        \"lines\": [\"Floor 4, Unit 12\", \"123 Innovation Drive\"],\n" +
            "        \"locality\": \"San Francisco\",\n" +
            "        \"postalCode\": \"94103\",\n" +
            "        \"countryCode\": \"US\"\n" +
            "      },\n" +
            "      \"telephone\": \"+880-1234567\",\n" +
            "      \"email\": \"john@doe.com\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"containers\": [\n" +
            "    {\n" +
            "      \"id\": \"20DCTBN1\",\n" +
            "      \"quantity\": 5,\n" +
            "      \"type\": \"R\",\n" +
            "      \"code\": \"20RF\",\n" +
            "      \"status\": \"F\",\n" +
            "      \"reeferSettings\": {\n" +
            "      },\n" +
            "      \"isOOG\": false,\n" +
            "      \"isSOC\": false,\n" +
            "      \"isDG\": true,\n" +
            "      \"isNOR\": true,\n" +
            "      \"tareWeight\": 2500,\n" +
            "      \"grossWeight\": 12300,\n" +
            "      \"commodities\": [\n" +
            "        {\n" +
            "          \"commodityGroup\": \"90\",\n" +
            "          \"hsCode\": \"9001101000\",\n" +
            "          \"packageType\": \"APBW\",\n" +
            "          \"packageQuantity\": 50,\n" +
            "          \"cargoWeight\": 10000,\n" +
            "          \"cargoWeightUnit\": \"K\",\n" +
            "          \"cargoVolume\": 120,\n" +
            "          \"cargoVolumeUnit\": \"M\",\n" +
            "          \"netWeight\": 7500,\n" +
            "          \"dangerousGoodsInfo\": {\n" +
            "            \"unNo\": \"0272\",\n" +
            "            \"imoClass\": \"1.3\",\n" +
            "            \"flashPoint\": 230,\n" +
            "            \"flashPointUnit\": \"C\",\n" +
            "            \"netExplosiveWeight\": 100.00,\n" +
            "            \"isMarinePollutant\": true\n" +
            "          }\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  ],\n" +
            "  \"route\": {\n" +
            "    \"id\": 12345,\n" +
            "    \"legs\": [\n" +
            "      {\n" +
            "        \"serviceCode\": \"PAX\",\n" +
            "        \"vesselCode\": \"P07\",\n" +
            "        \"voyageCode\": \"532\",\n" +
            "        \"serviceType\": \"M\",\n" +
            "        \"loadPortId\": \"CNNGB\",\n" +
            "        \"loadBound\": \"N\",\n" +
            "        \"loadTerminalId\": \"CNNGBCTT00\",\n" +
            "        \"loadType\": \"L\",\n" +
            "        \"dischargePortId\": \"ESBCN\",\n" +
            "        \"dischargeBound\": \"N\",\n" +
            "        \"dischargeTerminalId\": \"ESBCNHPB00\",\n" +
            "        \"dischargeType\": \"L\",\n" +
            "        \"etd\": \"2025-09-19T03:00:00+05:30\",\n" +
            "        \"eta\": \"2025-09-26T02:00:00+04:00\",\n" +
            "        \"groupCallId\": \"GrpCall-6736\",\n" +
            "        \"loadCallId\": \"GrpCall-6736\",\n" +
            "        \"dischargeCallId\": \"GrpCall-6739\"\n" +
            "      }\n" +
            "    ]\n" +
            "  }\n" +
            "}";

    /**
     * V2 API: Example response for GET /api/v2/bookings/{bookingId} (200).
     * Full booking view with customers, containers (reefer + DG), files, and audit fields.
     */
    public static final String V2_GET_BOOKING_RESPONSE_200 = "{\n" +
            "  \"id\": \"TSHGRTM26000361\",\n" +
            "  \"bookingRequestId\": \"BKON730\",\n" +
            "  \"originId\": \"DEDUI\",\n" +
            "  \"originName\": \"DUISBURG\",\n" +
            "  \"deliveryId\": \"ESBCN\",\n" +
            "  \"deliveryName\": \"BARCELONA, SPAIN\",\n" +
            "  \"polId\": \"NLMOE\",\n" +
            "  \"polName\": \"MOERDIJK, NETHERLANDS\",\n" +
            "  \"podId\": \"ESBCN\",\n" +
            "  \"podName\": \"BARCELONA, SPAIN\",\n" +
            "  \"etd\": \"2026-03-19T10:05:00+01:00\",\n" +
            "  \"eta\": \"2026-03-26T17:03:00+01:00\",\n" +
            "  \"status\": \"S\",\n" +
            "  \"agencyId\": \"NLERTM\",\n" +
            "  \"agencyRefNo\": \"AR1234\",\n" +
            "  \"teus\": 3,\n" +
            "  \"preCarriage\": \"N\",\n" +
            "  \"onCarriage\": \"N\",\n" +
            "  \"remarks\": \"Handle with care\",\n" +
            "  \"customerCode\": \"2024003615\",\n" +
            "  \"oceanFreight\": \"P\",\n" +
            "  \"templateName\": \"BKL_MOERDIJK_20RF\",\n" +
            "  \"cargoReadyDate\": \"2026-03-10T00:00:00Z\",\n" +
            "  \"isAmendment\": true,\n" +
            "  \"contractReference\": \"NLERTM78271\",\n" +
            "  \"exportRefNo\": \"E98211\",\n" +
            "  \"customerReference\": \"PO829281\",\n" +
            "  \"ensFilingType\": \"N\",\n" +
            "  \"ensType\": \"S\",\n" +
            "  \"t2lFiling\": \"Y\",\n" +
            "  \"customers\": [\n" +
            "    {\n" +
            "      \"type\": \"BP\",\n" +
            "      \"code\": \"2024003615\",\n" +
            "      \"name\": \"SND SQUARE INTERNATIONAL LTD.\",\n" +
            "      \"address\": {\n" +
            "        \"lines\": [\"ABDER,TELIHATI,SREEPUR,GAZIPUR, BANGLADESH.\"],\n" +
            "        \"locality\": \"San Francisco\",\n" +
            "        \"postalCode\": \"94103\",\n" +
            "        \"countryCode\": \"US\"\n" +
            "      },\n" +
            "      \"telephone\": \"+880-0197623832\",\n" +
            "      \"email\": \"Nathaly.jimenez@tailwind-shipping.com\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"type\": \"AP\",\n" +
            "      \"code\": \"2024003615\",\n" +
            "      \"name\": \"SND SQUARE INTERNATIONAL LTD.\",\n" +
            "      \"address\": {\n" +
            "        \"lines\": [\"ABDER,TELIHATI,SREEPUR,GAZIPUR, BANGLADESH.\"],\n" +
            "        \"locality\": \"San Francisco\",\n" +
            "        \"postalCode\": \"94103\",\n" +
            "        \"countryCode\": \"US\"\n" +
            "      },\n" +
            "      \"telephone\": \"+880-0197623832\",\n" +
            "      \"email\": \"Nathaly.jimenez@tailwind-shipping.com\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"containers\": [\n" +
            "    {\n" +
            "      \"id\": \"20RFTNB1\",\n" +
            "      \"quantity\": 1,\n" +
            "      \"type\": \"R\",\n" +
            "      \"code\": \"20RF\",\n" +
            "      \"status\": \"F\",\n" +
            "      \"reeferSettings\": {\n" +
            "        \"temperature\": 8.0,\n" +
            "        \"temperatureUnit\": \"C\",\n" +
            "        \"ventilation\": \"20\",\n" +
            "        \"humidity\": 65,\n" +
            "        \"humidityFlag\": \"Y\",\n" +
            "        \"drains\": \"O\",\n" +
            "        \"minTemp\": 5,\n" +
            "        \"maxTemp\": 10,\n" +
            "        \"o2Value\": 21,\n" +
            "        \"co2Value\": 5\n" +
            "      },\n" +
            "      \"isOOG\": false,\n" +
            "      \"isSOC\": true,\n" +
            "      \"isDG\": true,\n" +
            "      \"isNOR\": false,\n" +
            "      \"tareWeight\": 3060.0,\n" +
            "      \"grossWeight\": 9060.0,\n" +
            "      \"commodities\": [\n" +
            "        {\n" +
            "          \"commodityGroup\": \"09\",\n" +
            "          \"hsCode\": \"0902100000\",\n" +
            "          \"packageType\": \"CMPK\",\n" +
            "          \"packageQuantity\": 4,\n" +
            "          \"cargoWeight\": 6000.0,\n" +
            "          \"cargoWeightUnit\": \"K\",\n" +
            "          \"cargoVolume\": 4.0,\n" +
            "          \"cargoVolumeUnit\": \"M\",\n" +
            "          \"netWeight\": 7500,\n" +
            "          \"dangerousGoodsInfo\": {\n" +
            "            \"unNo\": \"1100\",\n" +
            "            \"imoClass\": \"3\",\n" +
            "            \"flashPoint\": 11.0,\n" +
            "            \"flashPointUnit\": \"C\",\n" +
            "            \"isMarinePollutant\": true\n" +
            "          }\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  ],\n" +
            "  \"files\": [\n" +
            "    {\n" +
            "      \"category\": \"DGC\",\n" +
            "      \"name\": \"BKON123_DGC.pdf\",\n" +
            "      \"size\": 123456\n" +
            "    }\n" +
            "  ],\n" +
            "  \"createdBy\": \"arul.prakash@solverminds.com\",\n" +
            "  \"createdAt\": \"2026-03-10T11:40:28Z\",\n" +
            "  \"updatedAt\": \"2026-03-10T17:29:47Z\",\n" +
            "  \"confirmedAt\": \"2026-01-02T08:00:00Z\"\n" +
            "}";

    /**
     * Sample VGM payload for {@code GET/PUT /api/v2/bookings/{bookingId}/vgm} OpenAPI examples.
     * On PUT, {@code source}, {@code version}, and {@code status} are not bound and are ignored if sent.
     */
    public static final String VGM_V2_BOOKING_GET_PUT_JSON = "{\n" +
            "  \"referenceNumber\": \"TSHGKOP26000441\",\n" +
            "  \"authorizedPerson\": \"kavin\",\n" +
            "  \"obtainedAt\": \"2026-04-27T00:00:00.0Z\",\n" +
            "  \"method\": \"SM1\",\n" +
            "  \"source\": \"C\",\n" +
            "  \"version\": 0,\n" +
            "  \"status\": \"A\",\n" +
            "  \"companyName\": \"NINGBO GENERAL UNION CO.,LTD.\",\n" +
            "  \"phone\": \"8077499087\",\n" +
            "  \"email\": \"NINGBO@gmail.com\",\n" +
            "  \"address\": {\n" +
            "    \"lines\": [\"chennai\"],\n" +
            "    \"locality\": \"Chennai\",\n" +
            "    \"countryCode\": \"IN\",\n" +
            "    \"postalCode\": \"600103\",\n" +
            "    \"administrativeArea\": \"\"\n" +
            "  },\n" +
            "  \"containers\": [\n" +
            "    {\n" +
            "      \"equipmentId\": \"TDTU6709271\",\n" +
            "      \"equipmentType\": \"40DC\",\n" +
            "      \"weight\": 5609,\n" +
            "      \"unit\": \"K\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"equipmentId\": \"TGHU5011704\",\n" +
            "      \"equipmentType\": \"40HC\",\n" +
            "      \"weight\": 3789,\n" +
            "      \"unit\": \"K\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    /**
     * Sample VGM payload for {@code GET/PUT /api/v3/bookings/{bookingId}/vgm}.
     * Header/declarant fields live on each container. On PUT, {@code source}, {@code version},
     * and {@code status} are read-only and ignored if sent.
     */
    public static final String VGM_V3_BOOKING_GET_PUT_JSON = "{\n" +
            "  \"containers\": [\n" +
            "    {\n" +
            "      \"equipmentId\": \"TDTU6709271\",\n" +
            "      \"equipmentType\": \"40DC\",\n" +
            "      \"weight\": 5609,\n" +
            "      \"unit\": \"K\",\n" +
            "      \"referenceNumber\": \"REF001\",\n" +
            "      \"authorizedPerson\": \"kavin\",\n" +
            "      \"obtainedAt\": \"2026-04-27T00:00:00.0Z\",\n" +
            "      \"method\": \"SM1\",\n" +
            "      \"source\": \"C\",\n" +
            "      \"version\": 1,\n" +
            "      \"status\": \"A\",\n" +
            "      \"companyName\": \"NINGBO GENERAL UNION CO.,LTD.\",\n" +
            "      \"phone\": \"8077499087\",\n" +
            "      \"email\": \"NINGBO@gmail.com\",\n" +
            "      \"address\": {\n" +
            "        \"lines\": [\"chennai\"],\n" +
            "        \"locality\": \"Chennai\",\n" +
            "        \"countryCode\": \"IN\",\n" +
            "        \"postalCode\": \"600103\",\n" +
            "        \"administrativeArea\": \"\"\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"equipmentId\": \"TGHU5011704\",\n" +
            "      \"equipmentType\": \"40HC\",\n" +
            "      \"weight\": 3789,\n" +
            "      \"unit\": \"K\",\n" +
            "      \"referenceNumber\": \"REF002\",\n" +
            "      \"authorizedPerson\": \"Priya\",\n" +
            "      \"obtainedAt\": \"2026-04-28T10:00:00Z\",\n" +
            "      \"method\": \"SM2\",\n" +
            "      \"source\": \"C\",\n" +
            "      \"version\": 1,\n" +
            "      \"status\": \"A\",\n" +
            "      \"companyName\": \"ABC Logistics\",\n" +
            "      \"phone\": \"9876543210\",\n" +
            "      \"email\": \"priya@abc.com\",\n" +
            "      \"address\": {\n" +
            "        \"lines\": [\"Street 1\"],\n" +
            "        \"locality\": \"Chennai\",\n" +
            "        \"countryCode\": \"IN\",\n" +
            "        \"postalCode\": \"600001\",\n" +
            "        \"administrativeArea\": \"Tamil Nadu\"\n" +
            "      }\n" +
            "    }\n" +
            "  ]\n" +
            "}";
}
