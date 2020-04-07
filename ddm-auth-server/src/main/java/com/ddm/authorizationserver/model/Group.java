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
import com.fasterxml.jackson.annotation.JsonManagedReference;


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
	@JsonManagedReference
	@OneToMany(fetch = FetchType.LAZY, 
			   mappedBy = "group", 
			   cascade = CascadeType.ALL, 
			   orphanRemoval = true)
	private Set<User> user = new HashSet<User>();

	public Group(String name, String description) {
		super();
		this.name = name;
		this.description = description;
	}

	public Group() {}

	public long getId() {
		return id;
	}
	
	@JsonIgnore
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

	@Override
	public String toString() {
		return "Group [id=" + id + ", name=" + name + ", description=" + description + ", user=" + user + "]";
	}

}
