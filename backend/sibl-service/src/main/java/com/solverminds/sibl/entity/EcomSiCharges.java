package com.solverminds.sibl.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * Entity for ecomsi_charges table
 * Insert charges from JSON array (ChargeDetailsJSON)
 */
@Entity
@Table(name = "ecomsi_charges", schema = "dbo")
public class EcomSiCharges implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "esino", length = 20, nullable = false)
    private String esino;

    @Column(name = "chargeid", length = 3)
    private String chargeid;

    @Column(name = "payagencyid", length = 5)
    private String payagencyid;

    @Column(name = "custtypeid", length = 2)
    private String custtypeid;

    @Column(name = "prepaidcollect", length = 5)
    private String prepaidcollect;

    @Column(name = "eqpTypeId", length = 4)
    private String eqpTypeId;

    // ===== Constructors =====
    public EcomSiCharges() {}

    // ===== Getters & Setters =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEsino() { return esino; }
    public void setEsino(String esino) { this.esino = esino; }

    public String getChargeid() { return chargeid; }
    public void setChargeid(String chargeid) { this.chargeid = chargeid; }

    public String getPayagencyid() { return payagencyid; }
    public void setPayagencyid(String payagencyid) { this.payagencyid = payagencyid; }

    public String getCusttypeid() { return custtypeid; }
    public void setCusttypeid(String custtypeid) { this.custtypeid = custtypeid; }

    public String getPrepaidcollect() { return prepaidcollect; }
    public void setPrepaidcollect(String prepaidcollect) { this.prepaidcollect = prepaidcollect; }

    public String getEqpTypeId() { return eqpTypeId; }
    public void setEqpTypeId(String eqpTypeId) { this.eqpTypeId = eqpTypeId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EcomSiCharges)) return false;
        EcomSiCharges that = (EcomSiCharges) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
