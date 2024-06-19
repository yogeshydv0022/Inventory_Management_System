package com.files.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
	
    private String token;
    private long expiresIn;
    
    public String getToken() {
        return token;
    }
}

