package com.ddm.authorizationserver.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "ENTITY_USERS")
public class EntityUser extends UserDateAudit implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(name="entityUserId")
	private long entityUserId;

	@JsonIgnore
	@OneToMany(mappedBy = "entityUser", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<User> userList;
	
	public EntityUser() {}

	public EntityUser(long entityUserId, List<User> userList) {
		super();
		this.entityUserId = entityUserId;
		this.userList = userList;
	}
	
	
	public long getId() {
		return id;
	}

	public List<User> getUserList() {
		return userList;
	}
	public void setUserList(List<User> userList) {
		this.userList = userList;
	}
	public long getEntityUserId() {
		return entityUserId;
	}

	public void setEntityUserId(long entityUserId) {
		this.entityUserId = entityUserId;
	}
	
}
