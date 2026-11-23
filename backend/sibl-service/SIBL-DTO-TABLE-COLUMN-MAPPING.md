# SIBL DTO ↔ Table/Column Mapping

Reference for **sicreate** (POST `/sicreate` – SIBLRequestDTO) and **view** (GET `/view` – SIBLResponseDTO).

Format: **Table name** | **Column name** | **DTO**

Schema for ecomSI tables: `dbo`. Bl tables: no schema prefix.

---

## Part 1: SICREATE (SIBLRequestDTO) – Insert mapping

For each DTO field: table and column where the value is **inserted** when you call POST `/sicreate`.

### 1.1 Root fields (ecomSI + ecomSIRequestMaster)

| Table name | Column name | DTO |
|------------|-------------|-----|
| ecomSI | eSINo | SIBLRequestDTO.onlineSINo (auto-generated) |
| ecomSI | blno | SIBLRequestDTO.blNo (null on insert) |
| ecomSI | bookno | SIBLRequestDTO.bookingNumber |
| ecomSI | Vessel | SIBLRequestDTO.vesselCode |
| ecomSI | Voyage | SIBLRequestDTO.voyageCode |
| ecomSI | bound | SIBLRequestDTO.loadBound |
| ecomSI | pol | SIBLRequestDTO.loadPortId |
| ecomSI | pod | SIBLRequestDTO.dischargePortId |
| ecomSI | pla | SIBLRequestDTO.originPortId |
| ecomSI | pld | SIBLRequestDTO.finalPortId |
| ecomSI | custcode | SIBLRequestDTO.customerCodeFromJWT |
| ecomSI | CrUser | SIBLRequestDTO.createdUser |
| ecomSI | CrDate | SIBLRequestDTO.createdDate |
| ecomSI | ChgUser | SIBLRequestDTO.changeUser |
| ecomSI | ChgDate | SIBLRequestDTO.changeDate |
| ecomSI | editbl | SIBLRequestDTO.editBl |
| ecomSI | blversion | SIBLRequestDTO.blVersion |
| ecomSI | status | SIBLRequestDTO.status |
| ecomSI | declined_remarks | SIBLRequestDTO.declinedRemarks |
| ecomSI | custremarks | SIBLRequestDTO.additionalRemarks |
| ecomSI | plaHeaderName | SIBLRequestDTO.plaHeaderName |
| ecomSI | polHeaderName | SIBLRequestDTO.polHeaderName |
| ecomSI | podHeaderName | SIBLRequestDTO.podHeaderName |
| ecomSI | pldHeaderName | SIBLRequestDTO.pldHeaderName |
| ecomSI | appversion | SIBLRequestDTO.appVersion |
| ecomSI | ccmailid | SIBLRequestDTO.ccMailId |
| ecomSI | shipper | SIBLRequestDTO.shipperAddress |
| ecomSI | shippername | SIBLRequestDTO.shipperName |
| ecomSI | consigee | SIBLRequestDTO.consigeeAddress |
| ecomSI | consigeename | SIBLRequestDTO.consigeeName |
| ecomSI | notify | SIBLRequestDTO.notifyAddress |
| ecomSI | notifyname | SIBLRequestDTO.notifyName |
| ecomSI | AESNumber | SIBLRequestDTO.aesNumber |
| ecomSI | AESDiscliamer | SIBLRequestDTO.aesDisclaimer |
| ecomSI | releasetype | SIBLRequestDTO.releaseType |
| ecomSI | declaredvalue | SIBLRequestDTO.declaredValue |
| ecomSI | blType | SIBLRequestDTO.blType |
| ecomSI | ACID | SIBLRequestDTO.acid |
| ecomSI | freight | SIBLRequestDTO.freight |
| ecomSI | cargoprotectid | SIBLRequestDTO.cargoProtectId |
| ecomSI | t2lfiling | SIBLRequestDTO.t2lFilling |
| ecomSI | packinglst | SIBLRequestDTO.packingList |
| ecomSI | invoiceupload | SIBLRequestDTO.invoiceUpload |
| ecomSI | telexrls | SIBLRequestDTO.telexRelease |
| ecomSI | ensType | SIBLRequestDTO.ensType |
| ecomSIRequestMaster | reqID | (eSINo – SI number) |
| ecomSIRequestMaster | userId | (customerCode) |
| ecomSIRequestMaster | requestedSI | SIBLRequestDTO.requestedSI |
| ecomSIRequestMaster | createdSI | SIBLRequestDTO.createdSI |
| ecomSIRequestMaster | bookno | SIBLRequestDTO.bookingNumber |
| ecomSIRequestMaster | custcode | SIBLRequestDTO.customerCodeFromJWT |
| ecomSIRequestMaster | crUser | SIBLRequestDTO.createdUser |
| ecomSIRequestMaster | crDate | SIBLRequestDTO.createdDate |
| ecomSIRequestMaster | chgUser | SIBLRequestDTO.changeUser |
| ecomSIRequestMaster | chgDate | SIBLRequestDTO.changeDate |

