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
@RequestMapping("/ims/superAdmin")
public class SuperAdminController {
	
	 @Autowired
    private AuthService authService;

	 //create superAdmin
    @PostMapping("/")
    public ResponseEntity<UserResponse> registerSuperAdmin(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.superAdminSignup(request));
    }
    
    //update admin
 	@PutMapping("/{id}")
 	public ResponseEntity<UserResponse> updateAdmin(@RequestBody RegisterRequest request, @PathVariable("id") long id) {
 		UserResponse updatedAdmin = authService.update(request, id);
 		return ResponseEntity.ok(updatedAdmin);
 	}
    //delete admin
    
}
