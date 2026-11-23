package com.solverminds.sibl.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.svm.ecom.common.enums.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Booking/SI view response DTO for SIBL service.
 * Minimal structure for viewing booking/SI details.
 */
@Schema(name = "BookingRequestDTO", description = "Booking/SI view details")
public class BookingRequestDTO {
    
    @Schema(description = "Booking number", example = "BKG20240115001")
    private String bookingNo;
    
    // ===== SIBLMasterVO Parameters (in specified order) =====
    @Schema(description = "SI Number", example = "SIREQ2826")
    private String siNo;
    
    @Schema(description = "BL Number", example = "BL20240115001")
    private String blNo;
    
    @Schema(description = "Booking Number", example = "BKG20240115001")
    private String bookNo;
    
    @Schema(description = "Vessel", example = "MSC OSCAR")
    private String vessel;
    
    @Schema(description = "Voyage", example = "123E")
    private String voyage;
    
    @Schema(description = "Bound", example = "E")
    private Bound bound;
    
    @Schema(description = "Place of Acceptance", example = "INMAA")
    private String pla;
    
    @Schema(description = "Port of Loading", example = "INMAA")
    private String pol;
    
    @Schema(description = "Port of Discharge", example = "SGSIN")
    private String pod;
    
    @Schema(description = "Place of Delivery", example = "SGSIN")
    private String pld;
    
    @Schema(description = "PLA Description", example = "CHENNAI, INDIA")
    private String plaDesc;
    
    @Schema(description = "PLD Description", example = "SINGAPORE")
    private String pldDesc;
    
    @Schema(description = "PLA Header Name", example = "CHENNAI, INDIA")
    private String plaHeaderName;
    
    @Schema(description = "POL Header Name", example = "CHENNAI, INDIA")
    private String polHeaderName;
    
    @Schema(description = "POD Header Name", example = "SINGAPORE")
    private String podHeaderName;
    
    @Schema(description = "PLD Header Name", example = "SINGAPORE")
    private String pldHeaderName;
    
    @Schema(description = "Customer Code", example = "20001374")
    private String custCode;
    
    @Schema(description = "Created User", example = "admin")
    private String crUser;
    
    @Schema(description = "Created Date", example = "2024-01-15T10:30:00Z")
    private String crDate;
    
    @Schema(description = "Changed User", example = "admin")
    private String chgUser;
    
    @Schema(description = "Changed Date", example = "2024-01-16T14:20:00Z")
    private String chgDate;
    
    @Schema(description = "Edit BL Flag", example = "0")
    private String editBL;
    
    @Schema(description = "BL Version", example = "1")
    private String blVersion;
    
    @Schema(description = "Status of the SI: D (Draft), S (Submitted), A (Accepted), V (Declined)", example = "D")
    private SiStatus status;
    
    @Schema(description = "Customer Remarks", example = "Handle with care")
    private String custRemarks;
    
    // Legacy fields for backward compatibility
    @Schema(description = "Online SI number", example = "SIREQ2826")
    private String onlineSINo;
    
    @Schema(description = "Bill of Lading number", example = "BL20240115001")
    private String blNumber;
    
    @Schema(description = "Load port ID", example = "INMAA")
    private String loadPortId;
    
    @Schema(description = "Discharge port ID", example = "SGSIN")
    private String dischargePortId;

    @Schema(description = "Shipper party details")
    private Party shipper;
    
    @Schema(description = "Consignee party details")
    private Party consignee;
    
    @Schema(description = "Notify party details")
    private Party notifyParty;
    
    @Schema(description = "Forwarder Name", example = "ABC Forwarders Ltd")
    private String forwarderName;
    
    @Schema(description = "Forwarder Header", example = "ABC Forwarders Ltd, Address")
    private String forwarderHeader;
    
    @Schema(description = "Warehouse Name", example = "XYZ Warehouse Inc")
    private String warehouseName;
    
    @Schema(description = "Warehouse Header", example = "XYZ Warehouse Inc, Address")
    private String warehouseHeader;
    
