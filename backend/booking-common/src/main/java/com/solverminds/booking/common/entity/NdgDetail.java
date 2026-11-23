package com.solverminds.booking.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

/**
 * Maps {@code ndgdetail}: per-DG-number line items (container-level IMO fields used by API).
 */
@Entity
@Table(name = "ndgdetail")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NdgDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "dgNo", length = 20, nullable = false)
    private String dgNo;

    @Column(name = "MarinePollutant", columnDefinition = "char(1)")
    private String marinePollutant;

    @Column(name = "UNNo", length = 10)
    private String unNo;

    @Column(name = "ContainerNo", length = 20)
    private String containerNo;

    @Column(name = "IMDGClass", length = 10)
    private String imdgClass;

    @Column(name = "FlashPoint")
    private Double flashPoint;

    /**
     * C/F for Celsius/Fahrenheit; legacy may use N for no unit (normalized when mapping to DTO).
     */
    @Column(name = "flashUnit", columnDefinition = "char(2)")
    private String flashUnit;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NdgDetail ndgDetail = (NdgDetail) o;
        return Objects.equals(id, ndgDetail.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