### 1.2 SIBLCustomerDTO (ecomSI_cust)

| Table name | Column name | DTO |
|------------|-------------|-----|
| ecomSI_cust | eSINo | SIBLCustomerDTO.onlineSINo |
| ecomSI_cust | custTypeId | SIBLCustomerDTO.customerType |
| ecomSI_cust | custId | SIBLCustomerDTO.customerCode |
| ecomSI_cust | countryId | SIBLCustomerDTO.countryId |
| ecomSI_cust | name | SIBLCustomerDTO.customerName |
| ecomSI_cust | address1 | SIBLCustomerDTO.address |
| ecomSI_cust | address2 | SIBLCustomerDTO.address2 |
| ecomSI_cust | city | SIBLCustomerDTO.city |
| ecomSI_cust | stateId | SIBLCustomerDTO.stateId |
| ecomSI_cust | telephone | SIBLCustomerDTO.telephone |
| ecomSI_cust | email | SIBLCustomerDTO.email |

### 1.3 SIBLContainerDTO (ecomSI_cargo, ecomSI_reefer, ecomSI_dg, ecomSI_oog)

| Table name | Column name | DTO |
|------------|-------------|-----|
| ecomSI_cargo | eSINo | SIBLContainerDTO.onlineSINo |
| ecomSI_cargo | conId | SIBLContainerDTO.cargoNo |
| ecomSI_cargo | eqpSize | SIBLContainerDTO.equipmentSize |
| ecomSI_cargo | containerno | SIBLContainerDTO.containerNo |
| ecomSI_cargo | shipperOwn | SIBLContainerDTO.socFlag |
| ecomSI_cargo | carrierSeal | SIBLContainerDTO.carrierSeal |
| ecomSI_cargo | ShipperSeal | SIBLContainerDTO.shipperSeal |
| ecomSI_cargo | tareWeight | SIBLContainerDTO.tareWeight |
| ecomSI_cargo | cargoWeight | SIBLContainerDTO.cargoWeight |
| ecomSI_cargo | grossweight | SIBLContainerDTO.grossweight |
| ecomSI_cargo | weightUnit | SIBLContainerDTO.weightUnit |
| ecomSI_cargo | ref_nor | SIBLContainerDTO.nor |
| ecomSI_cargo | eqpstatus | SIBLContainerDTO.equipmentStatus |
| ecomSI_cargo | oog | SIBLContainerDTO.oogFlag |
| ecomSI_cargo | dgflag | SIBLContainerDTO.dgFlag |
| ecomSI_cargo | rate_eqpTypeid | SIBLContainerDTO.rateEquipmentTypeId |
| ecomSI_reefer | eSINo | (link to SI) |
| ecomSI_reefer | conId | (link to container) |
| ecomSI_reefer | containerno | (link to container) |
| ecomSI_reefer | ref_setTemp | SIBLReeferDTO.tempSet |
| ecomSI_reefer | ref_minTemp | SIBLReeferDTO.tempMin |
| ecomSI_reefer | ref_maxTemp | SIBLReeferDTO.tempMax |
| ecomSI_reefer | ref_tempUnit | SIBLReeferDTO.tempSetUnit |
| ecomSI_dg | eSINo, conId, containerno, itemno | SIBLContainerDTO.dgDetails (ecomSI_dg) |
| ecomSI_oog | eSINo, conId, containerno, comItemNo | SIBLContainerDTO.oogDimensions (ecomSI_oog) |

