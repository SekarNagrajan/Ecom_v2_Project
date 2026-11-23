# SI Create vs SI Update – Consistency

## Objective

`sicreate` (POST) and `siupdate` (PUT) use the same request DTO (`SIBLRequestDTO`) and now affect the **same tables and columns**. They differ only in:

- **sicreate**: generates `onlineSINo`; sets `CrUser`/`CrDate`, `ChgUser`/`ChgDate` = null.
- **siupdate**: uses `onlineSINo` from path; keeps existing `CrUser`/`CrDate`; sets `ChgUser`/`ChgDate`.

## Tables and columns (same for create and update)

### 1. ecomSI (main SI)

| Source | Columns |
|--------|--------|
| Path (update only) / Generated (create) | eSINo |
| DTO | bookno, Vessel, Voyage, bound, pol, pod, pla, pld, custcode, CrUser, CrDate, ChgUser, ChgDate, editbl, blversion, status, declined_remarks, custremarks, plaHeaderName, polHeaderName, podHeaderName, pldHeaderName, appversion, ccmailid, shipper, shippername, consigee, consigeename, notify, notifyname, AESNumber, AESDiscliamer, releasetype, declaredvalue, blType, ACID, freight, cargoprotectid, t2lfiling, packinglst, invoiceupload, telexrls, ensType |
| Default (create) | blno = NULL |
| Preserved (update) | blno left unchanged |

### 2. ecomSI_cust (customers)

- **Mapping**: `createCustomerRecord(EcomSi, SIBLCustomerDTO)` → eSINo, custTypeId, custId, countryId, name, address1, city (and address2 if set on DTO).
- **Create**: insert one row per `request.getCustomers()`.
- **Update**: delete all by eSINo, then insert from `request.getCustomers()`.

### 3. ecomSI_cargo (containers)

- **Mapping**: `createContainerRecord(EcomSi, SIBLContainerDTO)` → eSINo, conId, eqpSize, containerno, cargoWeight, weightUnit, shipperOwn, carrierSeal, ShipperSeal, ref_nor, eqpstatus, dgflag, tareWeight, grossweight, rate_eqpTypeid, oog.
- **Create**: insert one row per `request.getContainers()`.
- **Update**: delete all by eSINo, then insert from `request.getContainers()`.

### 4. ecomSI_reefer (reefer settings)

- **Condition**: reefer=Y and nor≠Y and reeferSettings present.
- **Mapping**: `createReeferRecord(EcomSi, SIBLContainerDTO)`.
- **Create/Update**: same as containers (delete by eSINo, re-insert when condition holds).

### 5. ecomSI_oog (OOG dimensions)

- **Condition**: oogFlag=Y and oogDimensions present (container) or commodity.oog present.
- **Mapping**: `createOogRecord` / `createOogRecordFromCommodity`.
- **Create/Update**: delete by eSINo, re-insert when condition holds.

### 6. ecomSI_dg (dangerous goods)

- **Condition**: dgFlag=Y and dgDetails (container) or hazardous=Y and dangerousGoods/imoContact/imoPhone (commodity).
- **Mapping**: `createDgRecordFromContainer` / `createDgRecord` + `mapDangerousGoodsToEntity`.
- **Create/Update**: delete by eSINo, re-insert when condition holds.

### 7. ecomSI_commodity (commodities)

- **Mapping**: `createCommodityRecord(EcomSi, SIBLCommodityDTO)`.
- **Create**: insert one row per `request.getCommodities()` (if non-empty).
- **Update**: delete all by eSINo, then insert from `request.getCommodities()`.

### 8. ecomSIRequestMaster (request master)

- **Mapping**: `createRequestMasterRecord(EcomSi, customerCode, SIBLRequestDTO)` → reqID (= eSINo), userId, requestedSI, createdSI, bookno, custcode, crUser, crDate, chgUser, chgDate.
- **Create**: insert when repo available.
- **Update**: delete by reqID (eSINo) if present, then insert.

## Shared mapping

- **Main SI**: `mapRequestToEcomSi(SIBLRequestDTO, EcomSi)` is used for both create and update. Create uses `createMainSiRecord(request)` (new entity + `mapRequestToEcomSi`); update loads existing EcomSi and calls `mapRequestToEcomSi(request, existingSi)`.
- **Children**: Same methods `createCustomerRecord`, `createContainerRecord`, `createReeferRecord`, `createOogRecord`, `createDgRecordFromContainer`, `createDgRecord`, `createCommodityRecord`, `createOogRecordFromCommodity`, `createRequestMasterRecord` are used for both create and update (update: delete-by-eSINo then re-insert using these methods).

## Repository changes

- `EcomSiCustomerRepository`, `EcomSiContainerRepository`, `EcomSiCommodityRepository`: added `deleteByeSINo(String eSINo)` for update replace-children logic.
- `EcomSiReeferRepository`, `EcomSiDgRepository`, `EcomSiOogRepository`: already had `deleteByeSINo`.

## Controller

- **siupdate**: On `IllegalArgumentException` with message starting with `SI_NOT_FOUND:`, returns **404 Not Found**. Other validation failures return 400.
