package com.ddm.authorizationserver.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@Table(name = "DDM_GROUP")
public class Group extends UserDateAudit implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name="group_name")
	private String name;
	
	@Column(name = "description")
	private String description;
	
	@JsonIgnore //to-avoid circular dependency
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "group", cascade = CascadeType.ALL)
	private Set<User> user = new HashSet<User>();

	/*@OneToMany(fetch = FetchType.EAGER, mappedBy = "accessedBy", cascade = CascadeType.ALL)
	private Set<User> accessedBy = new HashSet<User>();*/
	
	public Group(String name, String description) {
		super();
		this.name = name;
		this.description = description;
	}

	public Group() {}

	public long getId() {
		return id;
	}

	public Set<User> getUser() {
		return user;
	}

	public void setUser(Set<User> user) {
		this.user = user;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/*public Set<User> getAccessedBy() {
		return accessedBy;
	}

	public void setAccessedBy(Set<User> accessedBy) {
		this.accessedBy = accessedBy;
	}*/
	
}
