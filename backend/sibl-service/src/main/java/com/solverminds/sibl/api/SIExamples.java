package com.solverminds.sibl.api;

public final class SIExamples {
    private SIExamples() {}
    
    public static final String MINIMAL_JSON = 
        "{\n" +
        "  \"bookingNumber\": \"SVMDMU2588929\",\n" +
        "  \"status\": \"D\",\n" +
        "  \"plaHeaderName\": \"NHAVA SHEVA,MUMBAI,MH,IN\",\n" +
        "  \"polHeaderName\": \"NHAVA SHEVA,MUMBAI,MH,IN\",\n" +
        "  \"podHeaderName\": \"JEBEL ALI,AE,AE\",\n" +
        "  \"pldHeaderName\": \"JEBEL ALI,AE,AE\",\n" +
        "  \"shipperAddress\": \"Dubai Fort Fortress, Block A, Office 105, Dubai, UAE\",\n" +
        "  \"shipperName\": \"DUBAI NXTON\",\n" +
        "  \"consigeeAddress\": \"Chennai Port Trust, Second Line Beach, Chennai 600001, INDIA\",\n" +
        "  \"consigeeName\": \"ARULPRAKASH S\",\n" +
        "  \"ensFilling\": \"N\",\n" +
        "  \"t2lFilling\": \"N\",\n" +
        "  \"ensType\": \"S\",\n" +
        "  \"customers\": [\n" +
        "    {\n" +
        "      \"customerType\": \"SH\",\n" +
        "      \"customerCode\": \"ECOM TESTING\",\n" +
        "      \"countryId\": \"CN\",\n" +
        "      \"customerName\": \"Xiamen Area of China\",\n" +
        "      \"address\": \"Beijiao, China\",\n" +
        "      \"city\": \"AJMAN\",\n" +
        "      \"telephone\": \"86-38-00138000\",\n" +
        "      \"email\": \"dhiman.p@solverminds.com\"\n" +
        "    }\n" +
        "  ],\n" +
        "  \"containers\": [\n" +
        "    {\n" +
        "      \"cargoNo\": 1,\n" +
        "      \"equipmentType\": \"D\",\n" +
        "      \"equipmentSize\": \"20DC\",\n" +
        "      \"containerNo\": \"ABDU0408131\",\n" +
        "      \"tareWeight\": 2200,\n" +
        "      \"cargoWeight\": 5000,\n" +
        "      \"weightUnit\": \"K\",\n" +
        "      \"equipmentStatus\": \"F\",\n" +
      
        "      \"reefer\": null\n" +
        "    }\n" +
        "  ],\n" +
        "  \"commodities\": [\n" +
        "    {\n" +
        "      \"cargoNo\": 1,\n" +
        "      \"containerNo\": \"ABDU0408131\",\n" +
        "      \"itemNo\": 1,\n" +
        "      \"itemWeight\": 5000,\n" +
        "      \"itemWeightUnit\": \"K\",\n" +
        "      \"commodityGroup\": \"91\",\n" +
        "      \"packageType\": \"CMPK\",\n" +
        "      \"packageQty\": 1,\n" +
        "      \"cargoVolume\": 2,\n" +
        "      \"volumnUnit\": \"M\",\n" +
        "      \"hsCode\": \"9101\",\n" +
        "      \"hazardous\": \"Y\",\n" +
        "      \"dangerousGoods\": {\n" +
        "        \"unNo\": \"1100\",\n" +
        "        \"classCode\": \"3\",\n" +
        "        \"flashPoint\": 11,\n" +
        "        \"flashPointUnit\": \"C\",\n" +
        "        \"isMarinePollutant\": true\n" +
        "      }\n" +
        "    }\n" +
        "  ],\n" +
        "  \"files\": [\n" +
        "    {\n" +
        "      \"fileCategory\": \"INV\",\n" +
        "      \"fileName\": \"INV123.pdf\"\n" +
        "    }\n" +
        "  ]\n" +
        "}";
    