    @Schema(description = "Container details")
    private List<ContainerSummary> containers;
    
    @Schema(description = "Commodity details")
    private List<CommoditySummary> commodities;
    
    @Schema(description = "Customer details")
    private List<CustomerSummary> customers;

    // ===== Constructors =====
    public BookingRequestDTO() {}

    // ===== Getters & Setters =====
    public String getBookingNo() { return bookingNo; }
    public void setBookingNo(String bookingNo) { this.bookingNo = bookingNo; }

    public String getOnlineSINo() { return onlineSINo; }
    public void setOnlineSINo(String onlineSINo) { this.onlineSINo = onlineSINo; }

    public String getBlNumber() { return blNumber; }
    public void setBlNumber(String blNumber) { this.blNumber = blNumber; }

    public SiStatus getStatus() { return status; }
    public void setStatus(SiStatus status) { this.status = status; }

    public String getLoadPortId() { return loadPortId; }
    public void setLoadPortId(String loadPortId) { this.loadPortId = loadPortId; }

    public String getDischargePortId() { return dischargePortId; }
    public void setDischargePortId(String dischargePortId) { this.dischargePortId = dischargePortId; }

    public Party getShipper() { return shipper; }
    public void setShipper(Party shipper) { this.shipper = shipper; }

    public Party getConsignee() { return consignee; }
    public void setConsignee(Party consignee) { this.consignee = consignee; }

    public Party getNotifyParty() { return notifyParty; }
    public void setNotifyParty(Party notifyParty) { this.notifyParty = notifyParty; }

    public List<ContainerSummary> getContainers() { return containers; }
    public void setContainers(List<ContainerSummary> containers) { this.containers = containers; }

    public List<CommoditySummary> getCommodities() { return commodities; }
    public void setCommodities(List<CommoditySummary> commodities) { this.commodities = commodities; }
    
    public List<CustomerSummary> getCustomers() { return customers; }
    public void setCustomers(List<CustomerSummary> customers) { this.customers = customers; }
    
    // ===== SIBLMasterVO Getters & Setters =====
    public String getSiNo() { return siNo; }
    public void setSiNo(String siNo) { this.siNo = siNo; }
    
    public String getBlNo() { return blNo; }
    public void setBlNo(String blNo) { this.blNo = blNo; }
    
    public String getBookNo() { return bookNo; }
    public void setBookNo(String bookNo) { this.bookNo = bookNo; }
    
    public String getVessel() { return vessel; }
    public void setVessel(String vessel) { this.vessel = vessel; }
    
    public String getVoyage() { return voyage; }
    public void setVoyage(String voyage) { this.voyage = voyage; }
    
    public Bound getBound() { return bound; }
    public void setBound(Bound bound) { this.bound = bound; }
    
    public String getPla() { return pla; }
    public void setPla(String pla) { this.pla = pla; }
    
    public String getPol() { return pol; }
    public void setPol(String pol) { this.pol = pol; }
    
    public String getPod() { return pod; }
    public void setPod(String pod) { this.pod = pod; }
    
    public String getPld() { return pld; }
    public void setPld(String pld) { this.pld = pld; }
    
    public String getPlaDesc() { return plaDesc; }
    public void setPlaDesc(String plaDesc) { this.plaDesc = plaDesc; }
    
    public String getPldDesc() { return pldDesc; }
    public void setPldDesc(String pldDesc) { this.pldDesc = pldDesc; }
    
    public String getPlaHeaderName() { return plaHeaderName; }
    public void setPlaHeaderName(String plaHeaderName) { this.plaHeaderName = plaHeaderName; }
    
    public String getPolHeaderName() { return polHeaderName; }
    public void setPolHeaderName(String polHeaderName) { this.polHeaderName = polHeaderName; }
    
    public String getPodHeaderName() { return podHeaderName; }
    public void setPodHeaderName(String podHeaderName) { this.podHeaderName = podHeaderName; }
    
