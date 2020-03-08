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

import com.fasterxml.jackson.annotation.JsonBackReference;


@Entity
@Table(name = "DDM_GROUP")
public class Group  implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long group_id;
	
	@Column(name="group_name")
	private String name;
	
	/*@JsonBackReference
	@OneToMany(fetch = FetchType.LAZY)
	@JoinTable(name="user_group", joinColumns = @JoinColumn(name="group_id"),
			inverseJoinColumns = @JoinColumn(name="user_id"))
	private Collection<User> user = new ArrayList<User>();*/
	
	@JsonBackReference
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "group", cascade = CascadeType.ALL)
	private Set<User> user = new HashSet<User>();

	public Group(String name) {
		super();
		this.name = name;
	}

	public Group() {}

	public Set<User> getUser() {
		return user;
	}

	public void setUser(Set<User> user) {
		this.user = user;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