### 1.4 SIBLCommodityDTO (ecomSI_commodity, ecomSI_dg, ecomSI_oog)

| Table name | Column name | DTO |
|------------|-------------|-----|
| ecomSI_commodity | eSINo | SIBLCommodityDTO.onlineSINo |
| ecomSI_commodity | conId | SIBLCommodityDTO.cargoNo |
| ecomSI_commodity | containerno | SIBLCommodityDTO.containerNo |
| ecomSI_commodity | itemno | SIBLCommodityDTO.itemNo |
| ecomSI_commodity | commodity | SIBLCommodityDTO.commodityGroup |
| ecomSI_commodity | package | SIBLCommodityDTO.packageType |
| ecomSI_commodity | packageQty | SIBLCommodityDTO.packageQty |
| ecomSI_commodity | HSCommodity | SIBLCommodityDTO.hsCode |
| ecomSI_commodity | hazardous | SIBLCommodityDTO.hazardous |
| ecomSI_commodity | itemWeight | SIBLCommodityDTO.itemWeight |
| ecomSI_commodity | itemWeightUnit | SIBLCommodityDTO.itemWeightUnit |
| ecomSI_commodity | nettwt | SIBLCommodityDTO.nettwt |
| ecomSI_commodity | itemVolume | SIBLCommodityDTO.itemVolume |
| ecomSI_commodity | itemVolumeUnit | SIBLCommodityDTO.itemVolumeUnit |
| ecomSI_commodity | marksNo | SIBLCommodityDTO.marksNo |
| ecomSI_commodity | marksDesc | SIBLCommodityDTO.marksDesc |
| ecomSI_commodity | bycode | SIBLCommodityDTO.buyerCode |
| ecomSI_commodity | slcode | SIBLCommodityDTO.sellerCode |
| ecomSI_commodity | byname | SIBLCommodityDTO.buyerName |
| ecomSI_commodity | slname | SIBLCommodityDTO.sellerName |
| ecomSI_commodity | cmdty_hblno | SIBLCommodityDTO.commodityHBLNo |
| ecomSI_dg | (per commodity) | SIBLCommodityDTO.dangerousGoods |
| ecomSI_oog | (per commodity) | SIBLCommodityDTO.oog |

---

## Part 2: VIEW (SIBLResponseDTO) – Read mapping

For each response field: table and column where the value is **read** when you call GET `/view`.

### 2.1 By onlineSINo (ecomSI path)

