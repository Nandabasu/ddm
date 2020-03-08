/*package com.ddm.authorizationserver.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "User_Entity_Type")
public class EntityUserType {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	@Column(name="entity_type_name")
	private String entityType;
	@Column(name="description")
	private String description;
	@OneToOne(mappedBy = "entityType")
	private ProfileDetails profileDetail;
	
	public EntityUserType() {}
	
	public EntityUserType(String entityType, String description, ProfileDetails profileDetail) {
		super();
		this.entityType = entityType;
		this.description = description;
		this.profileDetail = profileDetail;
	}

	public ProfileDetails getProfileDetail() {
		return profileDetail;
	}

	public void setProfileDetail(ProfileDetails profileDetail) {
		this.profileDetail = profileDetail;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}
	
	
}
*/