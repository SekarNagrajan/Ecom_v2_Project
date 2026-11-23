package com.solverminds.booking.mapper;

import com.solverminds.booking.dto.*;
import com.solverminds.booking.dto.v2.*;
import com.solverminds.booking.service.MasterDataCacheService;
import com.svm.ecom.common.enums.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for converting between V2 DTOs and V1 DTOs/Entities.
 * Handles all field name translations and structural transformations.
 */
@Component
public class BookingMapperV2 {

    private final MasterDataCacheService masterDataCacheService;
    private final DangerousGoodsMapper dangerousGoodsMapper;

    public BookingMapperV2(MasterDataCacheService masterDataCacheService,
                           DangerousGoodsMapper dangerousGoodsMapper) {
        this.masterDataCacheService = masterDataCacheService;
        this.dangerousGoodsMapper = dangerousGoodsMapper;
    }

    // ==================== V2 Request -> V1 Request ====================

    /**
     * Convert V2 BookingRequestUpsertPayloadV2 to V1 BookingRequestDTO
     */
    public BookingRequestDTO toV1BookingRequest(BookingRequestUpsertPayloadV2 v2, String customerCode) {
        if (v2 == null) return null;

        BookingRequestDTO v1 = new BookingRequestDTO();
        
        // Field mappings: V2 -> V1
        v1.setOriginPortId(v2.getOriginId());           // originId -> originPortId
        v1.setFinalPortId(v2.getDeliveryId());          // deliveryId -> finalPortId
        v1.setLoadPortId(v2.getPolId());                // polId -> loadPortId
        v1.setDischargePortId(v2.getPodId());           // podId -> dischargePortId
        v1.setAgencyId(v2.getAgencyId());
        v1.setAgencyRefNo(v2.getAgencyRefNo());
        v1.setRemarks(v2.getRemarks());
        v1.setTemplateName(v2.getTemplateName());
        v1.setExportRefNo(v2.getExportRefNo());
        v1.setCustomerCode(customerCode);
        
        // Date fields
        if (v2.getCargoReadyDate() != null) {
            v1.setCargoReadyDate(v2.getCargoReadyDate().toLocalDate());
        }
        
        // Status: V2 enum -> V1 enum (default D if null)
        v1.setStatus(v2.getStatus() != null ? v2.getStatus() : BookingStatus.D);
        
        // Carriage type and ocean freight: pass through enums
        v1.setPreCarriage(v2.getPreCarriage());
        v1.setOnCarriage(v2.getOnCarriage());
        v1.setOceanFreight(v2.getOceanFreight());
        
        // Boolean -> YesNoFlag for isAmendment; t2lFiling is YesNoFlag (pass-through)
        v1.setIsAmendment(boolToYesNo(v2.getIsAmendment()));
        v1.setT2lFilling(v2.getT2lFiling());
        
        // Contract reference -> rateRefNo
        v1.setRateRefNo(v2.getContractReference());
        
        // Customer reference -> customerPO
        v1.setCustomerPO(v2.getCustomerReference());
        
        // ENS fields: pass through enums
        v1.setEnsFilling(v2.getEnsFilingType());
        v1.setEnsType(v2.getEnsType());
        
        // Route mapping: V2 single route with legs -> V1 routes list
        if (v2.getRoute() != null) {
            v1.setRouteId(v2.getRoute().getId());
            v1.setRoutes(toV1Routes(v2.getRoute()));
        }
        
        // Customers mapping
        if (v2.getCustomers() != null) {
            v1.setCustomers(v2.getCustomers().stream()
                .map(this::toV1Customer)
                .collect(Collectors.toList()));
        }
        
        // Containers and Commodities mapping (V2 nests commodities inside containers)
        if (v2.getContainers() != null) {
            List<ContainerDTO> v1Containers = new ArrayList<>();
            List<CommodityDTO> v1Commodities = new ArrayList<>();
            
            int containerSeq = 1;
            for (BookingContainerV2 v2Container : v2.getContainers()) {
                ContainerDTO v1Container = toV1Container(v2Container, containerSeq);
                v1Containers.add(v1Container);
                // Use same containerNo as in toV1Container for commodity mapping
                String containerNo = v1Container.getContainerNo();
                
                // Extract commodities from container
                if (v2Container.getCommodities() != null) {
                    int itemSeq = 1;
                    for (BookingContainerCommodityV2 v2Commodity : v2Container.getCommodities()) {
                        CommodityDTO v1Commodity = toV1Commodity(v2Commodity, containerSeq, containerNo, itemSeq);
                        v1Commodities.add(v1Commodity);
                        itemSeq++;
                    }
                }
                containerSeq++;
            }
            
            v1.setContainers(v1Containers);
            v1.setCommodities(v1Commodities);
        }
        
        return v1;
    }

