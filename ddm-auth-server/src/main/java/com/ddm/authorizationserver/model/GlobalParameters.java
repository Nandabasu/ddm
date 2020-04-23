package com.ddm.authorizationserver.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class GlobalParameters extends UserDateAudit implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name = "parameter")
	private String parameter;
	
	@Column(name= "value")
	private int value;

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public long getId() {
		return id;
	}

	@Override
	public String toString() {
		return "GlobalParameters [id=" + id + ", parameter=" + parameter + ", value=" + value + "]";
	}

}
