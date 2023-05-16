package com.saied.binaryvault.auth;

import com.saied.binaryvault.auth.dtos.AuthenticationRefreshResponse;
import com.saied.binaryvault.auth.dtos.AuthenticationRequest;
import com.saied.binaryvault.auth.dtos.AuthenticationResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
        AuthenticationResponse response = authenticationService.login(request);
        return ResponseEntity
            .ok()
            .header(HttpHeaders.AUTHORIZATION, response.accessToken())
            .body(response);
    }

    @GetMapping("/token-refresh")
    public ResponseEntity<AuthenticationRefreshResponse> refreshToken(HttpServletRequest request) {
        String refreshToken = request.getHeader(HttpHeaders.AUTHORIZATION).substring("Bearer ".length());
        AuthenticationRefreshResponse response = authenticationService.getRefreshedTokens(refreshToken);
        return ResponseEntity.ok().body(response);
    }

}
