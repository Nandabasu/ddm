package com.ddm.authorizationserver.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "USER")
public class User extends UserDateAudit implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(name = "username")
	private String username;
	
	@Column(name = "password")
	private String password;
	
	@Column(name = "email")
	private String email;
		
	@Column(name = "fullname")
	private String fullName;
	
	@Column(name = "occupation")
	private String occupation;
	
	@Column(name = "pan")
	private String pan;
	
	@Column(name = "dob")
	private LocalDate dob;
	
	@Column(name = "mobile")
	private String mobile;
	
	@Column(name="enterprise_user")
	private boolean isEnterpriseUser;

	@Column(name = "enabled", columnDefinition = "tinyint(4) default '1'")
	private boolean enabled;
	
	@Column(name = "accountNonExpired", columnDefinition = "tinyint(4) default '1'")
	private boolean accountNonExpired;
		
	@Column(name = "credentialsNonExpired", columnDefinition = "tinyint(4) default '1'")
	private boolean credentialsNonExpired;
	
	@Column(name = "accountNonLocked", columnDefinition = "tinyint(4) default '1'")
	private boolean accountNonLocked;
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "role_user", 
			   joinColumns = { @JoinColumn(name = "user_id", referencedColumnName = "id") }, 
			   inverseJoinColumns = { @JoinColumn(name = "role_id", referencedColumnName = "id") })
	private Set<Role> roles = new HashSet<Role>();

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
	@JoinColumn(name = "groupId", nullable = false)
	private Group group;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
	private List<Entities> entities = new ArrayList<Entities>();
	
	public User() { }
			
	public User(User user) {
		this.id = user.getId();
		this.username = user.getUsername();
		this.password = user.getPassword();
		this.email = user.getEmail();
		this.enabled = user.isEnabled();
		this.accountNonExpired = user.isAccountNonExpired();
		this.credentialsNonExpired = user.isCredentialsNonExpired();
		this.accountNonLocked = user.isAccountNonLocked();
		this.roles = user.getRoles();
	}
	
	public User(String username, String password, String email, String fullName, String occupation, String pan,
			LocalDate dob, String mobile, boolean isEnterpriseUser, boolean enabled, boolean accountNonExpired,
			boolean credentialsNonExpired, boolean accountNonLocked, Set<Role> roles,
			Group group) {
		super();
		this.username = username;
		this.password = password;
		this.email = email;
		this.fullName = fullName;
		this.occupation = occupation;
		this.pan = pan;
		this.dob = dob;
		this.mobile = mobile;
		this.isEnterpriseUser = isEnterpriseUser;
		this.enabled = enabled;
		this.accountNonExpired = accountNonExpired;
		this.credentialsNonExpired = credentialsNonExpired;
		this.accountNonLocked = accountNonLocked;
		this.roles = roles;
		this.group = group;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
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
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	public void setAccountNonExpired(boolean accountNonExpired) {
		this.accountNonExpired = accountNonExpired;
	}
	
	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	public void setCredentialsNonExpired(boolean credentialsNonExpired) {
		this.credentialsNonExpired = credentialsNonExpired;
	}
	
	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	public void setAccountNonLocked(boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
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

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public boolean isEnterpriseUser() {
		return isEnterpriseUser;
	}

	public void setEnterpriseUser(boolean isEnterpriseUser) {
		this.isEnterpriseUser = isEnterpriseUser;
	}

	public List<Entities> getEntities() {
		return entities;
	}

	public void setEntities(List<Entities> entities) {
		this.entities = entities;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", password=" + password + ", email=" + email
				+ ", fullName=" + fullName + ", occupation=" + occupation + ", pan=" + pan + ", dob=" + dob
				+ ", mobile=" + mobile + ", isEnterpriseUser=" + isEnterpriseUser + ", enabled=" + enabled
				+ ", accountNonExpired=" + accountNonExpired + ", credentialsNonExpired=" + credentialsNonExpired
				+ ", accountNonLocked=" + accountNonLocked + ", roles=" + roles + ", group=" + group + ", entities="
				+ entities + "]";
	}
	
}