    /**
     * Convert V2 BookingCustomerV2 to V1 CustomerDTO
     */
    public CustomerDTO toV1Customer(BookingCustomerV2 v2) {
        if (v2 == null) return null;

        CustomerDTO v1 = new CustomerDTO();
        
        // type -> customerType (enum pass-through)
        v1.setCustomerType(v2.getType());
        
        // code -> customerCode
        v1.setCustomerCode(v2.getCode());
        
        // name -> customerName
        v1.setCustomerName(v2.getName());
        
        v1.setTelephone(v2.getTelephone());
        v1.setEmail(v2.getEmail());
        
        // Address mapping: nested AddressV2 -> flat fields
        if (v2.getAddress() != null) {
            AddressV2 addr = v2.getAddress();
            
            // lines[0] -> address, lines[1] -> address1
            if (addr.getLines() != null && !addr.getLines().isEmpty()) {
                v1.setAddress(addr.getLines().get(0));
                if (addr.getLines().size() > 1) {
                    v1.setAddress1(addr.getLines().get(1));
                }
            }
            
            // locality -> city
            v1.setCity(addr.getLocality());
            
            // postalCode -> zipCode
            v1.setZipCode(addr.getPostalCode());
            
            // countryCode -> countryId
            v1.setCountryId(addr.getCountryCode());
        }
        
        return v1;
    }

    /**
     * Convert V2 BookingContainerV2 to V1 ContainerDTO
     */
    public ContainerDTO toV1Container(BookingContainerV2 v2, int cargoNo) {
        if (v2 == null) return null;

        ContainerDTO v1 = new ContainerDTO();
        
        v1.setCargoNo(cargoNo);
        // When id is null, generate temp container number for containerConidByNo mapping (e.g. 20DCTBN1)
        String containerNo = v2.getId() != null && !v2.getId().trim().isEmpty()
            ? v2.getId()
            : (v2.getCode() != null ? v2.getCode().trim() : "CONT") + "TBN" + cargoNo;
        v1.setContainerNo(containerNo);
        v1.setEquipmentQty(v2.getQuantity());
        
        // type -> equipmentType, status -> equipmentStatus (enum pass-through)
        v1.setEquipmentType(v2.getType());
        v1.setEquipmentSize(v2.getCode());
        v1.setEquipmentStatus(v2.getStatus());
        
        // Boolean flags -> YesNoFlag
        v1.setOogFlag(boolToYesNo(v2.getIsOOG()));
        v1.setSocFlag(boolToYesNo(v2.getIsSOC()));
        v1.setDgFlag(boolToYesNo(v2.getIsDG()));
        v1.setNor(boolToYesNo(v2.getIsNOR()));
        
        // tareWeight - can be populated from v2 or will be calculated
        if (v2.getTareWeight() != null) {
            v1.setTareWeight(v2.getTareWeight().floatValue());
        }
        
        // grossWeight - from V2 container
        if (v2.getGrossWeight() != null) {
            v1.setGrossWeight(v2.getGrossWeight().floatValue());
        }
        
        // cargoWeight - derive from grossWeight - tareWeight, or sum from commodities
        Integer cargoWeight = deriveContainerCargoWeight(v2);
        if (cargoWeight != null) {
            v1.setCargoWeight(cargoWeight);
        }
        
        // Weight unit - V2 uses "K" or "T" strings
        v1.setWeightUnit(WeightUnit.K); // Default to Kilograms
        
        // Reefer settings mapping - only for Reefer (R) containers; DC (Dry) must have empty reefer
        if (v2.getType() != null && v2.getType() == EquipmentType.R && v2.getReeferSettings() != null) {
            v1.setReefer(com.solverminds.booking.util.ReeferMappingHelper.toV1Reefer(v2.getReeferSettings()));
        }
        
        return v1;
    }

