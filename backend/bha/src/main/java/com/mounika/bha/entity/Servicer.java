package com.mounika.bha.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;


@Entity
@Table(name="service", uniqueConstraints = @UniqueConstraint(columnNames = "email") )
public class Servicer {

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long Id;
	private String firstname;
	private String aadharnumber;
	private String email;
	private String phonenumber;
	private String service;
	private String password;
	private String confirmpassword;
	private String address;
	private String pincode;
	private String role;
	
	public Servicer() {
		super();
	}

	public Servicer(String firstname, String aadharnumber, String email, String phonenumber, String service,
			String password, String confirmpassword, String address, String pincode, String role) {
		super();
		this.firstname = firstname;
		this.aadharnumber = aadharnumber;
		this.email = email;
		this.phonenumber = phonenumber;
		this.service = service;
		this.password = password;
		this.confirmpassword = confirmpassword;
		this.address = address;
		this.pincode = pincode;
		this.role = role;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getAadharnumber() {
		return aadharnumber;
	}

	public void setAadharnumber(String aadharnumber) {
		this.aadharnumber = aadharnumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhonenumber() {
		return phonenumber;
	}

	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirmpassword() {
		return confirmpassword;
	}

	public void setConfirmpassword(String confirmpassword) {
		this.confirmpassword = confirmpassword;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPincode() {
		return pincode;
	}

	public void setPincode(String pincode) {
		this.pincode = pincode;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
	
	
	
}
