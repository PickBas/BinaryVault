package com.saied.binaryvault.auth;

import com.saied.binaryvault.appuser.dtos.AppUserDTO;
import com.saied.binaryvault.auth.dtos.AuthenticationRefreshResponse;
import com.saied.binaryvault.auth.dtos.AuthenticationRequest;
import com.saied.binaryvault.auth.dtos.AuthenticationResponse;
import com.saied.binaryvault.auth.dtos.RegistrationRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    @Operation(description = "Login with username and password.")
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "Login is successful.",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Bad credentials.",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
        }
    )
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
        AuthenticationResponse response = authenticationService.login(request);
        return ResponseEntity
            .ok()
            .header(HttpHeaders.AUTHORIZATION, response.accessToken())
            .body(response);
    }

    @PostMapping("/register")
    @Operation(description = "Register with username, email, and password.")
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "201",
                description = "Registration is successful.",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Invalid information was provided for registration.",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
        }
    )
    public ResponseEntity<AppUserDTO> register(@RequestBody @Valid RegistrationRequest request) {
        AppUserDTO userDTO = authenticationService.register(request);
        URI uri = URI.create(
            ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/v1/auth/register")
                .toUriString()
        );
        return ResponseEntity
            .created(uri)
            .body(userDTO);
    }

    @GetMapping("/token-refresh")
    @Operation(description = "Get new access token with refresh token.")
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "New tokens have been issued.",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(
                responseCode = "403",
                description = "Incorrect refresh token.",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
        }
    )
    public ResponseEntity<AuthenticationRefreshResponse> refreshToken(HttpServletRequest request) {
        String refreshToken = request.getHeader(HttpHeaders.AUTHORIZATION).substring("Bearer ".length());
        AuthenticationRefreshResponse response = authenticationService.getRefreshedTokens(refreshToken);
        return ResponseEntity.ok().body(response);
    }

}