    /**
     * Derive container cargo weight from V2 container: grossWeight - tareWeight, or sum of commodity cargoWeight.
     */
    private Integer deriveContainerCargoWeight(BookingContainerV2 v2) {
        if (v2.getGrossWeight() != null && v2.getTareWeight() != null) {
            double cargo = v2.getGrossWeight() - v2.getTareWeight();
            return cargo >= 0 ? (int) Math.round(cargo) : null;
        }
        if (v2.getCommodities() != null && !v2.getCommodities().isEmpty()) {
            double sum = 0;
            for (BookingContainerCommodityV2 c : v2.getCommodities()) {
                if (c.getCargoWeight() != null) {
                    sum += c.getCargoWeight();
                }
            }
            return sum > 0 ? (int) Math.round(sum) : null;
        }
        return null;
    }

    /**
     * Convert V2 BookingContainerCommodityV2 to V1 CommodityDTO
     */
    public CommodityDTO toV1Commodity(BookingContainerCommodityV2 v2, int cargoNo, String containerNo, int itemNo) {
        if (v2 == null) return null;

        CommodityDTO v1 = new CommodityDTO();
        
        v1.setCargoNo(cargoNo);
        v1.setContainerNo(containerNo);
        v1.setItemNo(itemNo);
        
        // commodityGroup as string preserves leading zeros e.g. "09"
        v1.setCommodityGroup(v2.getCommodityGroup());
        
        v1.setHsCode(v2.getHsCode());
        v1.setPackageType(v2.getPackageType());
        v1.setPackageQty(v2.getPackageQuantity());
        
        if (v2.getCargoVolume() != null) {
            v1.setCargoVolume(v2.getCargoVolume());
        }
        
        // cargoVolumeUnit -> volumnUnit, cargoWeightUnit -> itemWeightUnit (enum pass-through)
        v1.setVolumnUnit(v2.getCargoVolumeUnit());
        if (v2.getNetWeight() != null) {
            v1.setNettwt(v2.getNetWeight());
        }
        v1.setBuyerCode(v2.getBuyerCode());
        v1.setBuyerName(v2.getBuyerName());
        v1.setSellerCode(v2.getSellerCode());
        v1.setSellerName(v2.getSellerName());
        v1.setCommodityHBLNo(v2.getCommodityHBLNo());
        if (v2.getCargoWeight() != null) {
            v1.setItemWeight(v2.getCargoWeight().floatValue());
        }
        v1.setItemWeightUnit(v2.getCargoWeightUnit());
        
        if (v2.getDangerousGoodsInfo() != null) {
            v1.setDangerousGoods(copyDangerousGoodsInfo(v2.getDangerousGoodsInfo()));
        }

        return v1;
    }

    /**
     * Convert V2 DangerousGoodsInfoV2 to V1 DangerousGoodsDTO
     */
    public DangerousGoodsDTO toV1DangerousGoods(DangerousGoodsInfoV2 v2) {
        return dangerousGoodsMapper.toInternal(v2);
    }

    private DangerousGoodsInfoV2 copyDangerousGoodsInfo(DangerousGoodsInfoV2 src) {
        return dangerousGoodsMapper.copy(src);
    }

    /**
     * Convert V2 BookingRouteV2 to V1 List<RouteDTO>
     */
    public List<RouteDTO> toV1Routes(BookingRouteV2 v2) {
        if (v2 == null || v2.getLegs() == null) return new ArrayList<>();

        List<RouteDTO> routes = new ArrayList<>();
        int legNo = 1;
        
        for (BookingRouteLegV2 leg : v2.getLegs()) {
            RouteDTO route = toV1Route(leg, legNo);
            routes.add(route);
            legNo++;
        }
        
        return routes;
    }

