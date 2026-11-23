package com.solverminds.sibl.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * Entity for ecomSI_reefer table
 * Insert only if container is Reefer AND NOT Non-Operating Reefer
 * Condition: "Y".equals(reefer) && !"Y".equals(nor)
 */
@Entity
@Table(name = "ecomSI_reefer", schema = "dbo")
public class EcomSiReefer implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "eSINo", length = 20, nullable = false)
    private String eSINo;

    @Column(name = "conId", nullable = false)
    private Integer conId;

    @Column(name = "containerno", length = 11)
    private String containerno;

    @Column(name = "ref_setTemp")
    private Float refSetTemp;

    @Column(name = "ref_minTemp")
    private Float refMinTemp;

    @Column(name = "ref_maxTemp")
    private Float refMaxTemp;

    @Column(name = "ref_tempUnit", length = 1)
    private String refTempUnit;

    @Column(name = "Humidity", length = 5)
    private String humidity;

    @Column(name = "Humidity_flag")
    private Boolean humidityFlag;

    @Column(name = "Drains", length = 1)
    private String drains;

    @Column(name = "CO2Value", length = 10)
    private String co2Value;

    @Column(name = "O2Value", length = 10)
    private String o2Value;

    @Column(name = "VentPercentage", length = 5)
    private String ventPercentage;

    // ===== Constructors =====
    public EcomSiReefer() {}

    // ===== Getters & Setters =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String geteSINo() { return eSINo; }
    public void seteSINo(String eSINo) { this.eSINo = eSINo; }

    public Integer getConId() { return conId; }
    public void setConId(Integer conId) { this.conId = conId; }

    public String getContainerno() { return containerno; }
    public void setContainerno(String containerno) { this.containerno = containerno; }

    public Float getRefSetTemp() { return refSetTemp; }
    public void setRefSetTemp(Float refSetTemp) { this.refSetTemp = refSetTemp; }

    public Float getRefMinTemp() { return refMinTemp; }
    public void setRefMinTemp(Float refMinTemp) { this.refMinTemp = refMinTemp; }

    public Float getRefMaxTemp() { return refMaxTemp; }
    public void setRefMaxTemp(Float refMaxTemp) { this.refMaxTemp = refMaxTemp; }

    public String getRefTempUnit() { return refTempUnit; }
    public void setRefTempUnit(String refTempUnit) { this.refTempUnit = refTempUnit; }

    public String getHumidity() { return humidity; }
    public void setHumidity(String humidity) { this.humidity = humidity; }

    public Boolean getHumidityFlag() { return humidityFlag; }
    public void setHumidityFlag(Boolean humidityFlag) { this.humidityFlag = humidityFlag; }

    public String getDrains() { return drains; }
    public void setDrains(String drains) { this.drains = drains; }

    public String getCo2Value() { return co2Value; }
    public void setCo2Value(String co2Value) { this.co2Value = co2Value; }

    public String getO2Value() { return o2Value; }
    public void setO2Value(String o2Value) { this.o2Value = o2Value; }

    public String getVentPercentage() { return ventPercentage; }
    public void setVentPercentage(String ventPercentage) { this.ventPercentage = ventPercentage; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EcomSiReefer)) return false;
        EcomSiReefer that = (EcomSiReefer) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
