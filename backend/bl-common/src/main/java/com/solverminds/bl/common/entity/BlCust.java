package com.solverminds.bl.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

/**
 * Entity representing the bl_cust table in Documentation database.
 * Bill of Lading customer/party details.
 * Primary key: cid (bigint). bl_no is FK to bl.bl_no.
 */
@Entity
@Table(name = "bl_cust")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlCust implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cid", nullable = false)
    private Long cid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bl_no", referencedColumnName = "bl_no")
    private Bl bl;

    @Column(name = "custtypeid", length = 2)
    private String custTypeId;

    @Column(name = "custid", length = 15)
    private String custId;

    @Column(name = "countryid", length = 2)
    private String countryId;

    @Column(name = "stateId", length = 4)
    private String stateId;

    @Column(name = "name", length = 150)
    private String name;

    @Column(name = "address1", length = 150)
    private String address1;

    @Column(name = "address2", length = 150)
    private String address2;

    @Column(name = "POBox", length = 50)
    private String poBox;

    @Column(name = "city", length = 150)
    private String city;

    @Column(name = "zipcode", length = 15)
    private String zipCode;

    @Column(name = "telephone", length = 25)
    private String telephone;

    @Column(name = "fax", length = 25)
    private String fax;

    @Column(name = "contact", length = 40)
    private String contact;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "carriercode", length = 4)
    private String carrierCode;

    @Column(name = "linecustid", length = 20)
    private String lineCustId;

    @Column(name = "chkPhone", length = 20)
    private String chkPhone;

    @Column(name = "chkFax", columnDefinition = "char(1)")
    private String chkFax;

    @Column(name = "WYSWYG", length = 1)
    private String wyswyg;

    @Column(name = "uniqueid", length = 60)
    private String uniqueId;

    @Column(name = "chkemail", columnDefinition = "char(1)")
    private String chkEmail;

    @Column(name = "persontype", length = 50)
    private String personType;

    @Column(name = "agentname", length = 50)
    private String agentName;

    @Column(name = "eori", length = 17)
    private String eori;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlCust blCust = (BlCust) o;
        return Objects.equals(cid, blCust.cid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cid);
    }

    @Override
    public String toString() {
        return "BlCust{" +
                "cid=" + cid +
                ", custId='" + custId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