    public String getPldHeaderName() { return pldHeaderName; }
    public void setPldHeaderName(String pldHeaderName) { this.pldHeaderName = pldHeaderName; }
    
    public String getCustCode() { return custCode; }
    public void setCustCode(String custCode) { this.custCode = custCode; }
    
    public String getCrUser() { return crUser; }
    public void setCrUser(String crUser) { this.crUser = crUser; }
    
    public String getCrDate() { return crDate; }
    public void setCrDate(String crDate) { this.crDate = crDate; }
    
    public String getChgUser() { return chgUser; }
    public void setChgUser(String chgUser) { this.chgUser = chgUser; }
    
    public String getChgDate() { return chgDate; }
    public void setChgDate(String chgDate) { this.chgDate = chgDate; }
    
    public String getEditBL() { return editBL; }
    public void setEditBL(String editBL) { this.editBL = editBL; }
    
    public String getBlVersion() { return blVersion; }
    public void setBlVersion(String blVersion) { this.blVersion = blVersion; }
    
    public String getCustRemarks() { return custRemarks; }
    public void setCustRemarks(String custRemarks) { this.custRemarks = custRemarks; }
    
    public String getForwarderName() { return forwarderName; }
    public void setForwarderName(String forwarderName) { this.forwarderName = forwarderName; }
    
    public String getForwarderHeader() { return forwarderHeader; }
    public void setForwarderHeader(String forwarderHeader) { this.forwarderHeader = forwarderHeader; }
    
    public String getWarehouseName() { return warehouseName; }
    public void setWarehouseName(String warehouseName) { this.warehouseName = warehouseName; }
    
    public String getWarehouseHeader() { return warehouseHeader; }
    public void setWarehouseHeader(String warehouseHeader) { this.warehouseHeader = warehouseHeader; }

    // ===== Nested Classes =====
    @Schema(name = "Party", description = "Party information")
    public static class Party {
      
        
        @Schema(description = "Party name", example = "Acme Shipper")
        private String name;
        
        @Schema(description = "Primary address", example = "513B, Building A,Free Trade Financial Center")
        private String address;
        
        

        public Party() {}

    

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        
       
    }

    @Schema(name = "ContainerSummary", description = "Container summary information")
    public static class ContainerSummary {
        @Schema(description = "Cargo number", example = "1")
        private Integer cargoNo;
        
        @Schema(description = "Container number", example = "AASU1205051")
        private String containerNo;
        
        @Schema(description = "Equipment size", example = "20GP")
        private String eqpSize;
        
        @Schema(description = "Equipment quantity", example = "1")
        private Integer equipmentQty;
        
        @Schema(description = "Tare weight", example = "111")
        private String tareWeight;
        
        @Schema(description = "Cargo weight", example = "200")
        private String cargoWeight;
        
        @Schema(description = "Gross weight", example = "311")
        private String grossweight;
        
        @Schema(description = "Weight unit", example = "K")
        private WeightUnit weightUnit;
        
        @Schema(description = "Ref/NOR flag", example = "N")
        private YesNoFlag ref_nor;
        
        @Schema(description = "NOR flag", example = "N")
        private YesNoFlag nor;
        
        @Schema(description = "SOC flag", example = "N")
        private YesNoFlag socFlag;
        
        @Schema(description = "Carrier seal", example = "SEAL123")
        private String carrierSeal;
        
        @Schema(description = "Shipper seal", example = "SEAL456")
        private String shipperSeal;
        
        @Schema(description = "Equipment type", example = "D")
        private EquipmentType equipmentType;
        
        @Schema(description = "Equipment type status", example = "F")
        private EquipmentStatus equipmentTypestatus;
        
        @Schema(description = "Out of gauge flag", example = "N")
        private YesNoFlag oogFlag;
        
        @Schema(description = "Dangerous goods flag", example = "N")
        private YesNoFlag dangerousGoodsflag;
        
        @Schema(description = "Reefer container settings (only if refer = 'Y' AND nor != 'Y')")
        private ReeferSummary reefer;
        
