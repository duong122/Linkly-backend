
package com.example.backend.service;

import com.example.backend.dto.request.LoginRequest;
import com.example.backend.dto.request.RegisterRequest;
import com.example.backend.dto.response.JwtResponse;

public interface AuthService {
    
    JwtResponse login(LoginRequest loginRequest);
    
    JwtResponse register(RegisterRequest registerRequest);
}