| Table name | Column name | DTO |
|------------|-------------|-----|
| ecomSI | eSINo | SIBLResponseDTO.onlineSINo |
| ecomSI | blno | SIBLResponseDTO.blNo |
| ecomSI | bookno | SIBLResponseDTO.bookingNumber |
| ecomSI | Vessel | SIBLResponseDTO.vesselCode |
| ecomSI | Voyage | SIBLResponseDTO.voyageCode |
| ecomSI | bound | SIBLResponseDTO.loadBound |
| ecomSI | pol | SIBLResponseDTO.loadPortId |
| ecomSI | pod | SIBLResponseDTO.dischargePortId |
| ecomSI | pla | SIBLResponseDTO.originPortId |
| ecomSI | pld | SIBLResponseDTO.finalPortId |
| ecomSI | custcode | SIBLResponseDTO.customerCodeFromJWT |
| ecomSI | CrUser | SIBLResponseDTO.createdUser |
| ecomSI | CrDate | SIBLResponseDTO.createdDate |
| ecomSI | ChgUser | SIBLResponseDTO.changeUser |
| ecomSI | ChgDate | SIBLResponseDTO.changeDate |
| ecomSI | editbl | SIBLResponseDTO.editBl |
| ecomSI | blversion | SIBLResponseDTO.blVersion |
| ecomSI | status | SIBLResponseDTO.status |
| ecomSI | declined_remarks | SIBLResponseDTO.declinedRemarks |
| ecomSI | custremarks | SIBLResponseDTO.additionalRemarks |
| ecomSI | plaHeaderName | SIBLResponseDTO.plaHeaderName |
| ecomSI | polHeaderName | SIBLResponseDTO.polHeaderName |
| ecomSI | podHeaderName | SIBLResponseDTO.podHeaderName |
| ecomSI | pldHeaderName | SIBLResponseDTO.pldHeaderName |
| ecomSI | appversion | SIBLResponseDTO.appVersion |
| ecomSI | ccmailid | SIBLResponseDTO.ccMailId |
| ecomSI | shipper | SIBLResponseDTO.shipperAddress |
| ecomSI | shippername | SIBLResponseDTO.shipperName |
| ecomSI | consigee | SIBLResponseDTO.consigeeAddress |
| ecomSI | consigeename | SIBLResponseDTO.consigeeName |
| ecomSI | notify | SIBLResponseDTO.notifyAddress |
| ecomSI | notifyname | SIBLResponseDTO.notifyName |
| ecomSI | AESNumber | SIBLResponseDTO.aesNumber |
| ecomSI | AESDiscliamer | SIBLResponseDTO.aesDisclaimer |
| ecomSI | releasetype | SIBLResponseDTO.releaseType |
| ecomSI | declaredvalue | SIBLResponseDTO.declaredValue |
| ecomSI | blType | SIBLResponseDTO.blType |
| ecomSI | ACID | SIBLResponseDTO.acid |
| ecomSI | freight | SIBLResponseDTO.freight |
| ecomSI | cargoprotectid | SIBLResponseDTO.cargoProtectId |
| ecomSI | t2lfiling | SIBLResponseDTO.t2lFilling |
| ecomSI | packinglst | SIBLResponseDTO.packingList |
| ecomSI | invoiceupload | SIBLResponseDTO.invoiceUpload |
| ecomSI | telexrls | SIBLResponseDTO.telexRelease |
| ecomSI | ensType | SIBLResponseDTO.ensType |

| Table name | Column name | DTO |
|------------|-------------|-----|
| ecomSI_cust | eSINo | (link) |
| ecomSI_cust | custTypeId | SIBLCustomerDTO.customerType |
| ecomSI_cust | custId | SIBLCustomerDTO.customerCode |
| ecomSI_cust | countryId | SIBLCustomerDTO.countryId |
| ecomSI_cust | name | SIBLCustomerDTO.customerName |
| ecomSI_cust | address1 | SIBLCustomerDTO.address |
| ecomSI_cust | address2 | SIBLCustomerDTO.address2 |
| ecomSI_cust | city | SIBLCustomerDTO.city |
| ecomSI_cust | stateId | SIBLCustomerDTO.stateId |
| ecomSI_cust | telephone | SIBLCustomerDTO.telephone |
| ecomSI_cust | email | SIBLCustomerDTO.email |

