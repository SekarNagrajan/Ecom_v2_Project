package com.solverminds.edi.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "edireadermaster", schema = "dbo")
public class EdiReaderMaster implements Serializable {

    // ===== Primary Key =====
    @Id
    @Column(name = "edireaderid", length = 50, nullable = false)
    private String edireaderid;

    // ===== Columns =====
    @Column(name = "ediid", length = 15)
    private String ediid;

    @Column(name = "agencycode", length = 15)
    private String agencycode;

    @Column(name = "locationcode", length = 15)
    private String locationcode;

    @Column(name = "requestdate")
    private LocalDateTime requestdate;

    @Column(name = "edifilename", length = 200)
    private String edifilename;

    @Column(name = "edifile", length = 200)
    private String edifile;

    @Column(name = "edifilepath", length = 200)
    private String edifilepath;

    @Column(name = "uploadstatus", length = 20)
    private String uploadstatus;

    @Column(name = "datecr")
    private LocalDateTime datecr;

    @Column(name = "usercr", length = 15)
    private String usercr;

    @Column(name = "dateup")
    private LocalDateTime dateup;

    @Column(name = "userup", length = 15)
    private String userup;

    @Column(name = "edipartycode", length = 15)
    private String edipartycode;

    @Column(name = "edisource", length = 1)
    private String edisource;

    @Column(name = "lockid")
    private Integer lockid;

    @Column(name = "edireaderidref", length = 50)
    private String edireaderidref;

    @Column(name = "downloadeddate")
    private LocalDateTime downloadeddate;

    @Column(name = "downloadeduser", length = 15)
    private String downloadeduser;

    @Column(name = "downloadedcount")
    private Integer downloadedcount;

    @Column(name = "erhsender", columnDefinition = "varchar(max)")
    private String erhsender;

    @Column(name = "isprocess", length = 1)
    private String isprocess;

    @Column(name = "startedon")
    private LocalDateTime startedon;

    @Column(name = "partnerid", length = 15)
    private String partnerid;

    @Column(name = "remarks", length = 60)
    private String remarks;

    @Column(name = "InputDetails", length = 500)
    private String inputDetails;

    @Column(name = "notifiedon")
    private LocalDateTime notifiedon;

    @Column(name = "responsedetail", length = 2000)
    private String responsedetail;

    @Column(name = "completeddate")
    private LocalDateTime completeddate;

    // ===== Constructors =====
    public EdiReaderMaster() {
    }

    // ===== Getters and Setters =====
    public String getEdireaderid() {
        return edireaderid;
    }

    public void setEdireaderid(String edireaderid) {
        this.edireaderid = edireaderid;
    }

    public String getEdiid() {
        return ediid;
    }

    public void setEdiid(String ediid) {
        this.ediid = ediid;
    }

    public String getAgencycode() {
        return agencycode;
    }

    public void setAgencycode(String agencycode) {
        this.agencycode = agencycode;
    }

    public String getLocationcode() {
        return locationcode;
    }

    public void setLocationcode(String locationcode) {
        this.locationcode = locationcode;
    }

    public LocalDateTime getRequestdate() {
        return requestdate;
    }

    public void setRequestdate(LocalDateTime requestdate) {
        this.requestdate = requestdate;
    }

    public String getEdifilename() {
        return edifilename;
    }

    public void setEdifilename(String edifilename) {
        this.edifilename = edifilename;
    }

    public String getEdifile() {
        return edifile;
    }

    public void setEdifile(String edifile) {
        this.edifile = edifile;
    }

    public String getEdifilepath() {
        return edifilepath;
    }

    public void setEdifilepath(String edifilepath) {
        this.edifilepath = edifilepath;
    }

    public String getUploadstatus() {
        return uploadstatus;
    }

    public void setUploadstatus(String uploadstatus) {
        this.uploadstatus = uploadstatus;
    }

    public LocalDateTime getDatecr() {
        return datecr;
    }

    public void setDatecr(LocalDateTime datecr) {
        this.datecr = datecr;
    }

    public String getUsercr() {
        return usercr;
    }

    public void setUsercr(String usercr) {
        this.usercr = usercr;
    }

    public LocalDateTime getDateup() {
        return dateup;
    }

    public void setDateup(LocalDateTime dateup) {
        this.dateup = dateup;
    }

    public String getUserup() {
        return userup;
    }

    public void setUserup(String userup) {
        this.userup = userup;
    }

    public String getEdipartycode() {
        return edipartycode;
    }

    public void setEdipartycode(String edipartycode) {
        this.edipartycode = edipartycode;
    }

    public String getEdisource() {
        return edisource;
    }

    public void setEdisource(String edisource) {
        this.edisource = edisource;
    }

    public Integer getLockid() {
        return lockid;
    }

    public void setLockid(Integer lockid) {
        this.lockid = lockid;
    }

    public String getEdireaderidref() {
        return edireaderidref;
    }

    public void setEdireaderidref(String edireaderidref) {
        this.edireaderidref = edireaderidref;
    }

    public LocalDateTime getDownloadeddate() {
        return downloadeddate;
    }

    public void setDownloadeddate(LocalDateTime downloadeddate) {
        this.downloadeddate = downloadeddate;
    }

    public String getDownloadeduser() {
        return downloadeduser;
    }

    public void setDownloadeduser(String downloadeduser) {
        this.downloadeduser = downloadeduser;
    }

    public Integer getDownloadedcount() {
        return downloadedcount;
    }

    public void setDownloadedcount(Integer downloadedcount) {
        this.downloadedcount = downloadedcount;
    }

    public String getErhsender() {
        return erhsender;
    }

    public void setErhsender(String erhsender) {
        this.erhsender = erhsender;
    }

    public String getIsprocess() {
        return isprocess;
    }

    public void setIsprocess(String isprocess) {
        this.isprocess = isprocess;
    }

    public LocalDateTime getStartedon() {
        return startedon;
    }

    public void setStartedon(LocalDateTime startedon) {
        this.startedon = startedon;
    }

    public String getPartnerid() {
        return partnerid;
    }

    public void setPartnerid(String partnerid) {
        this.partnerid = partnerid;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getInputDetails() {
        return inputDetails;
    }

    public void setInputDetails(String inputDetails) {
        this.inputDetails = inputDetails;
    }

    public LocalDateTime getNotifiedon() {
        return notifiedon;
    }

    public void setNotifiedon(LocalDateTime notifiedon) {
        this.notifiedon = notifiedon;
    }

    public String getResponsedetail() {
        return responsedetail;
    }

    public void setResponsedetail(String responsedetail) {
        this.responsedetail = responsedetail;
    }

    public LocalDateTime getCompleteddate() {
        return completeddate;
    }

    public void setCompleteddate(LocalDateTime completeddate) {
        this.completeddate = completeddate;
    }

    // ===== equals, hashCode, toString =====
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EdiReaderMaster that = (EdiReaderMaster) o;
        return Objects.equals(edireaderid, that.edireaderid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(edireaderid);
    }

    @Override
    public String toString() {
        return "EdiReaderMaster{" +
                "edireaderid='" + edireaderid + '\'' +
                ", ediid='" + ediid + '\'' +
                ", agencycode='" + agencycode + '\'' +
                ", locationcode='" + locationcode + '\'' +
                ", partnerid='" + partnerid + '\'' +
                '}';
    }
}

