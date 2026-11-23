package com.solverminds.edi.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "editempbooking_footer", schema = "dbo")
public class EdiTempBookingFooter implements Serializable {

    // ===== Primary Key =====
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    // ===== Columns =====
    @Column(name = "book_no", length = 50)
    private String bookNo;

    @Column(name = "footerdesc", length = 7900)
    private String footerdesc;

    @Column(name = "freightpayableAt", length = 50)
    private String freightpayableAt;

    @Column(name = "placeOfIssue", length = 50)
    private String placeOfIssue;

    @Column(name = "NoofOriginals")
    private Short noofOriginals;

    @Column(name = "dateOfIssue")
    private LocalDateTime dateOfIssue;

    @Column(name = "BLdateIssue")
    private LocalDateTime bldateIssue;

    @Column(name = "shipperdeclaration", length = 1500)
    private String shipperdeclaration;

    @Column(name = "Footertext", length = 255)
    private String footertext;

    @Column(name = "sob", length = 1)
    private String sob;

    @Column(name = "rightofcontrol", length = 50)
    private String rightofcontrol;

    @Column(name = "printamountinusd", length = 1)
    private String printamountinusd;

    @Column(name = "edireaderid", length = 50)
    private String edireaderid;

    @Column(name = "edicleanstatus", length = 1)
    private String edicleanstatus;

    @Column(name = "version")
    private Short version;

    // ===== Constructors =====
    public EdiTempBookingFooter() {
    }

    // ===== Getters and Setters =====
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBookNo() {
        return bookNo;
    }

    public void setBookNo(String bookNo) {
        this.bookNo = bookNo;
    }

    public String getFooterdesc() {
        return footerdesc;
    }

    public void setFooterdesc(String footerdesc) {
        this.footerdesc = footerdesc;
    }

    public String getFreightpayableAt() {
        return freightpayableAt;
    }

    public void setFreightpayableAt(String freightpayableAt) {
        this.freightpayableAt = freightpayableAt;
    }

    public String getPlaceOfIssue() {
        return placeOfIssue;
    }

    public void setPlaceOfIssue(String placeOfIssue) {
        this.placeOfIssue = placeOfIssue;
    }

    public Short getNoofOriginals() {
        return noofOriginals;
    }

    public void setNoofOriginals(Short noofOriginals) {
        this.noofOriginals = noofOriginals;
    }

    public LocalDateTime getDateOfIssue() {
        return dateOfIssue;
    }

    public void setDateOfIssue(LocalDateTime dateOfIssue) {
        this.dateOfIssue = dateOfIssue;
    }

    public LocalDateTime getBldateIssue() {
        return bldateIssue;
    }

    public void setBldateIssue(LocalDateTime bldateIssue) {
        this.bldateIssue = bldateIssue;
    }

    public String getShipperdeclaration() {
        return shipperdeclaration;
    }

    public void setShipperdeclaration(String shipperdeclaration) {
        this.shipperdeclaration = shipperdeclaration;
    }

    public String getFootertext() {
        return footertext;
    }

    public void setFootertext(String footertext) {
        this.footertext = footertext;
    }

    public String getSob() {
        return sob;
    }

    public void setSob(String sob) {
        this.sob = sob;
    }

    public String getRightofcontrol() {
        return rightofcontrol;
    }

    public void setRightofcontrol(String rightofcontrol) {
        this.rightofcontrol = rightofcontrol;
    }

    public String getPrintamountinusd() {
        return printamountinusd;
    }

    public void setPrintamountinusd(String printamountinusd) {
        this.printamountinusd = printamountinusd;
    }

    public String getEdireaderid() {
        return edireaderid;
    }

    public void setEdireaderid(String edireaderid) {
        this.edireaderid = edireaderid;
    }

    public String getEdicleanstatus() {
        return edicleanstatus;
    }

    public void setEdicleanstatus(String edicleanstatus) {
        this.edicleanstatus = edicleanstatus;
    }

    public Short getVersion() {
        return version;
    }

    public void setVersion(Short version) {
        this.version = version;
    }

    // ===== equals, hashCode, toString =====
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EdiTempBookingFooter that = (EdiTempBookingFooter) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "EdiTempBookingFooter{" +
                "id=" + id +
                ", bookNo='" + bookNo + '\'' +
                '}';
    }
}

