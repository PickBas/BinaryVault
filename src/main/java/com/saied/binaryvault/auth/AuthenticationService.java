package com.saied.binaryvault.auth;

import com.saied.binaryvault.appuser.AppUser;
import com.saied.binaryvault.appuser.AppUserRepository;
import com.saied.binaryvault.appuser.AppUserService;
import com.saied.binaryvault.appuser.dtos.AppUserDTOMapper;
import com.saied.binaryvault.appuser.dtos.AppUserDTO;
import com.saied.binaryvault.auth.dtos.AuthenticationRefreshResponse;
import com.saied.binaryvault.auth.dtos.AuthenticationRequest;
import com.saied.binaryvault.auth.dtos.AuthenticationResponse;
import com.saied.binaryvault.security.jwt.JWTUtils;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final AppUserDTOMapper userDTOMapper;
    private final AppUserService userService;
    private final JWTUtils jwtUtils;

    public AuthenticationResponse login(AuthenticationRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                authRequest.username(),
                authRequest.password()
            )
        );
        AppUser principal = (AppUser) authentication.getPrincipal();
        AppUserDTO userDTO = userDTOMapper.apply(principal);
        String accessToken = jwtUtils.issueAccessToken(
            userDTO.username(),
            userDTO.authorities()
        );
        String refreshToken = jwtUtils.issueRefreshToken(
            userDTO.username(),
            userDTO.authorities()
        );
        return new AuthenticationResponse(accessToken, refreshToken, userDTO);
    }

    public AuthenticationRefreshResponse getRefreshedTokens(String oldRefreshToken) {
        AppUserDTO appUser = userDTOMapper.apply(
            userService.findByUsername(jwtUtils.getSubject(oldRefreshToken))
        );
        String accessToken = jwtUtils.issueAccessToken(
            appUser.username(),
            appUser.authorities()
        );
        String refreshToken = jwtUtils.issueRefreshToken(
            appUser.username(),
            appUser.authorities()
        );
        return new AuthenticationRefreshResponse(accessToken, refreshToken);
    }
}