    public static final String FULL_JSON = 
        "{\n" +
        "  \"bookingNumber\": \"SVMDMU2588929\",\n" +
        "  \"status\": \"D\",\n" +
        "  \"addtionalInfo\": \"Customer special instructions for handling\",\n" +
        "  \"plaHeaderName\": \"NHAVA SHEVA,MUMBAI,MH,IN\",\n" +
        "  \"polHeaderName\": \"NHAVA SHEVA,MUMBAI,MH,IN\",\n" +
        "  \"podHeaderName\": \"JEBEL ALI,AE,AE\",\n" +
        "  \"pldHeaderName\": \"JEBEL ALI,AE,AE\",\n" +
        "  \"shipperAddress\": \"Dubai Fort Fortress, Block A, Office 105, Dubai 12345, UAE\",\n" +
        "  \"shipperName\": \"DUBAI NXTON TRADING LLC\",\n" +
        "  \"consigeeAddress\": \"Chennai Port Trust, Second Line Beach, Chennai 600001, INDIA\",\n" +
        "  \"consigeeName\": \"ARULPRAKASH S\",\n" +
        "  \"notifyAddress\": \"Same as Consignee\",\n" +
        "  \"notifyName\": \"ARULPRAKASH S\",\n" +
        "  \"declaredValue\": \"USD 15000\",\n" +
        "  \"ensFilling\": \"S\",\n" +
        "  \"t2lFilling\": \"Y\",\n" +
        "  \"ensType\": \"C\",\n" +
        "  \"customers\": [\n" +
        "    {\n" +
        "      \"customerType\": \"SH\",\n" +
        "      \"customerCode\": \"ECOM TESTING\",\n" +
        "      \"countryId\": \"CN\",\n" +
        "      \"customerName\": \"Xiamen Area of China Trading Co\",\n" +
        "      \"address\": \"Beijiao Industrial Zone, Building 5\",\n" +
        "      \"address1\": \"Suite 302, Xiamen 361000\",\n" +
        "      \"city\": \"XIAMEN\",\n" +
        "      \"zipCode\": \"361000\",\n" +
        "      \"telephone\": \"86-38-00138000\",\n" +
        "      \"email\": \"dhiman.p@solverminds.com\",\n" +
        "      \"stateId\": \"FUJIAN\"\n" +
        "    },\n" +
        "    {\n" +
        "      \"customerType\": \"CN\",\n" +
        "      \"customerCode\": \"DUBAI SHIPPER\",\n" +
        "      \"countryId\": \"AE\",\n" +
        "      \"customerName\": \"Dubai International Shipping\",\n" +
        "      \"address\": \"Dubai Marina, Tower 1\",\n" +
        "      \"city\": \"DUBAI\",\n" +
        "      \"zipCode\": \"12345\",\n" +
        "      \"telephone\": \"+971-4-1234567\",\n" +
        "      \"email\": \"shipping@dubaiintl.ae\"\n" +
        "    }\n" +
        "  ],\n" +
        "  \"containers\": [\n" +
        "    {\n" +
        "      \"cargoNo\": 1,\n" +
        "      \"equipmentType\": \"R\",\n" +
        "      \"equipmentSize\": \"20RF\",\n" +
        "      \"containerNo\": \"ABDU0408131\",\n" +
        "      \"socFlag\": \"N\",\n" +
        "      \"carrierSeal\": \"CS1234567\",\n" +
        "      \"shipperSeal\": \"SS7654321\",\n" +
        "      \"tareWeight\": 2800,\n" +
        "      \"cargoWeight\": 15000,\n" +
        "      \"weightUnit\": \"K\",\n" +
        "      \"nor\": \"Y\",\n" +
        "      \"equipmentStatus\": \"F\",\n" +
 
