package com.dev.backend.model;

import java.time.Instant;
import java.util.HashSet;
import java.util.Iterator;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "auth_user")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "auth_user_id")
	private int id;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@Email(message = "Email is invalid")
	@Column(name = "email")
	private String email;

	// @Length(min = 5, message = "Password should be at least 5 characters")
	@Column(name = "password")
	private String password;

	@Column(name = "phoneNumber")
	private String phoneNumber;

	@Column(name = "dateRegisterated")
	private Instant dateRegisterated;

	@ManyToMany()
	@JoinTable(name = "auth_user_role", joinColumns = @JoinColumn(name = "auth_user_id"), inverseJoinColumns = @JoinColumn(name = "auth_role_id"))
	private Set<Role> roles;
	// @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true)
	// @JoinTable(name = "user_address", joinColumns = @JoinColumn(name =
	// "auth_user_id"), inverseJoinColumns = @JoinColumn(name = "address_id"))
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "user")
	private Set<Address> addresses = new HashSet<>();

	public int getId() {
		return id;
	}

	public Set<Address> getAddresses() {
		return addresses;
	}

	public void setAddress(Set<Address> address) {
		this.addresses.add(address.iterator().next());

	}

	public void setAddresses(Set<Address> address) {
		this.addresses = address;
	}

	public Instant getDateRegisterated() {
		return dateRegisterated;
	}

	public void setDateRegisterated(Instant dateRegisterated) {
		this.dateRegisterated = dateRegisterated;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getUsername() {
		return firstName + " " + lastName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public Set<Role> getRoles() {

		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
		System.out.println(roles);
	}

}
