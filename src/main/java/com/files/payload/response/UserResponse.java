package com.files.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
	
	private long id;
	private String firstname;
	private String lastname;
	private String email;
	private long phone;
	private String password;
//	private LocalDate createdAt;
	private String authorities;

}