        "      \"reefer\": {\n" +
        "        \"cargoNo\": 1,\n" +
        "        \"containerNo\": \"ABDU0408131\",\n" +
        "        \"itemNo\": 1,\n" +
        "        \"setTemp\": -18\n" +
        "      }\n" +
        "    },\n" +
        "    {\n" +
        "      \"cargoNo\": 2,\n" +
        "      \"equipmentType\": \"D\",\n" +
        "      \"equipmentSize\": \"40HC\",\n" +
        "      \"containerNo\": \"BCDU1234567\",\n" +
        "      \"socFlag\": \"N\",\n" +
        "      \"carrierSeal\": \"CS2345678\",\n" +
        "      \"tareWeight\": 3900,\n" +
        "      \"cargoWeight\": 25000,\n" +
        "      \"weightUnit\": \"K\",\n" +
        "      \"equipmentStatus\": \"F\",\n" +
        "      \"dgFlag\": \"Y\",\n" +
        "      \"reefer\": null\n" +
        "    }\n" +
        "  ],\n" +
        "  \"commodities\": [\n" +
        "    {\n" +
        "      \"cargoNo\": 1,\n" +
        "      \"containerNo\": \"ABDU0408131\",\n" +
        "      \"itemNo\": 1,\n" +
        "      \"itemWeight\": 15000,\n" +
        "      \"itemWeightUnit\": \"K\",\n" +
        "      \"commodityGroup\": \"91\",\n" +
        "      \"packageType\": \"CMPK\",\n" +
        "      \"packageQty\": 100,\n" +
        "      \"cargoVolume\": 15,\n" +
        "      \"volumnUnit\": \"M\",\n" +
        "      \"marksNo\": \"MRK001\",\n" +
        "      \"marksDesc\": \"FRAGILE - FROZEN GOODS\",\n" +
        "      \"hsCode\": \"9101\",\n" +
        "      \"hazardous\": \"N\",\n" +
        "      \"nettwt\": 14500,\n" +
        "      \"hsSuffixcode\": \"00\",\n" +
        "      \"buyerCode\": \"BUYER001\",\n" +
        "      \"sellerCode\": \"SELLER001\",\n" +
        "      \"buyerName\": \"International Buyer Corp\",\n" +
        "      \"sellerName\": \"Global Seller Ltd\",\n" +
        "      \"commodityHBLNo\": \"HBL2024001\",\n" +
        "      \"dangerousGoods\": null\n" +
        "    },\n" +
        "    {\n" +
        "      \"cargoNo\": 2,\n" +
        "      \"containerNo\": \"BCDU1234567\",\n" +
        "      \"itemNo\": 1,\n" +
        "      \"itemWeight\": 25000,\n" +
        "      \"itemWeightUnit\": \"K\",\n" +
        "      \"commodityGroup\": \"30\",\n" +
        "      \"packageType\": \"PKG\",\n" +
        "      \"packageQty\": 50,\n" +
        "      \"cargoVolume\": 35,\n" +
        "      \"volumnUnit\": \"M\",\n" +
        "      \"marksNo\": \"MRK002\",\n" +
        "      \"marksDesc\": \"DANGEROUS GOODS - CLASS 9\",\n" +
        "      \"hsCode\": \"2710\",\n" +
        "      \"hazardous\": \"Y\",\n" +
        "      \"nettwt\": 24000,\n" +
        "      \"hsSuffixcode\": \"19\",\n" +
        "      \"dangerousGoods\": {\n" +
        "        \"unNo\": \"3082\",\n" +
        "        \"classCode\": \"9\",\n" +
        "        \"flashPoint\": 65,\n" +
        "        \"flashPointUnit\": \"C\",\n" +
        "        \"isMarinePollutant\": true\n" +
        "      }\n" +
        "    }\n" +
        "  ],\n" +
        "  \"files\": [\n" +
        "    {\n" +
        "      \"fileCategory\": \"INV\",\n" +
        "      \"fileName\": \"Commercial_Invoice_001.pdf\"\n" +
        "    },\n" +
        "    {\n" +
        "      \"fileCategory\": \"PL\",\n" +
        "      \"fileName\": \"Packing_List_001.pdf\"\n" +
        "    },\n" +
        "    {\n" +
        "      \"fileCategory\": \"DGC\",\n" +
        "      \"fileName\": \"Dangerous_Goods_Certificate.pdf\"\n" +
        "    },\n" +
        "    {\n" +
        "      \"fileCategory\": \"VGM\",\n" +
        "      \"fileName\": \"VGM_Certificate.pdf\"\n" +
        "    }\n" +
        "  ]\n" +
        "}";