        @Schema(description = "Dangerous goods details (only if hazardous = 'y')")
        private DangerousGoodsSummary dangerousGoods;

        public ContainerSummary() {}

        public Integer getCargoNo() { return cargoNo; }
        public void setCargoNo(Integer cargoNo) { this.cargoNo = cargoNo; }

        public String getContainerNo() { return containerNo; }
        public void setContainerNo(String containerNo) { this.containerNo = containerNo; }
        
        public Integer getEquipmentQty() { return equipmentQty; }
        public void setEquipmentQty(Integer equipmentQty) { this.equipmentQty = equipmentQty; }

        public String getEqpSize() { return eqpSize; }
        public void setEqpSize(String eqpSize) { this.eqpSize = eqpSize; }
        
        public String getTareWeight() { return tareWeight; }
        public void setTareWeight(String tareWeight) { this.tareWeight = tareWeight; }
        
        public String getCargoWeight() { return cargoWeight; }
        public void setCargoWeight(String cargoWeight) { this.cargoWeight = cargoWeight; }
        
        public String getGrossweight() { return grossweight; }
        public void setGrossweight(String grossweight) { this.grossweight = grossweight; }
        
        public WeightUnit getWeightUnit() { return weightUnit; }
        public void setWeightUnit(WeightUnit weightUnit) { this.weightUnit = weightUnit; }
        
        public YesNoFlag getRef_nor() { return ref_nor; }
        public void setRef_nor(YesNoFlag ref_nor) { this.ref_nor = ref_nor; }
        
        public YesNoFlag getNor() { return nor; }
        public void setNor(YesNoFlag nor) { this.nor = nor; }
        
        public YesNoFlag getSocFlag() { return socFlag; }
        public void setSocFlag(YesNoFlag socFlag) { this.socFlag = socFlag; }
        
        public String getCarrierSeal() { return carrierSeal; }
        public void setCarrierSeal(String carrierSeal) { this.carrierSeal = carrierSeal; }
        
        public String getShipperSeal() { return shipperSeal; }
        public void setShipperSeal(String shipperSeal) { this.shipperSeal = shipperSeal; }
        
        public EquipmentType getEquipmentType() { return equipmentType; }
        public void setEquipmentType(EquipmentType equipmentType) { this.equipmentType = equipmentType; }
        
        public EquipmentStatus getEquipmentTypestatus() { return equipmentTypestatus; }
        public void setEquipmentTypestatus(EquipmentStatus equipmentTypestatus) { this.equipmentTypestatus = equipmentTypestatus; }
        
        public YesNoFlag getOogFlag() { return oogFlag; }
        public void setOogFlag(YesNoFlag oogFlag) { this.oogFlag = oogFlag; }
        
        public YesNoFlag getDangerousGoodsflag() { return dangerousGoodsflag; }
        public void setDangerousGoodsflag(YesNoFlag dangerousGoodsflag) { this.dangerousGoodsflag = dangerousGoodsflag; }
        
        public ReeferSummary getReefer() { return reefer; }
        public void setReefer(ReeferSummary reefer) { this.reefer = reefer; }
        
        public DangerousGoodsSummary getDangerousGoods() { return dangerousGoods; }
        public void setDangerousGoods(DangerousGoodsSummary dangerousGoods) { this.dangerousGoods = dangerousGoods; }
    }

    @Schema(name = "CommoditySummary", description = "Commodity summary information")
    public static class CommoditySummary {
        @Schema(description = "Cargo number", example = "1")
        private Integer cargoNo;
        
        @Schema(description = "Container number", example = "20DCTBN1")
        private String containerNo;
        
        @Schema(description = "Item number", example = "1")
        private Integer itemNo;
        
        @Schema(description = "Commodity group", example = "90")
        private String commodityGroup;
        
        @Schema(description = "HS Code", example = "9004909000")
        private String hsCode;
        
        @Schema(description = "Commodity description", example = "COMMODITY FOR COFFEE OR TEA")
        private String description;
        
        @JsonProperty("package")
        @Schema(description = "Package type", example = "CMPK")
        private String packageType;
        
