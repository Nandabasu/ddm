/*package com.ddm.authorizationserver.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="USER_GROUP")
public class UserGroup {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@Column(name="userId")
	private long userId;
	
	//rename variable name
	@OneToMany(mappedBy = "userGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User> userGroup;

	public List<User> getUserGroup() {
		return userGroup;
	}

	public void setUserGroup(List<User> userGroup) {
		this.userGroup = userGroup;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getId() {
		return id;
	}
	
}
*/