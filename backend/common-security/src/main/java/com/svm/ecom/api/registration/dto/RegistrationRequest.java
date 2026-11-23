package com.svm.ecom.api.registration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Schema(name = "RegistrationRequest", description = "Public registration payload")
public class RegistrationRequest {

    // Visible in Swagger (required where noted)
    @Schema(description = "Company name", example = "Ecom testing", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String compName;              // Comp_Name

    @Schema(description = "Address1", example = "138, Bhavbhuti Marg", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String address;               // Address

    @Schema(description = "City", example = "New Delhi", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String city;                  // City

    @Schema(description = "Control location (UN/LOCODE)", example = "INNSA", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String controlLocation;       // control_location

    @Schema(description = "Country code (ISO 2)", example = "IN", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    @Size(min = 2, max = 2)
    private String country;               // Country

    @Schema(description = "PIN/Postal code", example = "110006", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String pinCode;               // Pin_Code

    @Schema(description = "Company phone", example = "91-0724-69964")
    private String compPhone;             // Comp_Phone

    @Schema(description = "Company domain", example = "example.com")
    private String compDomain;            // Comp_Domain

    @Schema(description = "Login email", example = "shammer@solverminds.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @Email @NotBlank
    private String loginName;             // loginname

    @Schema(description = "Password", example = "password", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String password;              // password

    @Schema(description = "First name", example = "Shameer", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String firstName;             // First_Name

    @Schema(description = "Last name", example = "M", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String lastName;              // Last_Name

    @Schema(description = "User phone", example = "91-98-9876543345")
    private String userPhone;             // User_Phone

    @Schema(description = "User fax", example = "")
    private String userFax;               // User_Fax

    @Schema(description = "User email (defaults to login if blank)", example = "shammer@solverminds.com")
    private String userEmail;             // User_Email

    @Schema(description = "Preferred language", example = "EN", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String prefLang;              // prefLang

    @Schema(description = "User mobile", example = "87654-98765433456")
    private String userMobile;            // User_Mobile

    @Schema(description = "Title - Mr. or Messrs. or Mrs. or Ms.", example = "Mr.")
    private String title;                 // Title

    @Schema(description = "Agency code", example = "INDMUM", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String agencyCode;            // agencycode

    // Hidden from Swagger but present in DTO and defaulted server-side
    @Schema(description = "Customer Code", example = "20128909112")
    private String custCode;              // Cust_Code (nullable or generated elsewhere)

    @Schema(hidden = true)
    private String type = "N";            // type (default "N")

    @Schema(hidden = true)
    private String isValid = "E";         // isValid (default "E")

    @Schema(hidden = true)
    private String status = "A";          // status (default "A")

    @Schema(hidden = true)
    private String registeredDate;        // Registered_Date (set to current date-time server-side)

    @Schema(hidden = true)
    private String randomNumber;          // Random_Number (auto number string)

    @Schema(hidden = true)
    private String cityOther = "Y";       // city_other (default "Y")

    @Schema(hidden = true)
    private String regType = "S";         // RegType (default "S")

    @Schema(hidden = true)
    private String regNo;                 // reg_no (running number)

    // Constructors
    public RegistrationRequest() {
        // Set default values
        this.type = "N";
        this.isValid = "E";
        this.status = "A";
        this.cityOther = "Y";
        this.regType = "S";
    }

    public RegistrationRequest(String compName, String address, String city, String controlLocation, String country,
                             String pinCode, String compPhone, String compDomain, String loginName, String password,
                             String firstName, String lastName, String userPhone, String userFax, String userEmail,
                             String prefLang, String userMobile, String title, String agencyCode) {
        this();
        this.compName = compName;
        this.address = address;
        this.city = city;
        this.controlLocation = controlLocation;
        this.country = country;
        this.pinCode = pinCode;
        this.compPhone = compPhone;
        this.compDomain = compDomain;
        this.loginName = loginName;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userPhone = userPhone;
        this.userFax = userFax;
        this.userEmail = userEmail;
        this.prefLang = prefLang;
        this.userMobile = userMobile;
        this.title = title;
        this.agencyCode = agencyCode;
    }

    // Getters and Setters
    public String getCompName() { return compName; }
    public void setCompName(String compName) { this.compName = compName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getControlLocation() { return controlLocation; }
    public void setControlLocation(String controlLocation) { this.controlLocation = controlLocation; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getPinCode() { return pinCode; }
    public void setPinCode(String pinCode) { this.pinCode = pinCode; }

    public String getCompPhone() { return compPhone; }
    public void setCompPhone(String compPhone) { this.compPhone = compPhone; }

    public String getCompDomain() { return compDomain; }
    public void setCompDomain(String compDomain) { this.compDomain = compDomain; }

    public String getLoginName() { return loginName; }
    public void setLoginName(String loginName) { this.loginName = loginName; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getUserPhone() { return userPhone; }
    public void setUserPhone(String userPhone) { this.userPhone = userPhone; }

    public String getUserFax() { return userFax; }
    public void setUserFax(String userFax) { this.userFax = userFax; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getPrefLang() { return prefLang; }
    public void setPrefLang(String prefLang) { this.prefLang = prefLang; }

    public String getUserMobile() { return userMobile; }
    public void setUserMobile(String userMobile) { this.userMobile = userMobile; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAgencyCode() { return agencyCode; }
    public void setAgencyCode(String agencyCode) { this.agencyCode = agencyCode; }

    public String getCustCode() { return custCode; }
    public void setCustCode(String custCode) { this.custCode = custCode; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getIsValid() { return isValid; }
    public void setIsValid(String isValid) { this.isValid = isValid; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRegisteredDate() { return registeredDate; }
    public void setRegisteredDate(String registeredDate) { this.registeredDate = registeredDate; }

    public String getRandomNumber() { return randomNumber; }
    public void setRandomNumber(String randomNumber) { this.randomNumber = randomNumber; }

    public String getCityOther() { return cityOther; }
    public void setCityOther(String cityOther) { this.cityOther = cityOther; }

    public String getRegType() { return regType; }
    public void setRegType(String regType) { this.regType = regType; }

    public String getRegNo() { return regNo; }
    public void setRegNo(String regNo) { this.regNo = regNo; }
}