        @Schema(description = "Package quantity", example = "2")
        private String packageQty;
        
        @Schema(description = "Item weight", example = "200")
        private String itemWeight;
        
        @Schema(description = "Item weight unit", example = "K")
        private WeightUnit itemWeightUnit;
        
        @Schema(description = "Item volume", example = "0")
        private String itemVolume;
        
        @Schema(description = "Volume unit", example = "M")
        private VolumeUnit volumnUnit;
        
        @Schema(description = "Net weight", example = "7500")
        private String nettwt;
        
        @Schema(description = "Commodity HBL Number", example = "HBL12345")
        private String commodityHBLNo;
        
        @Schema(description = "Buyer code", example = "")
        private String buyerCode;
        
        @Schema(description = "Seller code", example = "")
        private String sellerCode;
        
        @Schema(description = "Buyer name", example = "")
        private String buyerName;
        
        @Schema(description = "Seller name", example = "")
        private String sellerName;
        
        @Schema(description = "Out of gauge dimensions (only if OOG = 'Y')")
        private OogSummary oog;
        
        @Schema(description = "Dangerous goods details (only if hazardous = 'y')")
        private List<DangerousGoodsSummary> dangerousGoods;

        public CommoditySummary() {}

        public Integer getCargoNo() { return cargoNo; }
        public void setCargoNo(Integer cargoNo) { this.cargoNo = cargoNo; }
        
        public String getContainerNo() { return containerNo; }
        public void setContainerNo(String containerNo) { this.containerNo = containerNo; }
        
        public Integer getItemNo() { return itemNo; }
        public void setItemNo(Integer itemNo) { this.itemNo = itemNo; }
        
        public String getCommodityGroup() { return commodityGroup; }
        public void setCommodityGroup(String commodityGroup) { this.commodityGroup = commodityGroup; }

        public String getHsCode() { return hsCode; }
        public void setHsCode(String hsCode) { this.hsCode = hsCode; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getPackageType() { return packageType; }
        public void setPackageType(String packageType) { this.packageType = packageType; }
        
        public String getPackageQty() { return packageQty; }
        public void setPackageQty(String packageQty) { this.packageQty = packageQty; }
        
        public String getItemWeight() { return itemWeight; }
        public void setItemWeight(String itemWeight) { this.itemWeight = itemWeight; }
        
        public WeightUnit getItemWeightUnit() { return itemWeightUnit; }
        public void setItemWeightUnit(WeightUnit itemWeightUnit) { this.itemWeightUnit = itemWeightUnit; }
        
        public String getItemVolume() { return itemVolume; }
        public void setItemVolume(String itemVolume) { this.itemVolume = itemVolume; }
        
        public VolumeUnit getVolumnUnit() { return volumnUnit; }
        public void setVolumnUnit(VolumeUnit volumnUnit) { this.volumnUnit = volumnUnit; }
        
        public String getNettwt() { return nettwt; }
        public void setNettwt(String nettwt) { this.nettwt = nettwt; }
        
        public String getCommodityHBLNo() { return commodityHBLNo; }
        public void setCommodityHBLNo(String commodityHBLNo) { this.commodityHBLNo = commodityHBLNo; }
        
        public String getBuyerCode() { return buyerCode; }
        public void setBuyerCode(String buyerCode) { this.buyerCode = buyerCode; }
        
        public String getSellerCode() { return sellerCode; }
        public void setSellerCode(String sellerCode) { this.sellerCode = sellerCode; }
        
        public String getBuyerName() { return buyerName; }
        public void setBuyerName(String buyerName) { this.buyerName = buyerName; }
        
        public String getSellerName() { return sellerName; }
        public void setSellerName(String sellerName) { this.sellerName = sellerName; }
        
        public OogSummary getOog() { return oog; }
        public void setOog(OogSummary oog) { this.oog = oog; }
        
        public List<DangerousGoodsSummary> getDangerousGoods() { return dangerousGoods; }
        public void setDangerousGoods(List<DangerousGoodsSummary> dangerousGoods) { this.dangerousGoods = dangerousGoods; }
    }
    
