
package com.example.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    
    private String accessToken;
    private String tokenType = "Bearer";
    private Long id;
    private String username;
    private String email;
    private String fullName;
    
    public JwtResponse(String accessToken, Long id, String username, String email, String fullName) {
        this.accessToken = accessToken;
        this.id = id;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
    }
}