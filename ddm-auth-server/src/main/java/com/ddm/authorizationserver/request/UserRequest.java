package com.ddm.authorizationserver.request;

import java.time.LocalDate;
import java.util.List;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class UserRequest {


    @JsonIgnore
    private long id;

    @NotBlank(message = "User name cannot be blank")
    private String userName;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, max = 20, message = "Password should contian minimum 6 and max 20 characters")
    private String password;

    @NotBlank(message = "email cannot be blank")
    @Email
    private String email;

    @NotBlank(message = "Full name cannot be blank")
    @Size(min = 2, max = 100)
    private String fullName;

    @NotBlank(message = "Occupation cannot be blank")
    private String occupation;

    @NotBlank(message = "PAN card number cannot be blank")
    @Pattern(regexp = "([A-Z]){5}([0-9]){4}([A-Z]){1}", message = "Please enter valid PAN card number")
    private String pan;

    private LocalDate dob;

    @NotBlank(message = "Mobile number cannot be blank")
    @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number should contain only numeric values, lenght should not be more than 10")
    private String mobile;

    private List<String> roles;


    public List<String> privileges;

    private Long groupId;

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public List<String> getPrivileges() {
        return privileges;
    }

    public void setPrivileges(List<String> privileges) {
        this.privileges = privileges;
    }


    @Override
    public String toString() {
        return "UserRequest [id=" + id + ", userName=" + userName + ", password=" + password + ", email=" + email
                + ", fullName=" + fullName + ", occupation=" + occupation + ", pan=" + pan + ", dob=" + dob
                + ", mobile=" + mobile + ", roles=" + roles + ", groupId=" + groupId
                + "]";
    }

}