    @Schema(name = "CustomerSummary", description = "Customer information")
    public static class CustomerSummary {
        @Schema(description = "Customer role code", example = "BP")
        private CustomerType customerType;
        
        @Schema(description = "Customer code", example = "20001374")
        private String customerCode;
        
        @Schema(description = "Country ID/code", example = "CN")
        private String countryId;
        
        @Schema(description = "Customer name", example = "ECOM TESTING")
        private String customerName;
        
        @Schema(description = "Primary address", example = "513B, Building A,Free Trade Financial Center")
        private String address;
        
        @Schema(description = "Additional address line", example = "Xiamen Area of China")
        private String address1;
        
        @Schema(description = "City", example = "Beijiao, China")
        private String city;
        
        @Schema(description = "ZIP/postal code", example = "603103")
        private String zipCode;
        
        @Schema(description = "Telephone number", example = "86-38-00138000")
        private String telephone;
        
        @Schema(description = "Email address", example = "email@solverminds.com")
        private String email;

        public CustomerSummary() {}

        public CustomerType getCustomerType() { return customerType; }
        public void setCustomerType(CustomerType customerType) { this.customerType = customerType; }
        
        public String getCustomerCode() { return customerCode; }
        public void setCustomerCode(String customerCode) { this.customerCode = customerCode; }
        
        public String getCountryId() { return countryId; }
        public void setCountryId(String countryId) { this.countryId = countryId; }
        
        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }
        
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        
        public String getAddress1() { return address1; }
        public void setAddress1(String address1) { this.address1 = address1; }
        
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        
        public String getZipCode() { return zipCode; }
        public void setZipCode(String zipCode) { this.zipCode = zipCode; }
        
