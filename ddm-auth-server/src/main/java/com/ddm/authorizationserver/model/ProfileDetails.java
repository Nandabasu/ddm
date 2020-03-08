package com.ddm.authorizationserver.model;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="USER_PROFILE")
public class ProfileDetails implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
    @Column(name="fullname")
    private String fullName;
    @Column(name="occupation")
    private String occupation;
    @Column(name="pan")
    private String pan;
    @Column(name="dob")
    private LocalDate dob;
    @Column(name="mobile")
    private String mobile;
    @Column(name="entity_type")
    private String entityType;
	@OneToOne(mappedBy = "profileDetail")
	private User user;
	
	/*@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name="entity_type", referencedColumnName = "id")
    private EntityUserType entityType;*/
	public ProfileDetails() {}
	
	/*public ProfileDetails(String fullName, String occupation, String pan, LocalDate dob, String mobile) {
		super();
		this.fullName = fullName;
		this.occupation = occupation;
		this.pan = pan;
		this.dob = dob;
		this.mobile = mobile;
	}*/
	
	public ProfileDetails(String fullName, String occupation, String pan, LocalDate dob, String mobile, String entityType) {
		super();
		this.fullName = fullName;
		this.occupation = occupation;
		this.pan = pan;
		this.dob = dob;
		this.mobile = mobile;
		this.entityType = entityType;
	}


	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
}
