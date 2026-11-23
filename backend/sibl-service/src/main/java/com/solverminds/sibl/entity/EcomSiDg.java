package com.solverminds.sibl.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * Entity for ecomSI_dg table (Dangerous Goods)
 * Insert only if commodity is hazardous: "Y".equals(hazardous)
 */
@Entity
@Table(name = "ecomSI_dg", schema = "dbo")
public class EcomSiDg implements Serializable {

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

    @Column(name = "itemno", nullable = false)
    private Integer itemno;

    @Column(name = "MarinePollutant", length = 1)
    private String marinePollutant;

    @Column(name = "unNo", length = 10)
    private String unNo;

    @Column(name = "class", length = 10)
    private String imdgClass;

    @Column(name = "FlashPt")
    private Double flashPt;

    @Column(name = "FlashPtUnit", length = 1)
    private String flashPtUnit;

    @Column(name = "imocontact", length = 50)
    private String imoContact;

    @Column(name = "imophone", length = 50)
    private String imoPhone;

    // ===== Constructors =====
    public EcomSiDg() {}

    // ===== Getters & Setters =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String geteSINo() { return eSINo; }
    public void seteSINo(String eSINo) { this.eSINo = eSINo; }

    public Integer getConId() { return conId; }
    public void setConId(Integer conId) { this.conId = conId; }

    public String getContainerno() { return containerno; }
    public void setContainerno(String containerno) { this.containerno = containerno; }

    public Integer getItemno() { return itemno; }
    public void setItemno(Integer itemno) { this.itemno = itemno; }

    public String getMarinePollutant() { return marinePollutant; }
    public void setMarinePollutant(String marinePollutant) { this.marinePollutant = marinePollutant; }

    public String getUnNo() { return unNo; }
    public void setUnNo(String unNo) { this.unNo = unNo; }

    public String getImdgClass() { return imdgClass; }
    public void setImdgClass(String imdgClass) { this.imdgClass = imdgClass; }

    public Double getFlashPt() { return flashPt; }
    public void setFlashPt(Double flashPt) { this.flashPt = flashPt; }

    public String getFlashPtUnit() { return flashPtUnit; }
    public void setFlashPtUnit(String flashPtUnit) { this.flashPtUnit = flashPtUnit; }

    public String getImoContact() { return imoContact; }
    public void setImoContact(String imoContact) { this.imoContact = imoContact; }

    public String getImoPhone() { return imoPhone; }
    public void setImoPhone(String imoPhone) { this.imoPhone = imoPhone; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EcomSiDg)) return false;
        EcomSiDg that = (EcomSiDg) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