    /**
     * Convert V2 BookingRouteLegV2 to V1 RouteDTO
     */
    public RouteDTO toV1Route(BookingRouteLegV2 v2, int legNo) {
        if (v2 == null) return null;

        RouteDTO v1 = new RouteDTO();
        
        v1.setLegNo(legNo);
        v1.setServiceCode(v2.getServiceCode());
        v1.setVesselCode(v2.getVesselCode());
        v1.setVoyageCode(v2.getVoyageCode());
        v1.setLoadPortId(v2.getLoadPortId());
        v1.setLoadTerminal(v2.getLoadTerminalId());
        v1.setDischargePortId(v2.getDischargePortId());
        v1.setDischargeTerminal(v2.getDischargeTerminalId());
        v1.setGroupCallId(v2.getGroupCallId());
        v1.setLoadCallId(v2.getLoadCallId());
        v1.setDischargeCallId(v2.getDischargeCallId());
        
        // eta/etd mapping (OffsetDateTime -> OffsetDateTime, preserve timezone)
        v1.setEtaDate(v2.getEta());
        v1.setEtdDate(v2.getEtd());
        
        // serviceType, bounds, load/discharge types (enum pass-through)
        v1.setServiceType(v2.getServiceType());
        v1.setLoadBound(v2.getLoadBound());
        v1.setDischargeBound(v2.getDischargeBound());
        v1.setLoadType(v2.getLoadType());
        v1.setDischType(v2.getDischargeType());
        
        return v1;
    }

    // ==================== V1 Response -> V2 Response ====================

    /**
     * Convert V1 BookingRequestDTO to V2 BookingV2 response
     */
    public BookingV2 toV2Booking(BookingRequestDTO v1, String bookingId) {
        if (v1 == null) return null;

        BookingV2 v2 = new BookingV2();
        
        // Set id (extBookNo/confirmed) and bookingRequestId (BKON) based on booking type
        // BKON: id = extBookNo when available, bookingRequestId = ebookno
        // Non-BKON: id = bookingId (LRP book_no), bookingRequestId = onlineBookNo
        if (bookingId != null && bookingId.toUpperCase().startsWith("BKON")) {
            v2.setId(v1.getBookingNumber()); // extBookNo from ecombooking
            v2.setBookingRequestId(bookingId);
        } else {
            v2.setId(bookingId);
            v2.setBookingRequestId(v1.getOnlineBookNo());
        }
        v2.setOriginId(v1.getOriginPortId());
        v2.setOriginName(getPortNameFromCache(v1.getOriginPortId()));
        v2.setDeliveryId(v1.getFinalPortId());
        v2.setDeliveryName(getPortNameFromCache(v1.getFinalPortId()));
        v2.setPolId(v1.getLoadPortId());
        v2.setPolName(getPortNameFromCache(v1.getLoadPortId()));
        v2.setPodId(v1.getDischargePortId());
        v2.setPodName(getPortNameFromCache(v1.getDischargePortId()));
        v2.setAgencyId(v1.getAgencyId());
        v2.setAgencyRefNo(v1.getAgencyRefNo());
        v2.setRemarks(v1.getRemarks());
        v2.setTemplateName(v1.getTemplateName());
        v2.setExportRefNo(v1.getExportRefNo());
        v2.setCustomerCode(v1.getCustomerCode());
        
        // Status, carriage, ocean freight (enum pass-through)
        v2.setStatus(v1.getStatus());
        v2.setPreCarriage(v1.getPreCarriage());
        v2.setOnCarriage(v1.getOnCarriage());
        v2.setOceanFreight(v1.getOceanFreight());
        
        // Boolean flags; t2lFiling is YesNoFlag (pass-through)
        v2.setIsAmendment(yesNoToBool(v1.getIsAmendment()));
        v2.setT2lFiling(v1.getT2lFilling());
        
        // Contract reference
        v2.setContractReference(v1.getRateRefNo());
        
        // Customer reference
        v2.setCustomerReference(v1.getCustomerPO());
        
        // ENS fields (enum pass-through)
        v2.setEnsFilingType(v1.getEnsFilling());
        v2.setEnsType(v1.getEnsType());
        
        // Cargo ready date
        if (v1.getCargoReadyDate() != null) {
            v2.setCargoReadyDate(v1.getCargoReadyDate().atStartOfDay());
        }
        
        // Dates: createdAt = datecr (record creation), updatedAt = dateup (last update)
        v2.setCreatedAt(v1.getDatecr() != null ? v1.getDatecr() : v1.getBookDate());
        v2.setUpdatedAt(v1.getDateup() != null ? v1.getDateup() : v1.getSubmittedDate());
        
        // Created by user and confirmed date
        v2.setCreatedBy(v1.getCreatedBy());
        v2.setConfirmedAt(v1.getConfirmedAt());
        
        // TEUs (calculated from ecombooking_cargo + ecom_equipments)
        v2.setTeus(v1.getTeus());

        // Booking source (read-only pass-through; already resolved by the shared view services)
        v2.setBookingSource(v1.getBookingSource());
        
        // Customers mapping
        if (v1.getCustomers() != null) {
            v2.setCustomers(v1.getCustomers().stream()
                .map(this::toV2Customer)
                .collect(Collectors.toList()));
        }
        
        // Containers with nested commodities
        if (v1.getContainers() != null) {
            v2.setContainers(toV2Containers(v1.getContainers(), v1.getCommodities()));
        }
        
        // Route mapping and extract booking-level eta/etd (with timezone offset)
        if (v1.getRoutes() != null && !v1.getRoutes().isEmpty()) {
            v2.setRoute(toV2Route(v1.getRouteId(), v1.getRoutes()));
            
            // Extract ETD from first leg (departure from POL) - preserves timezone
            RouteDTO firstLeg = v1.getRoutes().stream()
                .filter(r -> r.getLegNo() != null)
                .min((a, b) -> a.getLegNo().compareTo(b.getLegNo()))
                .orElse(v1.getRoutes().get(0));
            if (firstLeg.getEtdDate() != null) {
                v2.setEtd(firstLeg.getEtdDate());
            }
            
            // Extract ETA from last leg (arrival at POD) - preserves timezone
            RouteDTO lastLeg = v1.getRoutes().stream()
                .filter(r -> r.getLegNo() != null)
                .max((a, b) -> a.getLegNo().compareTo(b.getLegNo()))
                .orElse(v1.getRoutes().get(v1.getRoutes().size() - 1));
            if (lastLeg.getEtaDate() != null) {
                v2.setEta(lastLeg.getEtaDate());
            }
        }
        
        // Files mapping - always return array (empty if no files) per OpenAPI spec
        if (v1.getFiles() != null && !v1.getFiles().isEmpty()) {
            v2.setFiles(v1.getFiles().stream()
                .map(this::toV2Attachment)
                .collect(Collectors.toList()));
        } else {
            v2.setFiles(new ArrayList<>());
        }
        
        return v2;
    }