| Table name | Column name | DTO |
|------------|-------------|-----|
| ecomSI_cargo | eSINo | (link) |
| ecomSI_cargo | conId | SIBLContainerDTO.cargoNo |
| ecomSI_cargo | eqpSize | SIBLContainerDTO.equipmentSize |
| ecomSI_cargo | containerno | SIBLContainerDTO.containerNo |
| ecomSI_cargo | shipperOwn | SIBLContainerDTO.socFlag |
| ecomSI_cargo | carrierSeal | SIBLContainerDTO.carrierSeal |
| ecomSI_cargo | ShipperSeal | SIBLContainerDTO.shipperSeal |
| ecomSI_cargo | tareWeight | SIBLContainerDTO.tareWeight |
| ecomSI_cargo | cargoWeight | SIBLContainerDTO.cargoWeight |
| ecomSI_cargo | grossweight | SIBLContainerDTO.grossweight |
| ecomSI_cargo | weightUnit | SIBLContainerDTO.weightUnit |
| ecomSI_cargo | ref_nor | SIBLContainerDTO.nor |
| ecomSI_cargo | eqpstatus | SIBLContainerDTO.equipmentStatus |
| ecomSI_cargo | oog | SIBLContainerDTO.oogFlag |
| ecomSI_cargo | dgflag | SIBLContainerDTO.dgFlag |
| ecomSI_cargo | rate_eqpTypeid | SIBLContainerDTO.rateEquipmentTypeId |
| ecomSI_reefer | ref_setTemp, ref_minTemp, ref_maxTemp, ref_tempUnit | SIBLContainerDTO.reeferSettings |
| ecomSI_dg | MarinePollutant, unNo, class, FlashPt, FlashPtUnit | SIBLContainerDTO.dgDetails / SIBLCommodityDTO.dangerousGoods |
| ecomSI_oog | leftft, rightft, forward, Alt, topft, Unit | SIBLContainerDTO.oogDimensions / SIBLCommodityDTO.oog |

| Table name | Column name | DTO |
|------------|-------------|-----|
| ecomSI_commodity | eSINo | (link) |
| ecomSI_commodity | conId | SIBLCommodityDTO.cargoNo |
| ecomSI_commodity | containerno | SIBLCommodityDTO.containerNo |
| ecomSI_commodity | itemno | SIBLCommodityDTO.itemNo |
| ecomSI_commodity | commodity | SIBLCommodityDTO.commodityGroup |
| ecomSI_commodity | package | SIBLCommodityDTO.packageType |
| ecomSI_commodity | packageQty | SIBLCommodityDTO.packageQty |
| ecomSI_commodity | HSCommodity | SIBLCommodityDTO.hsCode |
| ecomSI_commodity | hazardous | SIBLCommodityDTO.hazardous |
| ecomSI_commodity | itemWeight | SIBLCommodityDTO.itemWeight |
| ecomSI_commodity | itemWeightUnit | SIBLCommodityDTO.itemWeightUnit |
| ecomSI_commodity | nettwt | SIBLCommodityDTO.nettwt |
| ecomSI_commodity | itemVolume | SIBLCommodityDTO.itemVolume |
| ecomSI_commodity | itemVolumeUnit | SIBLCommodityDTO.itemVolumeUnit |
| ecomSI_commodity | marksNo | SIBLCommodityDTO.marksNo |
| ecomSI_commodity | marksDesc | SIBLCommodityDTO.marksDesc |
| ecomSI_commodity | bycode | SIBLCommodityDTO.buyerCode |
| ecomSI_commodity | slcode | SIBLCommodityDTO.sellerCode |
| ecomSI_commodity | byname | SIBLCommodityDTO.buyerName |
| ecomSI_commodity | slname | SIBLCommodityDTO.sellerName |
| ecomSI_commodity | cmdty_hblno | SIBLCommodityDTO.commodityHBLNo |

### 2.2 By blNumber (bl path)

| Table name | Column name | DTO |
|------------|-------------|-----|
| bl | bl_no | SIBLResponseDTO.blNo |
| bl | book_no | SIBLResponseDTO.bookingNumber |
| bl | vesselCode | SIBLResponseDTO.vesselCode |
| bl | voyageCode | SIBLResponseDTO.voyageCode |
| bl | bound | SIBLResponseDTO.loadBound |
| bl | loadPortId | SIBLResponseDTO.loadPortId |
| bl | dischargePortid | SIBLResponseDTO.dischargePortId |
| bl | originPortId | SIBLResponseDTO.originPortId |
| bl | finalPortid | SIBLResponseDTO.finalPortId |
| bl | status | SIBLResponseDTO.status |

