package com.files.model;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Entity
@AllArgsConstructor
@Table(name = "users")
@Data
@Builder
public class User implements UserDetails {
//	public class User implements UserDetails {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_ID")
	private long id;

	@Column(nullable = false, name = "firstname")
	private String firstname;

	@Column(nullable = false, name = "lastname")
	private String lastname;

	@Column(unique = true, length = 100, nullable = false, name = "email")
	private String email;

	@Column(nullable = false, name = "password")
	private String password;

	@Column(unique = true, name = "phone no")
	private long phone;


	 @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	    @JoinColumn(name = "cart_id")
	    private Cart cart;

	    public User() {
	        this.cart = new Cart();  // Initialize the cart when the user is created
	    }

	@Enumerated(EnumType.STRING)
	private Role role;

	@Override

	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(role.name()));
	}

	@Override
	public String getUsername() {
		return email;
	}

	
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}
	
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}