    /**
     * Convert V1 CustomerDTO to V2 BookingCustomerV2
     */
    public BookingCustomerV2 toV2Customer(CustomerDTO v1) {
        if (v1 == null) return null;

        BookingCustomerV2 v2 = new BookingCustomerV2();
        
        v2.setType(v1.getCustomerType());
        v2.setCode(v1.getCustomerCode());
        v2.setName(v1.getCustomerName());
        v2.setTelephone(v1.getTelephone());
        v2.setEmail(v1.getEmail());
        
        // Build nested address
        AddressV2 address = new AddressV2();
        List<String> lines = new ArrayList<>();
        if (v1.getAddress() != null && !v1.getAddress().trim().isEmpty()) {
            lines.add(v1.getAddress());
        }
        if (v1.getAddress1() != null && !v1.getAddress1().trim().isEmpty()) {
            lines.add(v1.getAddress1());
        }
        address.setLines(lines.isEmpty() ? null : lines);
        address.setLocality(v1.getCity());
        address.setPostalCode(v1.getZipCode());
        address.setCountryCode(v1.getCountryId());
        
        v2.setAddress(address);
        
        return v2;
    }

    /**
     * Convert V1 containers and commodities to V2 containers with nested commodities
     */
    public List<BookingContainerV2> toV2Containers(List<ContainerDTO> v1Containers, List<CommodityDTO> v1Commodities) {
        if (v1Containers == null) return new ArrayList<>();

        List<BookingContainerV2> v2Containers = new ArrayList<>();
        
        for (ContainerDTO v1Container : v1Containers) {
            BookingContainerV2 v2Container = new BookingContainerV2();
            
            v2Container.setId(v1Container.getContainerNo());
            v2Container.setQuantity(v1Container.getEquipmentQty());
            v2Container.setType(v1Container.getEquipmentType());
            v2Container.setCode(v1Container.getEquipmentSize());
            v2Container.setStatus(v1Container.getEquipmentStatus());
            
            // Boolean flags
            v2Container.setIsOOG(yesNoToBool(v1Container.getOogFlag()));
            v2Container.setIsSOC(yesNoToBool(v1Container.getSocFlag()));
            v2Container.setIsDG(yesNoToBool(v1Container.getDgFlag()));
            v2Container.setIsNOR(yesNoToBool(v1Container.getNor()));
            
            // Tare weight
            if (v1Container.getTareWeight() != null) {
                v2Container.setTareWeight(v1Container.getTareWeight().doubleValue());
            }
            
            // Gross weight
            if (v1Container.getGrossWeight() != null) {
                v2Container.setGrossWeight(v1Container.getGrossWeight().doubleValue());
            }
            
            // Reefer settings (enum pass-through)
            if (v1Container.getReefer() != null) {
                v2Container.setReeferSettings(
                        com.solverminds.booking.util.ReeferMappingHelper.toV2Reefer(v1Container.getReefer()));
            }
            
            // Find and nest commodities for this container
            // Match by cargoNo (conid) - primary FK - or containerNo when both present
            // Pass container's cargoWeight to commodities (since cargoWeight is stored at container level in ecombooking_cargo)
            if (v1Commodities != null) {
                Double containerCargoWeight = v1Container.getCargoWeight() != null 
                    ? v1Container.getCargoWeight().doubleValue() : null;
                YesNoFlag containerDgFlag = v1Container.getDgFlag();
                WeightUnit containerWeightUnitEnum = v1Container.getWeightUnit();
                List<BookingContainerCommodityV2> nestedCommodities = v1Commodities.stream()
                    .filter(c -> matchesContainer(c, v1Container))
                    .map(c -> toV2CommodityWithContainerWeight(c, containerCargoWeight, containerWeightUnitEnum, containerDgFlag))
                    .collect(Collectors.toList());
                v2Container.setCommodities(nestedCommodities);
            }
            
            v2Containers.add(v2Container);
        }
        
        return v2Containers;
    }

