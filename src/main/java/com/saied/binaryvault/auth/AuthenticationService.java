package com.saied.binaryvault.auth;

import com.saied.binaryvault.appuser.AppUser;
import com.saied.binaryvault.appuser.dtos.AppUserDTOMapper;
import com.saied.binaryvault.appuser.dtos.AppUserDTO;
import com.saied.binaryvault.auth.dtos.AuthenticationRefreshResponse;
import com.saied.binaryvault.auth.dtos.AuthenticationRequest;
import com.saied.binaryvault.auth.dtos.AuthenticationResponse;
import com.saied.binaryvault.security.jwt.JWTUtils;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final AppUserDTOMapper customerDTOMapper;
    private final JWTUtils jwtUtils;

    public AuthenticationResponse login(AuthenticationRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                authRequest.username(),
                authRequest.password()
            )
        );
        AppUser principal = (AppUser) authentication.getPrincipal();
        AppUserDTO userDTO = customerDTOMapper.apply(principal);
        String accessToken = jwtUtils.issueAccessToken(userDTO.username(), new HashMap<>());
        String refreshToken = jwtUtils.issueRefreshToken(userDTO.username(), new HashMap<>());
        return new AuthenticationResponse(accessToken, refreshToken, userDTO);
    }

    public AuthenticationRefreshResponse getRefreshedTokens(String oldRefreshToken) {
        String accessToken = jwtUtils.issueAccessToken(
            jwtUtils.getSubject(oldRefreshToken),
            new HashMap<>()
        );
        String refreshToken = jwtUtils.issueRefreshToken(
            jwtUtils.getSubject(oldRefreshToken),
            new HashMap<>()
        );
        return new AuthenticationRefreshResponse(accessToken, refreshToken);
    }
}
