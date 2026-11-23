package com.solverminds.booking.entity;

import javax.persistence.*;
import java.util.Objects;

/**
 * Out of Gauge (OOG) details entity for oversized cargo containers.
 * Table: ecombooking_oog
 * Composite PK: ebookno, conid, comItemNo
 */
@Entity
@Table(name = "ecombooking_oog")
public class EcomBookingOog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "ebookno", length = 20, nullable = false)
    private String ebookno;

    @Column(name = "conid", nullable = false)
    private Integer conid;

    @Column(name = "comItemNo", nullable = false)
    private Integer comItemNo;

    @Column(name = "containerno", length = 11)
    private String containerno;

    @Column(name = "leftft")
    private Float leftft;

    @Column(name = "rightft")
    private Float rightft;

    @Column(name = "forward")
    private Float forward;

    @Column(name = "alt")
    private Float alt;

    @Column(name = "topft")
    private Float topft;

    @Column(name = "unit", length = 1)
    private String unit;

    public EcomBookingOog() {
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

    public Integer getComItemNo() {
        return comItemNo;
    }

    public void setComItemNo(Integer comItemNo) {
        this.comItemNo = comItemNo;
    }

    public String getContainerno() {
        return containerno;
    }

    public void setContainerno(String containerno) {
        this.containerno = containerno;
    }

    public Float getLeftft() {
        return leftft;
    }

    public void setLeftft(Float leftft) {
        this.leftft = leftft;
    }

    public Float getRightft() {
        return rightft;
    }

    public void setRightft(Float rightft) {
        this.rightft = rightft;
    }

    public Float getForward() {
        return forward;
    }

    public void setForward(Float forward) {
        this.forward = forward;
    }

    public Float getAlt() {
        return alt;
    }

    public void setAlt(Float alt) {
        this.alt = alt;
    }

    public Float getTopft() {
        return topft;
    }

    public void setTopft(Float topft) {
        this.topft = topft;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EcomBookingOog)) return false;
        EcomBookingOog that = (EcomBookingOog) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "EcomBookingOog{" +
                "id=" + id +
                ", containerno='" + containerno + '\'' +
                ", unit='" + unit + '\'' +
                '}';
    }
}