    /**
     * Check if commodity belongs to the given container.
     * Matches by cargoNo (conid) - primary FK - or containerNo when both non-null.
     */
    private boolean matchesContainer(CommodityDTO commodity, ContainerDTO container) {
        if (commodity == null || container == null) return false;
        // Primary: match by cargoNo (conid) - FK relationship
        if (commodity.getCargoNo() != null && container.getCargoNo() != null
                && commodity.getCargoNo().equals(container.getCargoNo())) {
            return true;
        }
        // Fallback: match by containerNo (containerno)
        if (commodity.getContainerNo() != null && container.getContainerNo() != null
                && commodity.getContainerNo().equals(container.getContainerNo())) {
            return true;
        }
        return false;
    }

    /**
     * Convert V1 CommodityDTO to V2 BookingContainerCommodityV2 with container's cargoWeight
     * The cargoWeight is stored at the container level (ecombooking_cargo) and passed to commodities.
     * When container dgFlag is N, returns empty object {} for nested dangerous goods (V2: dangerousGoodsInfo).
     */
    public BookingContainerCommodityV2 toV2CommodityWithContainerWeight(CommodityDTO v1, Double containerCargoWeight, WeightUnit containerWeightUnit, YesNoFlag containerDgFlag) {
        if (v1 == null) return null;

        BookingContainerCommodityV2 v2 = new BookingContainerCommodityV2();
        
        // commodityGroup (string) preserves leading zeros e.g. "09"
        v2.setCommodityGroup(v1.getCommodityGroup() != null && !v1.getCommodityGroup().trim().isEmpty() ? v1.getCommodityGroup().trim() : null);
        v2.setHsCode(v1.getHsCode());
        v2.setPackageType(v1.getPackageType());
        v2.setPackageQuantity(v1.getPackageQty());
        
        if (v1.getCargoVolume() != null) {
            v2.setCargoVolume(v1.getCargoVolume());
        }
        
        // Volume and weight units (enum pass-through; use commodity's or container's)
        v2.setCargoVolumeUnit(v1.getVolumnUnit());
        v2.setNetWeight(v1.getNettwt());
        v2.setBuyerCode(v1.getBuyerCode());
        v2.setBuyerName(v1.getBuyerName());
        v2.setSellerCode(v1.getSellerCode());
        v2.setSellerName(v1.getSellerName());
        v2.setCommodityHBLNo(v1.getCommodityHBLNo());
        if (v1.getItemWeight() != null) {
            v2.setCargoWeight(v1.getItemWeight().doubleValue());
            v2.setCargoWeightUnit(v1.getItemWeightUnit());
        } else {
            v2.setCargoWeight(containerCargoWeight);
            v2.setCargoWeightUnit(v1.getItemWeightUnit() != null ? v1.getItemWeightUnit() : containerWeightUnit);
        }
        
        // Dangerous goods - when dgFlag is N, return empty object {}; when Y, prefer response field then list
        if (containerDgFlag == YesNoFlag.Y) {
            if (v1.getDangerousGoods() != null) {
                v2.setDangerousGoodsInfo(copyDangerousGoodsInfo(v1.getDangerousGoods()));
            } else {
                v2.setDangerousGoodsInfo(new DangerousGoodsInfoV2());
            }
        } else {
            v2.setDangerousGoodsInfo(new DangerousGoodsInfoV2());
        }
        
        return v2;
    }

