package com.files.controller;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.files.jwt.JwtService;
import com.files.model.User;
import com.files.payload.request.LoginRequest;
import com.files.payload.response.AuthResponse;
import com.files.payload.response.UserResponse;
import com.files.repository.UserRepository;
import com.files.services.AuthService;

import io.jsonwebtoken.ExpiredJwtException;

@RestController
@RequestMapping("/ims/auth")
public class AuthController {

	@Autowired
    private  AuthService authService;
	
	@Autowired
    private  JwtService jwtService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ModelMapper mapper;
	

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@RequestBody LoginRequest login) {
        User authenticatedUser = authService.authenticateLogin(login);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        AuthResponse loginResponse = new AuthResponse(jwtToken, jwtService.getExpirationTime());
        return ResponseEntity.ok(loginResponse);
    }
    
    @GetMapping("/")
    public ResponseEntity<?> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            return ResponseEntity.ok(convertToDto(user));
        } catch (ExpiredJwtException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expired");
        }
    }
    
    private UserResponse convertToDto(User user) {
		return mapper.map(user, UserResponse.class);
	}
}
