package com.solverminds.booking.entity;

import javax.persistence.*;
import java.util.Objects;

/**
 * Reefer settings entity for refrigerated containers.
 * Table: ecombooking_refer
 * Composite PK: ebookno, conid
 */
@Entity
@Table(name = "ecombooking_refer")
public class EcomBookingReefer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "ebookno", length = 20, nullable = false)
    private String ebookno;

    @Column(name = "conid", nullable = false)
    private Integer conid;

    @Column(name = "itemno")
    private Integer itemno;

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

    public EcomBookingReefer() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEbookno() {
        return ebookno;
    }

    public void setEbookno(String ebookno) {
        this.ebookno = ebookno;
    }

    public Integer getConid() {
        return conid;
    }

    public void setConid(Integer conid) {
        this.conid = conid;
    }

    public Integer getItemno() {
        return itemno;
    }

    public void setItemno(Integer itemno) {
        this.itemno = itemno;
    }

    public String getContainerno() {
        return containerno;
    }

    public void setContainerno(String containerno) {
        this.containerno = containerno;
    }

    public Float getRefSetTemp() {
        return refSetTemp;
    }

    public void setRefSetTemp(Float refSetTemp) {
        this.refSetTemp = refSetTemp;
    }

    public Float getRefMinTemp() {
        return refMinTemp;
    }

    public void setRefMinTemp(Float refMinTemp) {
        this.refMinTemp = refMinTemp;
    }

    public Float getRefMaxTemp() {
        return refMaxTemp;
    }

    public void setRefMaxTemp(Float refMaxTemp) {
        this.refMaxTemp = refMaxTemp;
    }

    public String getRefTempUnit() {
        return refTempUnit;
    }

    public void setRefTempUnit(String refTempUnit) {
        this.refTempUnit = refTempUnit;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public Boolean getHumidityFlag() {
        return humidityFlag;
    }

    public void setHumidityFlag(Boolean humidityFlag) {
        this.humidityFlag = humidityFlag;
    }

    public String getDrains() {
        return drains;
    }

    public void setDrains(String drains) {
        this.drains = drains;
    }

    public String getCo2Value() {
        return co2Value;
    }

    public void setCo2Value(String co2Value) {
        this.co2Value = co2Value;
    }

    public String getO2Value() {
        return o2Value;
    }

    public void setO2Value(String o2Value) {
        this.o2Value = o2Value;
    }

    public String getVentPercentage() {
        return ventPercentage;
    }

    public void setVentPercentage(String ventPercentage) {
        this.ventPercentage = ventPercentage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EcomBookingReefer)) return false;
        EcomBookingReefer that = (EcomBookingReefer) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "EcomBookingReefer{" +
                "id=" + id +
                ", containerno='" + containerno + '\'' +
                ", refSetTemp=" + refSetTemp +
                '}';
    }
}