    /**
     * Convert V1 CommodityDTO to V2 BookingContainerCommodityV2
     */
    public BookingContainerCommodityV2 toV2Commodity(CommodityDTO v1) {
        return toV2CommodityWithContainerWeight(v1, null, WeightUnit.K, null);
    }

    /**
     * Convert V1 DangerousGoodsDTO to V2 DangerousGoodsInfoV2
     */
    public DangerousGoodsInfoV2 toV2DangerousGoods(DangerousGoodsDTO v1) {
        return dangerousGoodsMapper.toV2(v1);
    }

    /**
     * Convert V1 routes to V2 BookingRouteV2
     */
    public BookingRouteV2 toV2Route(Integer routeId, List<RouteDTO> v1Routes) {
        if (v1Routes == null || v1Routes.isEmpty()) return null;

        BookingRouteV2 v2 = new BookingRouteV2();
        v2.setId(routeId);
        
        List<BookingRouteLegV2> legs = v1Routes.stream()
            .map(this::toV2RouteLeg)
            .collect(Collectors.toList());
        v2.setLegs(legs);
        
        return v2;
    }

    /**
     * Convert V1 RouteDTO to V2 BookingRouteLegV2
     */
    public BookingRouteLegV2 toV2RouteLeg(RouteDTO v1) {
        if (v1 == null) return null;

        BookingRouteLegV2 v2 = new BookingRouteLegV2();
        
        v2.setServiceCode(v1.getServiceCode());
        v2.setVesselCode(v1.getVesselCode());
        v2.setVoyageCode(v1.getVoyageCode());
        v2.setLoadPortId(v1.getLoadPortId());
        v2.setLoadTerminalId(v1.getLoadTerminal());
        v2.setDischargePortId(v1.getDischargePortId());
        v2.setDischargeTerminalId(v1.getDischargeTerminal());
        v2.setGroupCallId(v1.getGroupCallId());
        v2.setLoadCallId(v1.getLoadCallId());
        v2.setDischargeCallId(v1.getDischargeCallId());
        v2.setEta(v1.getEtaDate());  // Preserve OffsetDateTime with timezone
        v2.setEtd(v1.getEtdDate());  // Preserve OffsetDateTime with timezone
        
        // Service type, bounds, load/discharge types (enum pass-through)
        v2.setServiceType(v1.getServiceType());
        v2.setLoadBound(v1.getLoadBound());
        v2.setDischargeBound(v1.getDischargeBound());
        v2.setLoadType(v1.getLoadType());
        v2.setDischargeType(v1.getDischType());
        
        return v2;
    }

    // ==================== Enum Mapping Helpers ====================

    private YesNoFlag boolToYesNo(Boolean value) {
        if (value == null) return YesNoFlag.N;
        return value ? YesNoFlag.Y : YesNoFlag.N;
    }

    private Boolean yesNoToBool(YesNoFlag flag) {
        if (flag == null) return false;
        return flag == YesNoFlag.Y;
    }

    /**
     * Helper method to get port name from cache.
     * @param portCode Port code
     * @return Port name or null if not found
     */
    private String getPortNameFromCache(String portCode) {
        if (portCode == null || portCode.trim().isEmpty()) {
            return null;
        }
        return masterDataCacheService.getPortName(portCode);
    }

    /**
     * Convert V1 FileAttachmentDTO to V2 BookingAttachmentV2
     */
    public BookingAttachmentV2 toV2Attachment(FileAttachmentDTO v1) {
        if (v1 == null) return null;

        BookingAttachmentV2 v2 = new BookingAttachmentV2();
        v2.setCategory(v1.getFileCategory());
        v2.setName(v1.getFileName());
        v2.setSize(v1.getFileSize() != null ? v1.getFileSize() : 0L);
        return v2;
    }
}