    /** Sample 200 response for GET /api/v1/bill-of-lading/view (detailed example for Swagger). */
    public static final String VIEW_RESPONSE_200 =
        "{\n" +
        "  \"blNo\": \"TSHGNGB25033626A\",\n" +
        "  \"bookingNumber\": \"TSHGNGB25033626\",\n" +
        "  \"vesselCode\": \"P07\",\n" +
        "  \"voyageCode\": \"520\",\n" +
        "  \"loadBound\": \"W\",\n" +
        "  \"loadPortId\": \"NINGBO, CHINA\",\n" +
        "  \"dischargePortId\": \"BARCELONA, SPAIN\",\n" +
        "  \"originPortId\": \"CNNGB\",\n" +
        "  \"finalPortId\": \"ESBCN\",\n" +
        "  \"declinedRemarks\": \"\",\n" +
        "  \"additionalRemarks\": \"\",\n" +
        "  \"plaHeaderName\": \"NINGBO\",\n" +
        "  \"polHeaderName\": \"NINGBO, CHINA\",\n" +
        "  \"podHeaderName\": \"BARCELONA, SPAIN\",\n" +
        "  \"pldHeaderName\": \"BARCELONA\",\n" +
        "  \"ccMailId\": \"\",\n" +
        "  \"shipperAddress\": \"Wragekamp 6\\r\\nHamburg\\r\\nGERMANY\\r\\n22397\\r\\nT:+49-52731-0805\\r\\nF:+49--\\r\\nEORI: DE134895841\\r\\nVAT: DE134895841\",\n" +
        "  \"shipperName\": \"DELTA-SPORT HANDELSKONTOR GMBH\",\n" +
        "  \"consigeeAddress\": \"STIFTSBERGSTRASSE 1 74167 NECKARSULM GERMANY\\r\\nNeckarsulm\\r\\nGERMANY\\r\\n74172\\r\\nT:\\r\\nEORI: DE2947277\",\n" +
        "  \"consigeeName\": \"LIDL STIFTUNG & CO.KG\",\n" +
        "  \"notifyAddress\": \"Stiftsbergstraße 1 ·D-74172\\r\\nNeckarsulm\\r\\nGERMANY\\r\\n74172\\r\\nT:+49-7132-942220\\r\\nF:+49--\\r\\nE:test@lidl.com\\r\\nEORI: DE6794610\\r\\nVAT: DE334978869\",\n" +
        "  \"notifyName\": \"LIDL EMB KG\",\n" +
        "  \"declaredValue\": \"\",\n" +
        "  \"serviceCode\": \"PAX\",\n" +
        "  \"groupCallId\": \"GrpCall-6664\",\n" +
        "  \"polTerminal\": \"CNNGBCTT00\",\n" +
        "  \"podTerminal\": \"ESBCNHPB00\",\n" +
        "  \"ensFilling\": \"N\",\n" +
        "  \"agencyRefNo\": \"\",\n" +
        "  \"requestedSI\": 0,\n" +
        "  \"createdSI\": 0,\n" +
        "  \"oogFlag\": \"N\",\n" +
        "  \"customers\": [\n" +
        "    {\n" +
        "      \"customerType\": \"BY\",\n" +
        "      \"customerCode\": \"2024006468\",\n" +
        "      \"countryId\": \"DE\",\n" +
        "      \"customerName\": \"LIDL STIFTUNG & CO. KG\",\n" +
        "      \"address\": \"Objektnummer 1027 z.Hd. QS-NF-Warenverteilung Dieselstraße 12 74076 Heilbronn, GERMANY\",\n" +
        "      \"address2\": \"\",\n" +
        "      \"city\": \"Heilbronn\",\n" +
        "      \"telephone\": \"+49-7132-942344\",\n" +
        "      \"email\": \"\",\n" +
        "      \"stateId\": \"\"\n" +
        "    },\n" +
        "    {\n" +
        "      \"customerType\": \"CN\",\n" +
        "      \"customerCode\": \"2022000069\",\n" +
        "      \"countryId\": \"DE\",\n" +
        "      \"customerName\": \"LIDL STIFTUNG & CO.KG\",\n" +
        "      \"address\": \"STIFTSBERGSTRASSE 1 74167 NECKARSULM GERMANY\",\n" +
        "      \"address2\": \"\",\n" +
        "      \"city\": \"Neckarsulm\",\n" +
        "      \"telephone\": \"\",\n" +
        "      \"email\": \"\",\n" +
        "      \"stateId\": \"\"\n" +
        "    },\n" +
        "    {\n" +
        "      \"customerType\": \"NT\",\n" +
        "      \"customerCode\": \"2022000001\",\n" +
        "      \"countryId\": \"DE\",\n" +
        "      \"customerName\": \"LIDL EMB KG\",\n" +
        "      \"address\": \"Stiftsbergstraße 1 ·D-74172\",\n" +
        "      \"address2\": \"\",\n" +
        "      \"city\": \"Neckarsulm\",\n" +
        "      \"telephone\": \"+49-7132-942220\",\n" +
        "      \"email\": \"test@lidl.com\",\n" +
        "      \"stateId\": \"\"\n" +
        "    },\n" +
        "    {\n" +
        "      \"customerType\": \"SH\",\n" +
        "      \"customerCode\": \"2023001752\",\n" +
        "      \"countryId\": \"DE\",\n" +
        "      \"customerName\": \"DELTA-SPORT HANDELSKONTOR GMBH\",\n" +
        "      \"address\": \"Wragekamp 6\",\n" +
        "      \"address2\": \"\",\n" +
        "      \"city\": \"Hamburg\",\n" +
        "      \"telephone\": \"+49-52731-0805\",\n" +
        "      \"email\": \"\",\n" +
        "      \"stateId\": \"\"\n" +
        "    },\n" +
        "    {\n" +
        "      \"customerType\": \"SL\",\n" +
        "      \"customerCode\": \"2022000063\",\n" +
        "      \"countryId\": \"CN\",\n" +
        "      \"customerName\": \"MILSPED SHENZHEN INTERNATIONAL FORWARDING LIMITED COMPANY\",\n" +
        "      \"address\": \"ROOM 22I , NEW TIMES PLAZA, NO.1, TAIZI ROAD, SHUIWAN COMMUNITY, ZHAOSHANG SUB-DISTRICT, NANSHAN DISTRICT,\",\n" +
        "      \"address2\": \"\",\n" +
        "      \"city\": \"Shenzhen\",\n" +
        "      \"telephone\": \"+86-755-21618794\",\n" +
        "      \"email\": \"\",\n" +
        "      \"stateId\": \"\"\n" +
        "    }\n" +
        "  ],\n" +
        "  \"containers\": [\n" +
        "    {\n" +
        "      \"cargoNo\": 1,\n" +
        "      \"equipmentSize\": \"40HC\",\n" +
        "      \"containerNo\": \"TAWU4155497\",\n" +
        "      \"carrierSeal\": \"1231234445\",\n" +
        "      \"shipperSeal\": \"\",\n" +
        "      \"tareWeight\": 3700,\n" +
        "      \"cargoWeight\": 4,\n" +
        "      \"weightUnit\": \"K\",\n" +
        "      \"nor\": \"N\",\n" +
        "      \"equipmentStatus\": \"F\",\n" +
        "      \"equipmentType\": \"D\",\n" +
        "      \"soc\": \"N\",\n" +
        "      \"grossWeight\": 3704,\n" +
        "      \"reeferFlag\": \"Y\",\n" +
        "      \"reefer\": {\n" +
        "        \"ventilation\": \"OPEN\",\n" +
        "        \"ventSet\": 25,\n" +
        "        \"humidity\": 60,\n" +
        "        \"humidityFlag\": \"Y\",\n" +
        "        \"drains\": \"OPEN\",\n" +
        "        \"setTemp\": 5,\n" +
        "        \"tempUnit\": \"C\",\n" +
        "        \"minTemp\": 2,\n" +
        "        \"maxTemp\": 8,\n" +
        "        \"o2Value\": 21,\n" +
        "        \"co2Value\": 5\n" +
        "      }\n" +
        "    }\n" +
        "  ],\n" +
        "  \"commodities\": [\n" +
        "    {\n" +
        "      \"cargoNo\": 1,\n" +
        "      \"containerNo\": \"TAWU4155497\",\n" +
        "      \"itemNo\": 1,\n" +
        "      \"commodityGroup\": \"01\",\n" +
        "      \"packageType\": \"BINS\",\n" +
        "      \"packageQty\": 25,\n" +
        "      \"marksNo\": \"EWDAS\",\n" +
        "      \"marksDesc\": \"FDSSASD\",\n" +
        "      \"hsCode\": \"0102902061\",\n" +
        "      \"hazardous\": \"Y\",\n" +
        "      \"buyerCode\": \"2024006468\",\n" +
        "      \"sellerCode\": \"2022000063\",\n" +
        "      \"buyerName\": \"LIDL STIFTUNG & CO. KG\",\n" +
        "      \"sellerName\": \"MILSPED SHENZHEN INTERNATIONAL FORWARDING LIMITED COMPANY\",\n" +
        "      \"commodityHBLNo\": \"\",\n" +
        "      \"itemWeight\": 4,\n" +
        "      \"itemWeightUnit\": \"K\",\n" +
        "      \"netWeight\": 0,\n" +
        "      \"cargoVolume\": 0.0,\n" +
        "      \"volumnUnit\": \"M\",\n" +
        "      \"dangerousGoods\": {\n" +
        "        \"unNo\": \"1100\",\n" +
        "        \"classCode\": \"3\",\n" +
        "        \"flashPoint\": 11,\n" +
        "        \"flashPointUnit\": \"C\",\n" +
        "        \"isMarinePollutant\": true\n" +
        "      }\n" +
        "    }\n" +
        "  ]\n" +
        "}";
}
