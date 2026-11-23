package com.solverminds.sibl.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * Entity for ecomSI_oog table (Out of Gauge)
 * Insert only if container is OOG: "Y".equals(oog)
 */
@Entity
@Table(name = "ecomSI_oog", schema = "dbo")
public class EcomSiOog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "eSINo", length = 20, nullable = false)
    private String eSINo;

    @Column(name = "conId", nullable = false)
    private Integer conId;

    @Column(name = "comItemNo", nullable = false)
    private Integer comItemNo;

    @Column(name = "containerno", length = 11)
    private String containerno;

    @Column(name = "leftft")
    private Double leftft;

    @Column(name = "rightft")
    private Double rightft;

    @Column(name = "forward")
    private Double forward;

    @Column(name = "Alt")
    private Double alt;

    @Column(name = "topft")
    private Double topft;

    @Column(name = "Unit", length = 1)
    private String unit;

    // ===== Constructors =====
    public EcomSiOog() {}

    // ===== Getters & Setters =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String geteSINo() { return eSINo; }
    public void seteSINo(String eSINo) { this.eSINo = eSINo; }

    public Integer getConId() { return conId; }
    public void setConId(Integer conId) { this.conId = conId; }

    public Integer getComItemNo() { return comItemNo; }
    public void setComItemNo(Integer comItemNo) { this.comItemNo = comItemNo; }

    public String getContainerno() { return containerno; }
    public void setContainerno(String containerno) { this.containerno = containerno; }

    public Double getLeftft() { return leftft; }
    public void setLeftft(Double leftft) { this.leftft = leftft; }

    public Double getRightft() { return rightft; }
    public void setRightft(Double rightft) { this.rightft = rightft; }

    public Double getForward() { return forward; }
    public void setForward(Double forward) { this.forward = forward; }

    public Double getAlt() { return alt; }
    public void setAlt(Double alt) { this.alt = alt; }

    public Double getTopft() { return topft; }
    public void setTopft(Double topft) { this.topft = topft; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EcomSiOog)) return false;
        EcomSiOog that = (EcomSiOog) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