| Table name | Column name | DTO |
|------------|-------------|-----|
| bl_header | pol | SIBLResponseDTO.loadPortId (override) |
| bl_header | pod | SIBLResponseDTO.dischargePortId (override) |
| bl_header | Shipper | SIBLResponseDTO.shipperAddress |
| bl_header | shippername | SIBLResponseDTO.shipperName |
| bl_header | Consignee | SIBLResponseDTO.consigeeAddress |
| bl_header | consigneeName | SIBLResponseDTO.consigeeName |
| bl_header | Notify | SIBLResponseDTO.notifyAddress |
| bl_header | notifierName | SIBLResponseDTO.notifyName |

| Table name | Column name | DTO |
|------------|-------------|-----|
| bl_cust | custtypeid | SIBLCustomerDTO.customerType |
| bl_cust | custid | SIBLCustomerDTO.customerCode |
| bl_cust | countryid | SIBLCustomerDTO.countryId |
| bl_cust | name | SIBLCustomerDTO.customerName |
| bl_cust | address1 | SIBLCustomerDTO.address |
| bl_cust | address2 | SIBLCustomerDTO.address2 |
| bl_cust | city | SIBLCustomerDTO.city |
| bl_cust | stateId | SIBLCustomerDTO.stateId |
| bl_cust | telephone | SIBLCustomerDTO.telephone |
| bl_cust | email | SIBLCustomerDTO.email |

| Table name | Column name | DTO |
|------------|-------------|-----|
| bl_cargo | bl_no | (link) |
| bl_cargo | eqpId | SIBLContainerDTO.containerNo |
| bl_cargo | cargoNo | SIBLContainerDTO.cargoNo |
| bl_cargo | eqptypeid | SIBLContainerDTO.equipmentSize |
| bl_cargo | shipperown | SIBLContainerDTO.socFlag |
| bl_cargo | seal1No | SIBLContainerDTO.carrierSeal |
| bl_cargo | seal2No | SIBLContainerDTO.shipperSeal |
| bl_cargo | tareweight | SIBLContainerDTO.tareWeight |
| bl_cargo | cargoWeight | SIBLContainerDTO.cargoWeight |
| bl_cargo | grossWeight | SIBLContainerDTO.grossweight |
| bl_cargo | weightUnit | SIBLContainerDTO.weightUnit |
| bl_cargo | nor | SIBLContainerDTO.nor |
| bl_cargo | eqpstatus | SIBLContainerDTO.equipmentStatus |
| bl_cargo | oog | SIBLContainerDTO.oogFlag |
| bl_cargo | dgflag | SIBLContainerDTO.dgFlag |

| Table name | Column name | DTO |
|------------|-------------|-----|
| bl_commodity | bl_no | (link) |
| bl_commodity | eqpId | SIBLCommodityDTO.containerNo |
| bl_commodity | cargoNo | SIBLCommodityDTO.cargoNo |
| bl_commodity | itemNo | SIBLCommodityDTO.itemNo |
| bl_commodity | commodityId | SIBLCommodityDTO.commodityGroup / SIBLCommodityDTO.hsCode |
| bl_commodity | packageId | SIBLCommodityDTO.packageType |
| bl_commodity | itemQty | SIBLCommodityDTO.packageQty |
| bl_commodity | itemweight | SIBLCommodityDTO.itemWeight |
| bl_commodity | itemWeightUnit | SIBLCommodityDTO.itemWeightUnit |
| bl_commodity | itemVolume | SIBLCommodityDTO.itemVolume |
| bl_commodity | itemVolumeUnit | SIBLCommodityDTO.itemVolumeUnit |
| bl_commodity | marksDesc | SIBLCommodityDTO.marksDesc |

---

*Generated from SiblController `/sicreate` (SIBLRequestDTO) and `/view` (SIBLResponseDTO), SiCreateService, SiViewService, and entity mappings.*
