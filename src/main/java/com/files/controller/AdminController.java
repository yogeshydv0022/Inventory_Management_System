package com.files.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.files.payload.request.RegisterRequest;
import com.files.payload.response.UserResponse;
import com.files.services.AuthService;

@RestController
@RequestMapping("/ims/admin")
public class AdminController {

	@Autowired
	private AuthService authService;

	// create admin
	@PostMapping("/")
	public ResponseEntity<UserResponse> registerAdmin(@RequestBody RegisterRequest request) {
		return ResponseEntity.ok(authService.adminSignup(request));
	}

	// update user
	@PutMapping("/{id}")
	public ResponseEntity<UserResponse> updateUser(@RequestBody RegisterRequest request, @PathVariable("id") long id) {
		UserResponse updatedUser = authService.update(request, id);
		return ResponseEntity.ok(updatedUser);
	}

	// delete user
}
