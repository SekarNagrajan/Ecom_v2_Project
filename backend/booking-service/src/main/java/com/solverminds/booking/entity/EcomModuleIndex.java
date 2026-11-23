package com.solverminds.booking.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "ecomMdlIndex", schema = "dbo")
public class EcomModuleIndex implements Serializable {

    // ===== Primary Key =====
    @Id
    @Column(name = "mdlId", length = 5, nullable = false)
    private String mdlId;

    // ===== Columns =====
    @Column(name = "mdlPrefix", length = 10)
    private String mdlPrefix;

    @Column(name = "mdlIndex")
    private Integer mdlIndex;

    // ===== Constructors =====
    public EcomModuleIndex() {
    }

    public EcomModuleIndex(String mdlId) {
        this.mdlId = mdlId;
    }

    // ===== Getters/Setters =====
    
    public String getMdlId() {
        return mdlId;
    }

    public void setMdlId(String mdlId) {
        this.mdlId = mdlId;
    }

    public String getMdlPrefix() {
        return mdlPrefix;
    }

    public void setMdlPrefix(String mdlPrefix) {
        this.mdlPrefix = mdlPrefix;
    }

    public Integer getMdlIndex() {
        return mdlIndex;
    }

    public void setMdlIndex(Integer mdlIndex) {
        this.mdlIndex = mdlIndex;
    }

    // ===== equals/hashCode by ID =====
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EcomModuleIndex)) return false;
        EcomModuleIndex that = (EcomModuleIndex) o;
        return Objects.equals(mdlId, that.mdlId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mdlId);
    }

    @Override
    public String toString() {
        return "EcomModuleIndex{" +
                "mdlId='" + mdlId + '\'' +
                ", mdlPrefix='" + mdlPrefix + '\'' +
                ", mdlIndex=" + mdlIndex +
                '}';
    }
}

