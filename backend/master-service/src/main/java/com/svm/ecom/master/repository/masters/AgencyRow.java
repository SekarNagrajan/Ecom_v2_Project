package com.svm.ecom.master.repository.masters;

/**
 * Row projection from {@code agency} for v2 agency list (eight columns).
 */
public class AgencyRow {

    private final String agencyCode;
    private final String agencyName;
    private final String type;
    private final String address;
    private final String city;
    private final String countryCode;
    private final String email;
    private final String location;

    /**
     * @param row native query row: agencycode, agencyname, type, address, city, countrycode, email, location
     */
    public AgencyRow(Object[] row) {
        if (row == null || row.length < 8) {
            throw new IllegalArgumentException("Expected 8 columns from agency query");
        }
        this.agencyCode = col(row, 0);
        this.agencyName = col(row, 1);
        this.type = col(row, 2);
        this.address = col(row, 3);
        this.city = col(row, 4);
        this.countryCode = col(row, 5);
        this.email = col(row, 6);
        this.location = col(row, 7);
    }

    private static String col(Object[] row, int i) {
        if (row[i] == null) {
            return null;
        }
        return String.valueOf(row[i]).trim();
    }

    public String getAgencyCode() {
        return agencyCode;
    }

    public String getAgencyName() {
        return agencyName;
    }

    public String getType() {
        return type;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getEmail() {
        return email;
    }

    public String getLocation() {
        return location;
    }
}