        public String getTelephone() { return telephone; }
        public void setTelephone(String telephone) { this.telephone = telephone; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
    
    @Schema(name = "ReeferSummary", description = "Reefer container temperature settings")
    public static class ReeferSummary {
        @Schema(description = "Cargo number", example = "1")
        private Integer cargoNo;
        
        @Schema(description = "Container number", example = "20DCTBN1")
        private String containerNo;
        
        @Schema(description = "Item number", example = "1")
        private Integer itemNo;
        
        @Schema(description = "Set temperature", example = "8")
        private Double setTemp;
        
        @Schema(description = "Min temperature", example = "-5")
        private Double minTemp;
        
        @Schema(description = "Max temperature", example = "10")
        private Double maxTemp;
        
        @Schema(description = "Temperature unit", example = "C")
        private TemperatureUnit tempUnit;

        public ReeferSummary() {}

        public Integer getCargoNo() { return cargoNo; }
        public void setCargoNo(Integer cargoNo) { this.cargoNo = cargoNo; }

        public String getContainerNo() { return containerNo; }
        public void setContainerNo(String containerNo) { this.containerNo = containerNo; }

        public Integer getItemNo() { return itemNo; }
        public void setItemNo(Integer itemNo) { this.itemNo = itemNo; }

        public Double getSetTemp() { return setTemp; }
        public void setSetTemp(Double setTemp) { this.setTemp = setTemp; }

        public Double getMinTemp() { return minTemp; }
        public void setMinTemp(Double minTemp) { this.minTemp = minTemp; }

        public Double getMaxTemp() { return maxTemp; }
        public void setMaxTemp(Double maxTemp) { this.maxTemp = maxTemp; }

        public TemperatureUnit getTempUnit() { return tempUnit; }
        public void setTempUnit(TemperatureUnit tempUnit) { this.tempUnit = tempUnit; }
    }
    
    @Schema(name = "DangerousGoodsSummary", description = "Dangerous goods information")
    public static class DangerousGoodsSummary {
        @Schema(description = "Cargo number", example = "1")
        private Integer cargoNo;
        
        @Schema(description = "Container number", example = "20DCTBN1")
        private String containerNo;
        
        @Schema(description = "Item number", example = "1")
        private Integer itemNo;
        
        @Schema(description = "Marine pollutant flag", example = "N")
        private YesNoFlag marinePollutant;
        
        @Schema(description = "UN Number", example = "3480")
        private String unNo;
        
        @Schema(description = "Flash point", example = "32")
        private Double flashPt;
        
        @Schema(description = "Flash point unit: C (Celsius), F (Fahrenheit)", example = "C")
        private TemperatureUnit flashPtUnit;
        
        @Schema(description = "Class code", example = "9")
        private String classCode;

        public DangerousGoodsSummary() {}

        public Integer getCargoNo() { return cargoNo; }
        public void setCargoNo(Integer cargoNo) { this.cargoNo = cargoNo; }

        public String getContainerNo() { return containerNo; }
        public void setContainerNo(String containerNo) { this.containerNo = containerNo; }

        public Integer getItemNo() { return itemNo; }
        public void setItemNo(Integer itemNo) { this.itemNo = itemNo; }

        public YesNoFlag getMarinePollutant() { return marinePollutant; }
        public void setMarinePollutant(YesNoFlag marinePollutant) { this.marinePollutant = marinePollutant; }

        public String getUnNo() { return unNo; }
        public void setUnNo(String unNo) { this.unNo = unNo; }

        public Double getFlashPt() { return flashPt; }
        public void setFlashPt(Double flashPt) { this.flashPt = flashPt; }

        public TemperatureUnit getFlashPtUnit() { return flashPtUnit; }
        public void setFlashPtUnit(TemperatureUnit flashPtUnit) { this.flashPtUnit = flashPtUnit; }

        public String getClassCode() { return classCode; }
        public void setClassCode(String classCode) { this.classCode = classCode; }
    }
    
    @Schema(name = "OogSummary", description = "Out of gauge dimensions")
    public static class OogSummary {
        @Schema(description = "Cargo number", example = "1")
        private Integer cargoNo;
        
        @Schema(description = "Container number", example = "20DCTBN1")
        private String containerNo;
        
        @Schema(description = "Item number", example = "1")
        private Integer itemNo;
        
        @Schema(description = "Left dimension (ft)", example = "0.5")
        private Double itemLeft;
        
        @Schema(description = "Right dimension (ft)", example = "0.5")
        private Double itemRight;
        
        @Schema(description = "Forward dimension (ft)", example = "1.0")
        private Double itemForward;
        
        @Schema(description = "Aft dimension (ft)", example = "0.5")
        private Double itemAft;
        
        @Schema(description = "Top dimension (ft)", example = "0.5")
        private Double itemTop;
        
        @Schema(description = "Length unit", example = "F")
        private LengthUnit itemLengthUnit;

        public OogSummary() {}

        public Integer getCargoNo() { return cargoNo; }
        public void setCargoNo(Integer cargoNo) { this.cargoNo = cargoNo; }

        public String getContainerNo() { return containerNo; }
        public void setContainerNo(String containerNo) { this.containerNo = containerNo; }

        public Integer getItemNo() { return itemNo; }
        public void setItemNo(Integer itemNo) { this.itemNo = itemNo; }

        public Double getItemLeft() { return itemLeft; }
        public void setItemLeft(Double itemLeft) { this.itemLeft = itemLeft; }

        public Double getItemRight() { return itemRight; }
        public void setItemRight(Double itemRight) { this.itemRight = itemRight; }

        public Double getItemForward() { return itemForward; }
        public void setItemForward(Double itemForward) { this.itemForward = itemForward; }

        public Double getItemAft() { return itemAft; }
        public void setItemAft(Double itemAft) { this.itemAft = itemAft; }

        public Double getItemTop() { return itemTop; }
        public void setItemTop(Double itemTop) { this.itemTop = itemTop; }

        public LengthUnit getItemLengthUnit() { return itemLengthUnit; }
        public void setItemLengthUnit(LengthUnit itemLengthUnit) { this.itemLengthUnit = itemLengthUnit; }
    }
}
