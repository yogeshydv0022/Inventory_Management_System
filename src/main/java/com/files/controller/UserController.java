package com.files.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.files.payload.request.RegisterRequest;
import com.files.payload.response.UserResponse;
import com.files.services.AuthService;

@RestController
@RequestMapping("/ims/users")
public class UserController {

	@Autowired
    private AuthService authService;

	//create user
    @PostMapping("/")
    public ResponseEntity<UserResponse> registerUser(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.userSignup(request));
    }
}
